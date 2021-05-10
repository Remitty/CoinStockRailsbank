package com.wyre.trade.usdc.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyre.trade.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransferCoinHistoryAdapter extends RecyclerView.Adapter<TransferCoinHistoryAdapter.OrderViewHolder> {

    ArrayList<JSONObject> history;

    public TransferCoinHistoryAdapter(ArrayList history) {
        this.history = history;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvUser, tvSendAmount, tvItemDate;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);

            tvUser = view.findViewById(R.id.user);
            tvSendAmount = view.findViewById(R.id.amount);
            tvItemDate = view.findViewById(R.id.date);

        }
    }

    @NonNull
    @Override
    public TransferCoinHistoryAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_transfer_history, parent, false);
        TransferCoinHistoryAdapter.OrderViewHolder vh = new TransferCoinHistoryAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final TransferCoinHistoryAdapter.OrderViewHolder holder, final int position) {
        JSONObject item = history.get(position);

        try {
            holder.tvUser.setText(item.getString("send_to"));
            holder.tvSendAmount.setText(item.getString("amount") + "USDC");
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
