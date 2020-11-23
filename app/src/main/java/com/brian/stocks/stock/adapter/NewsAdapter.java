package com.brian.stocks.stock.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brian.stocks.R;
import com.brian.stocks.model.NewsInfo;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.OrderViewHolder> {
    private List<NewsInfo> arrItems;
    private NewsAdapter.Listener listener;

    public NewsAdapter(List<NewsInfo> data, boolean viewType ){
        this.arrItems = data;
    };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, final int i) {
        NewsInfo item = arrItems.get(i);

        holder.mNewsTitle.setText(item.getNewsTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnGoToNewsDetail(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView mNewsTitle;
        public OrderViewHolder(View view) {
            super(view);

            mNewsTitle = view.findViewById(R.id.news_title);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        /**
         * @param position
         */
        void OnGoToNewsDetail(int position);
    }

}
