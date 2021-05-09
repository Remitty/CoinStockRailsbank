package com.wyre.trade.stock.stocktrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.stock.adapter.StockChartTabAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import static android.graphics.Color.RED;

public class TopStocksTradeActivity extends AppCompatActivity {
    private LoadToast loadToast;
    private KProgressHUD loadProgress;
    private TextView mStockName, mStockSymbol, mStockPriceInteger, mStockPriceFloat, mStockTodayChange, mStockTodayChangePerc;
    private String companysummary, companyindustry, companyweb;
    private MaterialButton mBtnBuy;
    private Intent mIntent;
    private LinearLayout mStocksContent;
    private String StockBalance="0.0";
    private double StockPrice = 0.0;
    TextView mTextCompanySummary, mTextCompanyWeb, mTextCompanyIndustry;

    private JSONArray mAggregateDay = new JSONArray(), mAggregateWeek = new JSONArray(), mAggregateMonth = new JSONArray(), mAggregate6Month = new JSONArray(), mAggregateYear = new JSONArray(), mAggregateAll = new JSONArray();
    private ViewPager mStockChartViewPager;
    private TabLayout mStockTabBar;
    StockChartTabAdapter mPageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_stocks_trade);
        loadToast = new LoadToast(this);
        loadProgress = KProgressHUD.create(TopStocksTradeActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        initComponents();

        mIntent = getIntent();

        mStockName.setText(mIntent.getStringExtra("stock_name"));
        mStockSymbol.setText(mIntent.getStringExtra("stock_symbol"));
        String price = mIntent.getStringExtra("stock_price");
        String[] separatedPrice = price.split("\\.");
        StockPrice = Double.parseDouble(price);
        mStockPriceInteger.setText(separatedPrice[0].trim());
        if(separatedPrice.length> 1)
            mStockPriceFloat.setText("."+separatedPrice[1].trim());

        mStockTodayChange.setText("$ "+mIntent.getStringExtra("stock_today_change"));

        if(mIntent.getStringExtra("stock_today_change").startsWith("-")) {
            mStockTodayChange.setTextColor(RED);
            mStockTodayChangePerc.setTextColor(RED);
        }
        mStockTodayChangePerc.setText(mIntent.getStringExtra("stock_today_change_perc"));

        mBtnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(StockPrice == 0)
//                    Toast.makeText(getBaseContext(), "Not working today.", Toast.LENGTH_SHORT).show();
//                else
                onBuyStack();
            }
        });

        mPageAdapter=new StockChartTabAdapter(getSupportFragmentManager());

        getStockDetailData();

    }

    private void initComponents() {
        mStockName = findViewById(R.id.stock_name);
        mStockSymbol = findViewById(R.id.stock_symbol);
        mStockPriceInteger = findViewById(R.id.stock_price_integer);
        mStockPriceFloat = findViewById(R.id.stock_price_float);
        mStockTodayChange = findViewById(R.id.stock_today_change);
        mStockTodayChangePerc = findViewById(R.id.stock_today_change_perc);

        mBtnBuy = findViewById(R.id.btn_stock_buy);

        mStockTabBar= findViewById(R.id.stock_chart_tab_bar);
        mStockChartViewPager = findViewById(R.id.stock_chart_view_pager);

        mStocksContent = findViewById(R.id.ll_stocks_content);

        mTextCompanyIndustry = findViewById(R.id.company_industry);
        mTextCompanySummary = findViewById(R.id.company_summary);
        mTextCompanyWeb = findViewById(R.id.company_web);

    }


    private void onBuyStack() {
        Intent intent = new Intent(this, StockBuyActivity.class);
        intent.putExtra("stock_price", mIntent.getStringExtra("stock_price"));
        intent.putExtra("stock_name", mStockName.getText());
        intent.putExtra("stock_symbol", mStockSymbol.getText());
        intent.putExtra("stock_balance", StockBalance);
//        intent.putExtra("stock_shares", mIntent.getStringExtra("stock_shares"));
        intent.putExtra("company_summary", companysummary);
        intent.putExtra("company_industry", companyindustry);
        intent.putExtra("company_web", companyweb);
        startActivity(intent);
    }

    private void getStockDetailData() {
//        loadToast.show();
        loadProgress.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ticker", mStockSymbol.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.GET_STOCK_DETAIL)
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
//                            loadToast.success();

                            try {
                                mAggregateDay = response.getJSONArray("aggregate_day");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                mAggregateWeek = response.getJSONArray("aggregate_week");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                mAggregateMonth = response.getJSONArray("aggregate_month");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                mAggregate6Month = response.getJSONArray("aggregate_month6");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                mAggregateYear = response.getJSONArray("aggregate_year");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                mAggregateAll = response.getJSONArray("aggregate_all");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mPageAdapter.addCharData(mAggregateDay);
                            mPageAdapter.addCharData(mAggregateWeek);
                            mPageAdapter.addCharData(mAggregateMonth);
                            mPageAdapter.addCharData(mAggregate6Month);
                            mPageAdapter.addCharData(mAggregateYear);
                            mPageAdapter.addCharData(mAggregateAll);
                            mStockChartViewPager.setAdapter(mPageAdapter);
                            mStockTabBar.setupWithViewPager(mStockChartViewPager);

                            try {

                                StockBalance = response.optString("stock_balance");

                                JSONObject company = response.getJSONObject("company");
                                if(company != null) {
                                    companysummary = company.optString("description");
                                    companyindustry = company.optString("industry");
                                    companyweb = company.optString("url");

                                    mTextCompanyIndustry.setText(companyindustry);
                                    mTextCompanySummary.setText(companysummary);
                                    mTextCompanyWeb.setText(companyweb);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            loadProgress.dismiss();
                        }

                        @Override
                        public void onError(ANError error) {
//                            loadToast.error();
                            loadProgress.dismiss();
                            // handle error
                            Toast.makeText(getBaseContext(), "Network error", Toast.LENGTH_SHORT).show();
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
