package com.brian.stocks.stock.stockwithdraw;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brian.stocks.R;
import com.brian.stocks.stock.stockwithdraw.adapters.StockWithdrawAdapter;

import org.json.JSONArray;

public class StockWithdrawHistoryFragment extends Fragment {

    private JSONArray history;
    RecyclerView historyView;
    StockWithdrawAdapter mAdapter;

    public StockWithdrawHistoryFragment() {
        // Required empty public constructor
    }

    public static StockWithdrawHistoryFragment newInstance(JSONArray history) {
        StockWithdrawHistoryFragment fragment = new StockWithdrawHistoryFragment();
        fragment.history = history;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_withdraw_history, container, false);

        historyView = view.findViewById(R.id.history_view);

//        mAdapter = new StockWithdrawAdapter(history);

        historyView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyView.setAdapter(mAdapter);

        return view;
    }
}
