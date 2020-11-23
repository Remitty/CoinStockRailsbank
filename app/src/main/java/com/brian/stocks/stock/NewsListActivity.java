package com.brian.stocks.stock;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.stock.NewsActivity;
import com.brian.stocks.stock.adapter.NewsAdapter;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.NewsInfo;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsListActivity extends AppCompatActivity {
    LoadToast loadToast;
    private List<NewsInfo> newsList = new ArrayList<>();
    private NewsAdapter mAdapter;
    private RecyclerView mNewsListView;
    private String mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        loadToast = new LoadToast(this);
        mSearch = getIntent().getStringExtra("symbol");

        Toolbar toolbar = findViewById(R.id.stocks_trade_toolbar);
        toolbar.setTitle(mSearch);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNewsListView = findViewById(R.id.news_list);

        mAdapter = new NewsAdapter(newsList, true);
        mAdapter.setListener(new NewsAdapter.Listener() {
            @Override
            public void OnGoToNewsDetail(int position) {
                GoToNewsDetail(position);
            }
        });
        mNewsListView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mNewsListView.setAdapter(mAdapter);

        getAllNews();
    }

    private void GoToNewsDetail(int position) {
        NewsInfo news = newsList.get(position);

        Intent intent = new Intent(this, NewsActivity.class);
        intent.putExtra("title", news.getNewsTitle());
        intent.putExtra("image", news.getImageURL());
        intent.putExtra("summary", news.getSummary());
        intent.putExtra("url", news.getURL());
        intent.putExtra("date", news.getDate());
        startActivity(intent);

    }

    private void getAllNews() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        String url = URLHelper.GET_STOCK_NEWS;
        if(!mSearch.equalsIgnoreCase(""))
            url = url + "?search="+mSearch;
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
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
