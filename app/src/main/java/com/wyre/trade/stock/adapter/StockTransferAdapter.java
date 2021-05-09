package com.wyre.trade.stock.adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.TransferInfo;

import java.util.List;

public class StockTransferAdapter extends RecyclerView.Adapter<StockTransferAdapter.OrderViewHolder> {
    private List<TransferInfo> arrItems;

    public StockTransferAdapter(List<TransferInfo> arrItems, boolean viewType) {
        this.arrItems = arrItems;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvStockTransferAmount, tvReceived, tvStockTransferStatus, tvStockTransferDate, tvUnit;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);

            tvStockTransferAmount = view.findViewById(R.id.stock_transfer_amount);
            tvReceived = view.findViewById(R.id.stock_received_amount);
            tvStockTransferStatus = view.findViewById(R.id.stock_transfer_status);
            tvStockTransferDate = view.findViewById(R.id.stock_transfer_date);
            tvUnit = view.findViewById(R.id.stock_transfer_unit);

        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_transfer, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        TransferInfo item = arrItems.get(position);

        holder.tvStockTransferStatus.setText(item.getStatus());
        holder.tvStockTransferDate.setText(item.getDate());
        holder.tvStockTransferAmount.setText("$"+item.getAmount());
        holder.tvReceived.setText("$"+item.getReceived());
        holder.tvUnit.setText(item.getUnit());

    }


    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public Object getItem(int i) {
        return arrItems.get(i);
    }

}
