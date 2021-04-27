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

import java.util.List;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

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
        holder.tvCoinRate.setText("$ " + String.format("%.2f", item.getCoinRate()));
        holder.tvCoinEffect.setText(item.getCoinEffect() + "%");

        if(item.getCoinEffect().startsWith("-")){
            holder.tvCoinEffect.setTextColor(RED);
            Picasso.with(mContext).load(R.drawable.ic_down).into(holder.arrowIcon);
        }else {
            holder.tvCoinEffect.setTextColor(GREEN);
            Picasso.with(mContext).load(R.drawable.ic_up).into(holder.arrowIcon);
        }

//        if(Double.parseDouble(item.getCoinBalance()) == 0) {
////            holder.tvCoinBalance.setVisibility(View.GONE);
//            holder.tvCoinBalance.setText("0.00"+ " " + item.getCoinSymbol());
//        }
//        else {
            holder.tvCoinBalance.setText(item.getCoinBalance());
            holder.tvCoinBalance.setVisibility(View.VISIBLE);
//        }
            holder.tvCoinEstAmount.setText("$ "+item.getCoinUsdc());
//        if(!item.getCoinSymbol().equals("DAI"))
            Picasso.with(mContext)
                .load(item.getCoinIcon())
                .placeholder(R.drawable.coin_bitcoin)
                .error(R.drawable.coin_bitcoin)
                .into(holder.coinIcon);

//        if(item.getCoinSymbol().equals("DAI")){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                holder.coinIcon.setImageDrawable(mContext.getDrawable(R.drawable.dai));
//            }
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.OnDeposit(position);
            }
        });
        
//        holder.btnDeposit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.OnDeposit(position);
//            }
//        });
//        holder.btnRamp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.OnBuyNow(position);
//            }
//        });
        String coins = "BTC ETH DAI XDAI USDC DOT";
//        if(coins.contains(item.getCoinSymbol())) {
//        if(item.getBuyNowOption() != 0 && item.getBuyNowOption() != 100) {
//            holder.btnRamp.setVisibility(View.VISIBLE);
//        }
//        else {
//            holder.btnRamp.setVisibility(View.GONE);
//        }

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
        void OnDeposit(int position);
        void OnBuyNow(int position);
    }
}
