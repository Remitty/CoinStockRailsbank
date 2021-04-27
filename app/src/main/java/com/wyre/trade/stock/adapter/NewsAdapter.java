package com.wyre.trade.stock.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.NewsInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.OrderViewHolder> {
    private ArrayList<NewsInfo> arrItems;
    private NewsAdapter.Listener listener;
    private Context mContext;
    public NewsAdapter(Context context, ArrayList<NewsInfo> data ){
        mContext = context;
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
        holder.mNewsSummary.setText(item.getSummary());
        holder.mNewsUrl.setText(item.getURL());
        holder.mNewsDate.setText(item.getDate());
        if(!item.getImageURL().equals(""))
        Picasso.with(mContext)
                .load(item.getImageURL())
                .into(holder.mNewsImage);
    }

    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView mNewsTitle, mNewsSummary, mNewsUrl, mNewsDate;
        ImageView mNewsImage;

        public OrderViewHolder(View view) {
            super(view);

            mNewsTitle = view.findViewById(R.id.title);
            mNewsSummary = view.findViewById(R.id.summary);
            mNewsDate = view.findViewById(R.id.date);
            mNewsUrl = view.findViewById(R.id.url);
            mNewsImage = view.findViewById(R.id.image);
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
