package com.wyre.trade.usdc.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.model.ContactUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserContactAdapter extends RecyclerView.Adapter<UserContactAdapter.OrderViewHolder> {
    private List<ContactUser> arrItems;
    private Listener listener;
    private boolean flagDelete = false;

    public UserContactAdapter(List<ContactUser> arrItems, boolean delete) {
        this.arrItems = arrItems;
        flagDelete = delete;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvContactName, tvContactEmail;
        ImageButton btnDelete;


        public OrderViewHolder(View view) {
            super(view);

            tvContactName = view.findViewById(R.id.contact_name);
            tvContactEmail = view.findViewById(R.id.contact_email);
            btnDelete = view.findViewById(R.id.btn_delete);

        }
    }

    @NonNull
    @Override
    public UserContactAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_coin, parent, false);
        UserContactAdapter.OrderViewHolder vh = new UserContactAdapter.OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserContactAdapter.OrderViewHolder holder, final int position) {
        ContactUser item = arrItems.get(position);

        holder.tvContactName.setText(item.getName());
        holder.tvContactEmail.setText(item.getEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSelect(position);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(position);
            }
        });

        if(!flagDelete)
            holder.btnDelete.setVisibility(View.GONE);

    }

    public void setListener(UserContactAdapter.Listener listener) {
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
        void onSelect(int position);
        void onDelete(int position);
    }
}

