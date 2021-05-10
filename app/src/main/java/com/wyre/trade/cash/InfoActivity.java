package com.wyre.trade.cash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.BankInfo;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    EditText usAccountId, usAccountNo, usRoutingNo, eurAccountId, eurIban, eurSwift, gbpAccountId, gbpAccountNo, gbpSortCode;
    Button btnAssign;
    ImageButton btnCopyUsdId,btnCopyUsdAccountNo, btnCopyUsdRoutingNo, btnCopyEurId, btnCopyEurIban, btnCopyEurSwift, btnCopyGbpId, btnCopyGbpAccountNo, btnCopyGbpSort;
    private LoadToast loadToast;

    ArrayList<BankInfo> currencies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        if(getSupportActionBar() != null)
        {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        initComponents();

        initListeners();

        getData();
    }

    private void initListeners() {
        btnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAssignIban();
            }
        });

        btnCopyUsdId.setOnClickListener(this);
        btnCopyUsdAccountNo.setOnClickListener(this);
        btnCopyUsdRoutingNo.setOnClickListener(this);
        btnCopyEurId.setOnClickListener(this);
        btnCopyEurIban.setOnClickListener(this);
        btnCopyEurSwift.setOnClickListener(this);
        btnCopyGbpId.setOnClickListener(this);
        btnCopyGbpAccountNo.setOnClickListener(this);
        btnCopyGbpSort.setOnClickListener(this);
    }

    private void initComponents() {
        usAccountId = findViewById(R.id.edit_usd_account);
        usAccountNo = findViewById(R.id.edit_usd_account_number);
        usRoutingNo = findViewById(R.id.edit_usd_routing_number);
        eurAccountId = findViewById(R.id.edit_eur_account);
        eurIban = findViewById(R.id.edit_eur_iban);
        eurSwift = findViewById(R.id.edit_eur_swift);
        gbpAccountId = findViewById(R.id.edit_gbp_account);
        gbpAccountNo = findViewById(R.id.edit_gbp_account_number);
        gbpSortCode = findViewById(R.id.edit_gbp_sort_code);
        btnAssign = findViewById(R.id.btn_assign);
        btnAssign.setVisibility(View.GONE);

        btnCopyUsdId = findViewById(R.id.btn_copy_usd_id);
        btnCopyUsdAccountNo = findViewById(R.id.btn_copy_usd_account_no);
        btnCopyUsdRoutingNo = findViewById(R.id.btn_copy_usd_routing_no);
        btnCopyEurId = findViewById(R.id.btn_copy_eur_id);
        btnCopyEurIban = findViewById(R.id.btn_copy_eur_iban);
        btnCopyEurSwift = findViewById(R.id.btn_copy_eur_swift);
        btnCopyGbpId = findViewById(R.id.btn_copy_gbp_id);
        btnCopyGbpAccountNo = findViewById(R.id.btn_copy_gbp_account_no);
        btnCopyGbpSort = findViewById(R.id.btn_copy_gbp_sort);
    }

    private void handleAssignIban() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.REQUEST_ADD_IBAN)
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

                        if(response.optBoolean("success")) {
                            eurIban.setText(response.optJSONObject("bank").optString("iban"));
                            eurSwift.setText(response.optJSONObject("bank").optString("bic_swift"));
                        }
                        else
                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_BANK_DETAIL)
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
                        currencies.clear();

                        JSONArray currencies_array = response.optJSONArray("currencies");
                        for(int i = 0; i < currencies_array.length(); i ++) {
                            BankInfo bankInfo = new BankInfo();
                            bankInfo.setData(currencies_array.optJSONObject(i));
                            currencies.add(bankInfo);
                            if(currencies.get(i).getCurrencyID().equals("1")) {
                                usAccountId.setText(currencies.get(i).getBankId());
                                usAccountNo.setText(currencies.get(i).getUSAccountNo());
                                usRoutingNo.setText(currencies.get(i).getUSRoutingNo());
                            }

                            if(currencies.get(i).getCurrencyID().equals("2")) {
                                eurAccountId.setText(currencies.get(i).getBankId());
                                eurIban.setText(currencies.get(i).getIban());
                                eurSwift.setText(currencies.get(i).getSwift());
                                if(currencies.get(i).getSwift().equals(""))
                                    btnAssign.setVisibility(View.VISIBLE);
                            }
                            if(currencies.get(i).getCurrencyID().equals("3")) {
                                gbpAccountId.setText(currencies.get(i).getBankId());
                                gbpAccountNo.setText(currencies.get(i).getUKAccount());
                                gbpSortCode.setText(currencies.get(i).getUKSortCode());
                            }
                        }


                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = null;
        switch (v.getId()) {
            case R.id.btn_copy_usd_id:
                clipData = ClipData.newPlainText("text", usAccountId.getText());
                break;
            case R.id.btn_copy_usd_account_no:
               clipData = ClipData.newPlainText("text", usAccountNo.getText());
                break;
            case R.id.btn_copy_usd_routing_no:
                clipData = ClipData.newPlainText("text", usRoutingNo.getText());
                break;
            case R.id.btn_copy_eur_id:
                clipData = ClipData.newPlainText("text", eurAccountId.getText());
                break;
            case R.id.btn_copy_eur_iban:
                clipData = ClipData.newPlainText("text", eurIban.getText());
                break;
            case R.id.btn_copy_eur_swift:
                clipData = ClipData.newPlainText("text", eurSwift.getText());
                break;
            case R.id.btn_copy_gbp_id:
                clipData = ClipData.newPlainText("text", gbpAccountId.getText());
                break;
            case R.id.btn_copy_gbp_account_no:
                clipData = ClipData.newPlainText("text", gbpAccountNo.getText());
                break;
            case R.id.btn_copy_gbp_sort:
                clipData = ClipData.newPlainText("text", gbpSortCode.getText());
                break;
        }
        manager.setPrimaryClip(clipData);
        Toast.makeText(getBaseContext(), "Copied successfully", Toast.LENGTH_SHORT).show();
    }
}
