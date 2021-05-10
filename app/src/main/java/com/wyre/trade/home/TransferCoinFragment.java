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
import com.wyre.trade.model.TopStocks;
import com.wyre.trade.stock.adapter.NewsAdapter;
import com.wyre.trade.stock.adapter.StocksAdapter;
import com.wyre.trade.stock.adapter.TopStocksAdapter;
import com.wyre.trade.stock.stocktrade.TopStocksTradeActivity;
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

    RecyclerView gainersView;
    ArrayList<TopStocks> gainers = new ArrayList<>();
    TopStocksAdapter gainerAdapter;

    RecyclerView losersView;
    ArrayList<TopStocks> losers = new ArrayList<>();
    TopStocksAdapter loserAdapter;

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

        losersView = view.findViewById(R.id.losers_view);
        loserAdapter = new TopStocksAdapter(getActivity(), losers);
        losersView.setLayoutManager(new LinearLayoutManager(getContext()));
        loserAdapter.setListener(new TopStocksAdapter.Listener() {
            @Override
            public void OnGoToTrade(int position) {
                TopStocks stock = losers.get(position);
                Intent intent = new Intent(getActivity(), TopStocksTradeActivity.class);
                intent.putExtra("stock_symbol", stock.getSymbol());
                intent.putExtra("stock_name", stock.getCompanyName());
                intent.putExtra("stock_price", stock.getPrice()+"");
                intent.putExtra("stock_today_change", stock.getChanges());
                intent.putExtra("stock_today_change_perc", stock.getChangesPercentage());
                startActivity(intent);
            }
        });
        losersView.setAdapter(loserAdapter);

        gainersView = view.findViewById(R.id.gainers_view);
        gainersView.setNestedScrollingEnabled(false);
        gainersView.setLayoutManager(new LinearLayoutManager(getContext()));
        gainerAdapter = new TopStocksAdapter(getActivity(), gainers);
        gainerAdapter.setListener(new TopStocksAdapter.Listener() {
            @Override
            public void OnGoToTrade(int position) {
                TopStocks stock = gainers.get(position);
                Intent intent = new Intent(getActivity(), TopStocksTradeActivity.class);
                intent.putExtra("stock_symbol", stock.getSymbol());
                intent.putExtra("stock_name", stock.getCompanyName());
                intent.putExtra("stock_price", stock.getPrice()+"");
                intent.putExtra("stock_today_change", stock.getChanges());
                intent.putExtra("stock_today_change_perc", stock.getChangesPercentage());
                startActivity(intent);
            }
        });
        gainersView.setLayoutManager(new LinearLayoutManager(getContext()));
        gainersView.setAdapter(gainerAdapter);



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
                            gainers.clear();
                            losers.clear();
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

                                String msg5 = response.getString("token_amount_for_stock_deposit_payment");
                                SharedHelper.putKey(getContext(), "token_amount_for_stock_deposit_payment", msg5);

                                String msg6 = response.getString("stock_deposit_from_card_fee_percent");
                                SharedHelper.putKey(getContext(), "stock_deposit_from_card_fee_percent", msg6);

                                String msg7 = response.getString("stock_deposit_from_card_daily_limit");
                                SharedHelper.putKey(getContext(), "stock_deposit_from_card_daily_limit", msg7);

                                JSONArray news = response.getJSONArray("news");
                                for (int i = 0; i < news.length(); i ++) {
                                    newsList.add(new NewsInfo(news.getJSONObject(i)));
                                }
                                mAdapter.notifyDataSetChanged();

                                JSONArray topgainers = response.getJSONArray("top_stocks_gainers");
                                for (int i = 0; i < topgainers.length(); i ++) {
                                    gainers.add(new TopStocks(topgainers.getJSONObject(i)));
                                }
                                gainerAdapter.notifyDataSetChanged();

                                JSONArray toplosers = response.getJSONArray("top_stocks_losers");
                                for (int i = 0; i < toplosers.length(); i ++) {
                                    losers.add(new TopStocks(toplosers.getJSONObject(i)));
                                }
                                loserAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError error) {
//                            loadToast.error();
                            // handle error
                            loadProgress.dismiss();
                            Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });
    }
}
