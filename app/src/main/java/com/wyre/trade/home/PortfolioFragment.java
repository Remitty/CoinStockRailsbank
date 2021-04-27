package com.wyre.trade.home;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wyre.trade.R;
import com.wyre.trade.adapters.PortfolioPageAdapter;
import com.wyre.trade.stock.stockorder.OrderStockFragment;

public class PortfolioFragment extends Fragment {
    TabLayout mTabBar;
    ViewPager mViewPager;
    PortfolioPageAdapter mPageAdapter;

    public PortfolioFragment() {
        // Required empty public constructor
    }
    public static PortfolioFragment newInstance() {
        PortfolioFragment fragment = new PortfolioFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        mTabBar = view.findViewById(R.id.tab_bar);
        mViewPager = view.findViewById(R.id.ta_view_pager);

        mPageAdapter=new PortfolioPageAdapter(getFragmentManager());
        mPageAdapter.add(InvestedStockFragment.newInstance());
        mPageAdapter.add(OrderStockFragment.newInstance());
//        mPageAdapter.add(StockTradeHistoryFragment.newInstance());
        mViewPager.setAdapter(mPageAdapter);
        mTabBar.setupWithViewPager(mViewPager);

        return view;
    }
}
