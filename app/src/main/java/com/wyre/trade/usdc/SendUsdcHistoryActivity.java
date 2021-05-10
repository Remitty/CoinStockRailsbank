package com.wyre.trade.usdc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.usdc.adapters.TransferCoinHistoryAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SendUsdcHistoryActivity extends AppCompatActivity {
    private LoadToast loadToast;

    RecyclerView payHistoryView;
    ArrayList<JSONObject> payHistory = new ArrayList();
    TransferCoinHistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_usdc_history);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);

        payHistoryView = findViewById(R.id.pay_history_view);

        historyAdapter = new TransferCoinHistoryAdapter(payHistory);
        payHistoryView.setAdapter(historyAdapter);
        payHistoryView.setLayoutManager(new LinearLayoutManager(this));

        getData();

    }

    private void getData() {
        loadToast.show();
        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.TRANSFER_COIN_HISTORY)
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

                            payHistory.clear();
                            try {
                                JSONArray payhistory = response.getJSONArray("pay_history");
                                for(int i = 0; i < payhistory.length(); i ++) {
                                    try {
                                        payHistory.add(payhistory.getJSONObject(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                historyAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
