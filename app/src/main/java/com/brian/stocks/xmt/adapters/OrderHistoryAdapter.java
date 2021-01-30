package com.brian.stocks.xmt.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.stocks.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    ArrayList<JSONObject> orders;

    public OrderHistoryAdapter(ArrayList orders) {
        this.orders = orders;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuantity, tvValue;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);
            tvQuantity = view.findViewById(R.id.quantity);
            tvValue = view.findViewById(R.id.value);
        }
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_orderbook_bids, parent, false);
        OrderHistoryAdapter.OrderViewHolder vh = new OrderHistoryAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderHistoryAdapter.OrderViewHolder holder, final int position) {
        JSONObject item = orders.get(position);

        holder.tvQuantity.setText(item.optString("quantity"));
        holder.tvValue.setText(item.optString("value"));

    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size(): 0;
    }

}

