package com.brian.stocks.stock.stocktrade;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.fragment.StockTradeInvoiceDialog;
import com.brian.stocks.helper.BigDecimalDouble;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class StockBuyActivity extends AppCompatActivity {
    LoadToast loadToast;
    private String mStockName, mStockSymbol, mStockTradeType="market";
    private Double mStockBalance = 0.0, mEstCost = 0.0, mStockShares=0.0, mStockPrice = 0.0;
    EditText mEditShares, mEditStockLimitPrice;
    TextView mTextMktPrice, mTextShareEstCost, mTextStockName, mTextStockSymbol, mTextStockBalance, mTextStockShares;
    LinearLayout llMktPrice, llLimitPrice, llMktPriceLabel, llLimitPriceLabel;
    RadioGroup mRdgStockTrade;
    AppCompatRadioButton mRdbMkt, mRdbLimit;
    BottomSheetDialog dialog;
    Button mBtnBuy;
    TextView mTextCompanySummary, mTextCompanyWeb, mTextCompanyIndustry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_buy);

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        mStockName = getIntent().getStringExtra("stock_name");
        mStockPrice = Double.parseDouble(getIntent().getStringExtra("stock_price"));
        mStockSymbol = getIntent().getStringExtra("stock_symbol");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
         getSupportActionBar().setTitle("");

        initComponents();

        View dialogView = getLayoutInflater().inflate(R.layout.stock_trade_bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        mRdgStockTrade = dialogView.findViewById(R.id.rdg_stock_trade);

        mTextMktPrice.setText("$ " + mStockPrice);
        mTextStockName.setText(mStockName);
        mTextStockSymbol.setText(mStockSymbol);
//        if(getIntent().getStringExtra("stock_balance").equals(""))
            mStockBalance = Double.parseDouble(SharedHelper.getKey(getBaseContext(), "stock_balance"));
//            else
//                mStockBalance = getIntent().getStringExtra("stock_balance");
        mTextStockBalance.setText("$ "+ new DecimalFormat("#,###.##").format(mStockBalance));
        mTextStockShares.setText(getIntent().getStringExtra("stock_shares"));

        mTextCompanyIndustry.setText(getIntent().getStringExtra("company_industry"));
        mTextCompanySummary.setText(getIntent().getStringExtra("company_summary"));
        mTextCompanyWeb.setText(getIntent().getStringExtra("company_web"));

        initListeners();
    }

    private void initListeners() {
        mBtnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SharedHelper.getKey(getBaseContext(),"stock_auto_sell").equals("1")){
                    Toast.makeText(getBaseContext(), "Account liquidation. You can't buy stocks now.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mEditShares.getText().toString().equals("")){
                    mEditShares.setError("!");
                    return;
                }
                if(mEstCost > mStockBalance){
                    Toast.makeText(getBaseContext(), "Insufficient Funds", Toast.LENGTH_SHORT).show();
                    return;
                }

                showBuyConfirmAlertDialog();
            }
        });

        llMktPriceLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        llLimitPriceLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        mEditShares.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                if(str.equals("") || str.equals("."))
                    mEstCost = 0.0;
                else
                    mEstCost = Double.parseDouble(str);
                mStockShares = mEstCost / mStockPrice;
                mTextShareEstCost.setText(new DecimalFormat("#,###.####").format(mStockShares));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEditStockLimitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String str = charSequence.toString();
                if(str.equals("") || str.equals(".")) {
                    mStockPrice = 0.0;
                }
                else {
                    mStockPrice = Double.parseDouble(str);
                }

                if(mStockPrice != 0)
                    mStockShares = mEstCost / mStockPrice;
                else mStockShares = 0.0;
                mTextShareEstCost.setText(new DecimalFormat("#,###.####").format(mStockShares));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mRdgStockTrade.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("radiobuttonid", i+"");
                dialog.dismiss();
                if(radioGroup.getCheckedRadioButtonId() == R.id.rdb_limit_price){//limit price
                    String limit =mEditStockLimitPrice.getText().toString();
                    if(limit.equals(""))
                        mStockPrice = 0.0;
                    llMktPrice.setVisibility(View.GONE);
                    llLimitPrice.setVisibility(View.VISIBLE);
                    mStockTradeType = "limit";
                }else{
                    mStockPrice = Double.parseDouble(getIntent().getStringExtra("stock_price"));
                    llMktPrice.setVisibility(View.VISIBLE);
                    llLimitPrice.setVisibility(View.GONE);
                    mStockTradeType = "market";
                }

                mStockShares = mEstCost / mStockPrice;
                mTextShareEstCost.setText(new DecimalFormat("#,###.####").format(mStockShares));
            }
        });
    }

    private void initComponents() {
        mEditShares = findViewById(R.id.edit_shares);
        mTextMktPrice = findViewById(R.id.stock_mkt_price);
        mEditStockLimitPrice = findViewById(R.id.stock_limit_price);
        mTextShareEstCost = findViewById(R.id.stock_est_price);
        mTextStockName = findViewById(R.id.stock_name);
        mTextStockSymbol = findViewById(R.id.stock_symbol);
        mTextStockBalance = findViewById(R.id.stock_balance);
        mTextStockShares = findViewById(R.id.stock_shares);

        mBtnBuy = findViewById(R.id.btn_stock_buy);

        mTextCompanyIndustry = findViewById(R.id.company_industry);
        mTextCompanySummary = findViewById(R.id.company_summary);
        mTextCompanyWeb = findViewById(R.id.company_web);

        llMktPrice = findViewById(R.id.ll_mkt_price);
        llLimitPrice = findViewById(R.id.ll_limit_price);
        llMktPriceLabel = findViewById(R.id.ll_mkt_label);
        llLimitPriceLabel = findViewById(R.id.ll_limit_label);


       mRdbLimit = findViewById(R.id.rdb_limit_price);
       mRdbMkt = findViewById(R.id.rdb_mkt_price);
    }

    private void onBuy() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ticker", mStockSymbol);
            jsonObject.put("shares", mStockShares);
            jsonObject.put("limit_price", mStockPrice);
            jsonObject.put("type", mStockTradeType);
            jsonObject.put("cost", mEstCost);
            jsonObject.put("buyorsell", "buy");
            Log.d("buystock", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.REQUEST_STOCK_ORDER_CREATE)
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
                            mStockBalance = response.optDouble("stock_balance");
                            mTextStockBalance.setText("$ "+ new DecimalFormat("#,###.##").format(mStockBalance));
                            SharedHelper.putKey(getBaseContext(), "stock_balance", mStockBalance+"");

                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
//                            Toast.makeText(getBaseContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                            AlertDialog.Builder alert = new AlertDialog.Builder(StockBuyActivity.this);
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
    private void showBuyConfirmAlertDialog() {
//        String total = BigDecimalDouble.newInstance().multify(mEditShares.getText().toString(), mStockPrice);
//        total = BigDecimalDouble.newInstance().add(total, "1.99");
//        final StockTradeInvoiceDialog dialog = new StockTradeInvoiceDialog(R.layout.dialog_stock_trade_invoice, mStockName, mEditShares.getText().toString(), "$ "+mStockPrice, "$ 1.99","BUY",total);
//        dialog.setListener(new StockTradeInvoiceDialog.Listener() {
//
//            @Override
//            public void onOk() {
//                dialog.dismiss();
//                onBuy();
//            }
//
//            @Override
//            public void onCancel() {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show(getSupportFragmentManager(), "deposit");
        AlertDialog.Builder alert = new AlertDialog.Builder(StockBuyActivity.this);
        alert.setIcon(R.mipmap.ic_launcher_round)
                .setTitle("Confirm Transaction")
                .setMessage("Please confirm your transaction.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBuy();
                    }
                })
                .show();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
