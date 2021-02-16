package com.brian.stocks.stock.stocktrade;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class StockReplaceActivity extends AppCompatActivity {
    LoadToast loadToast;
    private String StockSide, StockType, mStockPrice="0", mStockLimitPrice, mStockName, mStockSymbol, mEstCost, mStockBalance, mStockShares="0", mStockTradeType="market";
    EditText mEditShares, mEditStockLimitPrice;
    TextView mTextMktPrice, mTextShareEstCost, mTextStockName, mTextStockSymbol, mTextStockBalance, mTextStockShares;
    LinearLayout llMktPrice, llLimitPrice, llMktPriceLabel, llLimitPriceLabel;
    RadioGroup mRdgStockTrade;
    AppCompatRadioButton mRdbMkt, mRdbLimit;
    BottomSheetDialog dialog;
    Button mBtnReplace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_replace);

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        mStockName = getIntent().getStringExtra("stock_name");
        mStockPrice = getIntent().getStringExtra("stock_price");
        mStockSymbol = getIntent().getStringExtra("stock_symbol");
        StockSide = getIntent().getStringExtra("stock_side");
        mStockLimitPrice = getIntent().getStringExtra("stock_limit_price");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);

        initComponents();

        View dialogView = getLayoutInflater().inflate(R.layout.stock_trade_bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        mRdgStockTrade = dialogView.findViewById(R.id.rdg_stock_trade);

        mTextMktPrice.setText("$ " + mStockPrice);
        mTextStockName.setText(mStockName);
        mTextStockSymbol.setText(mStockSymbol);

        mStockBalance = SharedHelper.getKey(getBaseContext(), "stock_balance");
        mTextStockBalance.setText("$ "+mStockBalance);

        mTextStockShares.setText(getIntent().getStringExtra("stock_shares"));
        mStockShares = getIntent().getStringExtra("stock_order_shares");
        mEditShares.setText(mStockShares);

        mStockTradeType = getIntent().getStringExtra("stock_order_type");

        if(mStockTradeType.equals("limit")){
            llMktPrice.setVisibility(View.GONE);
            llLimitPrice.setVisibility(View.VISIBLE);
            mEditStockLimitPrice.setText(mStockLimitPrice);
            BigDecimal unitPrice = new BigDecimal(mStockLimitPrice);
            BigDecimal shares = new BigDecimal(getIntent().getStringExtra("stock_order_shares")) ;
            mEstCost = ""+unitPrice.multiply(shares);
            mTextShareEstCost.setText(mEstCost);
        }
        else{
            llMktPrice.setVisibility(View.VISIBLE);
            llLimitPrice.setVisibility(View.GONE);
            BigDecimal unitPrice = new BigDecimal(mStockPrice);
            BigDecimal shares = new BigDecimal(getIntent().getStringExtra("stock_order_shares")) ;
            mEstCost = ""+unitPrice.multiply(shares);
            mTextShareEstCost.setText(mEstCost);
        }

        mEditShares.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String count = charSequence.toString();
                BigDecimal unitPrice = new BigDecimal(mStockPrice);
                BigDecimal shares = new BigDecimal("0") ;
                if(!count.equalsIgnoreCase(""))
                    shares = new BigDecimal(count);
                mEstCost = ""+unitPrice.multiply(shares);
                mTextShareEstCost.setText("$ " + unitPrice.multiply(shares));
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

                mStockPrice = charSequence.toString();
                if(mStockPrice.equals(""))
                    mStockPrice = "0";
                BigDecimal unitPrice = new BigDecimal(mStockPrice);
                BigDecimal shares = new BigDecimal(mStockShares);
                mEstCost = ""+unitPrice.multiply(shares);
                mTextShareEstCost.setText("$ " + unitPrice.multiply(shares));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBtnReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEditShares.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Please input shares", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(StockSide.equals("buy") && Double.parseDouble(mEstCost) > Double.parseDouble(mStockBalance)){
                    Toast.makeText(getBaseContext(), "Insufficient Funds", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(StockSide.equals("sell") && Double.parseDouble(mEditShares.getText().toString()) > Double.parseDouble(mStockShares)){
                    Toast.makeText(getBaseContext(), "Insufficient Shares", Toast.LENGTH_SHORT).show();
                    return;
                }
                showReplaceConfirmAlertDialog();
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

        mRdgStockTrade.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("radiobuttonid", i+"");
                dialog.dismiss();
                if(radioGroup.getCheckedRadioButtonId() == R.id.rdb_limit_price){//limit price
                    mStockPrice=mEditStockLimitPrice.getText().toString();
                    if(mStockPrice.equals(""))
                        mStockPrice = "0";
                    llMktPrice.setVisibility(View.GONE);
                    llLimitPrice.setVisibility(View.VISIBLE);
                    mStockTradeType = "limit";
                }else{
                    mStockPrice = getIntent().getStringExtra("stock_price");
                    llMktPrice.setVisibility(View.VISIBLE);
                    llLimitPrice.setVisibility(View.GONE);
                    mStockTradeType = "market";
                }
                BigDecimal unitPrice = new BigDecimal(mStockPrice);
                BigDecimal shares = new BigDecimal(mStockShares);
                mEstCost = ""+unitPrice.multiply(shares);
                mTextShareEstCost.setText("$ " + unitPrice.multiply(shares));
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

        llMktPrice = findViewById(R.id.ll_mkt_price);
        llLimitPrice = findViewById(R.id.ll_limit_price);
        llMktPriceLabel = findViewById(R.id.ll_mkt_label);
        llLimitPriceLabel = findViewById(R.id.ll_limit_label);

        mBtnReplace = findViewById(R.id.btn_stock_replace);
    }

    private void onReplace() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ticker", mStockName);
            jsonObject.put("shares", mEditShares.getText());
            jsonObject.put("cost", mEstCost);
            jsonObject.put("buyorsell", StockSide);
            jsonObject.put("limit_price", mStockPrice);
            jsonObject.put("type", mStockTradeType);
            jsonObject.put("order_id", getIntent().getStringExtra("stock_order_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.REQUEST_STOCK_ORDER_REPLACE)
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
                            mStockBalance = response.optString("stock_balance");
                            mTextStockBalance.setText("$ "+mStockBalance);
                            SharedHelper.putKey(getBaseContext(), "stock_balance", mStockBalance);
                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getBaseContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    private void showReplaceConfirmAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Are you sure replace " + mEditShares.getText()+" shares ?\n Fee: $ 1.99")
                .setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onReplace();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
