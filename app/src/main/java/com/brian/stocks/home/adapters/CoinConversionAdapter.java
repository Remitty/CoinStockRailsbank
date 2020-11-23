package com.brian.stocks.home.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brian.stocks.R;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CoinConversionAdapter extends RecyclerView.Adapter<CoinConversionAdapter.OrderViewHolder> {

    ArrayList<JSONObject> history;

    public CoinConversionAdapter(ArrayList history) {
        this.history = history;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvSendAsset, tvGetAsset, tvSendAmount, tvItemDate;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);

            tvSendAsset = view.findViewById(R.id.send_asset);
            tvGetAsset = view.findViewById(R.id.get_asset);
            tvSendAmount = view.findViewById(R.id.send_amount);
            tvItemDate = view.findViewById(R.id.item_date);

        }
    }

    @NonNull
    @Override
    public CoinConversionAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_conversion_history, parent, false);
        CoinConversionAdapter.OrderViewHolder vh = new CoinConversionAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CoinConversionAdapter.OrderViewHolder holder, final int position) {
        JSONObject item = history.get(position);

        holder.tvSendAsset.setText(item.optString("from"));
        holder.tvGetAsset.setText(item.optString("to"));
        holder.tvSendAmount.setText(item.optString("amount"));
        holder.tvItemDate.setText(item.optString("updated_at").substring(0, 10));

    }

    @Override
    public int getItemCount() {
        return history != null ? history.size(): 0;
    }

}

