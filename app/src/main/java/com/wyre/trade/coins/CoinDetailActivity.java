package com.wyre.trade.coins;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.wyre.trade.R;
import com.wyre.trade.coins.adapter.CoinChartTabAdapter;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.DepositDialog;
import com.wyre.trade.model.CoinInfo;
import com.wyre.trade.stock.adapter.StockChartTabAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.graphics.Color.RED;

public class CoinDetailActivity extends AppCompatActivity {

    CoinInfo coin;
    private LoadToast loadToast;
    private TextView mStockName, mStockSymbol, mStockPriceInteger, mStockPriceFloat, mStockTodayChange, mStockTodayChangePerc, mTvBalance;
    TextView tvOpen, tvHigh, tvLow, tvClose, tvAsk, tvBid;
    private JSONArray mAggregateDay = new JSONArray(), mAggregateWeek = new JSONArray(), mAggregateMonth = new JSONArray(), mAggregate6Month = new JSONArray(), mAggregateYear = new JSONArray(), mAggregateAll = new JSONArray();
    private ViewPager mStockChartViewPager;
    private TabLayout mStockTabBar;
    private MaterialButton mBtnDeposit, mBtnWithdraw, mBtnTrade;
    CoinChartTabAdapter mPageAdapter;

    String onRamperCoins, mOnramperApikey, xanpoolApikey;
    private ArrayList<CoinInfo> coinList = new ArrayList<CoinInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("");
        }

        if(getIntent() != null) {
            coinList = getIntent().getParcelableArrayListExtra("coins");
            onRamperCoins = getIntent().getStringExtra("onrampercoins");
            mOnramperApikey = getIntent().getStringExtra("onramperApiKey");
            xanpoolApikey = getIntent().getStringExtra("xanpoolApiKey");

        }

        loadToast = new LoadToast(this);

        coin = getIntent().getParcelableExtra("coin");

        Log.d("coin info", coin.toString());

        initComponents();
        initListeners();

        getData();

        mStockName.setText(coin.getCoinName());
        mStockSymbol.setText(coin.getCoinSymbol());
        mTvBalance.setText(new DecimalFormat("#,###.####").format(coin.getCoinBalance()));

        setDisplayPrice(coin.getCoinRate());

//
        if(coin.getCoinEffect().startsWith("-")) {
            mStockTodayChange.setText("% " + coin.getCoinEffect());
            mStockTodayChange.setTextColor(RED);
//            mStockTodayChangePerc.setTextColor(RED);
        } else {
            mStockTodayChange.setText("% +" + coin.getCoinEffect());
        }
