package com.brian.stocks.cash;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.stocks.R;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class CollectCashActivity extends AppCompatActivity {

    TextView tvCurrency;
    EditText editAmount;
    MaterialButton btnAdd;
    String currency, currencyId;
    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collet_cash);

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        if(getIntent() != null) {
            currency = getIntent().getStringExtra("currency");
            currencyId = getIntent().getStringExtra("currency_id");
        }

        if(getSupportActionBar() != null)
        {
            // getSupportActionBar().setTitle("Add money");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tvCurrency = findViewById(R.id.selected_currency);
        editAmount = findViewById(R.id.add_amount);
        btnAdd = findViewById(R.id.btn_add_money);

        tvCurrency.setText(currency);

        btnAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!s.toString().equals(".")) {
                    BigDecimal amount = new BigDecimal(s.toString());
                    editAmount.setText(amount.multiply(new BigDecimal("1.0")).toString());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editAmount.getText().toString())) {
                    editAmount.setError("!");
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(CollectCashActivity.this);
                builder.setTitle(R.string.app_name)
                        .setMessage("Are you sure you want to add "+editAmount.getText().toString()+" "+currency+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleAddMoney();
                            }
                        })
                        .show();
            }
        });
    }

    private void handleAddMoney() {
        loadToast.show();
        JSONObject object = new JSONObject();
        try {
            object.put("amount", editAmount.getText().toString());
            object.put("currency", currencyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("save money params", object.toString());
        AndroidNetworking.post(URLHelper.REQUEST_ADD_MONEY)
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
                            loadToast.success();
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
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
