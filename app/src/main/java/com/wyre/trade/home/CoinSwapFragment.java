package com.wyre.trade.home;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.wyre.trade.coins.CoinSwapHistoryActivity;
import com.wyre.trade.model.SwapRateModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.adapters.BottomCoinAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.CoinInfo;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CoinSwapFragment extends Fragment {
    private Button mBtnExchange;
    private EditText mEditSendAmount;
    private TextView mtvGetCoin, mtvSendCoin, mtvSendLimit, mtvGetFee,
            mtvSendCoinBalance, mtvGetEstQty,
            mtvSendRateCoin, mtvGetRateCoin, mtvGetCoinRate,
            tvViewHistory;
    private ImageView sendIcon, getIcon;

    private View mView;
    private LoadToast loadToast;

    private BottomSheetDialog dialog;
    private RecyclerView recyclerView;
    BottomCoinAdapter mAdapter;
    private List<CoinInfo> coinList = new ArrayList<>();

    private CoinInfo sendCoin, getCoin;
    private SwapRateModel rateModel;
    private Double sellAmount = 0.1, getAmount = 0.0, fee = 0.0;
    private String selectedType = "get";

    public CoinSwapFragment() {
        // Required empty public constructor
    }

    public static CoinSwapFragment newInstance(String symbol) {
        CoinSwapFragment fragment = new CoinSwapFragment();
        return fragment;
    }

    public static CoinSwapFragment newInstance() {
        CoinSwapFragment fragment = new CoinSwapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadToast = new LoadToast(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_coin_swap, container, false);

        initComponents();

        initListeners();

//        mEditSendAmount.setText(sellAmount+"");

        loadData();

        return mView;
    }



    private void initComponents() {
        mBtnExchange = mView.findViewById(R.id.btn_coin_exchange);
        mtvGetCoin = mView.findViewById(R.id.tv_get_coin);
        mtvSendCoin = mView.findViewById(R.id.tv_send_coin);
        mEditSendAmount = mView.findViewById(R.id.editSendAmount);

        mtvGetFee = mView.findViewById(R.id.tvFee);
        mtvSendLimit = mView.findViewById(R.id.tvSendLimit);

        sendIcon = mView.findViewById(R.id.send_imgIcon);
        getIcon = mView.findViewById(R.id.get_imgIcon);


        mtvSendCoinBalance = mView.findViewById(R.id.tvSendCoinBalance);
        mtvGetEstQty = mView.findViewById(R.id.tvGetAmount);

        tvViewHistory = mView.findViewById(R.id.tvViewHistory);

        mtvSendRateCoin = mView.findViewById(R.id.tv_sell_rate_coin);
        mtvGetRateCoin = mView.findViewById(R.id.tv_buy_rate_coin);
        mtvGetCoinRate = mView.findViewById(R.id.tv_buy_rate_qty);

        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(dialogView);

        recyclerView = dialogView.findViewById(R.id.bottom_coins_list);
        mAdapter  = new BottomCoinAdapter(coinList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
    }

    private void initListeners() {
        mAdapter.setListener(new BottomCoinAdapter.Listener() {
            @Override
            public void onSelectCoin(int position) {
                CoinInfo coin = coinList.get(position);
                if(selectedType.equals("send")) {
                    sendCoin = coin;
                    mtvSendCoin.setText(coin.getCoinSymbol());
                    mtvSendCoinBalance.setText(coin.getCoinBalance());
                    Picasso.with(getActivity()).load(sendCoin.getCoinIcon()).into(sendIcon);
                }//sell coin
                if(selectedType.equals("get")) {
                    getCoin = coin;
                    mtvGetCoin.setText(coin.getCoinSymbol());
                    Picasso.with(getActivity()).load(getCoin.getCoinIcon()).into(getIcon);
                }//buy coin
                dialog.dismiss();
                getRate();
            }
        });

        mBtnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sellAmount == 0) {
                    mEditSendAmount.setError("!");
                    return;
                }

                if(sellAmount > Double.parseDouble(sendCoin.getCoinBalance())) {
                    Toast.makeText(getContext(), "Insufficient funds", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(sellAmount < Double.parseDouble(rateModel.getSendMin()) || sellAmount > Double.parseDouble(rateModel.getSendMax())) {
                    Toast.makeText(getContext(), "The deposit amount is not within the range", Toast.LENGTH_SHORT).show();
                    return;
                }

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setIcon(R.mipmap.ic_launcher_round)
                            .setTitle("Confirm Swap")
                            .setMessage("Please confirm your transaction.\n" +  SharedHelper.getKey(getContext(), "msgCoinSwapFeePolicy"))
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doExchange();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

            }
        });

        mtvGetCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedType = "get";
                dialog.show();
            }
        });

        mtvSendCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedType = "send";
                dialog.show();
            }
        });

        tvViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CoinSwapHistoryActivity.class));
            }
        });

        mEditSendAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String price=charSequence.toString();
                if(price.equals(".") || price.equals("")) {
                    sellAmount = 0.0;
                }
                else sellAmount = new Double(price);
                displayFee();
                displayEstCost();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getRate() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sendCoin", sendCoin.getCoinSymbol());
            jsonObject.put("receiveCoin", getCoin.getCoinSymbol());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("exchange param", jsonObject.toString());
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.GET_COIN_EXCHANGE_RATE)
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
                            rateModel = new SwapRateModel();
                            try {
                                rateModel.setData(response.getJSONObject("rate"));
                                displayFee();
                                displayEstCost();
                                displayExchangeRate();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });
    }

    private void displayFee(){
        getAmount = sellAmount * (1-rateModel.getSendFee()) * rateModel.getRate();
        fee = getAmount * rateModel.getFee();
        mtvGetFee.setText(new DecimalFormat("#,###.######").format(fee));
    }
    private void displayEstCost(){
        mtvGetEstQty.setText(new DecimalFormat("#,###.####").format(getAmount-fee));
    }
    private void displayExchangeRate(){
        mtvSendRateCoin.setText(sendCoin.getCoinSymbol());
        mtvGetRateCoin.setText(getCoin.getCoinSymbol());
        mtvGetCoinRate.setText(rateModel.getRate()+"");
        mtvSendLimit.setText(rateModel.getSendMin() + " - " + rateModel.getSendMax());
    }

    private void doExchange() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("receiveCoin", getCoin.getCoinSymbol());
            jsonObject.put("receive_amount",getAmount);
            jsonObject.put("sendCoin", sendCoin.getCoinSymbol());
            jsonObject.put("send_amount", mEditSendAmount.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("exchange param", jsonObject.toString());
        if(getContext() != null)
        AndroidNetworking.post(URLHelper.COIN_EXCHANGE)
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
                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
    }

    private void loadData() {
        loadToast.show();

        if(getContext() != null)
            AndroidNetworking.get(URLHelper.COIN_EXCHANGE)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("coin assets response", "" + response);
                            loadToast.success();

                            coinList.clear();

                            JSONArray coins = null;
                            try {
                                coins = response.getJSONArray("coins");
                                for(int i = 0; i < coins.length(); i ++) {
                                    try {
                                        coinList.add(new CoinInfo((JSONObject) coins.get(i)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                mAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            rateModel = new SwapRateModel();
                            rateModel.setData(response.optJSONObject("rate"));
                            getCoin = new CoinInfo(response.optJSONObject("receiveCoin"));
                            sendCoin = new CoinInfo(response.optJSONObject("sendCoin"));

                            mtvSendCoinBalance.setText(sendCoin.getCoinBalance());
                            Picasso.with(getContext()).load(getCoin.getCoinIcon()).into(getIcon);
                            mtvGetCoin.setText(getCoin.getCoinSymbol());

                            displayFee();
                            displayEstCost();
                            displayExchangeRate();

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

    @Override
    public void onStart() {
        super.onStart();
    }
}
