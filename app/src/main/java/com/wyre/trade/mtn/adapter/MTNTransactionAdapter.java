package com.wyre.trade.mtn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wyre.trade.R;
import com.wyre.trade.model.MTNTransactionItem;

import java.util.ArrayList;

public class MTNTransactionAdapter extends RecyclerView.Adapter<MTNTransactionAdapter.CustomerViewHolder> {
    ArrayList<MTNTransactionItem> data;

    public MTNTransactionAdapter(ArrayList<MTNTransactionItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MTNTransactionAdapter.CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mtn_transaction, parent, false);
        MTNTransactionAdapter.CustomerViewHolder vh = new MTNTransactionAdapter.CustomerViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MTNTransactionAdapter.CustomerViewHolder holder, final int position) {
        MTNTransactionItem item = data.get(position);

        holder.mtnTo.setText(item.getContact());
        holder.mtnAmount.setText(item.getAmount() + item.getCurrency());
        holder.mtnType.setText(item.getType());
        holder.mtnStatus.setText(item.getStatus());
        holder.mtnDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return data!= null? data.size(): 0;
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView mtnTo, mtnAmount, mtnType, mtnStatus, mtnDate;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            mtnTo = itemView.findViewById(R.id.mtn_to);
            mtnAmount = itemView.findViewById(R.id.mtn_amount);
            mtnType = itemView.findViewById(R.id.mtn_type);
            mtnStatus = itemView.findViewById(R.id.mtn_status);
            mtnDate = itemView.findViewById(R.id.mtn_date);
        }
    }
}
