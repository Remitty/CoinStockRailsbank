package com.wyre.trade.stock.adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.charts.Cartesian;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;

import com.wyre.trade.R;
import com.wyre.trade.model.TopStocks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TopStocksAdapter extends RecyclerView.Adapter<TopStocksAdapter.OrderViewHolder> {
    private List<TopStocks> arrItems;
    private Listener listener;

    public TopStocksAdapter(Context context, List<TopStocks> arrItems) {
        this.arrItems = arrItems;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvStocksName, tvStocksSymbol, tvStocksShared, tvStocksPrice, tvStocksChangeGreen, tvStocksChangeRed;
        LinearLayout llStocksChart;

        @SuppressLint("ResourceAsColor")
        public OrderViewHolder(View view) {
            super(view);

            tvStocksSymbol = view.findViewById(R.id.stock_symbol);
            tvStocksName = view.findViewById(R.id.stock_name);
            tvStocksShared = view.findViewById(R.id.stocks_qty);
            tvStocksChangeGreen = view.findViewById(R.id.stocks_change_green);
            tvStocksChangeRed = view.findViewById(R.id.stocks_change_red);
            tvStocksPrice = view.findViewById(R.id.stocks_price);

        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_stock, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        TopStocks item = arrItems.get(position);

        holder.tvStocksName.setText(item.getCompanyName());
        holder.tvStocksSymbol.setText(item.getSymbol());
        holder.tvStocksChangeGreen.setText("$"+item.getChanges() + item.getChangesPercentage());
        holder.tvStocksChangeRed.setText("$"+item.getChanges() + item.getChangesPercentage());
        if(item.getChanges().startsWith("-")) {
            holder.tvStocksChangeGreen.setVisibility(View.GONE);
            holder.tvStocksChangeRed.setVisibility(View.VISIBLE);
        }
        else{
            holder.tvStocksChangeGreen.setVisibility(View.VISIBLE);
            holder.tvStocksChangeRed.setVisibility(View.GONE);
        }

        holder.tvStocksPrice.setText("$ " + new DecimalFormat("#,###.##").format(item.getPrice()));
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
