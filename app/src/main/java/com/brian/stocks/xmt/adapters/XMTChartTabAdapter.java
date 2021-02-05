package com.brian.stocks.xmt.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.brian.stocks.stock.stocktrade.StockChartFragment;
import com.brian.stocks.xmt.XMTChartFragment;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class XMTChartTabAdapter extends FragmentPagerAdapter {
    private String[] items={"1H","6H", "7D", "All"};
    private List<JSONArray> chartData = new ArrayList<>();
    public XMTChartTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        XMTChartFragment fragment = XMTChartFragment.newInstance(chartData.get(i));
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
