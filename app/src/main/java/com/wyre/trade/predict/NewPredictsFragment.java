package com.wyre.trade.predict;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.PredictionModel;
import com.wyre.trade.predict.adapters.PredictAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NewPredictsFragment extends Fragment {
    private LoadToast loadToast;
    ConfirmAlert confirmAlert;
//    JSONArray data = new JSONArray();
    ArrayList<PredictionModel> dataList = new ArrayList<PredictionModel>();
    RecyclerView recyclerView;
    PredictAdapter mAdapter;
    LinearLayout emptyLayout;
    private String answer, bet_currency;

    public NewPredictsFragment(ArrayList all) {
        // Required empty public constructor
        this.dataList = all;
    }

    public static NewPredictsFragment newInstance(ArrayList all) {
        NewPredictsFragment fragment = new NewPredictsFragment(all);
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
        View view = inflater.inflate(R.layout.fragment_all_predicts, container, false);

        loadToast = new LoadToast(getActivity());
        confirmAlert = new ConfirmAlert(getActivity());

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new PredictAdapter(dataList, 0);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new PredictAdapter.Listener() {
            @Override
            public void onSelect(int position) {

            }

            @Override
            public void onCancel(int position) {

            }

            @Override
            public void onHandle(final int position, int est) {
                if(est == 0)
                    answer="No";
                else answer = "Yes";

                PredictionModel object = dataList.get(position);
                bet_currency = object.getSymbol();


                confirmAlert.confirm("You are about to disagree this predict.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sendAnswer(position);
                            }
                        }).show();
            }
        });
        emptyLayout = view.findViewById(R.id.empty_layout);
        if(dataList.size() > 0)
            emptyLayout.setVisibility(View.GONE);

        return view;
    }

    private void sendAnswer(final int idx) {
//        loadToast.show();
        confirmAlert.process();
        JSONObject object = new JSONObject();
        try {
            object.put("id", dataList.get(idx).getId());
            object.put("answer", answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(URLHelper.REQUEST_PREDICT_BID)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                .addJSONObjectBody(object)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
//                        loadToast.success();
                        if(response.optBoolean("success")) {
//                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                            dataList.remove(idx);
                            mAdapter.notifyDataSetChanged();
                            confirmAlert.success(response.optString("message"));
                        }
                        else
                            confirmAlert.error(response.optString("message"));
//                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
//                        loadToast.error();
                        // handle error
                        confirmAlert.error(error.getErrorBody());
//                        Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
    }
}
