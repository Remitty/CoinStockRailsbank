package com.brian.stocks.predict.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brian.stocks.R;
import com.brian.stocks.adapters.DepositAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PredictableStockAdapter extends RecyclerView.Adapter<PredictableStockAdapter.CustomerViewHolder> {
    JSONArray data = new JSONArray();

    Listener listener;

    public PredictableStockAdapter(JSONArray data) {
        this.data = data;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_predictable_stock, parent, false);
        CustomerViewHolder vh = new CustomerViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, final int position) {
        try {
            JSONObject item = data.getJSONObject(position);

            holder.mtvSymbol.setText(item.getString("symbol"));
            holder.mtvName.setText(item.getString("name"));
            holder.mtvPrice.setText(item.getString("price"));
            holder.mtvChange.setText(item.getString("change") + "% 24hrs");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelect(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data!= null? data.length(): 0;
    }

    public void setListener(Listener listener){this.listener = listener;}

    public class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView mtvSymbol, mtvName, mtvPrice, mtvChange;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            mtvSymbol = itemView.findViewById(R.id.symbol);
            mtvName = itemView.findViewById(R.id.name);
            mtvPrice = itemView.findViewById(R.id.price);
            mtvChange = itemView.findViewById(R.id.change);
        }
    }

    public interface Listener {
        /**
         * @param position
         */
        void onSelect(int position);
    }
}
