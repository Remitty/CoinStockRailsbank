package com.brian.stocks.xmt;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.xmt.adapters.OrderAdapter;
import com.brian.stocks.xmt.adapters.OrderHistoryAdapter;
import com.brian.stocks.xmt.adapters.OrderBookAsksAdapter;
import com.brian.stocks.xmt.adapters.OrderBookBidsAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.google.android.material.tabs.TabLayout;

public class CoinExchangeFragment extends Fragment {
    private Button mBtnTrade;
    private TabLayout tabLayout;
    private EditText mEditQuantity, mEditPrice;
    private TextView mTextChangeVolume, mTextChangeRate, mTextChangeLow, mTextChangeHigh, mTextCoinBuy2, mTextCoinSell2, mTextCoinBuyBalance, mTextCoinSellBalance, mTextOutputTrade, mTextAsksTotalUSD, mTextBidsTotalUSD, mTextPriceUSD;
    private static String CoinSymbol;
    private View mView;
    private DecimalFormat df = new DecimalFormat("#.########");
    private LoadToast loadToast;
    private RecyclerView orderView, orderHistoryView, orderbookAsksView, orderbookBidsView;
    OrderAdapter orderAdapter;
    OrderHistoryAdapter orderHistoryAdapter;
    OrderBookBidsAdapter orderBookBidsAdapter2;
    OrderBookAsksAdapter orderbookAsksAdapter;
    private boolean changedPrice = false;
    private boolean focusedPrice = false;

    private ArrayList<JSONObject> bidsList = new ArrayList<>();
    private ArrayList<JSONObject> asksList = new ArrayList<>();
    private ArrayList<JSONObject> ordersList = new ArrayList<>();
    private ArrayList<JSONObject> ordersHistoryList = new ArrayList<>();
    private String mPair, selType;
    private Float mBTCUSD_rate, mBTCXMT_rate;
    double buyCoinPrice=0, sellCoinPrice=0;
    private JSONArray bids = null;
    private JSONArray asks = null;
    private ArrayList<JSONObject> pairList = new ArrayList<>();
    final Handler h = new Handler();
    int select = 0;
    private Handler mHandler;
    private int i;
    private Runnable mUpdate = new Runnable() {
        public void run() {

            getData();
            i++;
            mHandler.postDelayed(this, 10000);

        }
    };

    public CoinExchangeFragment() {
        // Required empty public constructor
    }

    public static CoinExchangeFragment newInstance(String symbol) {
        CoinExchangeFragment fragment = new CoinExchangeFragment();
        CoinSymbol = symbol;
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_coin_exchange, container, false);


