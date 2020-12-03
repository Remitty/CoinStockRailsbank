package com.brian.stocks.predict.adapters;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PredictPageAdapter extends FragmentPagerAdapter {
    private String[] items={"Predict", "Results", "My posts"};
    private List<Fragment> fragments = new ArrayList<>();
    public PredictPageAdapter(FragmentManager fm) {
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
