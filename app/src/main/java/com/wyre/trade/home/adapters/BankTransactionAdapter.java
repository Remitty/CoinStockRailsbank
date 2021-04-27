package com.wyre.trade.home.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.BankTransaction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BankTransactionAdapter extends RecyclerView.Adapter<BankTransactionAdapter.OrderViewHolder> {
    private List<BankTransaction> arrItems;
    private Listener listener;

    public BankTransactionAdapter(List<BankTransaction> data){
        this.arrItems = data;
    };

    @NonNull
    @Override
    public BankTransactionAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_transaction_item, parent, false);
        BankTransactionAdapter.OrderViewHolder vh = new BankTransactionAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BankTransactionAdapter.OrderViewHolder holder, final int i) {
        BankTransaction item = arrItems.get(i);

        holder.mType.setText(item.getType());
        holder.mGetter.setText(item.getGetterName());
        holder.mAmount.setText(item.getAmount() + item.getCurrency());
        holder.mStatus.setText(item.getStatus());
        holder.mDate.setText(item.getDate());

        if(item.getGetterName().equals(""))
            holder.mGetterLayout.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView mType, mGetter, mAmount, mStatus, mDate;
        LinearLayout mGetterLayout;
        public OrderViewHolder(View view) {
            super(view);

            mType = view.findViewById(R.id.transaction_type);
            mGetter = view.findViewById(R.id.transaction_getter);
            mAmount = view.findViewById(R.id.transaction_amount);
            mStatus = view.findViewById(R.id.transaction_status);
            mDate = view.findViewById(R.id.transaction_date);

            mGetterLayout = view.findViewById(R.id.layout_getter);
        }
    }

    public void setListener(BankTransactionAdapter.Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        /**
         * @param position
         */
        void onClick(int position);
    }

}