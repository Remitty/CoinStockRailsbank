package com.wyre.trade.stock.stockwithdraw;

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
import com.wyre.trade.stock.stockwithdraw.adapters.StockWithdrawAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StockWithdrawHistoryActivity extends AppCompatActivity {

    private LoadToast loadToast;

    private ArrayList<JSONObject> history = new ArrayList<>();
    RecyclerView historyView;
    StockWithdrawAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_withdraw_history);

        loadToast = new LoadToast(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        historyView = findViewById(R.id.history_view);
        historyView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mAdapter = new StockWithdrawAdapter(history);
        historyView.setAdapter(mAdapter);

        getData();
    }

    private void getData() {
        loadToast.show();
        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.STOCK_WITHDRAW)
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
                                history.clear();
                                JSONArray temp = response.getJSONArray("history");
                                for(int i = 0; i < temp.length(); i ++) {
                                    try {
                                        history.add(temp.getJSONObject(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
                            Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
