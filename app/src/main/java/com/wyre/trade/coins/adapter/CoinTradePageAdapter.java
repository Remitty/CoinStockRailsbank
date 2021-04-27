package com.wyre.trade.coins.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CoinTradePageAdapter extends FragmentStatePagerAdapter {
    private String[] items={"  Exchange  ", "  Fiat payment  "};
    private List<Fragment> fragments = new ArrayList<>();

    public CoinTradePageAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int i) {
        Fragment sampleFragment=fragments.get(i);
        return sampleFragment;
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
