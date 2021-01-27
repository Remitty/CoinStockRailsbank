package com.brian.stocks.stock.stockwithdraw;

import com.brian.stocks.stock.stockwithdraw.adapters.StockWithdrawAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
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
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.stock.stockwithdraw.adapters.PageAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockCoinWithdrawActivity extends AppCompatActivity {
    LoadToast loadToast;

    TabLayout tab;
    private PageAdapter mPageAdapter;
    private ViewPager mViewPager;

//    private JSONArray history;
    private ArrayList<JSONObject> history = new ArrayList<>();
    RecyclerView historyView;
    StockWithdrawAdapter mAdapter;

    private Button mBtnWithdraw;
    private EditText mWalletAddress, mEditAmount;
    TextView mStockBalance, mUSDCRate;
    RadioGroup radioGroup;
    AppCompatRadioButton radioButton;
    String StockBalance = "0", USDCRate="0", DAIRate="0", WithdrawFee="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_coin_withdraw);

        loadToast = new LoadToast(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("STOCK2COIN WITHDRAW");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tab = findViewById(R.id.tab);
        mViewPager = findViewById(R.id.pager);

        mPageAdapter=new PageAdapter(this.getSupportFragmentManager());

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
                if(Double.parseDouble(mEditAmount.getText().toString()) > Double.parseDouble(USDCRate)){
                    Toast.makeText(getBaseContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
                    return;
                }
                showInvoiceDialog();
            }
        });
    }

    private void initComponent() {
        historyView = findViewById(R.id.history_view);
        historyView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mAdapter = new StockWithdrawAdapter(history);
        historyView.setAdapter(mAdapter);

        mStockBalance = findViewById(R.id.stock_balance);
        mUSDCRate = findViewById(R.id.stock_usdc_rate);
        mBtnWithdraw = findViewById(R.id.btn_coin_withdraw);
        mWalletAddress = findViewById(R.id.edit_withdraw_wallet_address);
        mEditAmount = findViewById(R.id.edit_coin_withdraw_amount);
        radioGroup = findViewById(R.id.rdg_withdraw_coins);
    }

    private void getData() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
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
                                StockBalance = response.getString("stock_balance");
                                USDCRate = response.getString("stock2usdc");
                                history.clear();
                                JSONArray temp = response.getJSONArray("history");
                                for(int i = 0; i < temp.length(); i ++) {
                                    try {
                                        history.add(temp.getJSONObject(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
//                                StockBalance = new DecimalFormat("####.####").format(StockBalance).toString();
                                mStockBalance.setText("$ "+StockBalance);
                                mUSDCRate.setText(USDCRate+" USDC");
                                
                                mPageAdapter.add(StockWithdrawFragment.newInstance(StockBalance, USDCRate));
//                                mPageAdapter.add(StockWithdrawHistoryFragment.newInstance(history));
                                mViewPager.setAdapter(mPageAdapter);
                                tab.setupWithViewPager(mViewPager);
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

//        if(Double.parseDouble(amount.toString()) <= 7.5) {
//            Toast.makeText(getBaseContext(), "You have to request amount than 7.5USDC at least.", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Double total = Double.parseDouble(amount.toString()) - 7.5;
//        if(radioButton.getText().toString().equals("USDC")){
//        }
//        else{
//            rate = new BigDecimal(DAIRate);
//        }
//        BigDecimal withdraw_fee = new BigDecimal(WithdrawFee);
//        String total = amount.multiply(rate).subtract(withdraw_fee).toString();

//        final WithdrawInvoiceDialog dialog = new WithdrawInvoiceDialog(R.layout.dialog_withdraw_invoice, "$ "+mEditAmount.getText().toString(), "$1 = "+rate+radioButton.getText().toString(), "$ "+WithdrawFee, "$ "+total);
//        dialog.setListener(new WithdrawInvoiceDialog.Listener() {
//
//            @Override
//            public void onOk() {
//                dialog.dismiss();
//                doWithdraw();
//            }
//
//            @Override
//            public void onCancel() {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show(getSupportFragmentManager(), "deposit");
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
                                StockBalance = response.getString("stock_balance");
                                mStockBalance.setText("$ "+StockBalance);
                                USDCRate = response.getString("usdc_balance");
                                mUSDCRate.setText("(" + USDCRate+" USDC" + ")");
                                history.clear();
                                JSONArray temp = response.getJSONArray("history");
                                for(int i = 0; i < temp.length(); i ++) {
                                    try {
                                        history.add(temp.getJSONObject(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
