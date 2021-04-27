package com.wyre.trade.coins;

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
import com.wyre.trade.coins.adapter.StakeAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StakeHistoryActivity extends AppCompatActivity {

    private LoadToast loadToast;

    RecyclerView stakeHistoryView;
    ArrayList stakeList = new ArrayList();
    StakeAdapter stakeAdapter;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stake_history);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        if(getIntent() != null) {
            id = getIntent().getStringExtra("id");
        }

        loadToast = new LoadToast(this);


        stakeHistoryView = findViewById(R.id.stake_history);
        stakeHistoryView.setLayoutManager(new LinearLayoutManager(this));
        stakeAdapter = new StakeAdapter(stakeList);
        stakeHistoryView.setAdapter(stakeAdapter);

        getData();
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_STAKE_BALANCE + "/" + id)
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
                        if(response.optBoolean("success")){
                            try {
                                stakeList.clear();
                                JSONArray stakes = response.getJSONArray("stake_histories");
                                for (int i = 0; i < stakes.length(); i ++) {
                                    stakeList.add(stakes.getJSONObject(i));
                                }
                                stakeAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else
                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
