package com.brian.stocks.coins;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.brian.stocks.R;
import com.brian.stocks.coins.adapter.CoinTradePageAdapter;
import com.brian.stocks.home.HomeActivity;
import com.brian.stocks.model.CoinInfo;
import com.brian.stocks.predict.PredictActivity;
import com.brian.stocks.token.CoinExchangeFragment;
import com.google.android.material.tabs.TabLayout;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;

public class CoinTradeActivity extends AppCompatActivity {
    TabLayout tab;
    ViewPager pager;
    CoinTradePageAdapter mAdapter;
    LoadToast loadToast;

    String onRamperCoins, mOnramperApikey, xanpoolApikey;

    private ArrayList<CoinInfo> coinList = new ArrayList<CoinInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_trade);

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

        tab = findViewById(R.id.tab);
        pager = findViewById(R.id.view_pager);

        mAdapter = new CoinTradePageAdapter(getSupportFragmentManager());
        mAdapter.add(CoinExchangeFragment.newInstance());
        mAdapter.add(FiatPaymentFragment.newInstance(coinList, mOnramperApikey, onRamperCoins, xanpoolApikey));

        pager.setAdapter(mAdapter);
        tab.setupWithViewPager(pager);

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
