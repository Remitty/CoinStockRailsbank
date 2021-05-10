package com.wyre.trade.predict;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.HomeActivity;
import com.wyre.trade.model.PredictionModel;
import com.wyre.trade.predict.adapters.PredictPageAdapter;
import com.wyre.trade.stock.StocksActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PredictActivity extends AppCompatActivity {
    private String usdcBalance="0";
    TabLayout tab;
    ViewPager pager;
    TextView predictNow;
    PredictPageAdapter mAdapter;

    private BottomSheetDialog dialog;

    LoadToast loadToast;
    private ArrayList<PredictionModel> all = new ArrayList(), incoming = new ArrayList(), my_post = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        if(getSupportActionBar() != null){
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);

        View dialogView = getLayoutInflater().inflate(R.layout.predict_select_kind, null);
        TextView stocks = dialogView.findViewById(R.id.tv_select_stocks);
        TextView coins = dialogView.findViewById(R.id.tv_select_coins);
        stocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(PredictActivity.this, StocksActivity.class);
                intent.putExtra("predict", true);
                startActivity(intent);
            }
        });
        coins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(PredictActivity.this, PredictableListActivity.class);
                startActivity(intent);
            }
        });
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        tab = findViewById(R.id.tab);
        pager = findViewById(R.id.view_pager);
        predictNow = findViewById(R.id.btn_post_predict);

        mAdapter = new PredictPageAdapter(getSupportFragmentManager(), all, incoming, my_post);
        pager.setAdapter(mAdapter);
        tab.setupWithViewPager(pager);

        predictNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        getData();
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.REQUEST_PREDICT)
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

                                JSONArray all_temp = response.getJSONArray("new_predict");
                                JSONArray incoming_temp = response.getJSONArray("incoming");
                                JSONArray my_post_temp = response.getJSONArray("my_post");

                                for (int i = 0; i < all_temp.length(); i ++) {
                                    try {
                                        all.add(new PredictionModel(all_temp.getJSONObject(i)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                for (int i = 0; i < incoming_temp.length(); i ++) {
                                    try {
                                        incoming.add(new PredictionModel(incoming_temp.getJSONObject(i)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                for (int i = 0; i < my_post_temp.length(); i ++) {
                                    try {
                                        my_post.add(new PredictionModel(my_post_temp.getJSONObject(i)));
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
        startActivity(new Intent(PredictActivity.this, HomeActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
