package com.wyre.trade.stock.deposit;

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
import com.wyre.trade.model.TransferInfo;
import com.wyre.trade.stock.adapter.StockTransferAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Coin2StockHistoryActivity extends AppCompatActivity {
    private LoadToast loadToast;

    RecyclerView mTransferListView;
    StockTransferAdapter mTransferAdapter;
    private List<TransferInfo> historyList = new ArrayList<>();

    String kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin2_stock_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        if(getIntent() != null) {
            kind = getIntent().getStringExtra("kind");
        }

        loadToast = new LoadToast(this);

        mTransferListView = findViewById(R.id.list_transfer_view);
        mTransferAdapter = new StockTransferAdapter(historyList, true);
        mTransferListView.setLayoutManager(new LinearLayoutManager(this));
        mTransferListView.setAdapter(mTransferAdapter);

        loadHistory();
    }

    private void loadHistory() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.GET_DEPOSIT_STOCK_HISTORY)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                    .addQueryParameter("kind", kind)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loadToast.success();

                            historyList.clear();

                            JSONArray stocks = response.optJSONArray("transfer_history");
                            for(int i = 0; i < stocks.length(); i ++) {
                                try {
                                    historyList.add(new TransferInfo((JSONObject) stocks.get(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            mTransferAdapter.notifyDataSetChanged();

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
