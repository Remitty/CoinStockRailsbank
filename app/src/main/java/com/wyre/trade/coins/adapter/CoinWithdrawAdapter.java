package com.wyre.trade.coins.adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyre.trade.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CoinWithdrawAdapter extends RecyclerView.Adapter<CoinWithdrawAdapter.OrderViewHolder> {

    ArrayList<JSONObject> history;

    public CoinWithdrawAdapter(ArrayList history) {
        this.history = history;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemAsset, tvItemPrice, tvItemStatus, tvItemDate, tvItemAddress;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);

            tvItemAsset = view.findViewById(R.id.item_asset);
            tvItemPrice = view.findViewById(R.id.item_price);
            tvItemStatus = view.findViewById(R.id.item_status);
            tvItemDate = view.findViewById(R.id.item_date);
            tvItemAddress = view.findViewById(R.id.item_address);

        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_withdraw_history, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        JSONObject item = history.get(position);

        holder.tvItemAsset.setText(item.optString("currency"));
        holder.tvItemPrice.setText(new DecimalFormat("#,###.######").format(item.optDouble("amount")));
        holder.tvItemStatus.setText(item.optString("status_text"));
        holder.tvItemAddress.setText(item.optString("address"));
        try {
            holder.tvItemDate.setText(item.getString("updated_at").substring(0, 10));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return history != null ? history.size(): 0;
    }

}
