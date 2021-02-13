package com.brian.stocks.home;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.brian.stocks.home.adapters.CoinConversionAdapter;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.adapters.BottomCoinAdapter;
import com.brian.stocks.helper.BigDecimalDouble;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.CoinInfo;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoinSwapFragment extends Fragment {
    private Button mBtnExchange;
    private EditText mEditSellingAmount;
    private LinearLayout sendLayout, getLayout;
    private TextView mEditBuyingCoin, mEditSellingCoin,
            mtvSellAvailabelQty, mtvBuyingEstQty,
            mtvSellRateCoin, mtvBuyRateCoin, mtvBuyRateQty, mtvBuyingCoinName, mtvSellingCoinName;
    private static String CoinSymbol;
    private View mView;
    private LoadToast loadToast;
    private BottomSheetDialog dialog;
    private RecyclerView recyclerView, conversionView;
    BottomCoinAdapter mAdapter;
    private List<CoinInfo> coinList = new ArrayList<>();
    CoinConversionAdapter conversionAdapter;
    private ArrayList<JSONObject> conversionList = new ArrayList<>();
    private String buyCoinId, sellCoinId, buyCoinPrice="0", sellCoinPrice;
    int select = 0;

    public CoinSwapFragment() {
        // Required empty public constructor
    }

    public static CoinSwapFragment newInstance(String symbol) {
        CoinSwapFragment fragment = new CoinSwapFragment();
        CoinSymbol = symbol;
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

        getConversionList();
        return mView;
    }

    private void getConversionList() {
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
                            conversionList.clear();

                            JSONArray history = null;
                            try {
                                history = response.getJSONArray("data");
                                if(history != null) {
                                    for (int i = 0; i < history.length(); i++) {
                                        try {
                                            conversionList.add(history.getJSONObject(i));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    conversionAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
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

    private void initComponents() {
        mBtnExchange = mView.findViewById(R.id.btn_coin_exchange);
        mEditBuyingCoin = mView.findViewById(R.id.edit_buying_coin);
//        mEditBuyingCoin.requestFocus();

        mEditSellingAmount = mView.findViewById(R.id.edit_selling_amount);
        mEditSellingCoin = mView.findViewById(R.id.edit_selling_coin);

        getLayout = mView.findViewById(R.id.layout_get);
        sendLayout = mView.findViewById(R.id.layout_send);

        mtvSellAvailabelQty = mView.findViewById(R.id.tv_sell_avail_qty);
        mtvBuyingEstQty = mView.findViewById(R.id.tv_buy_est_qty);

        mtvBuyingCoinName = mView.findViewById(R.id.buying_coin_name);
        mtvSellingCoinName = mView.findViewById(R.id.selling_coin_name);

        mtvSellRateCoin = mView.findViewById(R.id.tv_sell_rate_coin);
        mtvBuyRateCoin = mView.findViewById(R.id.tv_buy_rate_coin);
        mtvBuyRateQty = mView.findViewById(R.id.tv_buy_rate_qty);

        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(dialogView);

        recyclerView = dialogView.findViewById(R.id.bottom_coins_list);
        mAdapter  = new BottomCoinAdapter(coinList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        conversionView = mView.findViewById(R.id.list_conversion_view);
        conversionAdapter = new CoinConversionAdapter(conversionList);
        conversionView.setLayoutManager(new LinearLayoutManager(getContext()));
        conversionView.setAdapter(conversionAdapter);
    }

    private void initListeners() {
        mAdapter.setListener(new BottomCoinAdapter.Listener() {
            @Override
            public void onSelectCoin(int position) {
                CoinInfo coin = coinList.get(position);
                if(select == 1) {
                    mEditSellingCoin.setText(coin.getCoinSymbol());
                    mtvSellingCoinName.setText(coin.getCoinName());
                    mtvSellRateCoin.setText(coin.getCoinSymbol());
                    mtvSellAvailabelQty.setText("("+coin.getCoinBalance()+")");
//                    sellCoinPrice = coin.getCoinRate();
                    sellCoinId = coin.getCoinId();

                    initFormat();

                }//sell coin
                if(select == 2) {
                    if(!coin.getTradable()){
                        Toast.makeText(getContext(), "Current coins pair can't trade each other", Toast.LENGTH_SHORT).show();
                    }else{
                        mEditBuyingCoin.setText(coin.getCoinSymbol());
                        mtvBuyingCoinName.setText(coin.getCoinName());
                        buyCoinId = coin.getCoinId();
                        String price = mEditSellingAmount.getText().toString();
                        buyCoinPrice = coin.getCoinExchangeRate();
                        mtvBuyRateCoin.setText(coin.getCoinSymbol());
                        mtvBuyRateQty.setText(buyCoinPrice);
                        if(!price.equalsIgnoreCase("")) {
                            mtvBuyingEstQty.setText(BigDecimalDouble.newInstance().multify(price, buyCoinPrice));
                        }
                    }
                }//buy coin
                dialog.dismiss();
            }
        });

        mBtnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEditSellingAmount.getText().toString().equals("")||
                        mEditBuyingCoin.getText().toString().equals("") || mEditSellingCoin.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please fillout all inputs", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setIcon(R.mipmap.ic_launcher_round)
                            .setTitle("Confirm Exchange")
                            .setMessage("Please confirm your transaction? Exchange fees is 0.50%. If you hold 200XMT fees is 0.25%.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doExchange();
                                }
                            }).show();
                }
            }
        });

        getLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select= 2;
//                coinList.clear();
//                if(coinListBuying.size() == 0)
//                    getCoinAssets();
//                else {
//                    coinList.addAll(coinListBuying);
//                    mAdapter.notifyDataSetChanged();
//                    dialog.show();
//                }
                if(sellCoinId != null)
                    getBuyCoinAssets();
                else
                    Toast.makeText(getContext(), "Please select sending coin", Toast.LENGTH_SHORT).show();
            }
        });

        sendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select= 1;
