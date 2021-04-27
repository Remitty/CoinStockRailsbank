package com.wyre.trade.home;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.wyre.trade.stock.deposit.StockDepositActivity;
import com.wyre.trade.stock.StocksActivity;
import com.wyre.trade.stock.stocktrade.StocksTradingActivity;
import com.wyre.trade.home.adapters.OpenPositionAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.StocksInfo;
import com.wyre.trade.stock.stockwithdraw.StockCoinWithdrawActivity;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class InvestedStockFragment extends Fragment {
    private LoadToast loadToast;
    private View rootView;
    private OpenPositionAdapter mAdapter;
    private List<StocksInfo> stocksList = new ArrayList<>();
    private RecyclerView stocksListView;
    private TextView mTotalBalance, mStockBalance, mTextStockProfit, mTextStockMargin, marketStatus;
    private ImageView icArrow;
    Button btnInvest, btnDeposit, btnWithdraw;
    private SwipeRefreshLayout refreshLayout;

    Socket socket;

    public InvestedStockFragment() {
        // Required empty public constructor
    }
    public static InvestedStockFragment newInstance() {
        InvestedStockFragment fragment = new InvestedStockFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_invested_stock, container, false);
        stocksListView = (RecyclerView) rootView.findViewById(R.id.list_stocks_view);
        mTotalBalance = rootView.findViewById(R.id.total_balance);
        mStockBalance = rootView.findViewById(R.id.stock_balance);
        mTextStockProfit = rootView.findViewById(R.id.stock_profit);
        mTextStockMargin = rootView.findViewById(R.id.margin_balance);
        marketStatus = rootView.findViewById(R.id.market_status);

        icArrow = rootView.findViewById(R.id.ic_arrow);

        btnInvest = rootView.findViewById(R.id.btn_invest);
        btnDeposit = rootView.findViewById(R.id.btn_deposit);
        btnWithdraw = rootView.findViewById(R.id.btn_withdraw);

        btnInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), StocksActivity.class));
            }
        });

        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), StockDepositActivity.class));
            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), StockCoinWithdrawActivity.class));
            }
        });

        mAdapter = new OpenPositionAdapter(stocksList, true, getActivity());
        stocksListView.setLayoutManager(new LinearLayoutManager(getContext()));
        stocksListView.setAdapter(mAdapter);

        mAdapter.setListener(new OpenPositionAdapter.Listener() {
            @Override
            public void OnGoToTrade(int position) {
                GoToTrade(position);
            }
        });
        getAllStocks(true);

        refreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                }
        );

        initSocket();

        return rootView;
    }

    private void initSocket() {
        URI uri = URI.create("https://cloud-sse.iexapis.com/stable/news-stream?token=pk_dbf0fba01029463897491d37e16bb1e5&symbols=spy,ibm,twtr");

        IO.Options options = IO.Options.builder()
                // IO factory options
                .setForceNew(false)
//                .setMultiplex(true)
//
//                // low-level engine options
//                .setTransports(new String[] { Polling.NAME, WebSocket.NAME })
//                .setUpgrade(true)
//                .setRememberUpgrade(false)
//                .setPath("/stable/stocksUS/")
//                .setQuery("token = pk_dbf0fba01029463897491d37e16bb1e5")
//                .setQuery("symbols = AAPL")
//                .setExtraHeaders(singletonMap("Content-Type", singletonList("text/event-stream")))
//
//                // Manager options
//                .setReconnection(true)
//                .setReconnectionAttempts(Integer.MAX_VALUE)
//                .setReconnectionDelay(1_000)
//                .setReconnectionDelayMax(5_000)
//                .setRandomizationFactor(0.5)
//                .setTimeout(20_000)
//
//                // Socket options
//                .setAuth(null)
                .build();

        socket = IO.socket(uri, options);


        socket.on("socket", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("iex socket", "connected");
            }
        });

        socket.on("data", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0]);
            }
        });

        socket.on("error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0]);
                Log.d("iex socket", "connect error");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "connect error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        socket.connect();

    }

    private void refresh() {
        refreshLayout.setRefreshing(true);
        getAllStocks(false);
    }

    private void getAllStocks(final boolean loading) {
        if(loading)
            loadToast.show();
        JSONObject jsonObject = new JSONObject();
        String url = URLHelper.GET_STOCK_ORDER_INVESTED;
        Log.d("invest", url);
        if(getContext() != null)
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(loading)
                                loadToast.success();
                            refreshLayout.setRefreshing(false);
                            stocksList.clear();
                            try {
                                Log.d("invested stocks response", response.toString());
                                mTotalBalance.setText("$ " + new DecimalFormat("#,###.####").format(response.getDouble("total_balance")));
                                mStockBalance.setText("$ " + new DecimalFormat("#,###.####").format(response.getDouble("stock_balance")));
                                mTextStockProfit.setText("$ " + response.getString("stock_profit"));
                                mTextStockMargin.setText("$ " + response.getString("margin_balance"));
                                marketStatus.setText(response.getString("market_status"));
    
                                if(Double.parseDouble(response.getString("stock_profit"))>0)
                                    mTextStockProfit.setTextColor(GREEN);
                                else {
                                    mTextStockProfit.setTextColor(RED);
                                    Picasso.with(getActivity()).load(R.drawable.ic_down).into(icArrow);
                                }
    
                                if(response.getInt("stock_auto_sell") == 1){
                                    showStockAutoSellAlarm();
                                    SharedHelper.putKey(getContext(), "stock_auto_sell", response.getString("stock_auto_sell"));
                                }
    
                                if(response.getInt("stock_auto_sell") == 2){
                                    showAddFundAlarm();
                                }
    
                                if(getContext() != null)
                                SharedHelper.putKey(getContext(), "stock_balance", response.getString("stock_balance"));
    
                                JSONArray stocks = null;
                            
                                stocks = response.getJSONArray("stocks");
                                Log.d("stocks postfolio", stocks.toString());
                                if(stocks != null)
                                    for(int i = 0; i < stocks.length(); i ++) {
                                        try {
//                                            Log.d("stocksinvestitem", stocks.get(i).toString());
                                            stocksList.add(new StocksInfo((JSONObject) stocks.get(i)));
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
                            if(loading)
                                loadToast.error();
                            refreshLayout.setRefreshing(false);
                            // handle error
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });
    }

    private void showAddFundAlarm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getContext().getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Your stock balance is low. Please add funds")
                .setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setNegativeButton("No", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showStockAutoSellAlarm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getContext().getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Account liquidation.")
                .setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setNegativeButton("No", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void GoToTrade(int position) {
        StocksInfo stock = stocksList.get(position);
        Intent intent = new Intent(getActivity(), StocksTradingActivity.class);
        intent.putExtra("stock_symbol", stock.getSymbol());
        intent.putExtra("stock_name", stock.getName());
        intent.putExtra("stock_price", stock.getCurrentPrice());
        intent.putExtra("stock_shares", stock.getQty());
        intent.putExtra("stock_avg_price", stock.getAvgPrice());
        intent.putExtra("stock_equity", stock.getHolding());
        intent.putExtra("stock_today_change", stock.getChangePrice());
        intent.putExtra("stock_today_change_perc", stock.getChangePricePercent());
        intent.putExtra("type", "invest");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

//        getAllStocks();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        socket.off();
    }
}
