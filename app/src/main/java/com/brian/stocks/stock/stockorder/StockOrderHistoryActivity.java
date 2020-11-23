package com.brian.stocks.stock.stockorder;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.brian.stocks.R;
import com.brian.stocks.adapters.PortfolioPageAdapter;

public class StockOrderHistoryActivity extends AppCompatActivity {
    TabLayout mTabBar;
    ViewPager mViewPager;
    PortfolioPageAdapter mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_order_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle("Stock Order History");

        mTabBar = findViewById(R.id.tab_bar);
        mViewPager = findViewById(R.id.ta_view_pager);

        mPageAdapter=new PortfolioPageAdapter(this.getSupportFragmentManager());
        mPageAdapter.add(OrderStockFragment.newInstance());
        mPageAdapter.add(StockTradeHistoryFragment.newInstance());
        mViewPager.setAdapter(mPageAdapter);
        mTabBar.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
