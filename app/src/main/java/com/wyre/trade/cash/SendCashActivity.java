package com.wyre.trade.cash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class SendCashActivity extends AppCompatActivity {

    EditText editSend;
    TextView tvGet, tvBankName, tvBankCurrency, sendCurrency;
    Button btnSend;
    private LoadToast loadToast;

    private String getterId, bankName, bankCurrency, Currency, CurrencyId, type, rate="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_cash);

        if(getSupportActionBar() != null)
        {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        if(getIntent() != null) {
            getterId = getIntent().getStringExtra("getter_id");
            bankName = getIntent().getStringExtra("bank_name");
            bankCurrency = getIntent().getStringExtra("bank_currency");
            Currency = getIntent().getStringExtra("currency");
            CurrencyId = getIntent().getStringExtra("currency_id");
            type = getIntent().getStringExtra("bank_type");
        }

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        tvBankCurrency = findViewById(R.id.bank_currency);
        tvBankName = findViewById(R.id.bank_name);
        sendCurrency = findViewById(R.id.send_currency);
        tvBankCurrency.setText(bankCurrency);
        tvBankName.setText(bankName);
        sendCurrency.setText(Currency);

        editSend = findViewById(R.id.edit_send);
        tvGet = findViewById(R.id.tv_get);
        btnSend = findViewById(R.id.btn_send);

        initListeners();

        if(!bankCurrency.equals(Currency))
            getConversionRate();
    }

    private void initListeners() {
        editSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if(str.equals("") || str.equals("."))
                    tvGet.setText("0");
                else {
                    tvGet.setText(new DecimalFormat("#,###.##").format(Double.parseDouble(rate) * Double.parseDouble(str)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editSend.getText().toString())){
                    editSend.setError("!");
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(SendCashActivity.this);
                builder.setTitle(R.string.app_name)
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setMessage("Are you sure you want to send "+editSend.getText().toString()+" "+bankCurrency+ " to " + bankName +" ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendMoney();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void getConversionRate() {
        loadToast.show();
        JSONObject object = new JSONObject();
        try {
            object.put("getter_currency", bankCurrency);
            object.put("sender_currency", Currency);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("send money params", object.toString());
        AndroidNetworking.post(URLHelper.REQUEST_CONVERSION_RATE)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addJSONObjectBody(object)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        if(response.optBoolean("success"))
                            rate = response.optString("rate");
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

    private void sendMoney() {
        loadToast.show();
        JSONObject object = new JSONObject();
        try {

            object.put("getter_id", getterId);
            object.put("send_currency", CurrencyId);
            object.put("type", type);
            object.put("amount", editSend.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("send money params", object.toString());
        AndroidNetworking.post(URLHelper.REQUEST_SEND_MONEY)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(this,"access_token"))
                .addJSONObjectBody(object)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();

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
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
