package com.wyre.trade.predict.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.PredictAsset;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PredictableStockAdapter extends RecyclerView.Adapter<PredictableStockAdapter.CustomerViewHolder> {
    ArrayList<PredictAsset> data;

    Listener listener;
    Context mContext;

    public PredictableStockAdapter(Context context, ArrayList data) {
        this.data = data;
        mContext = context;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_predictable_stock, parent, false);
        CustomerViewHolder vh = new CustomerViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, final int position) {
        PredictAsset item = data.get(position);

        holder.mtvSymbol.setText(item.getSymbol());
        holder.mtvName.setText(item.getName());
        holder.mtvPrice.setText("$" + new DecimalFormat("#,###.##").format(item.getPrice()));
        holder.mtvChange.setText(new DecimalFormat("#,###.####").format(item.getChange()) + "%");
        String icon = item.getIcon();
        Picasso.with(mContext)
                .load(icon)
                .placeholder(R.drawable.coin_bitcoin)
                .error(R.drawable.coin_bitcoin)
                .into(holder.icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelect(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(Listener listener){this.listener = listener;}

    public class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView mtvSymbol, mtvName, mtvPrice, mtvChange;
        ImageView icon;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            mtvSymbol = itemView.findViewById(R.id.symbol);
            mtvName = itemView.findViewById(R.id.name);
            mtvPrice = itemView.findViewById(R.id.price);
            mtvChange = itemView.findViewById(R.id.change);
            icon = itemView.findViewById(R.id.predict_icon);
        }
    }

    public interface Listener {
        /**
         * @param position
         */
        void onSelect(int position);
    }
}
