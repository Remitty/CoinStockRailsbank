package com.wyre.trade.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PortfolioPageAdapter extends FragmentPagerAdapter {
//    private String[] items={"Invested","Orders", "History"};
    private String[] items={"Orders", "History"};
    private List<Fragment> fragments = new ArrayList<>();
    public PortfolioPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
//        Fragment fragment = null;
        Log.d("tabselect", i+"");

        Fragment fragment=fragments.get(i);
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

    public void add(Fragment fragment) {
        fragments.add(fragment);
    }
}
