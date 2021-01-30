package com.brian.stocks.coins;

import android.content.DialogInterface;

import com.brian.stocks.coins.adapter.CoinWithdrawAdapter;
import com.brian.stocks.stock.stockwithdraw.adapters.StockWithdrawAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
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
import com.brian.stocks.R;
import com.brian.stocks.adapters.BottomCoinAdapter;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.CoinInfo;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CoinWithdrawActivity extends AppCompatActivity {
    
    EditText editWithdrawAmount, editAddress;
    TextView mtvCoin,mtvCoinName, mtvAvailCoinQty, mtvCoinSymbol, mTVCoinBalance, mtvWithdrawalFee, mtvGetAmount;
    Button btnWithdraw;
    BottomCoinAdapter mBottomAdapter;
    private RecyclerView recyclerView;
    private BottomSheetDialog dialog;
    private LoadToast loadToast;

    private String CoinId="0", CoinUsdc="0", Fee="0", Coin;

    private List<CoinInfo> coinList = new ArrayList<>();

    private ArrayList<JSONObject> history = new ArrayList<>();
    RecyclerView historyView;
    CoinWithdrawAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_withdraw);
        loadToast = new LoadToast(this);
        loadToast.setBackgroundColor(R.color.colorBlack);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Coin Withdraw");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initComponents();
        initListeners();

        getWithdrawHistory();
    }

    private void initComponents() {
        editWithdrawAmount = findViewById(R.id.edit_withdraw_amount);
        editAddress = findViewById(R.id.edit_address);
        mtvCoin = findViewById(R.id.tv_coin);
        mtvCoinName = findViewById(R.id.coin_name);
        mtvAvailCoinQty = findViewById(R.id.tv_avail_qty);
//        mtvCoinSymbol = findViewById(R.id.tv_symbol);
        mTVCoinBalance = findViewById(R.id.tv_balance);

        mtvWithdrawalFee = findViewById(R.id.withdrawal_fee);
        mtvGetAmount = findViewById(R.id.receipt_amount);

        btnWithdraw = findViewById(R.id.btn_coin_withdraw);

        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        recyclerView = dialogView.findViewById(R.id.bottom_coins_list);
        mBottomAdapter  = new BottomCoinAdapter(coinList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mBottomAdapter);

        historyView = findViewById(R.id.history_view);
        historyView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mAdapter = new CoinWithdrawAdapter(history);
        historyView.setAdapter(mAdapter);
    }

    private void initListeners() {

        mtvCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCoinAssets();
            }
        });

        editWithdrawAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(".") && !s.toString().equals("")) {
                    BigDecimal amount = new BigDecimal(s.toString());
                    BigDecimal fee = new BigDecimal(Fee);
                    mtvGetAmount.setText(amount.subtract(fee).toString() + " " + Coin);
//                    Double amount = Double.parseDouble(s.toString());
//                    Double fee = Double.parseDouble(Fee);
//                    Double get = amount - fee;
//                    mtvGetAmount.setText(get+"");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = editWithdrawAmount.getText().toString();
                if(amount.equals("") || CoinId == null || editAddress.getText().toString().equals("")) {
                    if(amount.equals(""))
                        editWithdrawAmount.setError("!");
                    if(editAddress.getText().toString().equals(""))
                        editAddress.setError("!");
                    Toast.makeText(getBaseContext(), "Please fill in all inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Double.parseDouble(amount) > Double.parseDouble(CoinUsdc)) {
                    Toast.makeText(getBaseContext(), "Insufficient funds", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Double.parseDouble(amount) < Double.parseDouble(Fee)) {
                    Toast.makeText(getBaseContext(), "You have to request amount than $7.5 at least.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double total = Double.parseDouble(amount) - Double.parseDouble(Fee);
                new AlertDialog.Builder(CoinWithdrawActivity.this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Are you sure you want to withdraw $" + amount + " ? Fee is $" + Fee + ". Total is $" + total + ".")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        submitWitdraw();
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
        });

        mBottomAdapter.setListener(new BottomCoinAdapter.Listener() {
            @Override
            public void onSelectCoin(int position) {
                CoinInfo coin = coinList.get(position);
                mtvCoin.setText(coin.getCoinSymbol());
                Coin = coin.getCoinSymbol();
                mtvCoinName.setText(coin.getCoinName());
                mtvAvailCoinQty.setText(coin.getCoinBalance());
//                mtvCoinSymbol.setText(coin.getCoinSymbol() + " available");
                mTVCoinBalance.setText("$ "+coin.getCoinUsdc());
                CoinUsdc = coin.getCoinUsdc();
                CoinId = coin.getCoinId();
                Fee = coin.getWithdrawalFee();
                mtvWithdrawalFee.setText(Fee + " " + coin.getCoinSymbol());
                dialog.dismiss();
            }
        });
    }

    private void submitWitdraw() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("coin_id", CoinId);
            jsonObject.put("amount", editWithdrawAmount.getText().toString());
            jsonObject.put("address", editAddress.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("withdraw param", jsonObject.toString());
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.COIN_WITHDRAW)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("coin assets response", "" + response);
                            loadToast.success();
                            if(response.optBoolean("success")) {
                                CoinInfo coin = new CoinInfo(response.optJSONObject("result"));
                                mtvAvailCoinQty.setText(coin.getCoinBalance());
//                                mtvCoinSymbol.setText(coin.getCoinSymbol() + " available");
                                mTVCoinBalance.setText("$ "+coin.getCoinUsdc());
                                CoinUsdc = coin.getCoinUsdc();
                                Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();

                                history.clear();
                                JSONArray temp = null;
                                try {
                                    temp = response.getJSONArray("history");
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
                            else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(CoinWithdrawActivity.this);
                                alert.setIcon(R.mipmap.ic_launcher_round)
                                        .setTitle("Withdraw error")
                                        .setMessage(response.optString("message"))
                                        .show();
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

    private void getCoinAssets() {
        loadToast.show();

        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.GET_WITHDRAWBLE_COIN_ASSETS)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("coin assets response", "" + response);
                            loadToast.success();

                            coinList.clear();

                            JSONArray coins = null;
                            try {
                                coins = response.getJSONArray("assets");
                                for(int i = 0; i < coins.length(); i ++) {
                                    try {
                                        Log.d("coinitem", coins.get(i).toString());
                                        coinList.add(new CoinInfo((JSONObject) coins.get(i)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                mBottomAdapter.notifyDataSetChanged();
                                dialog.show();
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

    private void getWithdrawHistory() {
        loadToast.show();

        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.COIN_WITHDRAW)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("coin withdraw history ", "" + response);
                            loadToast.success();

                            history.clear();
                            try {
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
