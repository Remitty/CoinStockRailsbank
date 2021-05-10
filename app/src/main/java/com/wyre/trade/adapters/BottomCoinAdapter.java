package com.wyre.trade.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.CoinInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BottomCoinAdapter extends RecyclerView.Adapter<BottomCoinAdapter.OrderViewHolder> {
    private List<CoinInfo> arrItems;
    private Listener listener;
    private Context mContext;

    public BottomCoinAdapter(List<CoinInfo> arrItems, Context context) {
        this.arrItems = arrItems;
        this.mContext = context;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvCoinName;
        ImageView coinIcon;
        LinearLayout llCoinBalance, llTradableTick;

        public OrderViewHolder(View view) {
            super(view);

            tvCoinName = view.findViewById(R.id.coin_name);
            coinIcon = view.findViewById(R.id.coin_icon);
            llTradableTick = view.findViewById(R.id.ll_tradable_tick);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_coin, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        CoinInfo item = arrItems.get(position);

        holder.tvCoinName.setText(item.getCoinName());

//        if(item.getTradable())
//            holder.llTradableTick.setVisibility(View.VISIBLE);
//        else
//            holder.llTradableTick.setVisibility(View.GONE);

//        if(!item.getCoinSymbol().equalsIgnoreCase("DAI"))
            Picasso.with(mContext)
                    .load(item.getCoinIcon())
                    .placeholder(R.drawable.coin_bitcoin)
                    .error(R.drawable.coin_bitcoin)
                    .into(holder.coinIcon);

//        if(item.getCoinSymbol().equalsIgnoreCase("DAI")){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                holder.coinIcon.setImageDrawable(mContext.getDrawable(R.drawable.dai));
//            }
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSelectCoin(position);
            }
        });

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public Object getItem(int i) {
        return arrItems.get(i);
    }

    public interface Listener {
        /**
         * @param position
         */
        void onSelectCoin(int position);
    }
}
