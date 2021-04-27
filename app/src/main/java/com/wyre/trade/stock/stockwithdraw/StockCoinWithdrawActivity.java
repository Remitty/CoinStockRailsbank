package com.wyre.trade.stock.stockwithdraw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class StockCoinWithdrawActivity extends AppCompatActivity {
    LoadToast loadToast;

//    private JSONArray history;

    private Button mBtnWithdraw;
    private EditText mWalletAddress, mEditAmount;
    TextView mStockBalance, mUSDCRate;
    TextView tvViewHistory;
    RadioGroup radioGroup;
    Double StockBalance = 0.0, USDCRate = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_coin_withdraw);

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
         getSupportActionBar().setTitle("");

        initComponent();

        initListeners();

        getData();

    }

    private void initListeners() {
        mBtnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mEditAmount.getText().toString().equals("")) {
                    mEditAmount.setError("!");
                    return;
                }
                if(Double.parseDouble(mEditAmount.getText().toString()) > USDCRate){
                    Toast.makeText(getBaseContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
                    return;
                }
                showInvoiceDialog();
            }
        });

        tvViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StockCoinWithdrawActivity.this, StockWithdrawHistoryActivity.class));
            }
        });
    }

    private void initComponent() {

        mStockBalance = findViewById(R.id.stock_balance);
        mUSDCRate = findViewById(R.id.stock_usdc_rate);
        mBtnWithdraw = findViewById(R.id.btn_coin_withdraw);
        mWalletAddress = findViewById(R.id.edit_withdraw_wallet_address);
        mEditAmount = findViewById(R.id.edit_coin_withdraw_amount);
        radioGroup = findViewById(R.id.rdg_withdraw_coins);

        tvViewHistory = findViewById(R.id.tv_view_history);
    }

    private void getData() {
        loadToast.show();
        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.STOCK_WITHDRAW)
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
                                StockBalance = response.getDouble("stock_balance");
                                USDCRate = response.getDouble("stock2usdc");

                                mStockBalance.setText("$ "+ new DecimalFormat("#,###.##").format(StockBalance));
                                mUSDCRate.setText(new DecimalFormat("#,###.##").format(USDCRate));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    private void showInvoiceDialog() {
        BigDecimal amount = new BigDecimal(mEditAmount.getText().toString());
        BigDecimal rate;
        rate = new BigDecimal(USDCRate);

        if(amount.compareTo(rate) > 1) {
            Toast.makeText(getBaseContext(), "Insufficient funds", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(StockCoinWithdrawActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to withdraw " + amount + "USDC ?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doWithdraw();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // dismiss the dialog
                                dialogInterface.dismiss();

                                // dismiss the bottomsheet
                            }
                        }).show();
    }

    private void doWithdraw() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("currency", "USDC");
            jsonObject.put("amount", mEditAmount.getText());
            jsonObject.put("address", mWalletAddress.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(URLHelper.STOCK_WITHDRAW)
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
                        if(response.optBoolean("success")) {
                            try {
                                StockBalance = response.getDouble("stock_balance");
                                mStockBalance.setText("$ " + new DecimalFormat("#,###.##").format(StockBalance));
                                USDCRate = response.getDouble("usdc_balance");
                                mUSDCRate.setText(new DecimalFormat("#,###.##").format(USDCRate));

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
