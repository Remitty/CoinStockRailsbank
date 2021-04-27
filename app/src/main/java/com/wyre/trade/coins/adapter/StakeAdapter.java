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

import java.util.ArrayList;

public class StakeAdapter extends RecyclerView.Adapter<StakeAdapter.OrderViewHolder> {

    ArrayList<JSONObject> history;

    public StakeAdapter(ArrayList history) {
        this.history = history;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemAsset, tvItemAmount, tvItemType, tvItemDate, tvItemStatus;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);

            tvItemAsset = view.findViewById(R.id.item_asset);
            tvItemAmount = view.findViewById(R.id.item_amount);
            tvItemType = view.findViewById(R.id.item_type);
            tvItemDate = view.findViewById(R.id.item_date);

        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stake_history, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        JSONObject item = history.get(position);

        holder.tvItemAsset.setText(item.optString("asset"));
        holder.tvItemAmount.setText(item.optString("amount"));
        holder.tvItemType.setText(item.optString("type"));
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