//                coinList.clear();
                getSendCoinAssets();
//                mAdapter.notifyDataSetChanged();
//                dialog.show();
//                if(coinListSelling.size() == 0) {
//                    getCoinAssets();
//                }
//                else {
//
//                    coinList.addAll(coinListSelling);
//                    mAdapter.notifyDataSetChanged();
//                    dialog.show();
//                }
            }
        });

        mEditSellingAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String price=charSequence.toString();
                if(!price.equalsIgnoreCase(".")) {
                    if (!price.equalsIgnoreCase("")) {
                        mtvBuyingEstQty.setText(BigDecimalDouble.newInstance().multify(price, buyCoinPrice));
                    } else mtvBuyingEstQty.setText("0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initFormat() {
        mEditSellingAmount.setText("");
        mtvBuyRateQty.setText("1.00");
        mtvBuyRateCoin.setText("BTC");
        mEditBuyingCoin.setText("");
        mtvBuyingEstQty.setText("0.00");
    }

    private void doExchange() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("buy_coin_id", buyCoinId);
            jsonObject.put("buy_amount", mtvBuyingEstQty.getText().toString());
            jsonObject.put("sell_coin_id", sellCoinId);
            jsonObject.put("sell_amount", mEditSellingAmount.getText().toString());
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
                        if(response.optBoolean("success")) {
                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                            conversionList.clear();

                            JSONArray history = null;
                            try {
                                history = response.getJSONArray("history");
                                if(history != null) {
                                    for (int i = 0; i < history.length(); i++) {
                                        try {
                                            conversionList.add(history.getJSONObject(i));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    conversionAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setIcon(R.mipmap.ic_launcher_round)
                                    .setTitle("Error")
                                    .setMessage(response.optString("message"))
                                    .show();
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

    private void getSendCoinAssets() {
        loadToast.show();

        if(getContext() != null)
            AndroidNetworking.get(URLHelper.GET_SEND_COIN_ASSETS)
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

//                            coinListBuying.clear();
//                            coinListSelling.clear();
                            coinList.clear();

                            JSONArray coins = null;
                            try {
                                coins = response.getJSONArray("assets");
                                if(coins != null) {
                                    for (int i = 0; i < coins.length(); i++) {
                                        try {
                                            Log.d("coinitem", coins.get(i).toString());
//                                    if(coins.getJSONObject(i).getInt("exchange_possible") == 1)
//                                        coinListBuying.add(new CoinInfo((JSONObject) coins.get(i)));
//                                    coinListSelling.add(new CoinInfo((JSONObject) coins.get(i)));
                                            coinList.add(new CoinInfo((JSONObject) coins.get(i)));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    mAdapter.notifyDataSetChanged();
                                    if(coins.length() > 0)
                                        dialog.show();
                                    else Toast.makeText(getContext(), "You have no deposited coins.", Toast.LENGTH_SHORT).show();
                                }
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

    private void getBuyCoinAssets() {
        loadToast.show();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("send_coin_id", sellCoinId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(getContext() != null)
            AndroidNetworking.post(URLHelper.GET_BUY_COIN_ASSETS)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("coin assets response", "" + response);
                            loadToast.success();

                            if(response.optBoolean("success")){
                                coinList.clear();

                                JSONArray coins = null;
                                try {
                                    coins = response.getJSONArray("assets");
                                    for(int i = 0; i < coins.length(); i ++) {
                                        try {
                                            Log.d("coinitem", coins.get(i).toString());
//                                    if(coins.getJSONObject(i).getInt("exchange_possible") == 1)
//                                        coinListBuying.add(new CoinInfo((JSONObject) coins.get(i)));
//                                    coinListSelling.add(new CoinInfo((JSONObject) coins.get(i)));
                                            coinList.add(new CoinInfo((JSONObject) coins.get(i)));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    mAdapter.notifyDataSetChanged();
                                    dialog.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }catch (NullPointerException e) {
                                    e.printStackTrace();
                                }


                            }
                            else{
                                Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                            }

//                            coinListBuying.clear();
//                            coinListSelling.clear();

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