//        mStockTodayChangePerc.setText("( % "+mIntent.getStringExtra("stock_today_change_perc")+" )");
    }

    private void setDisplayPrice(Double value) {
        try {
            String price = new DecimalFormat("###.####").format(value);
            String[] separatedPrice = price.split("\\.");
            mStockPriceInteger.setText(separatedPrice[0].trim());
            if(separatedPrice.length> 1)
                mStockPriceFloat.setText("."+separatedPrice[1].trim());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {

        mStockName = findViewById(R.id.stock_name);
        mStockSymbol = findViewById(R.id.stock_symbol);
        mStockPriceInteger = findViewById(R.id.stock_price_integer);
        mStockPriceFloat = findViewById(R.id.stock_price_float);
        mStockTodayChange = findViewById(R.id.stock_today_change);
        mStockTodayChangePerc = findViewById(R.id.stock_today_change_perc);
        mTvBalance = findViewById(R.id.coin_balance);

        tvOpen = findViewById(R.id.coin_open);
        tvHigh = findViewById(R.id.coin_high);
        tvLow = findViewById(R.id.coin_low);
        tvClose = findViewById(R.id.coin_close);
        tvAsk = findViewById(R.id.coin_ask);
        tvBid = findViewById(R.id.coin_bid);

        mBtnDeposit = findViewById(R.id.btn_deposit);
        mBtnWithdraw = findViewById(R.id.btn_withdraw);
        mBtnTrade = findViewById(R.id.btn_trade);

        mStockTabBar= findViewById(R.id.stock_chart_tab_bar);
        mStockChartViewPager = findViewById(R.id.stock_chart_view_pager);
        mPageAdapter=new CoinChartTabAdapter(getSupportFragmentManager());

    }

    private void initListeners() {
        mBtnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGenerateWalletAddress();
            }
        });

        mBtnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CoinDetailActivity.this, CoinWithdrawActivity.class));
            }
        });

        mBtnTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoinDetailActivity.this, CoinTradeActivity.class);
                intent.putExtra("onrampercoins", onRamperCoins);
                intent.putParcelableArrayListExtra("coins", coinList);
                intent.putExtra("onramperApiKey", mOnramperApikey);
                intent.putExtra("xanpoolApiKey", xanpoolApikey);
                intent.putExtra("coin", coin);
                intent.putExtra("page_location", 1);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        loadToast.show();
        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.GET_COIN)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                    .addQueryParameter("asset", coin.getCoinSymbol())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();
                            JSONObject data = response.optJSONObject("data");

                            try {
                                mAggregateDay = data.getJSONArray("today_candel");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                mAggregateWeek = data.getJSONArray("week_candel");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                mAggregateMonth = data.getJSONArray("month_candel");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//
//                            try {
//                                mAggregate6Month = data.getJSONArray("aggregate_month6");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }

                            try {
                                mAggregateYear = data.getJSONArray("year_candel");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                mAggregateAll = data.getJSONArray("all_candel");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                            mPageAdapter.addCharData(mAggregateDay);
                            mPageAdapter.addCharData(mAggregateWeek);
                            mPageAdapter.addCharData(mAggregateMonth);
//                            mPageAdapter.addCharData(mAggregate6Month);
                            mPageAdapter.addCharData(mAggregateYear);
                            mPageAdapter.addCharData(mAggregateAll);
                            mStockChartViewPager.setAdapter(mPageAdapter);
                            mStockTabBar.setupWithViewPager(mStockChartViewPager);

                            JSONObject quote = data.getJSONObject("quote");
                            tvOpen.setText(new DecimalFormat("$ #,###.####").format(quote.getDouble("openPrice")));
                            tvHigh.setText(new DecimalFormat("$ #,###.####").format(quote.getDouble("highPrice")));
                            tvLow.setText(new DecimalFormat("$ #,###.####").format(quote.getDouble("lowPrice")));
                            tvClose.setText(new DecimalFormat("$ #,###.####").format(quote.getDouble("lastPrice")));
                            tvAsk.setText(new DecimalFormat("$ #,###.####").format(quote.getDouble("askPrice")));
                            tvBid.setText(new DecimalFormat("$ #,###.####").format(quote.getDouble("bidPrice")));

                                setDisplayPrice(quote.getDouble("lastPrice"));

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getBaseContext(), "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());

                        }
                    });
    }

    private void doGenerateWalletAddress() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("coin", coin.getCoinId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("deposit param", jsonObject.toString());
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.COIN_DEPOSIT)
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
                            if(response.optBoolean("success")) {
                                loadToast.success();
                                showWalletAddressDialog(response);
                            }
                            else {
                                loadToast.hide();
                                ConfirmAlert confirmAlert = new ConfirmAlert(CoinDetailActivity.this);
                                confirmAlert.error(response.optString("error"));
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            ConfirmAlert confirmAlert = new ConfirmAlert(CoinDetailActivity.this);
                            confirmAlert.alert(error.getErrorBody());
                            Log.d("errorm", "" + error.getErrorBody());

                        }
                    });
    }

    private void showWalletAddressDialog(JSONObject data) {

        DepositDialog mContentDialog;
        mContentDialog = new DepositDialog(R.layout.fragment_coin_deposit, data, coin.getCoinSymbol());
        mContentDialog.setListener(new DepositDialog.Listener() {

            @Override
            public void onOk() {
                Toast.makeText(getBaseContext(), "Copied successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
            }
        });
        mContentDialog.show(getSupportFragmentManager(), "deposit");

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
