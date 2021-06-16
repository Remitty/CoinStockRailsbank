package com.wyre.trade.coins;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.HighLowDataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Stock;
import com.anychart.core.cartesian.series.Line;
import com.anychart.core.stock.Plot;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.data.Table;
import com.anychart.data.TableMapping;
import com.anychart.enums.StockSeriesType;
import com.wyre.trade.R;
import com.wyre.trade.stock.stocktrade.StockChartFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoinChartFragment1 extends Fragment {
    private static JSONArray chartData;
    private AnyChartView mStocksChartView;

    public CoinChartFragment1() {
        // Required empty public constructor
    }

    public static CoinChartFragment1 newInstance(JSONArray data) {
        CoinChartFragment1 fragment = new CoinChartFragment1();
        chartData = data;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_chart, container, false);
        mStocksChartView = view.findViewById(R.id.stock_history_chart);
        drawStocksChart(chartData);

        return view;
    }

    private void drawStocksChart(JSONArray aggregate) {

        List<DataEntry> seriesData = new ArrayList<>();
        if(aggregate != null) {
            for (int i = 0; i < aggregate.length(); i++) {
                try {
                    JSONArray data = aggregate.getJSONArray(i);
                    String time = data.getString(0);
                    Double open = data.getDouble(1);
                    Double high = data.getDouble(2);
                    Double low = data.getDouble(3);
                    Double close = data.getDouble(4);
                    seriesData.add(new CoinChartFragment1.ChartDataEntry(time, close));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true)
                .tooltip(false)
                .xAxis(false)
                .yAxis(true)
                .background(false);
        cartesian.yGrid(3, true);

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.color("#3AE57F");
        series1.stroke("3 #3AE57F");

        mStocksChartView.setChart(cartesian);
    }

    private class ChartDataEntry extends ValueDataEntry {
        ChartDataEntry(String x, Number value) {
            super(x, value);
        }
    }
}

