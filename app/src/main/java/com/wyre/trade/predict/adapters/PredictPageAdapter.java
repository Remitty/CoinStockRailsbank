package com.wyre.trade.predict.adapters;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wyre.trade.predict.IncomingPredictsFragment;
import com.wyre.trade.predict.MyPredictsFragment;
import com.wyre.trade.predict.NewPredictsFragment;

public class PredictPageAdapter extends FragmentStatePagerAdapter {
    private String[] items={"  Predict  ", "  Results  ", "  My posts  "};
    private List<Fragment> fragments = new ArrayList<>();
    private ArrayList all, incoming, my_post;
    public PredictPageAdapter(FragmentManager fm, ArrayList all, ArrayList incoming, ArrayList my_post) {
        super(fm);
        this.all = all;
        this.incoming = incoming;
        this.my_post = my_post;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new NewPredictsFragment(all);
            case 1:
                return new IncomingPredictsFragment(incoming);
            case 2:
                return new MyPredictsFragment(my_post);
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof NewPredictsFragment) {
            return POSITION_NONE;
        }
        if (object instanceof IncomingPredictsFragment) {
            return POSITION_NONE;
        }
        return 1;
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
