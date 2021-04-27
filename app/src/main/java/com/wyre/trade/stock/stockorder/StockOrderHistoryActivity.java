package com.wyre.trade.stock.stockorder;

import com.wyre.trade.home.HomeActivity;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.wyre.trade.R;
import com.wyre.trade.adapters.PortfolioPageAdapter;

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
            getSupportActionBar().setElevation(0);
        this.setTitle("");

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
        startActivity(new Intent(StockOrderHistoryActivity.this, HomeActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
