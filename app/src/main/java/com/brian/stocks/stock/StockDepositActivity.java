package com.brian.stocks.stock;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.BankInfo;
import com.brian.stocks.model.TransferInfo;
import com.brian.stocks.stock.adapter.StockDepositPageAdapter;
import com.google.android.material.tabs.TabLayout;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockDepositActivity extends AppCompatActivity {
    private LoadToast loadToast;

    TabLayout tab;
    private StockDepositPageAdapter mPageAdapter;
    private ViewPager mViewPager;

    String mCoinBalance="0", mStockBalance="0", mUSDBalance="0", coinUSD ="0";
    private List<TransferInfo> coinStocksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_deposit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Deposit Stock");

//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
////        fragmentTransaction.replace(me.riddhimanadib.library.R.id.frameLayout, Coin2StockFragment.newInstance());
////        fragmentTransaction.commit();
        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        tab = findViewById(R.id.tab);
        mViewPager = findViewById(R.id.pager);

        mPageAdapter=new StockDepositPageAdapter(this.getSupportFragmentManager());
        mPageAdapter.add(Coin2StockFragment.newInstance(mStockBalance, mCoinBalance, coinUSD, coinStocksList));
        mPageAdapter.add(Bank2StockFragment.newInstance(mStockBalance, mUSDBalance));
        mViewPager.setAdapter(mPageAdapter);
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

                            coinStocksList.clear();

                            JSONArray stocks = response.optJSONArray("coin_stock_transfer");
                            for(int i = 0; i < stocks.length(); i ++) {
                                try {
                                    coinStocksList.add(new TransferInfo((JSONObject) stocks.get(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            mPageAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
