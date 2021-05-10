package com.wyre.trade.coins;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.coins.adapter.StakableCoinAdapter;
import com.wyre.trade.coins.adapter.StakeAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StakeAssetListActivity extends AppCompatActivity {

    private Double dailyReward = 0.0;
    RecyclerView stakableView;
    StakableCoinAdapter stakableCoinAdapter;
    ArrayList<JSONObject> stakableList = new ArrayList();

    RecyclerView stakeHistoryView;
    ArrayList stakeList = new ArrayList();
    StakeAdapter stakeAdapter;

    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_stake_list);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("");
        }

        loadToast = new LoadToast(this);

        stakeHistoryView = findViewById(R.id.stake_history);
        stakeHistoryView.setLayoutManager(new LinearLayoutManager(this));
        stakeAdapter = new StakeAdapter(stakeList);
        stakeHistoryView.setAdapter(stakeAdapter);

        stakableView = findViewById(R.id.stakes_view);
        stakableView.setLayoutManager(new LinearLayoutManager(this));
        stakableCoinAdapter = new StakableCoinAdapter(this, stakableList);
        stakableCoinAdapter.setListener(new StakableCoinAdapter.Listener() {
            @Override
            public void onSelect(int position) {
                JSONObject item = stakableList.get(position);
                Intent intent = new Intent(StakeAssetListActivity.this, StakeActivity.class);
                intent.putExtra("symbol", item.optString("symbol"));
                intent.putExtra("id", item.optString("id"));
                intent.putExtra("balance", item.optDouble("balance"));
                intent.putExtra("amount", item.optDouble("amount"));
                intent.putExtra("dailyReward", item.optDouble("daily_reward"));
                intent.putExtra("stake_reward_yearly_percent", item.optDouble("stake_reward_yearly_percent"));
                startActivity(intent);
            }
        });
        stakableView.setAdapter(stakableCoinAdapter);

        getData();
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_STAKE_LIST)
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

//                                dailyReward = response.getDouble("stake_reward_yearly_percent");

                                stakeList.clear();
//
//                                JSONArray stakes = response.getJSONArray("stake_histories");
//                                for (int i = 0; i < stakes.length(); i ++) {
//                                    stakeList.add(stakes.getJSONObject(i));
//                                }
//                                stakeAdapter.notifyDataSetChanged();
                                stakableList.clear();
                                JSONArray stakable = response.getJSONArray("stakes");
                                for (int i = 0; i < stakable.length(); i ++) {
                                    stakableList.add(stakable.getJSONObject(i));
                                }
                                stakableCoinAdapter.notifyDataSetChanged();

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
