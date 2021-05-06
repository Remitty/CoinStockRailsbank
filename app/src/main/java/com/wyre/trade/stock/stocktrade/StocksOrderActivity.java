package com.wyre.trade.stock.stocktrade;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.wyre.trade.model.StocksInfo;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.stock.adapter.StockChartTabAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.stock.stockorder.StockOrderHistoryActivity;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StocksOrderActivity extends AppCompatActivity {
    private LoadToast loadToast;
    KProgressHUD loadProgress;
    private TextView mStockName, mStockSymbol, mStockPriceInteger, mStockPriceFloat, mStockTodayChange, mStockTodayChangePerc;
    private TextView mStockShares, mStockQuantity, mStockAvgCost;
    private Button mBtnEdit, mBtnCancel;
    private Intent mIntent;
    private String StockBalance, StockSide;

    private JSONArray mAggregateDay, mAggregateWeek, mAggregateMonth, mAggregate6Month, mAggregateYear, mAggregateAll;
    private ViewPager mStockChartViewPager;
    private TabLayout mStockTabBar;
    StockChartTabAdapter mPageAdapter;
    private String price, shares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks_order);
        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);
        loadProgress = KProgressHUD.create(StocksOrderActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
         getSupportActionBar().setTitle("");

        initComponents();

        mIntent = getIntent();

        mStockName.setText(mIntent.getStringExtra("stock_name"));
//        mStockShares.setText(mIntent.getStringExtra("stock_order_shares"));
        StockSide = mIntent.getStringExtra("stock_order_side");


        mBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditOrder();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmCancelDialog();
            }
        });

        getStockDetailData();
    }

    private void initComponents() {
        mStockName = findViewById(R.id.stock_name);
        mStockSymbol = findViewById(R.id.stock_symbol);
        mStockPriceInteger = findViewById(R.id.stock_price_integer);
        mStockPriceFloat = findViewById(R.id.stock_price_float);
        mStockTodayChange = findViewById(R.id.stock_today_change);
        mStockTodayChangePerc = findViewById(R.id.stock_today_change_perc);

        mStockShares = findViewById(R.id.stock_shares);
        mStockQuantity = findViewById(R.id.stock_equity_values);
        mStockAvgCost = findViewById(R.id.stock_avg_costs);

        mBtnEdit = findViewById(R.id.btn_stock_edit);
        mBtnCancel = findViewById(R.id.btn_stock_cancel);

        mStockTabBar=  findViewById(R.id.stock_chart_tab_bar);
        mStockChartViewPager = findViewById(R.id.stock_chart_view_pager);

    }

    private void confirmCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Are you sure to cancel the order?")
                .setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCancelOrder();
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

    private void onCancelOrder() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", mIntent.getStringExtra("stock_order_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.REQUEST_STOCK_ORDER_CANCEL)
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
                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StocksOrderActivity.this, StockOrderHistoryActivity.class));
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

    private void onEditOrder() {
        Intent intent = new Intent(this, StockReplaceActivity.class);
        intent.putExtra("stock_price", price);
        intent.putExtra("stock_limit_price", mIntent.getStringExtra("stock_limit_price"));
        intent.putExtra("stock_name", mIntent.getStringExtra("stock_name"));
        intent.putExtra("stock_symbol", mStockSymbol.getText());
        intent.putExtra("stock_order_id", mIntent.getStringExtra("stock_order_id"));
        intent.putExtra("stock_shares", shares);
        intent.putExtra("stock_order_shares", mIntent.getStringExtra("stock_order_shares"));
        intent.putExtra("stock_est_cost", mIntent.getDoubleExtra("stock_est_cost", 0.0));
        intent.putExtra("stock_order_type", mIntent.getStringExtra("stock_order_type"));
        intent.putExtra("stock_balance", StockBalance);
        intent.putExtra("stock_side", StockSide);
        startActivity(intent);
    }

    private void getStockDetailData() {
//        loadToast.show();
        loadProgress.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ticker", mStockName.getText());
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
                            loadProgress.dismiss();
                            try {
                                mAggregateDay = response.getJSONArray("aggregate_day");
                                mAggregateWeek = response.getJSONArray("aggregate_week");
                                mAggregateMonth = response.getJSONArray("aggregate_month");
                                mAggregate6Month = response.getJSONArray("aggregate_month6");
                                mAggregateYear = response.getJSONArray("aggregate_year");
                                mAggregateAll = response.getJSONArray("aggregate_all");

                                mPageAdapter=new StockChartTabAdapter(getSupportFragmentManager());
                                mPageAdapter.addCharData(mAggregateDay);
                                mPageAdapter.addCharData(mAggregateWeek);
                                mPageAdapter.addCharData(mAggregateMonth);
                                mPageAdapter.addCharData(mAggregate6Month);
                                mPageAdapter.addCharData(mAggregateYear);
                                mPageAdapter.addCharData(mAggregateAll);
                                mStockChartViewPager.setAdapter(mPageAdapter);
                                mStockTabBar.setupWithViewPager(mStockChartViewPager);


                                mStockSymbol.setText(response.optString("symbol"));
                                StockBalance = response.optString("stock_balance");
                                StocksInfo stock = new StocksInfo(response.optJSONObject("stock"));
                                price = stock.getCurrentPrice();
                                String[] separatedPrice = price.split("\\.");
                                mStockPriceInteger.setText(separatedPrice[0].trim());
                                if(separatedPrice.length > 1)
                                    mStockPriceFloat.setText("."+separatedPrice[1].trim());
                                mStockTodayChange.setText("$ "+stock.getChangePrice());
                                mStockTodayChangePerc.setText("( % "+ stock.getChangePricePercent() + " )");
                                shares = stock.getQty();

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