        tabLayout = mView.findViewById(R.id.tabLayout);
        TabLayout.Tab tabsel = tabLayout.getTabAt(0);
        tabsel.select();
        selType = "buy";


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() > 0) {
                    selType = "sell";
                    mBtnTrade.setText("Sell");
                    mBtnTrade.setBackgroundColor(getResources().getColor(R.color.colorRedCrayon));
                } else {
                    selType = "buy";
                    mBtnTrade.setText("Buy");
                    mBtnTrade.setBackgroundColor(getResources().getColor(R.color.green));
                }
                focusedPrice = false;
                changedPrice = false;
                try {
                    mEditPrice.setText(df.format(getPrice()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mPair = "XMT-BTC";
        initComponents();

        initListeners();

        i = 0;
        mHandler = new Handler();
        mHandler.post(mUpdate);

        getPairs();
        return mView;
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

        ArrayAdapter<JSONObject> adapter = new ArrayAdapter<JSONObject>(getContext(), R.layout.pairs_row, R.id.tvPair, pairList) {

            public View getView(int position, View convertView,ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ImageView coin1 = v.findViewById(R.id.ivCoin1);
                TextView pair = v.findViewById(R.id.tvPair);
                ImageView coin2 = v.findViewById(R.id.ivCoin2);

                try {
                    pair.setText((String) pairList.get(position).get("symbol"));
                    new ImageLoadTask((String) pairList.get(position).get("icon1"), coin1).execute();
                    new ImageLoadTask((String) pairList.get(position).get("icon2"), coin2).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return v;

            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View v = super.getDropDownView(position, convertView, parent);
                ImageView coin1 = v.findViewById(R.id.ivCoin1);
                TextView pair = v.findViewById(R.id.tvPair);
                ImageView coin2 = v.findViewById(R.id.ivCoin2);

                try {
                    pair.setText((String) pairList.get(position).get("symbol"));
                    new ImageLoadTask((String) pairList.get(position).get("icon1"), coin1).execute();
                    new ImageLoadTask((String) pairList.get(position).get("icon2"), coin2).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return v;

            }
        };
        l.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                try {
                    mPair = (String) pairList.get(arg2).get("symbol");
                    //Log.d("pairs list response", "got pair " + mPair);

                    TextView vscoin = mView.findViewById(R.id.coin_buyy);
                    vscoin.setText(" "+mPair.split("-")[0]);

                    focusedPrice = false;
                    changedPrice = false;
                    getData();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        l.setAdapter(adapter);

        /*
        for(final String s : pairList){
            Button newButton = new Button(getContext());
            if (s.equals(mPair)) {
                newButton.setSelected(true);
                newButton.setBackgroundColor(0xFFFFFFFF);
            } else {
                newButton.setBackgroundColor(0xFFE9E9E9);
            }
            newButton.setText(s);
            newButton.setTextSize(15);
            newButton.setPadding(125,3,125,3);
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPair = s;
                    TextView vscoin = mView.findViewById(R.id.coin_buyy);
                    vscoin.setText(s.split("-")[1]);
                    updatePairs();

                    getData();

                }
            });
            l.addView(newButton);
        }

         */
    }
    private void getPairs() {

        if(getContext() != null) {
            loadToast.show();
            AndroidNetworking.get(URLHelper.COIN_REALEXCHANGE_LIST)
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
                                        pairList.add(response.getJSONObject(i));
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
                            Log.d("errorpairlist", "" + error.getMessage() + " responde: " + error.getResponse());
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
        //Log.d("exchange param", jsonObject.toString()+" -> "+URLHelper.COIN_REALEXCHANGE_DATA);
        //Log.d("token",SharedHelper.getKey(getContext(),"access_token"));
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.COIN_REALEXCHANGE_DATA)
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
                                if (!response.has("success") || response.getBoolean("success") == false) {
                                    ordersList.clear();
                                    ordersHistoryList.clear();
                                    bidsList.clear();
                                    asksList.clear();
                                    mTextPriceUSD.setText("$0");
                                    mTextAsksTotalUSD.setText("Asks ($0");
                                    mTextBidsTotalUSD.setText("Bids ($0)");
                                    mTextCoinBuyBalance.setText(df.format(0.0));
                                    mTextCoinSellBalance.setText(df.format(0.0));
                                    mTextChangeVolume.setText("Volume 24h: "+df.format(0.0)+" USD");
                                    mTextChangeRate.setText("Rate 24h: "+df.format(0.0)+" USD");
                                    mTextChangeHigh.setText("High: "+df.format(0.0)+" XMT");
                                    mTextChangeLow.setText("Low: "+df.format(0.0)+" XMT");
                                    updateComponents();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            ordersList.clear();
                            ordersHistoryList.clear();
                            bidsList.clear();
                            asksList.clear();

                            JSONObject responseObj = null;
                            try {
                                mTextChangeVolume.setText("Volume 24h: "+df.format(Float.parseFloat(response.getString("change_volume")))+" USD");
                                mTextChangeRate.setText("Rate 24h: "+df.format(Float.parseFloat(response.getString("change_rate")))+" USD");
                                mTextChangeHigh.setText("High: "+df.format(Float.parseFloat(response.getString("last_high")))+" XMT");
                                mTextChangeLow.setText("Low: "+df.format(Float.parseFloat(response.getString("last_low")))+" XMT");

                                mTextCoinBuyBalance.setText(df.format(Float.parseFloat(response.getString("coin2_balance"))));
                                mTextCoinSellBalance.setText(df.format(Float.parseFloat(response.getString("coin1_balance"))));
                                responseObj = response.getJSONObject("orders");
                                if(responseObj != null) {
                                    JSONArray orders = responseObj.getJSONArray("active");
                                    JSONArray ordersHistory = responseObj.getJSONArray("history");
                                    for (int i = 0; i < orders.length(); i++) {
                                        try {
                                            ordersList.add(orders.getJSONObject(i));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    for (int i = 0; i < ordersHistory.length(); i++) {
                                        try {
                                            ordersHistoryList.add(ordersHistory.getJSONObject(i));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    orderHistoryAdapter.notifyDataSetChanged();
                                    orderAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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


                            try {
                                mBTCUSD_rate = Float.parseFloat(response.getString("btc_rate"));
                                mBTCXMT_rate = mBTCUSD_rate * Float.parseFloat(String.valueOf(getPrice()));
                                mTextPriceUSD.setText("$"+df.format(mBTCXMT_rate));
                                mTextAsksTotalUSD.setText("Asks ($"+df.format(Float.parseFloat(response.getString("asks_total")))+")");
                                mTextBidsTotalUSD.setText("Bids ($"+df.format(Float.parseFloat(response.getString("bids_total")))+")");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                if (!changedPrice && !focusedPrice)
                                    mEditPrice.setText(df.format(getPrice()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            updateComponents();
                        }


                        @Override
                        public void onError(ANError error) {
                            // handle error
                            ordersList.clear();
                            ordersHistoryList.clear();
                            bidsList.clear();
                            asksList.clear();
                            mTextPriceUSD.setText("$0");
                            mTextAsksTotalUSD.setText("Asks ($0");
                            mTextBidsTotalUSD.setText("Bids ($0)");
                            mTextChangeVolume.setText("Volume 24h: "+df.format(0.0)+" USD");
                            mTextChangeRate.setText("Rate 24h: "+df.format(0.0)+" USD");
                            mTextChangeHigh.setText("High: "+df.format(0.0)+" XMT");
                            mTextChangeLow.setText("Low: "+df.format(0.0)+" XMT");
                            mTextCoinBuyBalance.setText(df.format(0.0));
                            mTextCoinSellBalance.setText(df.format(0.0));
                            updateComponents();
                            Log.d("errorm", "" + error.getMessage()+" responde: "+error.getResponse());
                        }
                    });
    }


    private void sendData() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("pair", mPair);
            jsonObject.put("type", selType);
            jsonObject.put("quantity", df.format(Float.parseFloat(mEditQuantity.getText().toString())));
            jsonObject.put("price", df.format(Float.parseFloat(mEditPrice.getText().toString())));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(getContext() != null) {
            loadToast.show();
            AndroidNetworking.post(URLHelper.COIN_REALEXCHANGE)
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
                                Toast.makeText(getContext(), "Order filled.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Order created, waiting to be filled.", Toast.LENGTH_SHORT).show();
                            }

                            changedPrice = false;


                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            loadToast.hide();
                            Log.d("errorpost", "" + error.getMessage()+" responde: "+error.getResponse());
                            Toast.makeText(getActivity(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateComponents() {
        orderAdapter = new OrderAdapter(ordersList);
        orderView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderView.setAdapter(orderAdapter);

        orderHistoryView = mView.findViewById(R.id.orders_history_view);
        orderHistoryAdapter = new OrderHistoryAdapter(ordersHistoryList);
        orderHistoryView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderHistoryView.setAdapter(orderHistoryAdapter);

        orderbookAsksView = mView.findViewById(R.id.orderbook_asks_view);
        orderbookAsksAdapter = new OrderBookAsksAdapter(asksList);
        orderbookAsksView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderbookAsksView.setAdapter(orderbookAsksAdapter);

        orderbookBidsView = mView.findViewById(R.id.orderbook_bids_view);
        orderBookBidsAdapter2 = new OrderBookBidsAdapter(bidsList);
        orderbookBidsView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderbookBidsView.setAdapter(orderBookBidsAdapter2);
    }

    private void initComponents() {
        mBtnTrade = mView.findViewById(R.id.btn_coin_trade);
        mBtnTrade.setText("Buy");
        mTextPriceUSD = mView.findViewById(R.id.price_usd);
        mEditQuantity = mView.findViewById(R.id.edit_quantity);
        mEditPrice = mView.findViewById(R.id.edit_price);
        mTextAsksTotalUSD = mView.findViewById(R.id.asks_total_usd);
        mTextBidsTotalUSD = mView.findViewById(R.id.bids_total_usd);
        try {
            mEditPrice.setText(df.format(getPrice()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mTextOutputTrade = mView.findViewById(R.id.output_trade);
        mTextCoinBuy2 = mView.findViewById(R.id.coin_buyy);
        mTextCoinSell2 = mView.findViewById(R.id.coin_selll);

        mTextCoinBuyBalance = mView.findViewById(R.id.coin_buy_balance);
        mTextCoinSellBalance = mView.findViewById(R.id.coin_sell_balance);


        mTextChangeLow = mView.findViewById(R.id.coin_low);
        mTextChangeHigh = mView.findViewById(R.id.coin_high);

        mTextChangeVolume = mView.findViewById(R.id.coin_volume_change);
        mTextChangeRate = mView.findViewById(R.id.coin_rate_change);

        orderView = mView.findViewById(R.id.orders_view);
        orderAdapter = new OrderAdapter(ordersList);
        orderView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderView.setAdapter(orderAdapter);

        orderHistoryView = mView.findViewById(R.id.orders_history_view);
        orderHistoryAdapter = new OrderHistoryAdapter(ordersHistoryList);
        orderHistoryView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderHistoryView.setAdapter(orderHistoryAdapter);

        orderbookAsksView = mView.findViewById(R.id.orderbook_asks_view);
        orderbookAsksAdapter = new OrderBookAsksAdapter(asksList);
        orderbookAsksView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderbookAsksView.setAdapter(orderbookAsksAdapter);

        orderbookBidsView = mView.findViewById(R.id.orderbook_bids_view);
        orderBookBidsAdapter2 = new OrderBookBidsAdapter(bidsList);
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
                            .setMessage("Are you sure you want to " + selType + " " + mEditQuantity.getText().toString() + "XMT? Price is $" + mEditPrice.getText().toString() + ".")
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
                if(!price.equalsIgnoreCase(".")) {
                    //            if (!price.equalsIgnoreCase("")) {
                    //                mtvBuyingEstQty.setText(BigDecimalDouble.newInstance().multify(price, buyCoinPrice));
                    //            } else mtvBuyingEstQty.setText("0.00");
                }
                calculate();
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
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditPrice.getText().toString().equalsIgnoreCase("")) {
                    return;
                }
                calculate();
                changedPrice = true;
            }
        });
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

    private double getPrice() throws JSONException {
        if(selType.equalsIgnoreCase("buy")) {
            if (asksList.isEmpty()) {
                return 0.00000001;
            } else {
                JSONObject item = asksList.get(0);
                return Float.parseFloat(df.format(Float.parseFloat(item.optString("price"))));
            }
        } else {
            if (bidsList.isEmpty()) {
                return 0.00000001;
            } else {
                JSONObject item = bidsList.get(0);
                return Float.parseFloat(df.format(Float.parseFloat(item.optString("price"))));
            }
        }
    }

    private void calculate() {
        //Log.d("calculate","Quantity: "+mEditQuantity.getText().toString()+" Price: "+mEditPrice.getText().toString());
        String fixval1, fixval2;
        fixval1 = mEditQuantity.getText().toString();
        if (fixval1 == null || fixval1.isEmpty()) {
            fixval1 = "0.00000001";
        }
        fixval2 = mEditPrice.getText().toString();
        if (fixval2 == null || fixval2.isEmpty()) {
            fixval2 = "0.00000001";
        }
        Float calc = Float.parseFloat(fixval1) * Float.parseFloat(fixval2);
    //Buying "+df.format(Float.parseFloat(fixval1))+" XMT, s
        if(selType.equalsIgnoreCase("buy")) {
            mTextOutputTrade.setText("Spending "+df.format(calc)+" BTC ($"+df.format(calc * mBTCUSD_rate)+")");
        } else {
            mTextOutputTrade.setText("Getting "+df.format(calc)+" BTC ($"+df.format(calc * mBTCUSD_rate)+")");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {

        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }
}
