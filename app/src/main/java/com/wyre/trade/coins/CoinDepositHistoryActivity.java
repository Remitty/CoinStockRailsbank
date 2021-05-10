package com.wyre.trade.coins;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.adapters.DepositAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.DepositInfo;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoinDepositHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private DepositAdapter mAdapter;
    private List<DepositInfo> depositList = new ArrayList<>();
    private LoadToast loadToast;
    private TextView noHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_deposit_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        loadToast = new LoadToast(this);

        initComponents();

        getDepositHistory();
    }

    private void initComponents() {
        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new DepositAdapter(depositList, getBaseContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(mAdapter);
        noHistory = findViewById(R.id.no_activity);
    }

    private void getDepositHistory() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        Log.d("access_token", SharedHelper.getKey(this, "access_token"));
        if(this != null)
            AndroidNetworking.get(URLHelper.COIN_DEPOSIT)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(this,"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();
                            depositList.clear();

                            JSONArray coins = response.optJSONArray("data");
                            for(int i = 0; i < coins.length(); i ++) {
                                try {
                                    depositList.add(new DepositInfo((JSONObject) coins.get(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(depositList.size() != 0)
                                noHistory.setVisibility(View.GONE);

                            mAdapter.notifyDataSetChanged();
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
