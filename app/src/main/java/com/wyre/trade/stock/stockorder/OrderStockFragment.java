package com.wyre.trade.stock.stockorder;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.stock.stocktrade.StocksOrderActivity;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.StocksOrderModel;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderStockFragment extends Fragment {
    private LoadToast loadToast;
    private View rootView;
    private StockOrderAdapter mAdapter;
    private List<StocksOrderModel> stocksList = new ArrayList<>();
    private RecyclerView stocksListView;
    LinearLayout emptyLayout;

    public OrderStockFragment() {
        // Required empty public constructor
    }

    public static OrderStockFragment newInstance() {
        OrderStockFragment fragment = new OrderStockFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_order_stock, container, false);
        stocksListView = (RecyclerView) rootView.findViewById(R.id.list_stocks_view);
        emptyLayout = rootView.findViewById(R.id.empty_layout);

        mAdapter = new StockOrderAdapter(stocksList, false);

        stocksListView.setLayoutManager(new LinearLayoutManager(getContext()));
        stocksListView.setAdapter(mAdapter);

        mAdapter.setListener(new StockOrderAdapter.Listener() {

            @Override
            public void OnGoToOrder(int position) {
                GoToOrder(position);
            }
        });

        getAllStocks();
        return rootView;
    }

    private void getAllStocks() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        String url = URLHelper.GET_STOCK_ORDER_PENDING;
        Log.d("order", url);
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
                            loadToast.success();
                            stocksList.clear();

                            JSONArray stocks = response.optJSONArray("stocks");
                            for(int i = 0; i < stocks.length(); i ++) {
                                try {
                                    Log.d("stocksorderitem", stocks.get(i).toString());
                                    stocksList.add(new StocksOrderModel((JSONObject) stocks.get(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(getContext() != null)
                            SharedHelper.putKey(getContext(), "stock_balance", response.optString("stock_balance"));
                            mAdapter.notifyDataSetChanged();
                            if(stocksList.size() > 0)
                                emptyLayout.setVisibility(View.GONE);
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

    private void GoToOrder(int position) {
        StocksOrderModel stock = stocksList.get(position);
        Intent intent = new Intent(getActivity(), StocksOrderActivity.class);
        intent.putExtra("stock_name", stock.getStockSymbol());
        intent.putExtra("stock_limit_price", stock.getStockOrderLimitPrice());
        intent.putExtra("stock_order_shares", stock.getStocksOrderShares());
        intent.putExtra("stock_est_cost", stock.getStockOrderCost());
        intent.putExtra("stock_order_id", stock.getStockOrderID());
        intent.putExtra("stock_order_side", stock.getStockOrderSide());
        intent.putExtra("stock_order_type", stock.getStockOrderType());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

//        getAllStocks();
    }
}
