package com.wyre.trade.token;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.TokenTradePair;
import com.wyre.trade.token.adapters.OrderAdapter;
import com.wyre.trade.token.adapters.OrderBookAsksAdapter;
import com.wyre.trade.token.adapters.OrderBookBidsAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.wyre.trade.token.adapters.TokenChartTabAdapter;
import com.google.android.material.tabs.TabLayout;

import static android.graphics.Color.BLACK;

public class CoinExchangeFragment extends Fragment {
    private Button mBtnTrade;
    private TextView mBtnBuy, mBtnSell;
    private EditText mEditQuantity, mEditPrice;
    private TextView mTextChangeVolume, mTextChangeRate, mTextChangeLow, mTextChangeHigh, mTextCoinBuy, mTextCoinBuyBalance, mTextCoinSellBalance, mTextCoinSellBalance1, mTextOutputTrade, mTextAsksTotalUSD, mTextBidsTotalUSD, mTextPriceUSD;
    private TextView mtvOrderSymbol;
    private TextView mtvMaxBidQty, mtvMaxBidValue;
    private TextView mtvTradeHistory, mtvOrderBook;
    private String marketCoinSymbol, tradeCoinSymbol = "PEPE";
    private View mView;
    private DecimalFormat df = new DecimalFormat("#.####");
    private LoadToast loadToast;
    private RecyclerView orderView, orderHistoryView, orderbookAsksView, orderbookBidsView;
    OrderAdapter orderAdapter;

    OrderBookBidsAdapter orderBookBidsAdapter2;
    OrderBookAsksAdapter orderbookAsksAdapter;
    private boolean changedPrice = false;
    private boolean focusedPrice = false;

    private ArrayList<JSONObject> bidsList = new ArrayList<>();
    private ArrayList<JSONObject> asksList = new ArrayList<>();
    private ArrayList<JSONObject> ordersList = new ArrayList<>();

    private String mPair, selType;
    private Double High = 0.0, Low = 0.0;
    private Double mBTCUSD_rate, mBTCXMT_rate;
    private JSONArray bids = null;
    private JSONArray asks = null;
    private ArrayList<TokenTradePair> pairList = new ArrayList<>();
    final Handler h = new Handler();
    private Handler mHandler;
    private int i;
    private boolean graph_flag = false;
    private Runnable mUpdate = new Runnable() {
        public void run() {

            getData();
            i++;
            mHandler.postDelayed(this, 10000);

        }
    };

    private JSONObject mAggregateDay, mAggregateWeek, mAggregateMonth, mAggregate6Month, mAggregateYear, mAggregateAll;
    private ViewPager mXMTChartViewPager;
    private TabLayout mXMTTabBar;
    TokenChartTabAdapter mPageAdapter;

    public CoinExchangeFragment() {
        // Required empty public constructor
    }

    public static CoinExchangeFragment newInstance(String symbol) {
        CoinExchangeFragment fragment = new CoinExchangeFragment();
        fragment.marketCoinSymbol = symbol;
        return fragment;
    }

    public static CoinExchangeFragment newInstance() {
        CoinExchangeFragment fragment = new CoinExchangeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadToast = new LoadToast(getActivity());
//        loadToast.setProgressColor(R.color.green);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_coin_exchange, container, false);

        mBtnBuy = mView.findViewById(R.id.btnBuy);
        mBtnSell = mView.findViewById(R.id.btnSell);

        selType = "buy";

        mPair = "PEPE-XLM";
        initComponents();

        initListeners();

        i = 0;
        mHandler = new Handler();
        mHandler.post(mUpdate);

        getPairs();
        return mView;
    }


