package com.brian.stocks.xmt;

import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Area;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.ScaleStackMode;
import com.brian.stocks.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class XMTChartFragment extends Fragment {
    private static JSONArray chartData;
    private AnyChartView mXMTChartView;

    public XMTChartFragment() {
        // Required empty public constructor
    }

    public static XMTChartFragment newInstance(JSONArray data) {
        XMTChartFragment fragment = new XMTChartFragment();
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
        View view = inflater.inflate(R.layout.fragment_xmt_chart, container, false);
        mXMTChartView = view.findViewById(R.id.xmt_history_chart);
        drawStocksChart(chartData);

        return view;
    }

    private void drawStocksChart(JSONArray aggregate) {

        Cartesian areaChart = AnyChart.area();

        areaChart.animation(false)
                .tooltip(false)
                .xAxis(false)
                .yAxis(true)
//                .yGrid(5, true)
                .background(false);
//        areaChart.yScale().stackMode(ScaleStackMode.VALUE);
        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry("Q2 2014", 87.982, 80.941 ));
        seriesData.add(new CustomDataEntry("Q3 2014", 97.574, 93.659));
        seriesData.add(new CustomDataEntry("Q4 2014", 99.75, 90.35));
        seriesData.add(new CustomDataEntry("Q1 2015", 90.6, 87.2));
        seriesData.add(new CustomDataEntry("Q2 2015", 81.316, 82.204));
        seriesData.add(new CustomDataEntry("Q3 2015", 70.209, 80.342));
        seriesData.add(new CustomDataEntry("Q4 2015", 71.773, 90.577));
        seriesData.add(new CustomDataEntry("Q1 2016", 79.3, 87.9));

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");

        Area series1 = areaChart.area(series1Data);
//        series1.name("Americas");
        series1.stroke("0 #0ff");
        series1.color("#0ff");
//        series1.hovered().stroke("3 #fff");
//        series1.hovered().markers().enabled(true);
//        series1.hovered().markers()
//                .type(MarkerType.CIRCLE)
//                .size(4d)
//                .stroke("1.5 #fff");
//        series1.markers().zIndex(100d);

        Area series2 = areaChart.area(series2Data);
        series2.stroke("0 #f0f");
        series2.color("#f0f");
//        areaChart.legend().enabled(true);
//        areaChart.legend().fontSize(13d);
//        areaChart.legend().padding(0d, 0d, 20d, 0d);

//        areaChart.xAxis(0).title(false);
//        areaChart.yAxis(0).title("Revenue (in Billons USD)");

//        areaChart.interactivity().hoverMode(HoverMode.BY_X);
//        areaChart.tooltip()
//                .valuePrefix("$")
//                .valuePostfix(" bln.")
//                .displayMode(TooltipDisplayMode.UNION);

        mXMTChartView.setChart(areaChart);
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }
    }
}
