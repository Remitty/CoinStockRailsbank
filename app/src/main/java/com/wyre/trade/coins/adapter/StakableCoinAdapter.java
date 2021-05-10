package com.wyre.trade.coins.adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.helper.URLHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StakableCoinAdapter extends RecyclerView.Adapter<StakableCoinAdapter.CustomViewHolder> {

    ArrayList<JSONObject> list;
    Context mContext;
    Listener listener;

    public StakableCoinAdapter(Context context, ArrayList list) {
        this.list = list;
        mContext = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stake_coin, parent, false);
        CustomViewHolder vh = new CustomViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        JSONObject item = list.get(position);

        holder.tvItemAsset.setText(item.optString("symbol"));
        holder.tvItemBalance.setText(new DecimalFormat("#,###.####").format(item.optDouble("balance")));
        holder.tvItemStake.setText(new DecimalFormat("#,###.####").format(item.optDouble("amount")));
        String icon = item.optString("icon");
        if(!icon.startsWith("http"))
            icon = URLHelper.base + icon;
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
        return list != null ? list.size(): 0;
    }

    public void setListener(Listener listener){this.listener = listener;}

    public interface Listener {
        /**
         * @param position
         */
        void onSelect(int position);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemAsset, tvItemBalance, tvItemStake;
        ImageView icon;

        @SuppressLint("ResourceAsColor")
        public CustomViewHolder(View view) {
            super(view);

            tvItemAsset = view.findViewById(R.id.symbol);
            tvItemBalance = view.findViewById(R.id.balance);
            tvItemStake = view.findViewById(R.id.stake);
            icon = view.findViewById(R.id.icon);

        }
    }

}
