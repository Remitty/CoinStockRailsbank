package com.wyre.trade.coins;

import android.content.DialogInterface;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.adapters.BottomCoinAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.CoinInfo;
import com.phonenumberui.PhoneNumberActivity;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CoinWithdrawActivity extends AppCompatActivity {
    private static final int REQUEST_PHONE_VERIFICATION = 1080;
    EditText editWithdrawAmount, editAddress;
    TextView mtvCoin,mtvAvailCoinQty, mTVCoinBalance, mtvWithdrawalFee, mtvWithdrawalFeeSymbol, mtvGasFee, mtvGasFeeSymbol, mtvGetAmount, mtvGetAmountSymbol, mtvWeeklyLimit, tvViewHistory;
    ImageView imgIcon;
    Button btnWithdraw;
    BottomCoinAdapter mBottomAdapter;
    private RecyclerView recyclerView;
    private BottomSheetDialog dialog;
    private LoadToast loadToast;

    private String CoinId="1", CoinUsdc="0", Fee="0", Coin="BTC", GasFee = "0";

    private List<CoinInfo> coinList = new ArrayList<>();
    private CoinInfo selectedCoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_withdraw);
        loadToast = new LoadToast(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        initComponents();
        initListeners();

        getWithdrawHistory();
    }

    private void initComponents() {
        editWithdrawAmount = findViewById(R.id.edit_withdraw_amount);
        editAddress = findViewById(R.id.edit_address);
        mtvCoin = findViewById(R.id.tv_coin);
        imgIcon = findViewById(R.id.imgIcon);
        mtvAvailCoinQty = findViewById(R.id.tv_avail_qty);
        mTVCoinBalance = findViewById(R.id.tv_balance);

        mtvWithdrawalFee = findViewById(R.id.withdrawal_fee);
        mtvWithdrawalFeeSymbol = findViewById(R.id.withdrawal_fee_symbol);
        mtvGasFee = findViewById(R.id.gas_fee);
        mtvGasFeeSymbol = findViewById(R.id.gas_fee_symbol);
        mtvGetAmount = findViewById(R.id.receipt_amount);
        mtvGetAmountSymbol = findViewById(R.id.receipt_amount_symbol);

        mtvWeeklyLimit = findViewById(R.id.tv_weekly_limit);

        btnWithdraw = findViewById(R.id.btn_coin_withdraw);
        tvViewHistory = findViewById(R.id.tv_view_history);


        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        recyclerView = dialogView.findViewById(R.id.bottom_coins_list);
        mBottomAdapter  = new BottomCoinAdapter(coinList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mBottomAdapter);


    }

    private void initListeners() {

        tvViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CoinWithdrawActivity.this, CoinWithdrawHistoryActivity.class));
            }
        });

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
                String amount = s.toString();
                if(amount.equals("") || amount.equals(".")) {
                    mtvGetAmount.setText("0");
                } else {
                    mtvGetAmount.setText(new DecimalFormat("#,###.####").format(Double.parseDouble(amount) - Double.parseDouble(Fee)));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                confirmPhoneVerification();
                String amount = editWithdrawAmount.getText().toString();
                if(amount.equals("") || CoinId.equals("0") || editAddress.getText().toString().equals("")) {
                    if(amount.equals(""))
                        editWithdrawAmount.setError("!");
                    if(editAddress.getText().toString().equals(""))
                        editAddress.setError("!");
                    if(CoinId.equals("0"))
                        Toast.makeText(getBaseContext(), "Please select coin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Double.parseDouble(amount) > Double.parseDouble(CoinUsdc)) {
                    Toast.makeText(getBaseContext(), "Insufficient funds", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Double.parseDouble(amount) < Double.parseDouble(Fee)) {
                    Toast.makeText(getBaseContext(), "You have to request amount than " + Fee + " at least.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String total = new DecimalFormat("#,###.####").format(Double.parseDouble(amount) - Double.parseDouble(Fee));
                new AlertDialog.Builder(CoinWithdrawActivity.this)
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to withdraw " + amount + Coin + " ? Fee is " + Fee + Coin + ".\nTotal is " + total + Coin + ".\n" + SharedHelper.getKey(getBaseContext(), "msgCoinWithdrawFeePolicy"))
                        .setPositiveButton("Confirm",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        submitWithdraw();
                                        confirmPhoneVerification();
                                    }
                                })
                        .setNegativeButton("Cancel",
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
                Picasso.with(getBaseContext()).load(coin.getCoinIcon()).into(imgIcon);
                Coin = coin.getCoinSymbol();
                mtvAvailCoinQty.setText(coin.getCoinBalance());
                mTVCoinBalance.setText("$ "+coin.getCoinUsdc());
                CoinUsdc = coin.getCoinBalance();
                CoinId = coin.getCoinId();
                Fee = coin.getWithdrawalFee();
                mtvWithdrawalFee.setText(Fee);
                mtvWithdrawalFeeSymbol.setText(Coin);
                mtvGetAmountSymbol.setText(Coin);

                if(coin.getType().equals("ERC20"))
                    mtvGasFee.setText(GasFee);
                else mtvGasFee.setText("0");

                String withdraw_amount = editWithdrawAmount.getText().toString();
                if(!withdraw_amount.equals("")) {
                    BigDecimal amount = new BigDecimal(withdraw_amount);
                    BigDecimal fee = new BigDecimal(Fee);
                    mtvGetAmount.setText(new DecimalFormat("#,###.####").format(amount.subtract(fee).doubleValue()));
                }
                dialog.dismiss();
            }
        });
    }

    private void submitWithdraw() {
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
                            AlertDialog.Builder alert = new AlertDialog.Builder(CoinWithdrawActivity.this);
                            alert.setIcon(R.mipmap.ic_launcher_round)
                                    .setTitle("Alert")
                                    .setMessage(error.getErrorBody())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
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

                            mtvWeeklyLimit.setText("Weekly withdrawal limit = $" + response.optString("weekly_withdraw_limit"));
                            GasFee = response.optString("coin_withdraw_gas_fee");
//                            mtvGasFee.setText(GasFee);
                            selectedCoin = new CoinInfo(response.optJSONObject("coin"));
                            mtvAvailCoinQty.setText(selectedCoin.getCoinBalance());

                            Coin = selectedCoin.getCoinSymbol();
                            CoinUsdc = selectedCoin.getCoinBalance();
                            CoinId = selectedCoin.getCoinId();
                            Fee = selectedCoin.getWithdrawalFee();
                            mtvWithdrawalFee.setText(Fee);
                            mtvWithdrawalFeeSymbol.setText(Coin);

                            mtvGetAmountSymbol.setText(Coin);
                            String withdraw_amount = editWithdrawAmount.getText().toString();
                            if(!withdraw_amount.equals("")) {
                                BigDecimal amount = new BigDecimal(withdraw_amount);
                                BigDecimal fee = new BigDecimal(Fee);
                                mtvGetAmount.setText(new DecimalFormat("#,###.####").format(amount.subtract(fee).doubleValue()));
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

    private void confirmPhoneVerification() {
        Intent intent = new Intent(CoinWithdrawActivity.this, PhoneNumberActivity.class);
        startActivityForResult(intent, REQUEST_PHONE_VERIFICATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PHONE_VERIFICATION:
                if (data != null && data.hasExtra("PHONE_NUMBER") && data.getStringExtra("PHONE_NUMBER") != null) {
                    submitWithdraw();
                } else {
                    // If mobile number is not verified successfully You can hendle according to your requirement.
                    Toast.makeText(getBaseContext(), "Mobile number verification fails",Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
