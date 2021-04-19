package com.brian.stocks.stock.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.brian.stocks.stock.stocktrade.StockChartFragment;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class StockChartTabAdapter extends FragmentStatePagerAdapter {
    private String[] items={"1D","1W", "1M", "6M", "1Y", "All"};
    private List<JSONArray> chartData = new ArrayList<>();
    public StockChartTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        StockChartFragment fragment = StockChartFragment.newInstance(chartData.get(i));
        return fragment;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof StockChartFragment) {
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
