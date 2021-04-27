package com.wyre.trade.cash;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wyre.trade.R;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

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
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        tvCurrency = findViewById(R.id.selected_currency);
        editAmount = findViewById(R.id.add_amount);
        btnAdd = findViewById(R.id.btn_add_money);

        tvCurrency.setText(currency);


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
                        .setNegativeButton("No", null)
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
