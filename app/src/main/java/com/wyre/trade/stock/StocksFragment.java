package com.wyre.trade.stock;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.model.StocksInfo;
import com.wyre.trade.predict.AddPredictActivity;
import com.wyre.trade.stock.stocktrade.StocksTradingActivity;
import com.wyre.trade.stock.adapter.StocksAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StocksFragment extends Fragment {
    private LoadToast loadToast;
    private View rootView;
    private StocksAdapter mAdapter;
    private List<StocksInfo> stocksList = new ArrayList<>();
    private RecyclerView stocksListView;
    private Handler handler;
    private EditText mEditStockSearch;
    private String mSearch="";
    private SwipeRefreshLayout refreshLayout;
    private TextView marketStatus;
    JSONArray aggregates = new JSONArray();
    private boolean isPredict = false;

    public StocksFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static StocksFragment newInstance() {
        StocksFragment fragment = new StocksFragment();
        return fragment;
    }

    public static StocksFragment newInstance(boolean isPredict) {
        StocksFragment fragment = new StocksFragment();
        fragment.isPredict = isPredict;
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
        rootView = inflater.inflate(R.layout.fragment_stocks, container, false);

        stocksListView = (RecyclerView) rootView.findViewById(R.id.list_stocks_view);

        mEditStockSearch = rootView.findViewById(R.id.edit_stock_search);

        refreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        marketStatus = rootView.findViewById(R.id.market_status);

        mAdapter = new StocksAdapter(stocksList, true);
        stocksListView.setLayoutManager(new LinearLayoutManager(getContext()));
        stocksListView.setAdapter(mAdapter);

        mAdapter.setListener(new StocksAdapter.Listener() {
            @Override
            public void OnGoToTrade(int position) {
                if(!isPredict)
                    GoToTrade(position);
                else {
                    GoToAddPredict(position);
                }
            }

        });
        mEditStockSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getAllStocks(false);
                    aggregates=new JSONArray();
                    stocksList.clear();
//                    getAggregate();
                    return true;
                }
                return false;
            }
        });
//        mEditStockSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                mSearch = charSequence.toString().toUpperCase();
//                getAllStocks(false);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getAllStocks(true);
                    }
                }
        );
        getAllStocks(false);

//        getAggregate();

        return rootView;
    }

    private void getAggregate() {
        String url = URLHelper.GET_ALL_STOCKS_AGGREGATE;
        if(!mSearch.equalsIgnoreCase(""))
            url = url + "?search="+mSearch;
        Log.d("stock aggregate url", url);
        AndroidNetworking.get(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("aggregate response", "" + response);
                        try {
                            aggregates = response.getJSONArray("aggregates");

                            if(stocksList.size() > 0 && aggregates.length() == stocksList.size()) {
                                for (int i = 0; i < aggregates.length(); i++) {
//                                    StocksInfo stock = stocksList.get(i);
//                                    stock.setStockAggregate(aggregates.optJSONArray(i));
                                }

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        // handle error
//                        Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
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
        intent.putExtra("type", "stock");
        startActivity(intent);
    }

    private void GoToAddPredict(int position) {
        StocksInfo stock = stocksList.get(position);
        Intent intent = new Intent(getActivity(), AddPredictActivity.class);
        intent.putExtra("symbol", stock.getSymbol());
        intent.putExtra("name", stock.getName());
        intent.putExtra("price", stock.getCurrentPrice());
        startActivity(intent);
    }

    private void getAllStocks(final boolean refresh) {
        if(refresh)
            refreshLayout.setRefreshing(true);
        else
            loadToast.show();
        JSONObject jsonObject = new JSONObject();
        mSearch = mEditStockSearch.getText().toString();
        String url = URLHelper.GET_ALL_STOCKS_DAILY;
        if(!mSearch.equalsIgnoreCase(""))
            url = url + "?search="+mSearch;
        Log.d("stockurl", url);
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
                        Log.d("daily response", "" + response);
                        if(refresh)
                            refreshLayout.setRefreshing(false);
                        else
                            loadToast.success();

                        stocksList.clear();

                        try {

                            marketStatus.setText(response.getString("market_status"));

                            JSONArray stocks = response.getJSONArray("stocks");
                            if(stocks != null)
                            for(int i = 0; i < stocks.length(); i ++) {
                                try {

//                                    StocksInfo stock = new StocksInfo((JSONObject) stocks.get(i));
                                    stocksList.add(new StocksInfo((JSONObject) stocks.get(i)));
//                                    if(aggregates.length() > 0 && aggregates.length() == stocks.length())
//                                    aggregates = response.getJSONArray("aggregates");
//
//                                    stock.setStockAggregate(aggregates.optJSONArray(i));
//                                    stocksList.add(stock);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            if(getContext() != null)
                                SharedHelper.putKey(getContext(), "stock_balance", response.getString("stock_balance"));

                            if(response.getInt("stock_auto_sell") == 1){
                                showStockAutoSellAlarm();
                                SharedHelper.putKey(getContext(), "stock_auto_sell", response.getString("stock_auto_sell"));
                            }

                            if(response.getInt("stock_auto_sell") == 2){
                                showAddFundAlarm();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }

//                        if(!refresh)
//                            getAggregate();
                    }

                    @Override
                    public void onError(ANError error) {
                        if(refresh)
                            refreshLayout.setRefreshing(false);
                        else
                            loadToast.error();

                        // handle error
                        Toast.makeText(getContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
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

    @Override
    public void onResume() {
        super.onResume();

//        getAllStocks();
//        handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getAllStocksUpdae();
//                handler.postDelayed(this, 120000);
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
