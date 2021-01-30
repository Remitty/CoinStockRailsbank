package com.brian.stocks.coins;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.brian.stocks.helper.BigDecimalDouble;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

public class CoinStakeActivity extends AppCompatActivity {
    Button btnStake, btnRelease;
    TextView mtvYearlyFee, mtvBalance, mtvStakingBalance, mtvDailyReward;
    EditText editAmount;
    private LoadToast loadToast;
    private String mBalance, mStakingBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_stake);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Stake XMT");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadToast = new LoadToast(this);
        loadToast.setBackgroundColor(R.color.colorBlack);

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

        getData();
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_STAKE_BALANCE)
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
                                mBalance = response.getString("xmt_balance");
                                mStakingBalance = response.getString("stake_balance");
                                mtvBalance.setText(String.format("%.2f", Double.parseDouble(mBalance)));
                                mtvStakingBalance.setText(String.format("%.2f", Double.parseDouble(mStakingBalance)));
                                mtvDailyReward.setText(String.format("+ %.4f", Double.parseDouble(response.optString("daily_reward"))));
                                mtvYearlyFee.setText(String.format("+ %.2f", Double.parseDouble(response.optString("yearly_fee"))) + " %");
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
                .setMessage("Are you sure you want to release " + editAmount.getText().toString() + "XMT?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendStakeRelease();
                    }
                })
                .show();
    }

    private void confirmStakeAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setIcon(R.mipmap.ic_launcher_round)
                .setTitle("Confirm stake")
                .setMessage("Are you sure you want to stake " + editAmount.getText().toString() + "XMT?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendStake();
                    }
                })
                .show();
    }

    private void sendStake() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", editAmount.getText().toString());
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
                                mBalance = response.getString("xmt_balance");
                                mStakingBalance = response.getString("stake_balance");
                                mtvBalance.setText(String.format("%.2f", Double.parseDouble(mBalance)));
                                mtvStakingBalance.setText(String.format("%.2f", Double.parseDouble(mStakingBalance)));
                                mtvDailyReward.setText(String.format("+ %.4f", Double.parseDouble(response.optString("daily_reward"))));
                                mtvYearlyFee.setText(String.format("+ %.2f", Double.parseDouble(response.optString("yearly_fee"))) + " %");
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
                                mBalance = response.getString("xmt_balance");
                                mStakingBalance = response.getString("stake_balance");
                                mtvBalance.setText(String.format("%.2f", Double.parseDouble(mBalance)));
                                mtvStakingBalance.setText(String.format("%.2f", Double.parseDouble(mStakingBalance)));
                                mtvDailyReward.setText(String.format("+ %.4f", Double.parseDouble(response.optString("daily_reward"))));
                                mtvYearlyFee.setText(String.format("+ %.2f", Double.parseDouble(response.optString("yearly_fee"))) + " %");
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
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
