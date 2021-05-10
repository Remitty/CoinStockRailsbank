package com.wyre.trade.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListPageAdapter extends FragmentPagerAdapter {
//    private String[] items={"Deposit","Exchange", "Withdraw"};
    private String[] items={"Deposit", "Transfer"};
    private List<Fragment> fragments = new ArrayList<>();
    public ListPageAdapter(FragmentManager fm) {
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
