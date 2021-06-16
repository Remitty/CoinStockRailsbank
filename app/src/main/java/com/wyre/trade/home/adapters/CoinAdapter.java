package com.wyre.trade.home.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.CoinInfo;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.parseColor;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.OrderViewHolder> {
    private List<CoinInfo> arrItems;
    private Listener listener;
    private Context mContext;

    public CoinAdapter(List<CoinInfo> arrItems, Context context, boolean viewType) {
        this.arrItems = arrItems;
        this.mContext = context;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvCoinName, tvCoinSymbol, tvCoinRate, tvCoinEstAmount, tvCoinBalance, tvCoinEffect;
        ImageView coinIcon, arrowIcon;
        LinearLayout llCoinBalance;
        Button btnDeposit, btnRamp;

        public OrderViewHolder(View view) {
            super(view);

            tvCoinName = view.findViewById(R.id.coin_name);
            tvCoinSymbol = view.findViewById(R.id.coin_symbol);
            tvCoinRate= view.findViewById(R.id.coin_rate);
            tvCoinBalance= view.findViewById(R.id.coin_balance);
            tvCoinEffect= view.findViewById(R.id.coin_effect);
            tvCoinEstAmount= view.findViewById(R.id.coin_est_usdc);
            coinIcon = view.findViewById(R.id.coin_icon);
            llCoinBalance = view.findViewById(R.id.ll_coin_balance);
            btnDeposit = view.findViewById(R.id.btn_deposit);
            btnRamp = view.findViewById(R.id.btn_ramp);
            arrowIcon = view.findViewById(R.id.ic_arrow);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        CoinInfo item = arrItems.get(position);

        holder.tvCoinName.setText(item.getCoinName());
        holder.tvCoinSymbol.setText(item.getCoinSymbol());
        holder.tvCoinRate.setText("$ " + new DecimalFormat("#,###.####").format(item.getCoinRate()));
        holder.tvCoinEffect.setText(item.getCoinEffect() + "%");

        if(item.getCoinEffect().startsWith("-")){
            holder.tvCoinEffect.setTextColor(RED);
            Picasso.with(mContext).load(R.drawable.ic_down).into(holder.arrowIcon);
        }else {
            holder.tvCoinEffect.setTextColor(mContext.getColor(R.color.green));
            Picasso.with(mContext).load(R.drawable.ic_up).into(holder.arrowIcon);
        }

        Double balance = item.getCoinBalance();
        if(balance > 0)
            holder.tvCoinBalance.setText(new DecimalFormat("#,###.####").format(balance));
        else holder.tvCoinBalance.setText("0.0000");
            holder.tvCoinEstAmount.setText("$ "+item.getCoinUsdc());

            Picasso.with(mContext)
                .load(item.getCoinIcon())
                .placeholder(R.drawable.coin_bitcoin)
                .error(R.drawable.coin_bitcoin)
                .into(holder.coinIcon);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position);
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
        void onClick(int position);
    }
}
