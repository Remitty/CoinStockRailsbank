package com.brian.stocks.coins;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.adapters.DepositAdapter;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.DepositInfo;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_deposit_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Coin Activity");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadToast = new LoadToast(this);
        loadToast.setBackgroundColor(R.color.colorBlack);

        initComponents();

        getDepositHistory();
    }

    private void initComponents() {
        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new DepositAdapter(depositList, getBaseContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(mAdapter);
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
