package com.wyre.trade.stock.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wyre.trade.R;
import com.wyre.trade.model.Card;

import java.util.List;

public class BottomCardAdapter extends RecyclerView.Adapter<BottomCardAdapter.OrderViewHolder> {
    private List<Card> arrItems;
    private BottomCardAdapter.Listener listener;
    private Context mContext;

    public BottomCardAdapter(List<Card> arrItems, Context context) {
        this.arrItems = arrItems;
        this.mContext = context;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvCardId;
        ImageView CardIcon;

        public OrderViewHolder(View view) {
            super(view);

            tvCardId = view.findViewById(R.id.tv_card_id);
            CardIcon = view.findViewById(R.id.img_card_icon);
        }
    }

    @NonNull
    @Override
    public BottomCardAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_card, parent, false);
        BottomCardAdapter.OrderViewHolder vh = new BottomCardAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final BottomCardAdapter.OrderViewHolder holder, final int position) {
        Card item = arrItems.get(position);

        holder.tvCardId.setText("XXXX-XXXX-XXXX-" + item.getLastFour());
        if(item.getBrand().equals("Visa")) {
            Picasso.with(mContext)
                    .load(R.drawable.ic_visa)
                    .into(holder.CardIcon);
        } else {
            Picasso.with(mContext)
                    .load(R.drawable.ic_mastercard)
                    .into(holder.CardIcon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSelectCard(position);
            }
        });

    }

    public void setListener(BottomCardAdapter.Listener listener) {
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
        void onSelectCard(int position);
    }
}

