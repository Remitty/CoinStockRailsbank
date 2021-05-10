package com.wyre.trade.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.StocksInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OpenPositionAdapter extends RecyclerView.Adapter<OpenPositionAdapter.OrderViewHolder> {
    private List<StocksInfo> arrItems;
    private Listener listener;
    private Context mContext;
    public OpenPositionAdapter(List<StocksInfo> arrItems, boolean viewType, Context context) {
        this.arrItems = arrItems;
        mContext = context;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvStockName, tvStockSymbol, tvStockShared, tvStockPrice, tvStockChangePrice, tvStockHoldingPrice, tvStockProfit;
        ImageView icArrow;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);

            tvStockSymbol = view.findViewById(R.id.stock_symbol);
            tvStockName = view.findViewById(R.id.stock_name);
            tvStockShared = view.findViewById(R.id.stocks_qty);
            tvStockPrice = view.findViewById(R.id.stocks_price);
            tvStockChangePrice = view.findViewById(R.id.stocks_change);
            tvStockHoldingPrice = view.findViewById(R.id.stocks_holding_price);
            tvStockProfit = view.findViewById(R.id.stocks_profit);
            icArrow = view.findViewById(R.id.ic_arrow);

        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_open_position, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        StocksInfo item = arrItems.get(position);

        holder.tvStockSymbol.setText(item.getSymbol());
        holder.tvStockName.setText(item.getName());
        holder.tvStockShared.setText(item.getQty());
        holder.tvStockPrice.setText("$ "+item.getCurrentPrice());
        holder.tvStockChangePrice.setText("$ "+item.getChangePrice());
        holder.tvStockHoldingPrice.setText("$ "+item.getHolding());
        holder.tvStockProfit.setText("$ "+item.getProfit());

        if(Double.parseDouble(item.getProfit()) >= 0){
            holder.tvStockProfit.setText("$ +"+item.getProfit());
            holder.tvStockProfit.setTextColor(mContext.getResources().getColor(R.color.green));
            Picasso.with(mContext).load(R.drawable.ic_up).into(holder.icArrow);
        }
        else {
            holder.tvStockProfit.setTextColor(mContext.getResources().getColor(R.color.colorRedCrayon));
            Picasso.with(mContext).load(R.drawable.ic_down).into(holder.icArrow);
        }

        if(Double.parseDouble(item.getChangePrice()) >= 0){
            holder.tvStockChangePrice.setText("$ +"+item.getChangePrice());
            holder.tvStockChangePrice.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        else holder.tvStockChangePrice.setTextColor(mContext.getResources().getColor(R.color.colorRedCrayon));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           listener.OnGoToTrade(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public Object getItem(int i) {
        return arrItems.get(i);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        /**
         * @param position
         */
        void OnGoToTrade(int position);
    }

}
