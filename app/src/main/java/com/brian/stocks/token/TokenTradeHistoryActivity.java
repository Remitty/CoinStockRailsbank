package com.brian.stocks.token;

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
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.token.adapters.OrderHistoryAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TokenTradeHistoryActivity extends AppCompatActivity {

    private String mPair;
    TextView noHistory;
    LoadToast loadToast;

    RecyclerView orderHistoryView;
    OrderHistoryAdapter orderHistoryAdapter;
    private ArrayList<JSONObject> ordersHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmt_trade_history);

        if(getSupportActionBar() != null) {
            // getSupportActionBar().setTitle("XMT Trading");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadToast = new LoadToast(this);

        if(getIntent() != null)
            mPair = getIntent().getStringExtra("pair");

        noHistory = findViewById(R.id.empty_text);

        orderHistoryView = findViewById(R.id.history_view);
        orderHistoryAdapter = new OrderHistoryAdapter(ordersHistoryList);
        orderHistoryView.setLayoutManager(new LinearLayoutManager(this));
        orderHistoryView.setAdapter(orderHistoryAdapter);

        getHistoryData();
    }

    private void getHistoryData() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("pair", mPair);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadToast.show();
        Log.d("exchange param", jsonObject.toString()+" -> "+ URLHelper.COIN_TRADE_HISTORY_DATA);
            AndroidNetworking.post(URLHelper.COIN_TRADE_HISTORY_DATA)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(this,"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loadToast.hide();
                            Log.d("xmt trade history response", "" + response.toString());
                            try {
                                JSONArray ordersHistory = response.getJSONArray("history");

                                for (int i = 0; i < ordersHistory.length(); i++) {
                                    try {
                                        ordersHistoryList.add(ordersHistory.getJSONObject(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(ordersHistoryList.size() > 0)
                                    noHistory.setVisibility(View.GONE);

                                orderHistoryAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        @Override
                        public void onError(ANError error) {
                            // handle error
                            loadToast.hide();
                            Toast.makeText(getBaseContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        }
                    });

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
