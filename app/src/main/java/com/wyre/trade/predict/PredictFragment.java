package com.wyre.trade.predict;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.PredictionModel;
import com.wyre.trade.predict.adapters.PredictPageAdapter;
import com.wyre.trade.stock.StocksActivity;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PredictFragment extends Fragment {

    private View mView;

    TabLayout tab;
    ViewPager pager;
    TextView predictNow;
    PredictPageAdapter mAdapter;

    private BottomSheetDialog dialog;

    LoadToast loadToast;
    private ArrayList<PredictionModel> all = new ArrayList(), incoming = new ArrayList(), my_post = new ArrayList();

    public PredictFragment() {
        // Required empty public constructor
    }

    public static PredictFragment newInstance() {
        PredictFragment fragment = new PredictFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadToast = new LoadToast(getActivity());

        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_predict, container, false);

        View dialogView = getLayoutInflater().inflate(R.layout.predict_select_kind, null);
        TextView stocks = dialogView.findViewById(R.id.tv_select_stocks);
        TextView coins = dialogView.findViewById(R.id.tv_select_coins);
        stocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), StocksActivity.class);
                intent.putExtra("predict", true);
                startActivity(intent);
            }
        });
        coins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), PredictableListActivity.class);
                startActivity(intent);
            }
        });
        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(dialogView);

        tab = mView.findViewById(R.id.tab);
        pager = mView.findViewById(R.id.view_pager);
        predictNow = mView.findViewById(R.id.btn_post_predict);

        mAdapter = new PredictPageAdapter(getActivity().getSupportFragmentManager(), all, incoming, my_post);
        pager.setAdapter(mAdapter);
        tab.setupWithViewPager(pager);

        predictNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        return mView;
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.REQUEST_PREDICT)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        try {

                            JSONArray all_temp = response.getJSONArray("new_predict");
                            JSONArray incoming_temp = response.getJSONArray("incoming");
                            JSONArray my_post_temp = response.getJSONArray("my_post");

                            all.clear();
                            incoming.clear();
                            my_post.clear();

                            for (int i = 0; i < all_temp.length(); i ++) {
                                try {
                                    all.add(new PredictionModel(all_temp.getJSONObject(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            for (int i = 0; i < incoming_temp.length(); i ++) {
                                try {
                                    incoming.add(new PredictionModel(incoming_temp.getJSONObject(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            for (int i = 0; i < my_post_temp.length(); i ++) {
                                try {
                                    my_post.add(new PredictionModel(my_post_temp.getJSONObject(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
    }
}
