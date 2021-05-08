package com.wyre.trade.stock.stockwithdraw;

import androidx.annotation.Nullable;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wyre.trade.R;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.PlaidConnect;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.WebViewActivity;
import com.wyre.trade.model.Card;
import com.wyre.trade.payment.AddCardActivity;
import com.wyre.trade.payment.CardActivity;
import com.wyre.trade.stock.adapter.BottomCardAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StockCoinWithdrawActivity extends AppCompatActivity {
    private static final int STRIPE_CONNECT = 200;
    LoadToast loadToast;
    ConfirmAlert confirmAlert;
//    private JSONArray history;

    private Button mBtnWithdrawCoin, btnWithdrawBank, btnWithdrawCard, btnConnectBank, btnAddCard;
    private EditText mWalletAddress, mEditAmount;
    TextView mStockBalance, mUSDCRate;
    TextView tvViewHistory;
    RadioGroup radioGroup;
    Double StockBalance = 0.0, USDCRate = 0.0;

    BottomSheetDialog dialog;
    BottomCardAdapter mBottomAdapter;
    RecyclerView recyclerView;
    ArrayList<Card> cardList = new ArrayList<Card>();
    String selectedCard, type, alertMsg, bank;
    boolean stripeAccountVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_coin_withdraw);

        loadToast = new LoadToast(this);
        confirmAlert = new ConfirmAlert(StockCoinWithdrawActivity.this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
         getSupportActionBar().setTitle("");

        initComponent();

        initListeners();

        initBottomSheet();

        getData();

    }

    private void initListeners() {
        mBtnWithdrawCoin.setOnClickListener(new View.OnClickListener() {
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
                type = "USDC";
                alertMsg = "Are you sure withdraw " + mEditAmount.getText().toString() + " USDC?";
                showInvoiceDialog();
            }
        });

        btnWithdrawBank.setOnClickListener(new View.OnClickListener() {
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
                type = "bank";
                alertMsg = "Are you sure withdraw $" + mEditAmount.getText().toString() + " to your bank?";
                showInvoiceDialog();
            }
        });

        btnWithdrawCard.setOnClickListener(new View.OnClickListener() {
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
                type = "card";
                getCard();
            }
        });

        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stripeAccountVerified) {
                    Intent intent = new Intent(StockCoinWithdrawActivity.this, CardActivity.class);
                    intent.putExtra("withdrawal", 1);
                    startActivity(intent);
                } else {
                    confirmAlert.confirm("No connected account. Would you connect?")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    createStripeConnectLink(true);
                                    confirmAlert.dismissWithAnimation();
                                }
                            }).show();
                }
            }
        });

        btnConnectBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bank.isEmpty()) {
                    if(stripeAccountVerified)
                        new PlaidConnect(StockCoinWithdrawActivity.this).openPlaid();
                    else {
                        confirmAlert.confirm("No connected account. Would you connect?")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        createStripeConnectLink(true);
                                        confirmAlert.dismissWithAnimation();
                                    }
                                }).show();
                    }

                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(StockCoinWithdrawActivity.this);
                    alert.setIcon(R.mipmap.ic_launcher_round)
                            .setTitle("Confirm")
                            .setMessage("You connected to " + bank + " already. Would you replace with other bank?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new PlaidConnect(StockCoinWithdrawActivity.this).openPlaid();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        });

        tvViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StockCoinWithdrawActivity.this, StockWithdrawHistoryActivity.class));
            }
        });
    }

    private void createStripeConnectLink(boolean create) {
        loadToast.show();
        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.REQUEST_STRIPE_CONNECT)
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

                                stripeAccountVerified = response.getBoolean("stripe_account_verified");
                                if(stripeAccountVerified) {
                                    confirmAlert.alert(response.getString("message"));
                                } else {
                                    if(create) {
                                        Intent intent = new Intent(StockCoinWithdrawActivity.this, WebViewActivity.class);
                                        intent.putExtra("uri", response.getString("stripe_connect_link"));
                                        startActivityForResult(intent, STRIPE_CONNECT);

                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            confirmAlert.new_error(error.getErrorBody());
                        }
                    });
    }

    private void initComponent() {

        mStockBalance = findViewById(R.id.stock_balance);
        mUSDCRate = findViewById(R.id.stock_usdc_rate);
        mBtnWithdrawCoin = findViewById(R.id.btn_coin_withdraw);
        btnWithdrawBank = findViewById(R.id.btn_bank_withdraw);
        btnWithdrawCard = findViewById(R.id.btn_card_withdraw);
        btnConnectBank = findViewById(R.id.btn_connect_bank);
        btnAddCard = findViewById(R.id.btn_add_card);
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

                                bank = response.getString("stripe_bank");
                                stripeAccountVerified = response.getInt("stripe_account_verified") == 0? false: true;


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

    private void initBottomSheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        recyclerView = dialogView.findViewById(R.id.bottom_coins_list);
        mBottomAdapter  = new BottomCardAdapter(cardList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mBottomAdapter);

        mBottomAdapter.setListener(new BottomCardAdapter.Listener() {
            @Override
            public void onSelectCard(int position) {
                Card card = cardList.get(position);
                selectedCard = card.getCardId();
                alertMsg = "Are you sure withdraw $" + mEditAmount.getText().toString() + " to " + card.getLastFour() + "?";
                dialog.dismiss();
                showInvoiceDialog();
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

//        new AlertDialog.Builder(StockCoinWithdrawActivity.this)
////                .setTitle(getString(R.string.app_name))
////                .setMessage(alertMsg)
////                .setPositiveButton("Yes",
////                        new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                doWithdraw();
////                            }
////                        })
////                .setNegativeButton("No",
////                        new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialogInterface, int i) {
////
////                                // dismiss the dialog
////                                dialogInterface.dismiss();
////
////                                // dismiss the bottomsheet
////                            }
////                        }).show();

        confirmAlert.confirm(alertMsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        confirmAlert.process();
                        doWithdraw();
                    }
                })
                .show();
    }

    private void doWithdraw() {
//        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("type", type);
            jsonObject.put("card_id", selectedCard);
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
//                        loadToast.success();
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
//                        Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        confirmAlert.success(response.optString("message"));
                    }

                    @Override
                    public void onError(ANError error) {
//                        loadToast.error();
                        // handle error
//                        AlertDialog.Builder alert = new AlertDialog.Builder(StockCoinWithdrawActivity.this);
//                        alert.setTitle("Alert")
//                                .setIcon(R.mipmap.ic_launcher_round)
//                                .setMessage(error.getErrorBody())
//                                .setPositiveButton("Ok", null)
//                                .show();
                       confirmAlert.error(error.getErrorBody());
                    }
                });
    }

    private void getCard() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.REQUEST_CARD)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(this,"access_token"))
                .addQueryParameter("withdrawal", "1")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToast.success();

                        cardList.clear();

                        JSONArray cards = response.optJSONArray("cards");
                        for(int i = 0; i < cards.length(); i ++) {
                            try {
                                cardList.add(new Card(cards.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(cards.length() > 0) {
                            mBottomAdapter.notifyDataSetChanged();
                            dialog.show();
                        } else {
                            Toast.makeText(getBaseContext(), "No card", Toast.LENGTH_SHORT).show();
                        }

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == STRIPE_CONNECT) {
            createStripeConnectLink(false);
        }

        if (!new PlaidConnect(StockCoinWithdrawActivity.this).myPlaidResultHandler.onActivityResult(requestCode, resultCode, data)) {
//            Log.i(MainActivityJava.class.getSimpleName(), "Not handled");
//            Log.d("plaid connect data", data.getDataString());
//            Log.d("plaid connect data1", data.toString());
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
