package com.wyre.trade.zabo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.DepositDialog;
import com.wyre.trade.model.ZaboAsset;
import com.wyre.trade.zabo.adapters.ZaboAssetAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ZaboAccountActivity extends AppCompatActivity {

    private LoadToast loadToast;
    private String accountId = "";
    TextView noWallet;
    RecyclerView accountView;
    ArrayList<ZaboAsset> assetList = new ArrayList();
    ZaboAssetAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zaho_account);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("");
        }

        accountId = getIntent().getStringExtra("account_id");

        loadToast = new LoadToast(this);

        noWallet = findViewById(R.id.no_wallet);

        accountView = findViewById(R.id.asset_view);
        mAdapter = new ZaboAssetAdapter(this, assetList);
        accountView.setLayoutManager(new LinearLayoutManager(this));
        accountView.setAdapter(mAdapter);
        mAdapter.setListener(new ZaboAssetAdapter.Listener() {
            @Override
            public void onDeposit(int position) {
                ZaboAsset asset = assetList.get(position);
                sendDeposit(asset.getSymbol());
            }

        });
        
        getData();
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_ZABO_ACCOUNT + accountId)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        try {

                            assetList.clear();
                            JSONArray accounts = response.getJSONArray("assets");
                            for (int i = 0; i < accounts.length(); i ++) {
                                ZaboAsset asset = new ZaboAsset();
                                asset.setData(accounts.getJSONObject(i));
                                assetList.add(asset);
                            }
                            if(assetList.size() > 0)
                                noWallet.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getBaseContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
    }

    private void sendDeposit(final String symbol) {
        loadToast.show();
        JSONObject object = new JSONObject();
        try {
            object.put("asset", symbol);
            object.put("account_id", accountId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("zabo deposit params", object.toString());
        AndroidNetworking.post(URLHelper.REQUEST_ZABO_DEPOSIT)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addJSONObjectBody(object)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();

                        showWalletAddressDialog(response, symbol);

                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getBaseContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
    }

    private void showWalletAddressDialog(JSONObject data, String symbol) {
        DepositDialog mContentDialog;
        mContentDialog = new DepositDialog(R.layout.fragment_coin_deposit, data, symbol);
        mContentDialog.setListener(new DepositDialog.Listener() {

            @Override
            public void onOk() {
//                mContentDialog.dismiss();
                Toast.makeText(getBaseContext(), "Copied successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
//                mContentDialog.dismiss();
            }
        });
        mContentDialog.show(getSupportFragmentManager(), "deposit");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
