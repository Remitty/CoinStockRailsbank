package com.wyre.trade.stock.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wyre.trade.stock.stocktrade.StockChartFragment;

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
    public CharSequence getPageTitle(int position) {
        return items[position];
    }

    public void addCharData(JSONArray data){
        chartData.add(data);
    }
}
