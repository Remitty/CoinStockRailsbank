package com.brian.stocks.home;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.home.adapters.CoinAdapter;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.CoinInfo;

import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import network.ramp.instantsdk.facade.RampInstantSDK;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class CoinsFragment extends Fragment {

    private static final int REQUEST_ONRAMPER = 100;
    private static final int REQUEST_XANPOOL = 200;
    private LoadToast loadToast;
    private View rootView;
    private CoinAdapter mAdapter;
    private List<CoinInfo> coinList = new ArrayList<>();
    private RecyclerView coinListView;
    private TextView mTotalBalance, mTotalEffect, mUSDBalance, mtvUserName;
    private Handler handler;
    private SwipeRefreshLayout refreshLayout;
    private String CoinSymbol, CoinId;

    private String mOnramperApikey;
    private String onRamperCoins="";
    private ImageView icArrow;
    private String xanpoolApikey;

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
        mUSDBalance = rootView.findViewById(R.id.usd_balance);
        mtvUserName = rootView.findViewById(R.id.user_name);
        mtvUserName.setText(SharedHelper.getKey(getContext(), "fullName"));
        icArrow = rootView.findViewById(R.id.ic_arrow);

        refreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);

        mAdapter = new CoinAdapter(coinList, getActivity(), true);
        coinListView.setLayoutManager(new LinearLayoutManager(getContext()));
        coinListView.setAdapter(mAdapter);

        mAdapter.setListener(new CoinAdapter.Listener() {
            @Override
            public void OnDeposit(int position) {
//                index = position;
//                GoToTransaction(position);
                CoinSymbol = coinList.get(position).getCoinSymbol();
                CoinId = coinList.get(position).getCoinId();

                if (CoinSymbol.equals("XMT")) {
                    showInputDialog(inflater, container,
                            savedInstanceState);
                } else {
                    doGenerateWalletAddress("", 0);
                }
            }

            @Override
            public void OnBuyNow(final int position) {
                final CoinInfo coin = coinList.get(position);
                CoinSymbol = coin.getCoinSymbol();
                CoinId = coin.getCoinId();
                if(coin.getBuyNowOption() >= 2) { // btc, eth, usdc, dai
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    View view = getLayoutInflater().inflate(R.layout.dialog_coin_buy_option, null);
                    final RadioGroup coinRdg = view.findViewById(R.id.coin_rdg);
                    if(coin.getBuyNowOption() == 2) {
                        RadioButton rdb3 = view.findViewById(R.id.coin_rdb_3);
                        rdb3.setVisibility(View.GONE);
                    }
                    alert.setView(view)
                            .setIcon(R.mipmap.ic_launcher_round)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (coinRdg.getCheckedRadioButtonId() == R.id.coin_rdb_1)
                                        doGenerateWalletAddress("", 1); // ramp
                                    else if(coinRdg.getCheckedRadioButtonId() == R.id.coin_rdb_2)doGenerateWalletAddress("", 2); // onramp
                                    else if(coinRdg.getCheckedRadioButtonId() == R.id.coin_rdb_3)doGenerateWalletAddress("", 3); // xanpool
                                    else {
                                        Toast.makeText(getContext(), "Please select option", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            })
                            .show();
                }
                else { // xrp, bch, ltc
                    doGenerateWalletAddress("", 2);
                }
            }
        });

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

        return rootView;
    }

    protected void showInputDialog(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {

        // get prompts.xml view
        View promptView = inflater.inflate(R.layout.prompt_deposit_amount, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //depositAmount.setText(editText.getText());
                        doGenerateWalletAddress(String.valueOf(editText.getText()), 0);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
        .show();

        // create an alert dialog
//        AlertDialog alert = alertDialogBuilder.create();
//        alert.show();
    }

    private void showWalletAddressDialog(JSONObject data) {
        if (CoinSymbol.equals("XMT")) {
            DepositERC20Dialog DepositDialogERC20;
            DepositDialogERC20 = new DepositERC20Dialog(R.layout.fragment_coin_deposit_erc20, data, CoinSymbol);
            DepositDialogERC20.setListener(new DepositERC20Dialog.Listener() {

                @Override
                public void onOk() {
//                mContentDialog.dismiss();
                    Toast.makeText(getContext(), "Copied successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
//                mContentDialog.dismiss();
                }
            });
            DepositDialogERC20.show(this.getActivity().getSupportFragmentManager(), "deposit");
        } else {
            DepositDialog mContentDialog;
            mContentDialog = new DepositDialog(R.layout.fragment_coin_deposit, data, CoinSymbol);
            mContentDialog.setListener(new DepositDialog.Listener() {

                @Override
                public void onOk() {
//                mContentDialog.dismiss();
                    Toast.makeText(getContext(), "Copied successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
//                mContentDialog.dismiss();
                }
            });
            mContentDialog.show(getActivity().getSupportFragmentManager(), "deposit");
        }
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
                            mUSDBalance.setText(new DecimalFormat("#,###.##").format(response.getDouble("usd_balance")));
                            mOnramperApikey = response.getString("onramper_api_key");
                            xanpoolApikey = response.getString("xanpool_api_key");
                            icArrow.setVisibility(View.VISIBLE);
                            if(response.getString("total_effect").startsWith("-")) {
                                Picasso.with(getActivity()).load(R.drawable.ic_down).into(icArrow);
                                mTotalEffect.setTextColor(RED);
                            }
                            else mTotalEffect.setTextColor(GREEN);

                            JSONArray coins = response.getJSONArray("coins");
                            for(int i = 0; i < coins.length(); i ++) {
                                try {
                                    CoinInfo coin = new CoinInfo((JSONObject) coins.get(i));
                                    coinList.add(coin);
                                    if(coin.getBuyNowOption() >= 2)
                                        onRamperCoins = onRamperCoins+coin.getCoinSymbol()+",";

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(onRamperCoins.length() > 2)
                                onRamperCoins = onRamperCoins.substring(0, onRamperCoins.length() - 1);
                            mAdapter.notifyDataSetChanged();
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

    private void doGenerateWalletAddress(String depositQuant, final int type) {
        loadToast.show();
//        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("coin", CoinId);
            if (!depositQuant.isEmpty()) {
                jsonObject.put("deposit_amount", depositQuant);
            }
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
                                if(type == 0)
                                    showWalletAddressDialog(response);
                                else if(type == 1){
                                    RampInstantSDK rampInstantSDK = new RampInstantSDK(
                                            getContext(),
                                            address,
                                            "https://cdn-images-1.medium.com/max/2600/1*nqtMwugX7TtpcS-5c3lRjw.png",
                                            "Coins",
                                            "com.brian.stocks",
                                            CoinSymbol,
                                            "",
                                            "",
                                            "https://widget-instant.ramp.network/"
//                                            "https://ri-widget-staging-ropsten.firebaseapp.com/"
                                    );
                                    rampInstantSDK.show();
                                } else if(type == 2){
                                    String coin_address = "&defaultAddrs="+CoinSymbol+":"+address;
//                                    String coin_address = "";
                                    String excludeCryptos = "&excludeCryptos=EOS,USDT,XLM,BUSD,GUSD,HUSD,PAX,USDS";
                                    String url = "https://widget.onramper.com?color=1d2d50&apiKey="+mOnramperApikey+"&defaultCrypto="
                                                +CoinSymbol+excludeCryptos+coin_address+"&onlyCryptos="+onRamperCoins
                                                +"&isAddressEditable=false";
                                    Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
                                    browserIntent.putExtra("uri", url);
                                    startActivityForResult(browserIntent, REQUEST_ONRAMPER);
                                }
                                else if(type == 3){ //xanpool
                                    String base = "https://checkout.xanpool.com/";
                                    String apikey = "?apiKey="+xanpoolApikey;
                                    String wallet = "&wallet=" + address;
                                    String cryptoCurrency = "&cryptoCurrency=" + CoinSymbol;
                                    String transactionType = "&transactionType=";
                                    String isWebview = "&isWebview=true";
                                    String partnerData = "&partnerData=88824d8683434f4e";

                                    String url = base + apikey + wallet + cryptoCurrency + transactionType + isWebview;
                                    Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
                                    browserIntent.putExtra("uri", url);
                                    startActivityForResult(browserIntent, REQUEST_XANPOOL);
                                }
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
