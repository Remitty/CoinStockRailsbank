package com.wyre.trade.mtn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.wyre.trade.model.MTNTransactionItem;
import com.wyre.trade.mtn.adapter.MTNTransactionAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MtnTransactionActivity extends AppCompatActivity {
    TextView emptyText;
    RecyclerView mtnTransactionView;
    MTNTransactionAdapter mtnTransactionAdapter;
    ArrayList<MTNTransactionItem> transactions= new ArrayList<>();
    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtn_transaction);

        if(getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        emptyText = findViewById(R.id.mtn_no_transaction);

        mtnTransactionView = findViewById(R.id.mtn_transaction_view);
        mtnTransactionView.setLayoutManager(new LinearLayoutManager(this));
        mtnTransactionAdapter = new MTNTransactionAdapter(transactions);
        mtnTransactionView.setAdapter(mtnTransactionAdapter);

        getData();
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_MTN_TRANSACTION)
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
                        if(response.optBoolean("success")) {
                            transactions.clear();
                            try {
                                JSONArray data = response.getJSONArray("transactions");
                                for (int i = 0; i < data.length(); i ++) {
                                    MTNTransactionItem item = new MTNTransactionItem();
                                    item.setData(data.getJSONObject(i));
                                    transactions.add(item);
                                }
                                mtnTransactionAdapter.notifyDataSetChanged();
                                if(transactions.size() > 0) {
                                    emptyText.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
