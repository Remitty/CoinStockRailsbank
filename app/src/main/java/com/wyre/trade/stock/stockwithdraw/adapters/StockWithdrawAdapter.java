package com.wyre.trade.stock.stockwithdraw.adapters;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyre.trade.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class StockWithdrawAdapter extends RecyclerView.Adapter<StockWithdrawAdapter.OrderViewHolder> {

    ArrayList<JSONObject> history;

    public StockWithdrawAdapter(ArrayList history) {
        this.history = history;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemPrice, tvItemStatus, tvItemDate, tvItemReceived;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);

            tvItemPrice = view.findViewById(R.id.item_price);
            tvItemReceived = view.findViewById(R.id.item_received);
            tvItemStatus = view.findViewById(R.id.item_status);
            tvItemDate = view.findViewById(R.id.item_date);

        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_withdraw_history, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        JSONObject item = history.get(position);

        holder.tvItemPrice.setText(item.optString("req_amount"));
        holder.tvItemReceived.setText(item.optString("payable_amount"));
        holder.tvItemStatus.setText(item.optString("status"));
        holder.tvItemDate.setText(item.optString("updated_at").substring(0, 10));

    }

    @Override
    public int getItemCount() {
        return history != null ? history.size(): 0;
    }

}
