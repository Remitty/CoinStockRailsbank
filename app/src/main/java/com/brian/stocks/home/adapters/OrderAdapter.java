package com.brian.stocks.home.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    ArrayList<JSONObject> orders;
    Context thiscontext;
    private Button mBtnTrade;
    private DecimalFormat df = new DecimalFormat("#.########");
    public OrderAdapter(ArrayList orders) {
        this.orders = orders;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvType, tvQuantity, tvValue, tvDate;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);
            tvType = view.findViewById(R.id.type);
            tvQuantity = view.findViewById(R.id.quantity);
            mBtnTrade = view.findViewById(R.id.btn_cancel);
            tvValue = view.findViewById(R.id.value);
            tvDate = view.findViewById(R.id.date);
            thiscontext = view.getContext();
        }
    }

    private void cancelOrder(int orderid) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("orderid", orderid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (thiscontext != null)
            AndroidNetworking.post(URLHelper.COIN_REALEXCHANGE_CANCEL)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(thiscontext,"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("coin post response", "" + response.toString());

                            if (!response.has("success")) {
                                Toast.makeText(thiscontext, "Order Cancelled.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error

                            Log.d("errorpost", "" + error.getMessage()+" responde: "+error.getResponse());
                        }
                    });
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_order, parent, false);
        OrderAdapter.OrderViewHolder vh = new OrderAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderAdapter.OrderViewHolder holder, final int position) {
        JSONObject item = orders.get(position);


        holder.tvType.setText(item.optString("type"));
        holder.tvQuantity.setText(df.format(Float.parseFloat(item.optString("quantity"))));
        holder.tvValue.setText(df.format(Float.parseFloat(item.optString("price"))));
        holder.tvDate.setText(item.optString("created_at"));

        mBtnTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrder(orders.get(position).optInt("id"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size(): 0;
    }

}

