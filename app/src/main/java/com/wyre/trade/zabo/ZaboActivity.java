package com.wyre.trade.zabo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.WebViewActivity;
import com.wyre.trade.model.ZaboAccount;
import com.wyre.trade.zabo.adapters.ZaboAccountAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ZaboActivity extends AppCompatActivity {

    private static final int ZAHO_CONNECT = 100;
    private LoadToast loadToast;
    private String clientId, testmode;
    Button btnConnect;
    RecyclerView accountView;
    ArrayList<ZaboAccount> accountList = new ArrayList();
    ZaboAccountAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zabo);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("");
        }

        if(getIntent().getDataString() != null) {
            Intent intent = getIntent();
//            Log.d("zaho deep link", intent.getDataString());
        }

        loadToast = new LoadToast(this);

        accountView = findViewById(R.id.accounts_view);
        mAdapter = new ZaboAccountAdapter(this, accountList);
        accountView.setLayoutManager(new LinearLayoutManager(this));
        accountView.setAdapter(mAdapter);
        mAdapter.setListener(new ZaboAccountAdapter.Listener() {
            @Override
            public void onSelect(int position) {
                ZaboAccount account = accountList.get(position);
                Intent intent = new Intent(ZaboActivity.this, ZaboAccountActivity.class);
                intent.putExtra("account_id", account.getId());
                startActivity(intent);
            }
        });

        btnConnect = findViewById(R.id.btn_connect_account);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String base = "https://connect.zabo.com/connect";
                String apikey = "?client_id="+clientId;
                String origin = "&origin=wyretrade.com";
                String env = "&zabo_env="+testmode;
                String version = "&zabo_version=latest";
                String userId = SharedHelper.getKey(getBaseContext(), "userId");
//                String redirect = "&redirect_uri=" + URLHelper.ZABO_REDIRECT;
//                String redirect = "";

                String url = base + apikey + origin + env + version + "&" + userId;
                Log.d("zabo url:", url);
                Intent browserIntent = new Intent(ZaboActivity.this, WebViewActivity.class);
                browserIntent.putExtra("uri", url);
                startActivityForResult(browserIntent, ZAHO_CONNECT);
            }
        });

        getData();
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_ZABO_ACCOUNTS)
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
                            clientId = response.getString("client_id");
                            testmode = response.getString("test_mode");
                            accountList.clear();
                            JSONArray accounts = response.getJSONArray("accounts");
                            for (int i = 0; i < accounts.length(); i ++) {
                                ZaboAccount account = new ZaboAccount();
                                account.setData(accounts.getJSONObject(i));
                                accountList.add(account);
                            }
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

    private void sendAccount(String id, String token) {
        loadToast.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("send zabo account", object.toString());
        AndroidNetworking.post(URLHelper.REQUEST_ZABO_ACCOUNTS)
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
                        accountList.clear();
                        JSONArray accounts = null;
                        try {
                            accounts = response.getJSONArray("accounts");
                            for (int i = 0; i < accounts.length(); i ++) {
                                ZaboAccount account = new ZaboAccount();
                                account.setData(accounts.getJSONObject(i));
                                accountList.add(account);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ZAHO_CONNECT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra("response")) {
                        try {
                            String url = data.getStringExtra("response");
                            URL aURL = new URL(url);
                            String query = aURL.getQuery();
                            query = query.replace("%20", "");
                            query = query.replace("%22", "'");
                            query = query.substring(8);
                            try {
                                JSONObject account = new JSONObject(query);
                                String id = account.getString("id");
                                String token = account.getString("token");
//                                sendAccount(id, token);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
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
