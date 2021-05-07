package com.wyre.trade.payment.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wyre.trade.R;
import com.wyre.trade.model.Card;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CustomViewHolder> {
    private ArrayList<Card> arrItems;
    private CardAdapter.Listener listener;
    private Context mContext;
    public CardAdapter(Context context, ArrayList<Card> data ){
        mContext = context;
        this.arrItems = data;
    };

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        CustomViewHolder vh = new CustomViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int i) {
        Card item = arrItems.get(i);

        holder.tvCardId.setText("XXXX-XXXX-XXXX-" + item.getLastFour());
        if(item.getBrand().equals("Visa")) {
            Picasso.with(mContext).load(R.drawable.ic_visa).into(holder.imgCardIcon);
        } else Picasso.with(mContext).load(R.drawable.ic_mastercard).into(holder.imgCardIcon);
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnDelete(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tvCardId;
        ImageView imgCardIcon, imgDelete;

        public CustomViewHolder(View view) {
            super(view);

            tvCardId = view.findViewById(R.id.tv_card_id);
            imgCardIcon = view.findViewById(R.id.img_card_icon);
            imgDelete = view.findViewById(R.id.img_delete);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        /**
         * @param position
         */
        void OnDelete(int position);
    }

}