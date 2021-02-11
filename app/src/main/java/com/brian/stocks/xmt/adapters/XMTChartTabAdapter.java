package com.brian.stocks.xmt.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.brian.stocks.xmt.XMTChartFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class XMTChartTabAdapter extends FragmentPagerAdapter {
    private String[] items={"1H","6H", "7D", "All"};
    public XMTChartTabAdapter(FragmentManager fm) {
        super(fm);
    }
    private JSONObject data;
    @Override
    public Fragment getItem(int i) {
        XMTChartFragment fragment = XMTChartFragment.newInstance(this.data);
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

    public void addCharData(JSONObject data){
        this.data = data;
    }
}
