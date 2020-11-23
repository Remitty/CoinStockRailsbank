package com.brian.stocks.stock.stockorder;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.StocksInfo;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockTradeHistoryFragment extends Fragment {
    private LoadToast loadToast;
    private View rootView;
    private StockOrderAdapter mAdapter;
    private List<StocksInfo> stocksList = new ArrayList<>();
    private RecyclerView stocksListView;

    public StockTradeHistoryFragment() {
        // Required empty public constructor
    }

    public static StockTradeHistoryFragment newInstance() {
        StockTradeHistoryFragment fragment = new StockTradeHistoryFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_stock_trade_history, container, false);
        stocksListView = (RecyclerView) rootView.findViewById(R.id.list_stocks_view);

        mAdapter = new StockOrderAdapter(stocksList, false);
        stocksListView.setLayoutManager(new LinearLayoutManager(getContext()));
        stocksListView.setAdapter(mAdapter);
        mAdapter.setListener(new StockOrderAdapter.Listener() {

            @Override
            public void OnGoToOrder(int position) {

            }
        });
        getAllStocks();
        return rootView;
    }

    private void getAllStocks() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        String url = URLHelper.GET_STOCK_ORDER;
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
                            Log.d("response", "" + response);
                            loadToast.success();
                            stocksList.clear();

                            JSONArray stocks = response.optJSONArray("stocks");
                            for(int i = 0; i < stocks.length(); i ++) {
                                try {
                                    Log.d("stocksitem", stocks.get(i).toString());
                                    stocksList.add(new StocksInfo((JSONObject) stocks.get(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            mAdapter.notifyDataSetChanged();
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
    public void onResume() {
        super.onResume();

//        getAllStocks();
    }
}
