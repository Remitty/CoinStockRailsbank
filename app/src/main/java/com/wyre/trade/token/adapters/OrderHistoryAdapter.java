package com.wyre.trade.token.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wyre.trade.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    ArrayList<JSONObject> orders;

    public OrderHistoryAdapter(ArrayList orders) {
        this.orders = orders;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuantity, tvValue, tvType, tvDate, tvPair;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);
            tvQuantity = view.findViewById(R.id.quantity);
            tvValue = view.findViewById(R.id.value);
            tvType = view.findViewById(R.id.type);
            tvDate = view.findViewById(R.id.date);
            tvPair = view.findViewById(R.id.pair);
        }
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_order_history, parent, false);
        OrderHistoryAdapter.OrderViewHolder vh = new OrderHistoryAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderHistoryAdapter.OrderViewHolder holder, final int position) {
        JSONObject item = orders.get(position);

        holder.tvQuantity.setText(new DecimalFormat("#,###.######").format(item.optDouble("quantity")));
        holder.tvValue.setText(new DecimalFormat("#,###.######").format(item.optDouble("price")));
        holder.tvPair.setText(item.optString("pair"));
        holder.tvType.setText(item.optString("type"));
        holder.tvDate.setText(item.optString("updated_at").substring(0, 10));

    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size(): 0;
    }

}

