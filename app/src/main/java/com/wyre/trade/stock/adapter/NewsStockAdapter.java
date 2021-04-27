package com.wyre.trade.stock.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.StocksOrderModel;
import java.util.List;

public class NewsStockAdapter extends RecyclerView.Adapter<NewsStockAdapter.OrderViewHolder> {
    private List<StocksOrderModel> arrItems;
    private NewsStockAdapter.Listener listener;

    public NewsStockAdapter(List<StocksOrderModel> data, boolean viewType ){
        this.arrItems = data;
    };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_group_layout, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, final int i) {
        StocksOrderModel item = arrItems.get(i);

//        holder.mStockName.setText(item.getStocksTickerOther() + " - "+item.getStockName());
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.OnGoToNewsList(i);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView mStockName;
        public OrderViewHolder(View view) {
            super(view);

            mStockName = view.findViewById(R.id.expandedListItem);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
    public interface Listener {
        /**
         * @param position
         */
        void OnGoToNewsList(int position);
    }

}