    private void initComponents() {
        mBtnTrade = mView.findViewById(R.id.btn_coin_trade);
        mBtnTrade.setText("Buy " + tradeCoinSymbol);
        mTextPriceUSD = mView.findViewById(R.id.price_usd);
        mEditQuantity = mView.findViewById(R.id.edit_quantity);
        mEditPrice = mView.findViewById(R.id.edit_price);
        mTextAsksTotalUSD = mView.findViewById(R.id.asks_total_usd);
        mTextBidsTotalUSD = mView.findViewById(R.id.bids_total_usd);

        mtvTradeHistory = mView.findViewById(R.id.tv_trade_history);
        mtvOrderBook = mView.findViewById(R.id.tv_view_order_book);

        mtvMaxBidQty = mView.findViewById(R.id.max_bid_quantity);
        mtvMaxBidValue = mView.findViewById(R.id.max_bid_value);

//        try {
//            mEditPrice.setText(new DecimalFormat("#.########").format(getPrice()));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        mTextOutputTrade = mView.findViewById(R.id.output_trade);
        mTextCoinBuy = mView.findViewById(R.id.coin_buyy);

        mTextCoinBuyBalance = mView.findViewById(R.id.coin_buy_balance);
        mTextCoinSellBalance = mView.findViewById(R.id.coin_sell_balance);
        mTextCoinSellBalance1 = mView.findViewById(R.id.coin_sell_balance1);


        mTextChangeLow = mView.findViewById(R.id.coin_low);
        mTextChangeHigh = mView.findViewById(R.id.coin_high);

        mTextChangeVolume = mView.findViewById(R.id.coin_volume_change);
        mTextChangeRate = mView.findViewById(R.id.coin_rate_change);

        orderView = mView.findViewById(R.id.orders_view);
        orderAdapter = new OrderAdapter(ordersList);
        orderView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter.setListener(new OrderAdapter.Listener() {
            @Override
            public void cancelOrder(final int position) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setIcon(R.mipmap.ic_launcher_round)
                        .setTitle("Confirm cancel")
                        .setMessage("Are you sure you want to cancel this order?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submitCancel(position);
                            }
                        })
                        .show();

            }
        });
        orderView.setAdapter(orderAdapter);

        orderbookAsksView = mView.findViewById(R.id.orderbook_asks_view);
        orderbookAsksAdapter = new OrderBookAsksAdapter(asksList);
        orderbookAsksAdapter.setListener(new OrderBookAsksAdapter.Listener() {
            @Override
            public void OnClickQty(int position) {
                JSONObject object = asksList.get(position);
                Double qty = object.optDouble("quantity");
                mEditQuantity.setText(new DecimalFormat("#.########").format(qty));
            }

            @Override
            public void OnClickValue(int position) {
                JSONObject object = asksList.get(position);
                Double price = object.optDouble("price");
                mEditPrice.setText(new DecimalFormat("#.########").format(price));
            }
        });
        orderbookAsksView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderbookAsksView.setAdapter(orderbookAsksAdapter);

        orderbookBidsView = mView.findViewById(R.id.orderbook_bids_view);
        orderBookBidsAdapter2 = new OrderBookBidsAdapter(bidsList);
        orderBookBidsAdapter2.setListener(new OrderBookBidsAdapter.Listener() {
            @Override
            public void OnClickQty(int position) {
                JSONObject object = bidsList.get(position);
                Double qty = object.optDouble("quantity");
                mEditQuantity.setText(new DecimalFormat("#.########").format(qty));
            }

            @Override
            public void OnClickValue(int position) {
                JSONObject object = bidsList.get(position);
                Double price = object.optDouble("price");
                mEditPrice.setText(new DecimalFormat("#.########").format(price));
            }
        });
        orderbookBidsView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderbookBidsView.setAdapter(orderBookBidsAdapter2);

    }

    private void initListeners() {

        mBtnTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setIcon(R.mipmap.ic_launcher_round)
                            .setTitle("Confirm trade")
                            .setMessage("Are you sure you want to " + selType + " " + mEditQuantity.getText().toString() + tradeCoinSymbol +"?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendData();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }


            }
        });
        mBtnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selType = "buy";
                mBtnTrade.setText("Buy " + tradeCoinSymbol);
                mBtnTrade.setBackgroundColor(getResources().getColor(R.color.green));

                mBtnBuy.setTextColor(getResources().getColor(R.color.green));
                mBtnSell.setTextColor(BLACK);

                focusedPrice = false;
                changedPrice = false;
                try {
                    mEditPrice.setText(new DecimalFormat("#.########").format(getPrice()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mBtnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selType = "sell";
                mBtnTrade.setText("Sell " + tradeCoinSymbol);
                mBtnTrade.setBackgroundColor(getResources().getColor(R.color.colorRedCrayon));

                mBtnSell.setTextColor(getResources().getColor(R.color.green));
                mBtnBuy.setTextColor(BLACK);

                focusedPrice = false;
                changedPrice = false;
                try {
                    mEditPrice.setText(new DecimalFormat("#.########").format(getPrice()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mEditPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {
                    focusedPrice = true;
                } else {
                    focusedPrice = false;
                }
            }
        });
        mEditQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String price=charSequence.toString();
//                if(!price.equalsIgnoreCase(".")) {
//                    //            if (!price.equalsIgnoreCase("")) {
//                    //                mtvBuyingEstQty.setText(BigDecimalDouble.newInstance().multify(price, buyCoinPrice));
//                    //            } else mtvBuyingEstQty.setText("0.00");
//                }
//                calculate();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditQuantity.getText().toString().equalsIgnoreCase("")) {
                    return;
                }
                calculate();

            }
        });

        mEditPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changedPrice = true;
                String str = charSequence.toString();
                if (str.equals("") || str.equals(".")) {
                    mBTCXMT_rate = 0.0;
                } else
                    mBTCXMT_rate = mBTCUSD_rate * Double.parseDouble(str);
                mTextPriceUSD.setText("$" + new DecimalFormat("#,###.##").format(mBTCXMT_rate));
                calculate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mtvTradeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TokenTradeHistoryActivity.class);
                startActivity(intent);
            }
        });

        mtvOrderBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TokenOrderBookActivity.class);
                startActivity(intent);
            }
        });
    }

    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

    private void updatePairs() {
        Spinner l = mView.findViewById(R.id.pairslist);

        ArrayAdapter<TokenTradePair> adapter = new ArrayAdapter<TokenTradePair>(getContext(), R.layout.pairs_row, R.id.tvPair, pairList) {

            public View getView(int position, View convertView,ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ImageView coin1 = v.findViewById(R.id.ivCoin1);
                TextView tvPair = v.findViewById(R.id.tvPair);
                ImageView coin2 = v.findViewById(R.id.ivCoin2);

                TokenTradePair pair = pairList.get(position);
                tvPair.setText(pair.getTradeSymbol() +"/" + pair.getMarketSymbol());
//                    new ImageLoadTask((String) pairList.get(position).get("icon1"), coin1).execute();
//                    new ImageLoadTask((String) pairList.get(position).get("icon2"), coin2).execute();

                return v;

            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View v = super.getDropDownView(position, convertView, parent);
                ImageView coin1 = v.findViewById(R.id.ivCoin1);
                TextView tvPair = v.findViewById(R.id.tvPair);
                ImageView coin2 = v.findViewById(R.id.ivCoin2);

                TokenTradePair pair = pairList.get(position);
                tvPair.setText(pair.getTradeSymbol() +"/" + pair.getMarketSymbol());
//                    new ImageLoadTask((String) pairList.get(position).get("icon1"), coin1).execute();
//                    new ImageLoadTask((String) pairList.get(position).get("icon2"), coin2).execute();
                return v;

            }
        };
        l.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                mPair = pairList.get(arg2).getPair();
                marketCoinSymbol = pairList.get(arg2).getMarketSymbol();
                tradeCoinSymbol = pairList.get(arg2).getTradeSymbol();
                focusedPrice = false;
                changedPrice = false;
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        l.setAdapter(adapter);

    }
    private void getPairs() {

        if(getContext() != null) {
            loadToast.show();
            AndroidNetworking.get(URLHelper.COIN_TRADE_LIST)
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(), "access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //Log.d("pairs list response", "" + response.toString());
                            loadToast.hide();
                            pairList.clear();


                            if (response != null && response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        TokenTradePair pair = new TokenTradePair();
                                        pair.setData(response.getJSONObject(i));
                                        pairList.add(pair);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            updatePairs();
                        }


                        @Override
                        public void onError(ANError error) {
                            // handle error
                            loadToast.hide();
                            Log.d("errorpairlist", "" + error.getErrorBody() + " responde: " + error.getResponse());
                        }
                    });
        }
    }

    private void getData() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("pair", mPair);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("exchange param", jsonObject.toString()+" -> "+URLHelper.COIN_TRADE_DATA);
        //Log.d("token",SharedHelper.getKey(getContext(),"access_token"));
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.COIN_TRADE_DATA)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("coin assets response", "" + response.toString());
                            try {
                                if (response.getBoolean("success") == true) {

                                    bidsList.clear();
                                    asksList.clear();

                                    JSONObject responseObj = null;
                                    marketCoinSymbol = response.getString("coin1");
                                    mTextChangeVolume.setText("$ "+new DecimalFormat("#,###.##").format(response.getDouble("change_volume")));
                                    mTextChangeRate.setText("$ "+df.format(response.getDouble("change_rate")));
                                    High = response.getDouble("last_high");
                                    mTextChangeHigh.setText( new DecimalFormat("#.####").format(High) +marketCoinSymbol);
                                    Low = response.getDouble("last_low");
                                    mTextChangeLow.setText( new DecimalFormat("#.####").format(Low) + marketCoinSymbol);
//                                    mtvOrderSymbol.setText("("+marketCoinSymbol+")");
                                    try {
                                        JSONObject maxBid = response.getJSONObject("max_bid");
                                        mtvMaxBidQty.setText(new DecimalFormat("#.########").format(maxBid.getDouble("quantity")));
                                        mtvMaxBidValue.setText(new DecimalFormat("#.########").format(maxBid.getDouble("price")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    mTextCoinBuyBalance.setText(new DecimalFormat("#,###.####").format(response.getDouble("coin2_balance")));
                                    mTextCoinBuy.setText(tradeCoinSymbol);
                                    mTextCoinSellBalance.setText(df.format(response.getDouble("coin1_balance")) + marketCoinSymbol);
                                    mTextCoinSellBalance1.setText(df.format(response.getDouble("coin1_balance")) + marketCoinSymbol);
                                    if(graph_flag == false) {
                                        ordersList.clear();
                                        try {
                                            JSONArray orders = response.getJSONArray("orders");
                                            for (int i = 0; i < orders.length(); i++) {
                                                try {
                                                    ordersList.add(orders.getJSONObject(i));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            orderAdapter.notifyDataSetChanged();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        bids = response.getJSONArray("bids");
                                        if(bids != null) {
                                            for (int i = 0; i < bids.length(); i++) {
                                                try {
                                                    bidsList.add(bids.getJSONObject(i));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            orderBookBidsAdapter2.notifyDataSetChanged();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        asks = response.getJSONArray("asks");
                                        if(asks != null) {
                                            for (int i = 0; i < asks.length(); i++) {
                                                try {
                                                    asksList.add(asks.getJSONObject(i));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            orderbookAsksAdapter.notifyDataSetChanged();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    initGraph(response);
                                    try {
                                        mBTCUSD_rate = response.getDouble("btc_rate");
                                        mBTCXMT_rate = mBTCUSD_rate * getPrice();
                                        mTextPriceUSD.setText("$"+new DecimalFormat("#,###.##").format(mBTCXMT_rate));
                                        mTextAsksTotalUSD.setText("$ "+new DecimalFormat("#,###.##").format(response.getDouble("asks_total")));
                                        mTextBidsTotalUSD.setText("$ "+new DecimalFormat("#,###.##").format(response.getDouble("bids_total")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                     if (!changedPrice && !focusedPrice)
                                         mEditPrice.setText(new DecimalFormat("#.########").format(getPrice()));

                                }else {
                                    Toast.makeText(getContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Toast.makeText(getContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    private void sendData() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("pair", mPair);
            jsonObject.put("type", selType);
            jsonObject.put("quantity", mEditQuantity.getText().toString());
            jsonObject.put("price", mEditPrice.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("xmt exchange params", jsonObject.toString());
        if(getContext() != null) {
            loadToast.show();
            AndroidNetworking.post(URLHelper.COIN_TRADE)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("xmt trading response", "" + response.toString());
                            loadToast.hide();
                            if (!response.has("success")) {
                                /*
                                try {

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                 */
                                Toast.makeText(getContext(), "Order failed.", Toast.LENGTH_SHORT).show();

                                return;
                            }

                            if (response.has("filled")) {
                                Toast.makeText(getContext(), "Order placed successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Order created, waiting to be filled.", Toast.LENGTH_SHORT).show();
                            }

                            ordersList.clear();
                            try {
                                JSONArray orders = response.getJSONArray("orders");
                                for (int i = 0; i < orders.length(); i++) {
                                    try {
                                        ordersList.add(orders.getJSONObject(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                orderAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            changedPrice = false;


                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            loadToast.hide();
                            Log.d("errorpost", "" + error.getErrorBody());
                            Toast.makeText(getActivity(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void submitCancel(final int position) {
        JSONObject jsonObject = new JSONObject();
        String orderid = null;
        try {
            orderid = ordersList.get(position).getString("id");
            jsonObject.put("orderid", orderid);
            Log.d("cancel xmt orderid", orderid);
            if (getContext() != null) {
                loadToast.show();
                AndroidNetworking.post(URLHelper.COIN_TRADE_CANCEL)
                        .addHeaders("Content-Type", "application/json")
                        .addHeaders("accept", "application/json")
                        .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(), "access_token"))
                        .addJSONObjectBody(jsonObject)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadToast.hide();
                                Log.d("coin post response", "" + response.toString());

                                if (response.optBoolean("success")) {
                                    Toast.makeText(getContext(), "Order Cancelled.", Toast.LENGTH_SHORT).show();
                                    ordersList.remove(position);
                                    orderAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onError(ANError error) {
                                // handle error
                                loadToast.hide();
                                Toast.makeText(getContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                                Log.d("errorpost", "" + error.getErrorBody() + " responde: " + error.getResponse());
                            }
                        });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private boolean validate() {
        boolean validation = true;
        if(mEditPrice.getText().toString().equals("")) {
            mEditPrice.setError("!");
            validation = false;
        }
        if(mEditQuantity.getText().toString().equals("")) {
            mEditQuantity.setError("!");
            validation = false;
        }
        return validation;
    }

    private Double getPrice() throws JSONException {
        try {
            if (selType.equalsIgnoreCase("buy")) {
                if (asksList.isEmpty()) {
                    return Low;
                } else {
                    JSONObject item = asksList.get(0);
                    return item.optDouble("price");
                }
            } else {
                if (bidsList.isEmpty()) {
                    return High;
                } else {
                    JSONObject item = bidsList.get(0);
                    return item.optDouble("price");
                }
            }
        } catch (Exception e) {
            return 1.0;
        }
    }

    private void calculate() {
        //Log.d("calculate","Quantity: "+mEditQuantity.getText().toString()+" Price: "+mEditPrice.getText().toString());
        String fixval1, fixval2;
        fixval1 = mEditQuantity.getText().toString();
        if (fixval1 == null || fixval1.isEmpty()) {
            fixval1 = "0";
        }
        fixval2 = mEditPrice.getText().toString();
        if (fixval2 == null || fixval2.isEmpty()) {
            fixval2 = "0";
        }
        Float calc = Float.parseFloat(fixval1) * Float.parseFloat(fixval2);
        mTextOutputTrade.setText(new DecimalFormat("#.####").format(calc) + marketCoinSymbol);

    }

    private void initGraph(JSONObject response) {
        if(graph_flag == false) {
            graph_flag = true;
            try {
                mAggregateDay = response.getJSONObject("aggregates");
                mAggregateWeek = response.getJSONObject("aggregates");
                mAggregateMonth = response.getJSONObject("aggregates");
                mAggregate6Month = response.getJSONObject("aggregates");
                mAggregateYear = response.getJSONObject("aggregates");
                mAggregateAll = response.getJSONObject("aggregates");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mXMTTabBar = mView.findViewById(R.id.xmt_chart_tab_bar);
            mXMTChartViewPager = mView.findViewById(R.id.xmt_chart_view_pager);
            mPageAdapter = new TokenChartTabAdapter(getFragmentManager());
            mPageAdapter.addCharData(mAggregateDay);
            mPageAdapter.addCharData(mAggregateWeek);
            mPageAdapter.addCharData(mAggregateMonth);
            mPageAdapter.addCharData(mAggregate6Month);
            mPageAdapter.addCharData(mAggregateYear);
            mPageAdapter.addCharData(mAggregateAll);
            mXMTChartViewPager.setAdapter(mPageAdapter);
            mXMTTabBar.setupWithViewPager(mXMTChartViewPager);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {

        super.onPause();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
