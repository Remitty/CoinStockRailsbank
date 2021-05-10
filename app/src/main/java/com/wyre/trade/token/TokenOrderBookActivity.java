package com.wyre.trade.token;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
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
import com.wyre.trade.token.adapters.OrderAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TokenOrderBookActivity extends AppCompatActivity {

    TextView noHistory;
    LoadToast loadToast;

    RecyclerView orderBookView;
    OrderAdapter orderAdapter;
    private ArrayList<JSONObject> ordersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_order_book);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        loadToast = new LoadToast(this);

        noHistory = findViewById(R.id.empty_text);

        orderBookView = findViewById(R.id.orderbook);
        orderAdapter = new OrderAdapter(ordersList);
        orderBookView.setLayoutManager(new LinearLayoutManager(this));
        orderBookView.setAdapter(orderAdapter);
        orderAdapter.setListener(new OrderAdapter.Listener() {
            @Override
            public void cancelOrder(final int position) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TokenOrderBookActivity.this);
                alert.setIcon(R.mipmap.ic_launcher_round)
                        .setTitle("Confirm cancel")
                        .setMessage("Are you sure you want to cancel this order?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submitCancel(position);
                            }
                        })
                        .show();

            }
        });


        getData();

    }

    private void getData() {

        loadToast.show();
        AndroidNetworking.get(URLHelper.COIN_TRADE_ORDERS)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(this,"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToast.hide();
                        Log.d("xmt trade history response", "" + response.toString());
                        try {
                            JSONArray ordersHistory = response.getJSONArray("history");

                            for (int i = 0; i < ordersHistory.length(); i++) {
                                try {
                                    ordersList.add(ordersHistory.getJSONObject(i));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(ordersList.size() > 0)
                                noHistory.setVisibility(View.GONE);

                            orderAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(ANError error) {
                        // handle error
                        loadToast.hide();
                        Toast.makeText(getBaseContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void submitCancel(final int position) {
        JSONObject jsonObject = new JSONObject();
        String orderid = null;
        try {
            orderid = ordersList.get(position).getString("id");
            jsonObject.put("orderid", orderid);
            Log.d("cancel xmt orderid", orderid);
            if (getBaseContext() != null) {
                loadToast.show();
                AndroidNetworking.post(URLHelper.COIN_TRADE_CANCEL)
                        .addHeaders("Content-Type", "application/json")
                        .addHeaders("accept", "application/json")
                        .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(), "access_token"))
                        .addJSONObjectBody(jsonObject)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadToast.hide();
                                Log.d("coin post response", "" + response.toString());

                                if (response.optBoolean("success")) {
                                    Toast.makeText(getBaseContext(), "Order Cancelled.", Toast.LENGTH_SHORT).show();
                                    ordersList.remove(position);
                                    orderAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onError(ANError error) {
                                // handle error
                                loadToast.hide();
                                Toast.makeText(getBaseContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                                Log.d("errorpost", "" + error.getErrorBody() + " responde: " + error.getResponse());
                            }
                        });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
