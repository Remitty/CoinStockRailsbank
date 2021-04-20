package com.brian.stocks.coins;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.coins.adapter.StakeAdapter;
import com.brian.stocks.helper.BigDecimalDouble;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.home.HomeActivity;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CoinStakeActivity extends AppCompatActivity {
    private String symbol, id;
    private Double balance = 0.0, amount = 0.0, dailyReward=0.0, rewardPercent = 0.0;
    Button btnStake, btnRelease;
    TextView mtvYearlyFee, mtvBalance, mtvStakingBalance, mtvDailyReward, mtvSymbol, mtVSymbolInput;
    EditText editAmount;
    private LoadToast loadToast;
    private Double mBalance = 0.0, mStakingBalance = 0.0;

    RecyclerView stakeHistoryView;
    ArrayList stakeList = new ArrayList();
    StakeAdapter stakeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_stake);

        if(getSupportActionBar() != null){
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        mtvSymbol = findViewById(R.id.symbol);
        mtVSymbolInput = findViewById(R.id.symbol_input);

        mtvBalance = findViewById(R.id.xmt_balance);
        mtvYearlyFee = findViewById(R.id.yearly_fee);
        mtvStakingBalance = findViewById(R.id.staking_balance);
        mtvDailyReward = findViewById(R.id.daily_reward);
        editAmount = findViewById(R.id.edit_send_amount);

        btnStake = findViewById(R.id.btn_stake);
        btnRelease = findViewById(R.id.btn_stake_release);
        btnStake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmStakeAlert();
            }
        });
        btnRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmReleaseAlert();
            }
        });

        stakeHistoryView = findViewById(R.id.stake_history);
        stakeHistoryView.setLayoutManager(new LinearLayoutManager(this));
        stakeAdapter = new StakeAdapter(stakeList);
        stakeHistoryView.setAdapter(stakeAdapter);

        if(getIntent() != null) {
            symbol = getIntent().getStringExtra("symbol");
            id = getIntent().getStringExtra("id");
            rewardPercent = getIntent().getDoubleExtra("stake_reward_yearly_percent", 5);
            mBalance = getIntent().getDoubleExtra("balance", 0);
            mStakingBalance = getIntent().getDoubleExtra("amount", 0);
            dailyReward = getIntent().getDoubleExtra("dailyReward", 0);

            mtvSymbol.setText(symbol);
            mtVSymbolInput.setText(symbol);

            mtvBalance.setText(new DecimalFormat("#,###.##").format(mBalance));
            mtvStakingBalance.setText(new DecimalFormat("#,###.##").format(mStakingBalance));
            mtvDailyReward.setText(String.format("+ %.4f", dailyReward));
            mtvYearlyFee.setText(String.format("+ %.2f", rewardPercent) + " %");
        }

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
//                                mBalance = response.getDouble("coin_balance");
//                                mStakingBalance = response.getDouble("stake_balance");
//                                mtvBalance.setText(new DecimalFormat("#,###.##").format(mBalance));
//                                mtvStakingBalance.setText(new DecimalFormat("#,###.##").format(mStakingBalance));
//                                mtvDailyReward.setText(String.format("+ %.4f", response.optDouble("daily_reward")));
//                                mtvYearlyFee.setText(String.format("+ %.2f", response.optDouble("yearly_fee")) + " %");

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

    private void confirmReleaseAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setIcon(R.mipmap.ic_launcher_round)
                .setTitle("Confirm stake release")
                .setMessage("Are you sure you want to release " + editAmount.getText().toString() + symbol + "?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendStakeRelease();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void confirmStakeAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setIcon(R.mipmap.ic_launcher_round)
                .setTitle("Confirm stake")
                .setMessage("Are you sure you want to stake " + editAmount.getText().toString() + symbol + "?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendStake();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void sendStake() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", editAmount.getText().toString());
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(URLHelper.REQUEST_STAKE)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        if(response.optBoolean("success")){
                            try {
                                mBalance = response.getDouble("coin_balance");
                                mStakingBalance = response.getDouble("stake_balance");
                                mtvBalance.setText(new DecimalFormat("#,###.##").format(mBalance));
                                mtvStakingBalance.setText(new DecimalFormat("#,###.##").format(mStakingBalance));
                                mtvDailyReward.setText(String.format("+ %.4f", response.optDouble("daily_reward")));
                                mtvYearlyFee.setText(String.format("+ %.2f", response.optDouble("yearly_fee")) + " %");

                                stakeList.clear();
                                JSONArray stakes = response.getJSONArray("stake_histories");
                                for (int i = 0; i < stakes.length(); i ++) {
                                    stakeList.add(stakes.getJSONObject(i));
                                }
                                stakeAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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

    private void sendStakeRelease() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", editAmount.getText().toString());
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(URLHelper.REQUEST_STAKE_RELEASE)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        if(response.optBoolean("success")){
                            try {
                                mBalance = response.getDouble("coin_balance");
                                mStakingBalance = response.getDouble("stake_balance");
                                mtvBalance.setText(new DecimalFormat("#,###.##").format(mBalance));
                                mtvStakingBalance.setText(new DecimalFormat("#,###.##").format(mStakingBalance));
                                mtvDailyReward.setText(String.format("+ %.4f", response.optDouble("daily_reward")));
                                mtvYearlyFee.setText(String.format("+ %.2f", response.optDouble("yearly_fee")) + " %");

                                stakeList.clear();
                                JSONArray stakes = response.getJSONArray("stake_histories");
                                for (int i = 0; i < stakes.length(); i ++) {
                                    stakeList.add(stakes.getJSONObject(i));
                                }
                                stakeAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CoinStakeActivity.this, HomeActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
