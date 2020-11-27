package com.brian.stocks.predict;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.predict.adapters.PredictableStockAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PredictableListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PredictableStockAdapter mAdapter;
    JSONArray data = new JSONArray();
    private LoadToast loadToast;
    private String usdcBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictable_list);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Select to predict");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadToast = new LoadToast(this);

        recyclerView = findViewById(R.id.recyclerView);

        getPredictableItemList();
    }

    private void getPredictableItemList() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_PREDICTABLE_LIST)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                            try {
                                data = response.getJSONArray("data");
                                usdcBalance = response.getString("usdc_balance");
                                mAdapter = new PredictableStockAdapter(data);
                                recyclerView.setLayoutManager(new LinearLayoutManager(PredictableListActivity.this));
                                recyclerView.setAdapter(mAdapter);
                                mAdapter.setListener(new PredictableStockAdapter.Listener() {
                                    @Override
                                    public void onSelect(int position) {
                                        try {
                                            Intent intent = new Intent(PredictableListActivity.this, AddPredictActivity.class);
                                            intent.putExtra("symbol", data.getJSONObject(position).getString("symbol"));
                                            intent.putExtra("symbol_id", data.getJSONObject(position).getString("id"));
                                            intent.putExtra("name", data.getJSONObject(position).getString("name"));
                                            intent.putExtra("price", data.getJSONObject(position).getString("price"));
                                            intent.putExtra("usdc_balance", usdcBalance);
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (NullPointerException e) {
                                e.printStackTrace();
                            }
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
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
