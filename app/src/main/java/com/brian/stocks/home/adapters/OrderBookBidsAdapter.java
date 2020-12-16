package com.brian.stocks.home.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.stocks.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderBookBidsAdapter extends RecyclerView.Adapter<OrderBookBidsAdapter.OrderViewHolder> {

    ArrayList<JSONObject> orders;

    private DecimalFormat df = new DecimalFormat("#.########");
    public OrderBookBidsAdapter(ArrayList orders) {
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
    public OrderBookBidsAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_orderbook_bids, parent, false);
        OrderBookBidsAdapter.OrderViewHolder vh = new OrderBookBidsAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderBookBidsAdapter.OrderViewHolder holder, final int position) {
        JSONObject item = orders.get(position);

        holder.tvQuantity.setText(df.format(Float.parseFloat(item.optString("quantity"))));
        holder.tvValue.setText(df.format(Float.parseFloat(item.optString("price"))));

    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size(): 0;
    }

}

