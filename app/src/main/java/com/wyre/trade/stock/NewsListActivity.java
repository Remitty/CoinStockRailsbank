package com.wyre.trade.stock;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.stock.adapter.NewsAdapter;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.NewsInfo;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity {
    LoadToast loadToast;
    private ArrayList<NewsInfo> newsList = new ArrayList<>();
    private NewsAdapter mAdapter;
    private RecyclerView mNewsListView;
    private String mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

//        mSearch = getIntent().getStringExtra("symbol");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        // getSupportActionBar().setTitle(mSearch);

        mNewsListView = findViewById(R.id.news_list);

        mAdapter = new NewsAdapter(getBaseContext(), newsList);
        mNewsListView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mNewsListView.setAdapter(mAdapter);

        getAllNews();
    }

    private void getAllNews() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        String url = URLHelper.GET_STOCK_NEWS;
//        if(!mSearch.equalsIgnoreCase(""))
//            url = url + "?search="+mSearch;
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

                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
