package com.brian.stocks.stock.stockorder;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brian.stocks.R;
import com.brian.stocks.model.StocksInfo;

import java.util.List;

public class StockOrderAdapter extends RecyclerView.Adapter<StockOrderAdapter.OrderViewHolder> {
    private List<StocksInfo> arrItems;
    private StockOrderAdapter.Listener listener;
    public StockOrderAdapter(List<StocksInfo> arrItems, boolean viewType) {
        this.arrItems = arrItems;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvStocksName, tvStocksShared, tvStocksOrderStatus, tvStocksOrderSide, tvStocksOrderDate;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);

            tvStocksName = view.findViewById(R.id.stock_symbol);
            tvStocksShared = view.findViewById(R.id.stocks_qty);
            tvStocksOrderStatus = view.findViewById(R.id.stocks_order_status);
            tvStocksOrderSide = view.findViewById(R.id.stocks_order_side);
            tvStocksOrderDate = view.findViewById(R.id.stocks_order_date);

        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_order_history, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        StocksInfo item = arrItems.get(position);

        holder.tvStocksName.setText(item.getStocksOrderName());
        holder.tvStocksShared.setText(item.getStocksOrderShares());

        holder.tvStocksOrderSide.setText(item.getStockOrderSide());
        holder.tvStocksOrderStatus.setText(item.getStockOrderStatus());
        holder.tvStocksOrderDate.setText(item.getStockOrderDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             listener.OnGoToOrder(position);
            }
        });
    }
    public void setListener(StockOrderAdapter.Listener listener) {
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
        void OnGoToOrder(int position);
    }
}
