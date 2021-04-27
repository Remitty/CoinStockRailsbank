package com.wyre.trade.predict;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wyre.trade.R;
import com.wyre.trade.predict.adapters.PredictAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class IncomingPredictsFragment extends Fragment {
    JSONArray data = new JSONArray();
    ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
    RecyclerView recyclerView;
    PredictAdapter mAdapter;
    LinearLayout emptyLayout;

    public IncomingPredictsFragment(ArrayList my_answer) {
        // Required empty public constructor
        this.dataList = my_answer;
    }

    public static IncomingPredictsFragment newInstance(ArrayList my_answer) {
        IncomingPredictsFragment fragment = new IncomingPredictsFragment(my_answer);
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
        View view = inflater.inflate(R.layout.fragment_my_predict_answer, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new PredictAdapter(dataList, 1);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new PredictAdapter.Listener() {
            @Override
            public void onSelect(int position) {

            }

            @Override
            public void onCancel(int position) {

            }

            @Override
            public void onHandle(int position, int est) {

            }
        });

        emptyLayout = view.findViewById(R.id.empty_layout);
        if(dataList.size() > 0)
            emptyLayout.setVisibility(View.GONE);

        return view;
    }
}
