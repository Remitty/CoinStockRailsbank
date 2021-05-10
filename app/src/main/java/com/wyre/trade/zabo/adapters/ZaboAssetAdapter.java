package com.wyre.trade.zabo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.ZaboAsset;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ZaboAssetAdapter extends RecyclerView.Adapter<ZaboAssetAdapter.CustomerViewHolder> {
    ArrayList<ZaboAsset> data = new ArrayList();

    Listener listener;
    Context mContext;

    public ZaboAssetAdapter(Context context, ArrayList data) {
        this.data = data;
        mContext = context;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zabo_asset, parent, false);
        CustomerViewHolder vh = new CustomerViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, final int position) {
        ZaboAsset item = data.get(position);

        holder.mtvName.setText(item.getName());
        holder.mtvFiat.setText("$" + new DecimalFormat("#,###.##").format(item.getFiatValue()));
        holder.mtvBalance.setText(new DecimalFormat("#,###.####").format(item.getBalance()) + " " + item.getSymbol());
        String icon = item.getIcon();
        if(icon.equals(""))
            holder.icon.setVisibility(View.GONE);
        else {
            if (!icon.startsWith("http"))
                icon = URLHelper.base + icon;
            Picasso.with(mContext)
                    .load(icon)
                    .placeholder(R.drawable.coin_bitcoin)
                    .error(R.drawable.coin_bitcoin)
                    .into(holder.icon);
        }

        holder.btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeposit(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data!= null? data.size(): 0;
    }

    public void setListener(Listener listener){this.listener = listener;}

    public class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView mtvName, mtvBalance, mtvFiat;
        ImageView icon;
        Button btnDeposit;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            mtvName = itemView.findViewById(R.id.coin_name);
            mtvBalance = itemView.findViewById(R.id.balance);
            mtvFiat = itemView.findViewById(R.id.fiat);
            icon = itemView.findViewById(R.id.icon);
            btnDeposit = itemView.findViewById(R.id.btn_deposit);
        }
    }

    public interface Listener {
        /**
         * @param position
         */
        void onDeposit(int position);
    }
}
