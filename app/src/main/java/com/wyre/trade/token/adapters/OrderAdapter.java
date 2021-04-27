package com.wyre.trade.token.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
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


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    ArrayList<JSONObject> orders;
    Context thiscontext;
    Listener listener;
    private TextView mbtnCancel;
    private DecimalFormat df = new DecimalFormat("#.########");
    public OrderAdapter(ArrayList orders) {
        this.orders = orders;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvType, tvQuantity, tvValue, tvDate, tvSymbol;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);
            tvType = view.findViewById(R.id.type);
            tvSymbol = view.findViewById(R.id.symbol);
            tvQuantity = view.findViewById(R.id.quantity);
            mbtnCancel = view.findViewById(R.id.btn_cancel);
            tvValue = view.findViewById(R.id.value);
            tvDate = view.findViewById(R.id.date);
            thiscontext = view.getContext();
        }
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_order, parent, false);
        OrderAdapter.OrderViewHolder vh = new OrderAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderAdapter.OrderViewHolder holder, final int position) {
        JSONObject item = orders.get(position);


        holder.tvType.setText(item.optString("type"));
        holder.tvSymbol.setText(item.optString("pair"));
        holder.tvQuantity.setText(df.format(Float.parseFloat(item.optString("quantity"))));
        holder.tvValue.setText(df.format(Float.parseFloat(item.optString("price"))));
        holder.tvDate.setText(item.optString("updated_at").substring(0, 10));

        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.cancelOrder(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size(): 0;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        /**
         * @param position
         */
        void cancelOrder(int position);
    }
}

