package com.wyre.trade.token.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.wyre.trade.token.TokenChartFragment;

import org.json.JSONObject;

public class TokenChartTabAdapter extends FragmentPagerAdapter {
    private String[] items={"1H","6H", "7D", "All"};
    public TokenChartTabAdapter(FragmentManager fm) {
        super(fm);
    }
    private JSONObject data;
    @Override
    public Fragment getItem(int i) {
        TokenChartFragment fragment = TokenChartFragment.newInstance(this.data);
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
