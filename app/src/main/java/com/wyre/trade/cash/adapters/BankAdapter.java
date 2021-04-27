package com.wyre.trade.cash.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.BankInfo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.OrderViewHolder> {
    private List<BankInfo> arrItems;
    private Listener listener;

    public BankAdapter(List<BankInfo> data){
        this.arrItems = data;
    };

    @NonNull
    @Override
    public BankAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_item, parent, false);
        BankAdapter.OrderViewHolder vh = new BankAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BankAdapter.OrderViewHolder holder, final int i) {
        BankInfo item = arrItems.get(i);

        holder.mBankAlias.setText(item.getBankAlias());
        holder.mBankCurrency.setText(item.getCurrency());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onGotoSend(i);
            }
        });
        holder.mButDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelBank(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView mBankAlias, mBankCurrency;
        ImageButton mButDel;
        public OrderViewHolder(View view) {
            super(view);

            mBankAlias = view.findViewById(R.id.bank_alias);
            mBankCurrency = view.findViewById(R.id.bank_currency);
            mButDel = view.findViewById(R.id.btn_bank_delete);
        }
    }

    public void setListener(BankAdapter.Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        /**
         * @param position
         */
        void onDelBank(int position);
        void onGotoSend(int position);
    }

}