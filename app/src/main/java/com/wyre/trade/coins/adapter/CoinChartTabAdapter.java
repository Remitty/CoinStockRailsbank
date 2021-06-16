package com.wyre.trade.coins.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wyre.trade.coins.CoinChartFragment;
import com.wyre.trade.coins.CoinChartFragment1;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class CoinChartTabAdapter extends FragmentStatePagerAdapter {
    private String[] items={"1D","1W", "1M", "1Y", "All"};
    private List<JSONArray> chartData = new ArrayList<>();
    public CoinChartTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        if( i == 0) {
            fragment = CoinChartFragment1.newInstance(chartData.get(i));
        } else
            fragment = CoinChartFragment.newInstance(chartData.get(i));
        return fragment;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public int getItemPosition(Object object) {

        if (object instanceof CoinChartFragment1) {
            return POSITION_NONE;
        }
        if (object instanceof CoinChartFragment) {
            return POSITION_NONE;
        }
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items[position];
    }

    public void addCharData(JSONArray data){
        chartData.add(data);
    }
}
