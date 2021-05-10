package com.wyre.trade.stock.deposit;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.Card;
import com.wyre.trade.stock.adapter.StockDepositPageAdapter;
import com.google.android.material.tabs.TabLayout;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONObject;

public class StockDepositActivity extends AppCompatActivity {
    private LoadToast loadToast;

    TabLayout tab;
    private StockDepositPageAdapter mPageAdapter;
    private ViewPager mViewPager;

    String mCoinBalance="0", mStockBalance="0", mUSDBalance="0", coinUSD ="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_deposit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
         getSupportActionBar().setTitle("");

//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
////        fragmentTransaction.replace(me.riddhimanadib.library.R.id.frameLayout, Coin2StockFragment.newInstance());
////        fragmentTransaction.commit();
        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        tab = findViewById(R.id.tab);
        mViewPager = findViewById(R.id.pager);

        mPageAdapter=new StockDepositPageAdapter(this.getSupportFragmentManager(), mStockBalance, mCoinBalance, coinUSD, mUSDBalance);

        tab.setupWithViewPager(mViewPager);

        getBalances();

    }

    private void getBalances() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.GET_USER_BALANCES)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loadToast.success();
                            Log.d("user balances", response.toString());
                            mStockBalance = response.optString("stock_balance");
                            mCoinBalance = response.optString("usdc_balance");
                            coinUSD = response.optString("usdc_est_usd");
                            mUSDBalance = response.optString("bank_usd_balance");

                            Card card = null;
                            if(response.optJSONObject("card") != null)
                                card = new Card(response.optJSONObject("card"));
                            String stripe_pub_key = response.optString("stripe_pub_key");
                            JSONObject paypal = response.optJSONObject("paypal");

                            mPageAdapter.add(Coin2StockFragment.newInstance(mStockBalance, Double.parseDouble(mCoinBalance), coinUSD));
//                            mPageAdapter.add(Bank2StockFragment.newInstance(mStockBalance, mUSDBalance));
                            mPageAdapter.add(Card2StockFragment.newInstance(mStockBalance, card, stripe_pub_key));
                            mPageAdapter.add(Paypal2StockFragment.newInstance(mStockBalance, paypal));
                            mViewPager.setAdapter(mPageAdapter);
                            mPageAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
