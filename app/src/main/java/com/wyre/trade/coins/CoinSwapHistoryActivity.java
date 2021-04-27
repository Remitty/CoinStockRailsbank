package com.wyre.trade.coins;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.adapters.CoinConversionAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CoinSwapHistoryActivity extends AppCompatActivity {
    private LoadToast loadToast;

    CoinConversionAdapter conversionAdapter;
    private RecyclerView conversionView;
    private ArrayList<JSONObject> conversionList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_swap_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        loadToast = new LoadToast(this);

        conversionView = findViewById(R.id.list_conversion_view);
        conversionAdapter = new CoinConversionAdapter(conversionList);
        conversionView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        conversionView.setAdapter(conversionAdapter);

        loadHistory();
    }

    private void loadHistory() {
        loadToast.show();

        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.GET_COIN_EXCHANGE_LIST)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
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
                            Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
