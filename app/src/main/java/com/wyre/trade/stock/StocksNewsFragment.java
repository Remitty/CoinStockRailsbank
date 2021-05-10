package com.wyre.trade.stock;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.stock.adapter.NewsAdapter;
import com.wyre.trade.stock.adapter.NewsStockAdapter;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.NewsInfo;
import com.wyre.trade.model.StocksOrderModel;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StocksNewsFragment extends Fragment {
    private RecyclerView mNewsListView;
    private LoadToast loadToast;
    private String mSearch="";
    private EditText mEditStockSearch;
    private List<StocksOrderModel> stocksList = new ArrayList<>();
    private ArrayList<NewsInfo> newsList = new ArrayList<>();
    private NewsStockAdapter mAdapter;
    private NewsAdapter mAdapter1;

    public StocksNewsFragment() {
        // Required empty public constructor
    }
    public static StocksNewsFragment newInstance() {
        StocksNewsFragment fragment = new StocksNewsFragment();
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

        View view = inflater.inflate(R.layout.fragment_stocks_news, container, false);
        mNewsListView = view.findViewById(R.id.news_expandableListView);

        mEditStockSearch = view.findViewById(R.id.edit_stock_search);

        mEditStockSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSearch = charSequence.toString().toUpperCase();
                if(!mSearch.equalsIgnoreCase(""))
                    getStockNews();
                else getAllStocksName();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mAdapter = new NewsStockAdapter(stocksList, true);
        mAdapter.setListener(new NewsStockAdapter.Listener() {
            @Override
            public void OnGoToNewsList(int position) {
                GoToNewsList(position);
            }
        });
        mNewsListView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter1 = new NewsAdapter(getContext(), newsList);
        mAdapter1.setListener(new NewsAdapter.Listener() {
            @Override
            public void OnGoToNewsDetail(int position) {
                GoToNewsDetail(position);
            }
        });

        getAllStocksName();

        return view;
    }

    private void GoToNewsDetail(int position) {
        NewsInfo news = newsList.get(position);

        Intent intent = new Intent(getActivity(), NewsActivity.class);
        intent.putExtra("title", news.getNewsTitle());
        intent.putExtra("image", news.getImageURL());
        intent.putExtra("summary", news.getSummary());
        intent.putExtra("url", news.getURL());
        intent.putExtra("date", news.getDate());
        startActivity(intent);
    }

    private void GoToNewsList(int position) {
//        Intent intent = new Intent(getActivity(), NewsListActivity.class);
//        intent.putExtra("symbol", stocksList.get(position).getStocksTickerOther());
//        startActivity(intent);
    }

    private void getStockNews() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        String url = URLHelper.GET_STOCK_NEWS;
        if(!mSearch.equalsIgnoreCase(""))
            url = url + "?search="+mSearch;
        Log.d("stocknews", url);
        AndroidNetworking.get(url)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("accept", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("stocknews", "" + response);
                    loadToast.success();
                    newsList.clear();

                    JSONArray stocks = response.optJSONArray("news");
                    for(int i = 0; i < stocks.length(); i ++) {
                        try {
                            newsList.add(new NewsInfo((JSONObject) stocks.get(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    mNewsListView.setAdapter(mAdapter1);
                    mAdapter1.notifyDataSetChanged();
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

    private void getAllStocksName() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        AndroidNetworking.get(URLHelper.GET_ALL_STOCKS)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("accept", "application/json")
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
                            Log.d("newssitem", stocks.get(i).toString());
                            stocksList.add(new StocksOrderModel((JSONObject) stocks.get(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mNewsListView.setAdapter(mAdapter);
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
}
