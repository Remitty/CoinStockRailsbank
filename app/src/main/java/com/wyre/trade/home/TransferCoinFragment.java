package com.wyre.trade.home;

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
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.NewsInfo;
import com.wyre.trade.stock.adapter.NewsAdapter;
import com.wyre.trade.usdc.PaymentUserActivity;
import com.wyre.trade.usdc.SendUsdcActivity;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TransferCoinFragment extends Fragment {
    TextView tvBalance;

    LinearLayout sendingLayout, addLayout;

    Double usdcBalance= 0.0;

    private LoadToast loadToast;
    private KProgressHUD loadProgress;

    private TextView mtvUserName;

    RecyclerView newsView;
    private ArrayList<NewsInfo> newsList = new ArrayList<>();
    NewsAdapter mAdapter;

    public TransferCoinFragment() {
        // Required empty public constructor
    }

    public static TransferCoinFragment newInstance() {
        TransferCoinFragment fragment = new TransferCoinFragment();
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
        View view = inflater.inflate(R.layout.fragment_transfer_coin, container, false);

        loadToast = new LoadToast(getActivity());

        loadProgress = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        tvBalance = view.findViewById(R.id.usdc_balance);

        sendingLayout = view.findViewById(R.id.ll_send_usdc);
        addLayout = view.findViewById(R.id.ll_add_contact);

        mtvUserName = view.findViewById(R.id.user_name);
        mtvUserName.setText(SharedHelper.getKey(getContext(), "fullName"));

        sendingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usdcBalance > 0)
                    startActivity(new Intent(getActivity(), SendUsdcActivity.class));
                else Toast.makeText(getContext(), "No balance", Toast.LENGTH_SHORT).show();
            }
        });

        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PaymentUserActivity.class));
            }
        });

        newsView = view.findViewById(R.id.news_view);
        mAdapter = new NewsAdapter(getActivity(), newsList);
        newsView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsView.setAdapter(mAdapter);

        getData();

        return view;
    }

    private void getData() {
//        loadToast.show();
        loadProgress.show();
        if(getContext() != null)
            AndroidNetworking.get(URLHelper.GET_HOME_DATA)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
//                            loadToast.success();
                            loadProgress.dismiss();
                            newsList.clear();
                            try {
                                usdcBalance = response.getDouble("usdc_balance");
                                tvBalance.setText(new DecimalFormat("###,###.##").format(usdcBalance));

                                String msg1 = response.getString("msgMarginAccountUsagePolicy");
                                SharedHelper.putKey(getContext(), "msgMarginAccountUsagePolicy", msg1);

                                String msg2 = response.getString("msgCoinSwapFeePolicy");
                                SharedHelper.putKey(getContext(), "msgCoinSwapFeePolicy", msg2);

                                String msg3 = response.getString("msgStockTradeFeePolicy");
                                SharedHelper.putKey(getContext(), "msgStockTradeFeePolicy", msg3);

                                String msg4 = response.getString("msgCoinWithdrawFeePolicy");
                                SharedHelper.putKey(getContext(), "msgCoinWithdrawFeePolicy", msg4);

                                JSONArray news = response.getJSONArray("news");
                                for (int i = 0; i < news.length(); i ++) {
                                    newsList.add(new NewsInfo(news.getJSONObject(i)));
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
                            Toast.makeText(getContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });
    }
}
