package com.wyre.trade.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wyre.trade.R;
import com.wyre.trade.SharedPrefs;
import com.wyre.trade.helper.GridSpacingItemDecoration;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.main.SplashActivity;
import com.wyre.trade.model.NewsInfo;
import com.wyre.trade.model.TopStocks;
import com.wyre.trade.stock.adapter.NewsAdapter;
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
import java.util.List;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

public class HomeFragment extends Fragment {
    TextView tvBalance;

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

    BannerSlider bannerSlider;
    List<Banner> banners = new ArrayList<>();
    ArrayList<String> imageUrls = new ArrayList<>();

    LinearLayout emptyLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        loadToast = new LoadToast(getActivity());

        loadProgress = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        tvBalance = view.findViewById(R.id.usdc_balance);

        emptyLayout = view.findViewById(R.id.layout_empty);

        mtvUserName = view.findViewById(R.id.user_name);
        mtvUserName.setText(SharedHelper.getKey(getContext(), "fullName"));

        newsView = view.findViewById(R.id.news_view);
        mAdapter = new NewsAdapter(getActivity(), newsList);
        newsView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsView.setAdapter(mAdapter);

        GridSpacingItemDecoration gridSpacingDec = new GridSpacingItemDecoration(2, 10, false);

        losersView = view.findViewById(R.id.losers_view);
        loserAdapter = new TopStocksAdapter(getActivity(), losers);
        losersView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        losersView.addItemDecoration(gridSpacingDec);
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
        gainerAdapter = new TopStocksAdapter(getActivity(), gainers);
//        gainersView.setNestedScrollingEnabled(false);
        gainersView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        gainersView.addItemDecoration(gridSpacingDec);
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
        gainersView.setAdapter(gainerAdapter);

        bannerSlider = view.findViewById(R.id.banner_slider);

//        bannerSlider.setOnBannerClickListener(position -> {
//            if (banners.size() > 0) {
//
//                Intent i = new Intent(getActivity(), FullScreenViewActivity.class);
//                i.putExtra("imageUrls", imageUrls);
//                i.putExtra("position", position);
//                startActivity(i);
//            }
//        });

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
                            emptyLayout.setVisibility(View.GONE);
                            loadProgress.dismiss();
                            newsList.clear();
                            gainers.clear();
                            losers.clear();
                            try {
                                usdcBalance = response.getDouble("usdc_balance");
                                tvBalance.setText(new DecimalFormat("###,###.##").format(usdcBalance));

                                SharedHelper.putKey(getContext(), "stocks_balance", response.getString("stocks_balance"));

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

                                banners.clear();
                                imageUrls.clear();
//                                if(bannerSlider != null) bannerSlider.removeAllBanners();
                                for (int i = 0; i < response.getJSONArray("banners").length(); i++) {
                                    String path = response.getJSONArray("banners").getJSONObject(i).getString("image");
                                    if(!path.startsWith("http"))
                                        path = URLHelper.base + "storage/" + path;
                                    banners.add(new RemoteBanner(path));
                                    imageUrls.add(path);
                                    banners.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
                                }

                                if (banners.size() > 0)
                                    bannerSlider.setBanners(banners);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError error) {
//                            loadToast.error();
                            // handle error
                            loadProgress.dismiss();
                            Log.d("errorm", "" + error.getErrorBody());
                            if(error.getErrorBody().equals("null")) {
                                Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(error.getErrorBody().contains("Unauthenticated")) {
                                SharedPrefs sharedPrefs = new SharedPrefs(getActivity());
                                sharedPrefs.clearLogin();
                                startActivity(new Intent(getActivity(), SplashActivity.class));

                            } else
                                Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        }
                    });
    }
}
