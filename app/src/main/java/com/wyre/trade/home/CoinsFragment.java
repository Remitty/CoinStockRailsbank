package com.wyre.trade.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.adapters.BottomCoinAdapter;
import com.wyre.trade.coins.CoinTradeActivity;
import com.wyre.trade.coins.CoinWithdrawActivity;
import com.wyre.trade.home.adapters.CoinAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.CoinInfo;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class CoinsFragment extends Fragment {

    private static final int REQUEST_ONRAMPER = 100;
    private static final int REQUEST_XANPOOL = 200;
    private LoadToast loadToast;
    private View rootView;
    private CoinAdapter mAdapter;
    private ArrayList<CoinInfo> coinList = new ArrayList<>();
    private RecyclerView coinListView;
    private TextView mTotalBalance, mTotalEffect, mUSDBalance, mtvUserName;
    private Handler handler;
    private SwipeRefreshLayout refreshLayout;
    private String CoinSymbol, CoinId;

    private String mOnramperApikey;
    private String onRamperCoins="";
    private String xanpoolApikey;
    private ImageView icArrow;
    private Button btnDeposit, btnWithdraw, btnTrade;

    BottomCoinAdapter mBottomAdapter;
    private RecyclerView recyclerView;
    private BottomSheetDialog dialog;

    public CoinsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CoinsFragment newInstance() {
        CoinsFragment fragment = new CoinsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadToast = new LoadToast(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_coins, container, false);

        coinListView = (RecyclerView) rootView.findViewById(R.id.list_coins_view);
        mTotalBalance = rootView.findViewById(R.id.total_balance);
        mTotalEffect = rootView.findViewById(R.id.total_effect);
//        mUSDBalance = rootView.findViewById(R.id.usd_balance);
//        mtvUserName = rootView.findViewById(R.id.user_name);
//        mtvUserName.setText(SharedHelper.getKey(getContext(), "fullName"));
        icArrow = rootView.findViewById(R.id.ic_arrow);

        btnDeposit = rootView.findViewById(R.id.btn_deposit);
        btnWithdraw = rootView.findViewById(R.id.btn_withdraw);
        btnTrade = rootView.findViewById(R.id.btn_trade);

        refreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);

        initListeners();
        initCoinsRecyclerView();

        getAllCoins(false);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAllCoins(true);
                handler.postDelayed(this, 60000);
            }
        }, 60000);

        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                }
        );

        final Handler handler = new Handler();

        initBottomSheet();

        return rootView;
    }

    private void initListeners() {
        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CoinWithdrawActivity.class));
            }
        });
        btnTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CoinTradeActivity.class);
                intent.putExtra("onrampercoins", onRamperCoins);
                intent.putParcelableArrayListExtra("coins", coinList);
                intent.putExtra("onramperApiKey", mOnramperApikey);
                intent.putExtra("xanpoolApiKey", xanpoolApikey);
                startActivity(intent);
            }
        });
    }

    private void initCoinsRecyclerView() {
        mAdapter = new CoinAdapter(coinList, getActivity(), true);
        coinListView.setLayoutManager(new LinearLayoutManager(getContext()));
        coinListView.setAdapter(mAdapter);
    }

    private void initBottomSheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(dialogView);

        recyclerView = dialogView.findViewById(R.id.bottom_coins_list);
        mBottomAdapter  = new BottomCoinAdapter(coinList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mBottomAdapter);

        mBottomAdapter.setListener(new BottomCoinAdapter.Listener() {
            @Override
            public void onSelectCoin(int position) {
                CoinInfo coin = coinList.get(position);
                CoinSymbol = coin.getCoinSymbol();
                CoinId = coin.getCoinId();

                doGenerateWalletAddress();

                dialog.dismiss();
            }
        });
    }

    private void showWalletAddressDialog(JSONObject data) {

            DepositDialog mContentDialog;
            mContentDialog = new DepositDialog(R.layout.fragment_coin_deposit, data, CoinSymbol);
            mContentDialog.setListener(new DepositDialog.Listener() {

                @Override
                public void onOk() {
                    Toast.makeText(getContext(), "Copied successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                }
            });
            mContentDialog.show(getActivity().getSupportFragmentManager(), "deposit");

    }

    private void refresh() {
        refreshLayout.setRefreshing(true);
        JSONObject jsonObject = new JSONObject();

        Log.d("access_token", SharedHelper.getKey(getContext(), "access_token"));
        if(getContext() != null)
            AndroidNetworking.get(URLHelper.GET_ALL_COINS)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            refreshLayout.setRefreshing(false);
                            coinList.clear();

                            try {
                                mTotalBalance.setText("$ " +new DecimalFormat("#,###.##").format(response.getDouble("total_balance")));
                                JSONArray coins = response.optJSONArray("coins");
                                if(coins != null)
                                    for(int i = 0; i < coins.length(); i ++) {
                                        try {
                                            Log.d("coinitem", coins.get(i).toString());
                                            coinList.add(new CoinInfo((JSONObject) coins.get(i)));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError error) {
                            refreshLayout.setRefreshing(false);
                            // handle error
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });
    }

    private void getAllCoins(boolean update) {
        if(!update)
            loadToast.show();
        JSONObject jsonObject = new JSONObject();

        Log.d("access_token", SharedHelper.getKey(getContext(), "access_token"));
        if(getContext() != null)
            AndroidNetworking.get(URLHelper.GET_ALL_COINS)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        coinList.clear();

                        try {
                            mTotalBalance.setText("$ " +new DecimalFormat("#,###.##").format(response.getDouble("total_balance")));
                            mTotalEffect.setText(response.getString("total_effect")+" %");
//                            mUSDBalance.setText(new DecimalFormat("#,###.##").format(response.getDouble("usd_balance")));
                            mOnramperApikey = response.getString("onramper_api_key");
                            xanpoolApikey = response.getString("xanpool_api_key");
                            icArrow.setVisibility(View.VISIBLE);
                            if(response.getString("total_effect").startsWith("-")) {
                                Picasso.with(getContext()).load(R.drawable.ic_down).into(icArrow);
                                mTotalEffect.setTextColor(RED);
                            }
                            else {
                                Picasso.with(getContext()).load(R.drawable.ic_up).into(icArrow);
                                mTotalEffect.setTextColor(GREEN);
                            }

                            JSONArray coins = response.getJSONArray("coins");
                            for(int i = 0; i < coins.length(); i ++) {
                                try {
                                    CoinInfo coin = new CoinInfo((JSONObject) coins.get(i));
                                    coinList.add(coin);
                                    if(coin.getBuyNowOption() >= 2 && coin.getBuyNowOption() < 100)
                                        onRamperCoins = onRamperCoins+coin.getCoinSymbol()+",";

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(onRamperCoins.length() > 2)
                                onRamperCoins = onRamperCoins.substring(0, onRamperCoins.length() - 1);
                            mAdapter.notifyDataSetChanged();
                            mBottomAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }

    private void doGenerateWalletAddress() {
        loadToast.show();
//        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("coin", CoinId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("deposit param", jsonObject.toString());
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.COIN_DEPOSIT)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();
                            if(response.optBoolean("success")) {
                                String address = response.optString("address");
                                showWalletAddressDialog(response);
                            }
                            else
                                Toast.makeText(getContext(), response.optString("error"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                            Log.d("errorm", "" + error.getErrorBody());

                        }
                    });
    }

    @Override
    public void onResume() {
        super.onResume();

//        getAllCoins();
//        handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getAllCoinsUpdae();
//                handler.postDelayed(this, 5000);
//            }
//        }, 10000);
    }

    @Override
    public void onPause() {

        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
