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
import com.anychart.charts.Stock;
import com.anychart.core.stock.Plot;
import com.anychart.data.Table;
import com.anychart.data.TableMapping;
import com.anychart.enums.StockSeriesType;
import com.wyre.trade.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CoinChartFragment2 extends Fragment {
    private static JSONArray chartData;
    private AnyChartView mStocksChartView;

    public CoinChartFragment2() {
        // Required empty public constructor
    }

    public static CoinChartFragment2 newInstance(JSONArray data) {
        CoinChartFragment2 fragment = new CoinChartFragment2();
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
            for (int i = aggregate.length()-1; i >= 0; i--) {
                try {
//                    JSONObject data = aggregate.getJSONObject(i);
//                    Long time = data.optLong("openTime");
//                    Double open = data.optDouble("open");
//                    Double high = data.optDouble("high");
//                    Double low = data.optDouble("low");
//                    Double close = data.optDouble("close");
                    JSONArray data = aggregate.getJSONArray(i);
                    Long time = data.getLong(0);
                    Double open = data.getDouble(1);
                    Double high = data.getDouble(2);
                    Double low = data.getDouble(3);
                    Double close = data.getDouble(4);
                    seriesData.add(new CoinChartFragment2.OHCLDataEntry(time, open, high, low, close));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Table table = Table.instantiate("x");
        table.addData(seriesData);

        TableMapping mapping = table.mapAs("{open: 'open', high: 'high', low: 'low', close: 'close'}");

        Stock stock = AnyChart.stock();

        Plot plot = stock.plot(0);
        plot.yGrid(true)
                .xGrid(false)
                .xAxis(false)
                .yAxis(true)
                .yMinorGrid(false)
                .xMinorGrid(false);

        plot.ema(table.mapAs("{value: 'close'}"), 20d, StockSeriesType.LINE);

//        plot.ohlc(mapping);

        stock.scroller().ohlc(mapping);
        mStocksChartView.setChart(stock);
    }

    private class OHCLDataEntry extends HighLowDataEntry {
        OHCLDataEntry(Long x, Double open, Double high, Double low, Double close) {
            super(x, high, low);
            setValue("open", open);
            setValue("close", close);
        }
    }
}


