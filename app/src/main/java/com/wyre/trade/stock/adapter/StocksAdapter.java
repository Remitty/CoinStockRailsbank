package com.wyre.trade.stock.adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import com.wyre.trade.model.StocksInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.OrderViewHolder> {
    private List<StocksInfo> arrItems;
    private Listener listener;
    private AnyChartView  mStocksChartView;
    private Boolean mViewType;

    public StocksAdapter(List<StocksInfo> arrItems, boolean viewType) {
        this.arrItems = arrItems;
        mViewType = viewType;
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
            llStocksChart = view.findViewById(R.id.coin_est_usdc);
            
            mStocksChartView = view.findViewById(R.id.any_chart_view);
            mStocksChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        StocksInfo item = arrItems.get(position);

        holder.tvStocksName.setText(item.getName());
        holder.tvStocksSymbol.setText(item.getSymbol());
        holder.tvStocksShared.setText(item.getQty());
        holder.tvStocksChangeGreen.setText(item.getChangePricePercent() + "%");
        holder.tvStocksChangeRed.setText(item.getChangePricePercent() + "%");
        if(item.getChangePricePercent().startsWith("-")) {
            holder.tvStocksChangeGreen.setVisibility(View.GONE);
            holder.tvStocksChangeRed.setVisibility(View.VISIBLE);
        }
        else{
            holder.tvStocksChangeGreen.setVisibility(View.VISIBLE);
            holder.tvStocksChangeRed.setVisibility(View.GONE);
        }

        holder.tvStocksPrice.setText("$ " + item.getCurrentPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnGoToTrade(position);
            }
        });

//        JSONArray aggregate = item.getStockAggregate();
//
//        drawStocksChart(aggregate);

    }

    private void drawStocksChart(JSONArray aggregate) {

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);
        cartesian.tooltip(false);
        cartesian.xAxis(false);
        cartesian.yAxis(false);
//        cartesian.background(false);
//        cartesian.background("#ccc");
        List<DataEntry> seriesData = new ArrayList<>();
        if(aggregate != null) {
            for (int i = 0; i < aggregate.length(); i++) {
                try {
                    JSONObject data = aggregate.getJSONObject(i);
                    String time = data.optString("t");
                    Double vw = data.optDouble("vw");
                    seriesData.add(new CustomDataEntry(time, vw));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Set set = Set.instantiate();
            set.data(seriesData);
            Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

            Line series1 = cartesian.line(series1Mapping);
            series1.color("#ee204d");

            mStocksChartView.setChart(cartesian);
        }
    }

    public void setListener(Listener listener) {
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
        void OnGoToTrade(int position);
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value) {
            super(x, value);
        }

    }
}
