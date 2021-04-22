package com.brian.stocks.stock.adapter;


import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.brian.stocks.model.TransferInfo;

public class StockDepositPageAdapter extends FragmentPagerAdapter {
    private String[] items={"Coin", "Bank"};
    private List<Fragment> fragments = new ArrayList<>();
    private String mStockBalance, mCoinBalance, coinUSD, mUSDBalance;

    public StockDepositPageAdapter(FragmentManager fm, String mStockBalance, String mCoinBalance, String coinUSD, String mUSDBalance) {
        super(fm);
        this.mStockBalance = mStockBalance;
        this.mCoinBalance = mCoinBalance;
        this.coinUSD = coinUSD;
        this.mUSDBalance = mUSDBalance;
    }

    @Override
    public Fragment getItem(int i) {
//        Fragment fragment = null;
//        switch (i) {
//            case 0:
//                return Coin2StockFragment.newInstance(mStockBalance, mCoinBalance, coinUSD, coinStocksList);
//            case 1:
//                return Bank2StockFragment.newInstance(mStockBalance, mUSDBalance);
//            default:
//                return null;
//        }
        Fragment fragment = fragments.get(i);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items[position];
    }

    public void add(Fragment fragment) {
        fragments.add(fragment);
    }
}