package com.wyre.trade.predict;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.CoinBalanceFormat;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.google.android.material.button.MaterialButtonToggleGroup;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddPredictActivity extends AppCompatActivity implements View.OnClickListener {
    LoadToast loadToast;
    ConfirmAlert confirmAlert;
    private String symbol;
    private int duration = 2;
    private Double usdcBalance = 0.0;
    TextView mtvSymbol, mtvName, mtvEndDate, mtvPrice, mtvUsdcBalance;
    EditText meditEstPrice, meditBetPrice;
    RadioGroup radioGroup;
    MaterialButtonToggleGroup group1;
    Button btn1H, btn1W, btn2W, btn3W, btn4W, btn5W,  btnCreate;
    Button btn10, btn50, btn100, btn200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_predict);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        loadToast = new LoadToast(this);
        confirmAlert = new ConfirmAlert(AddPredictActivity.this);

        initComponents();
        initListeners();
        if(getIntent() != null) {
            symbol = getIntent().getStringExtra("symbol");
            mtvSymbol.setText(symbol);
            String price = new DecimalFormat("#,###.##").format(getIntent().getDoubleExtra("price", 0.0));
            mtvPrice.setText("$ "+ price);
            mtvName.setText(getIntent().getStringExtra("name"));
//            mtvUsdcBalance.setText(String.format("%.4f",Double.parseDouble(getIntent().getStringExtra("usdc_balance"))));
        }
        loadUSDCBalance();
    }

    private void loadUSDCBalance() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_USDC_BALANCE)
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
                            usdcBalance = response.optDouble("usdc_balance");
                            mtvUsdcBalance.setText(new CoinBalanceFormat(usdcBalance).toString());

                        }catch (NumberFormatException e) {
                            Toast.makeText(getBaseContext(), "Please load again. Network error.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error. getErrorBody());
                    }
                });
    }

    private void initComponents() {
        mtvSymbol = findViewById(R.id.symbol);
        mtvName = findViewById(R.id.name);
        mtvPrice = findViewById(R.id.price);
//        mtvEndDate = findViewById(R.id.tv_end_date);
        mtvUsdcBalance = findViewById(R.id.usdc_balance);
        radioGroup = findViewById(R.id.rdg_type);
        meditEstPrice = findViewById(R.id.edit_est_price);
        meditBetPrice = findViewById(R.id.edit_bet_price);

        group1 = findViewById(R.id.btn_group1);

        btn1H = findViewById(R.id.btn_48hrs);
        btn1W = findViewById(R.id.btn_1w);
        btn2W = findViewById(R.id.btn_2w);
        btn3W = findViewById(R.id.btn_3w);
        btn4W = findViewById(R.id.btn_4w);
        btn5W = findViewById(R.id.btn_5w);
        btnCreate = findViewById(R.id.btn_create);

        btn10 = findViewById(R.id.btn_10);
        btn50 = findViewById(R.id.btn_50);
        btn100 = findViewById(R.id.btn_100);
        btn200 = findViewById(R.id.btn_200);
    }

    private void initListeners() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidate()) {

                    confirmAlert.confirm("Are you sure you want to post this prediction?")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    postPrediction();
                                }
                            }).show();
                }
            }
        });

        group1.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                if(isChecked) {
                    switch (checkedId) {
                        case R.id.btn_48hrs:
                            duration = 2;
                            break;
                        case R.id.btn_1w:
                            duration = 7;
                            break;
                        case R.id.btn_2w:
                            duration = 14;
                            break;
                        case R.id.btn_3w:
                            duration = 21;
                            break;
                        case R.id.btn_4w:
                            duration = 28;
                            break;
                        case R.id.btn_5w:
                            duration = 35;
                            break;
                    }
                }
            }
        });

        btn10.setOnClickListener(this);
        btn50.setOnClickListener(this);
        btn100.setOnClickListener(this);
        btn200.setOnClickListener(this);

    }

    private boolean isValidate() {
        String estPrice = meditEstPrice.getText().toString();
        if(TextUtils.isEmpty(estPrice) || estPrice.startsWith(".")){
            meditEstPrice.setError("!");
            return false;
        }

        String betPrice = meditBetPrice.getText().toString();
        if(TextUtils.isEmpty(betPrice) || betPrice.startsWith(".")){
            meditBetPrice.setError("!");
            return false;
        }
        try {
            if(Double.parseDouble(betPrice) > usdcBalance) {
                confirmAlert.error("Insufficient balance");
                return false;
            }

        }catch (NumberFormatException e) {
            meditBetPrice.setError("!");
            return false;
        }

        if((int)radioGroup.getCheckedRadioButtonId() <= 0) {
            confirmAlert.alert("Please select type.");
            return false;
        }
        return true;
    }

    private void postPrediction() {
//        loadToast.show();
        confirmAlert.process();
        JSONObject object = new JSONObject();
        try {
            object.put("est_price", meditEstPrice.getText().toString());
            object.put("bet_price", meditBetPrice.getText().toString());
            object.put("duration", duration);
            object.put("symbol", symbol);
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.rdb_bigger:
                    object.put("type", 2);
                    break;
                case R.id.rdb_same:
                    object.put("type", 0);
                    break;
                case R.id.rdb_smaller:
                    object.put("type", 1);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(URLHelper.REQUEST_PREDICT)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addJSONObjectBody(object)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
//                        loadToast.success();
                        if(response.optBoolean("success")) {
//                            Toast.makeText(getBaseContext(), "Posted successfully", Toast.LENGTH_SHORT).show();
                            confirmAlert.success("Posted successfully");
//                            startActivity(new Intent(AddPredictActivity.this, PredictActivity.class));
                        }
                        else
                            confirmAlert.error(response.optString("message"));
//                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
//                        loadToast.error();
//                        // handle error
//                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                        confirmAlert.error(error.getErrorBody());
                    }
                });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_10:
                meditBetPrice.setText("25");
            break;
            case R.id.btn_50:
                meditBetPrice.setText("50");
                break;
            case R.id.btn_100:
                meditBetPrice.setText("100");
                break;
            case R.id.btn_200:
                meditBetPrice.setText("200");
                break;
        }
    }
}
