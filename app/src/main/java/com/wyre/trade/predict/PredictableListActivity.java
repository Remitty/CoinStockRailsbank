package com.wyre.trade.predict;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
import com.wyre.trade.model.PredictAsset;
import com.wyre.trade.predict.adapters.PredictableStockAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PredictableListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PredictableStockAdapter mAdapter;
    ArrayList<PredictAsset> data = new ArrayList<PredictAsset>();
    private LoadToast loadToast;
    private String usdcBalance;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictable_list);

        if(getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        mContext = getBaseContext();

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new PredictableStockAdapter(mContext, data);
        recyclerView.setLayoutManager(new LinearLayoutManager(PredictableListActivity.this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new PredictableStockAdapter.Listener() {
            @Override
            public void onSelect(int position) {
                Intent intent = new Intent(PredictableListActivity.this, AddPredictActivity.class);
                PredictAsset item = data.get(position);
                intent.putExtra("symbol", item.getSymbol());
                intent.putExtra("id", item.getId());
                intent.putExtra("name", item.getName());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("usdc_balance", usdcBalance);
                startActivity(intent);
            }
        });

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
                                JSONArray data1 = response.getJSONArray("data");
                                for(int i = 0; i < data1.length(); i ++) {
                                    PredictAsset temp = new PredictAsset(data1.getJSONObject(i));
                                    data.add(temp);
                                }
                                mAdapter.notifyDataSetChanged();
                                usdcBalance = response.getString("usdc_balance");


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
