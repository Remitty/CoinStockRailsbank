package com.wyre.trade.zabo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.ZaboAccount;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ZaboAccountAdapter extends RecyclerView.Adapter<ZaboAccountAdapter.CustomerViewHolder> {
    ArrayList<ZaboAccount> data = new ArrayList();

    Listener listener;
    Context mContext;

    public ZaboAccountAdapter(Context context, ArrayList data) {
        this.data = data;
        mContext = context;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zabo_account, parent, false);
        CustomerViewHolder vh = new CustomerViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, final int position) {
        ZaboAccount item = data.get(position);

        holder.mtvName.setText(item.getProvider());
        holder.mtvBalance.setText("$" + new DecimalFormat("#,###.##").format(item.getBalance()));
        holder.mtvCounts.setText(item.getAssetsCount());
//            String icon = item.getString("icon");
//            if(!icon.startsWith("http"))
//                icon = URLHelper.base + icon;
//            Picasso.with(mContext)
//                    .load(icon)
//                    .placeholder(R.drawable.coin_bitcoin)
//                    .error(R.drawable.coin_bitcoin)
//                    .into(holder.icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelect(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data!= null? data.size(): 0;
    }

    public void setListener(Listener listener){this.listener = listener;}

    public class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView mtvName, mtvBalance, mtvCounts;
//        ImageView icon;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            mtvName = itemView.findViewById(R.id.account_provider);
            mtvBalance = itemView.findViewById(R.id.account_balance);
            mtvCounts = itemView.findViewById(R.id.account_cnt);
//            icon = itemView.findViewById(R.id.provider_icon);
        }
    }

    public interface Listener {
        /**
         * @param position
         */
        void onSelect(int position);
    }
}
