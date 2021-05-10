package com.wyre.trade.coins;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.adapters.BottomCoinAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.WebViewActivity;
import com.wyre.trade.model.CoinInfo;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import network.ramp.instantsdk.facade.RampInstantSDK;

public class FiatPaymentFragment extends Fragment {
    private static final int REQUEST_ONRAMPER = 100;
    private static final int REQUEST_XANPOOL = 200;

    private LoadToast loadToast;

    private ArrayList<CoinInfo> coinList = new ArrayList<>();
    private ArrayList<CoinInfo> assetList = new ArrayList<>();
    private String mOnramperApikey;
    private String onRamperCoins="";
    private String xanpoolApikey;

    BottomCoinAdapter mBottomAdapter;
    private RecyclerView recyclerView;
    private BottomSheetDialog dialog;

    View mView;
    CardView cardXanpool, cardRamp, cardOnramp;
    private int type;

    public FiatPaymentFragment() {
        // Required empty public constructor
    }

    public static FiatPaymentFragment newInstance(ArrayList<CoinInfo> coinList, String onramperkey, String onramperCoins, String xanpoolkey) {
        FiatPaymentFragment fragment = new FiatPaymentFragment();
        fragment.coinList = coinList;
        fragment.mOnramperApikey = onramperkey;
        fragment.onRamperCoins = onramperCoins;
        fragment.xanpoolApikey = xanpoolkey;
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
        mView =  inflater.inflate(R.layout.fragment_fiat_payment, container, false);
        cardRamp = mView.findViewById(R.id.card_ramp);
        cardXanpool = mView.findViewById(R.id.card_xanpool);
        cardOnramp = mView.findViewById(R.id.card_onramp);

        loadToast = new LoadToast(getActivity());

        initListener();
        initBottomSheet();
        return mView;
    }

    private void initListener() {

        cardRamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assetList.clear();
                type = 1;
                for (CoinInfo coin: coinList) {
                    if(coin.getBuyNowOption() > 0 && coin.getBuyNowOption() < 100)
                    assetList.add(coin);
                }
                mBottomAdapter.notifyDataSetChanged();
                dialog.show();
            }
        });

        cardXanpool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assetList.clear();
                type = 3;
                for (CoinInfo coin: coinList) {
                    if(coin.getBuyNowOption() > 1 && coin.getBuyNowOption() < 100)
                        assetList.add(coin);
                }
                mBottomAdapter.notifyDataSetChanged();
                dialog.show();
            }
        });

        cardOnramp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assetList.clear();
                type = 2;
                for (CoinInfo coin: coinList) {
                    if(coin.getBuyNowOption() > 2 && coin.getBuyNowOption() < 100)
                        assetList.add(coin);
                }
                mBottomAdapter.notifyDataSetChanged();
                dialog.show();
            }
        });
    }

    private void initBottomSheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(dialogView);

        recyclerView = dialogView.findViewById(R.id.bottom_coins_list);
        mBottomAdapter  = new BottomCoinAdapter(assetList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mBottomAdapter);

        mBottomAdapter.setListener(new BottomCoinAdapter.Listener() {
            @Override
            public void onSelectCoin(int position) {
                CoinInfo coin = assetList.get(position);
                doGenerateWalletAddress(coin.getCoinSymbol(), coin.getCoinId());

                dialog.dismiss();
            }
        });
    }

    private void doGenerateWalletAddress(final String symbol, String id) {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("coin", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("deposit param", jsonObject.toString());
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.COIN_DEPOSIT)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();
                            if(response.optBoolean("success")) {
                                String address = response.optString("address");

                                if(type == 1){
                                    doRamp(symbol, address);
                                } else if(type == 2){
                                    doOnramp(symbol, address);
                                }
                                else if(type == 3){ //xanpool
                                    doXanpool(symbol, address);
                                }
                            }
                            else
                                Toast.makeText(getContext(), response.optString("error"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                            Log.d("errorm", "" + error.getErrorBody());

                        }
                    });
    }

    private void doXanpool(String symbol, String address) {
        String base = "https://checkout.sandbox.xanpool.com/";
        String apikey = "?apiKey="+xanpoolApikey;
        String wallet = "&wallet=" + address;
        String cryptoCurrency = "&cryptoCurrency=" + symbol;
        String transactionType = "&transactionType=";
        String isWebview = "&isWebview=true";
        String partnerData = "&partnerData=88824d8683434f4e";

        String url = base + apikey + wallet + cryptoCurrency + transactionType + isWebview;
        Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
        browserIntent.putExtra("uri", url);
        startActivityForResult(browserIntent, REQUEST_XANPOOL);
    }

    private void doRamp(String symbol, String address) {
        RampInstantSDK rampInstantSDK = new RampInstantSDK(
                getContext(),
                address,
                "https://cdn-images-1.medium.com/max/2600/1*nqtMwugX7TtpcS-5c3lRjw.png",
                "Coins",
                "com.wyre.trade",
                symbol,
                "",
                "",
                "https://widget-instant.ramp.network/"
//                                            "https://ri-widget-staging-ropsten.firebaseapp.com/"
        );
        rampInstantSDK.show();
    }

    private void doOnramp(String symbol, String address) {
        String coin_address = "&wallets="+symbol+":"+address;
//                                    String coin_address = "";
        String excludeCryptos = "&excludeCryptos=EOS,USDT,XLM,BUSD,GUSD,HUSD,PAX,USDS";
        String url = "https://widget.onramper.com?color=1d2d50&apiKey="+mOnramperApikey+"&defaultCrypto="
                +symbol+excludeCryptos+coin_address+"&onlyCryptos="+onRamperCoins
                +"&isAddressEditable=false";
        Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
        browserIntent.putExtra("uri", url);
        startActivityForResult(browserIntent, REQUEST_ONRAMPER);
    }
}
