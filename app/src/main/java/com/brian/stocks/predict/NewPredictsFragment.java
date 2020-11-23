package com.brian.stocks.predict;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.predict.adapters.PredictAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewPredictsFragment extends Fragment {
    JSONArray data = new JSONArray();
    RecyclerView recyclerView;
    PredictAdapter mAdapter;
    LinearLayout emptyLayout;
    private String answer, bet_currency;
    private LoadToast loadToast;

    public NewPredictsFragment(JSONArray all) {
        // Required empty public constructor
        this.data = all;
    }

    public static NewPredictsFragment newInstance(JSONArray all) {
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

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new PredictAdapter(data, 0);
        recyclerView.setAdapter(mAdapter);
        mAdapter.seListener(new PredictAdapter.Listener() {
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

                JSONObject object = data.optJSONObject(position);
                bet_currency = object.optJSONObject("coin").optString("coin_symbol");

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setIcon(R.mipmap.ic_launcher_round)
                        .setTitle("Confirm Prediction")
                        .setMessage("You are about to disagree that " + object.optJSONObject("item").optString("symbol") + " will be "+ getEstType(object.optInt("type")) +" $"+object.optString("est_price") +"." + "You call price is " + object.optString("bet_price")+" " + bet_currency +".")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendAnswer(position);
                            }
                        })
                        .show();
            }
        });
        emptyLayout = view.findViewById(R.id.empty_layout);
        if(data.length() > 0)
            emptyLayout.setVisibility(View.GONE);

        return view;
    }

    private String getEstType(int type) {
        switch (type) {
            case 0:
                return "not change";
            case 1:
                return "lower than";
            case 2:
                return "higher than";
            default:
                return "not change";
        }
    }

    private void sendAnswer(final int idx) {
        loadToast.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", data.getJSONObject(idx).getString("id"));
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
                        loadToast.success();
                        if(response.optBoolean("success")) {
                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                            data.remove(idx);
                        }
                        else
                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }
}
