package com.wyre.trade.home.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.wyre.trade.R;
import com.wyre.trade.model.BankInfo;

import java.util.ArrayList;

public class CashBalancePagerAdapter extends PagerAdapter {
    Context mContext;
    ArrayList<BankInfo> mBalances;

    public CashBalancePagerAdapter(Context context, ArrayList<BankInfo> balances) {
        mContext = context;
        mBalances = balances;
    }

    @Override
    public int getCount() {
        return mBalances.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BankInfo bankInfo = mBalances.get(position);

        TextView textView = new TextView(mContext);
        String balance = bankInfo.getBalance();
        String currency = bankInfo.getCurrency();
        textView.setText(balance + " " + currency);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setTextColor(mContext.getColor(R.color.green));
        container.addView(textView);

        return textView;
    }
}
