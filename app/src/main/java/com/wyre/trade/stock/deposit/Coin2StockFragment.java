package com.wyre.trade.stock.deposit;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.adapters.BottomCoinAdapter;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.CoinInfo;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Coin2StockFragment extends Fragment {
    private static String CoinSymbol, CoinUsdc="0.0", CoinRate, CoinIcon;
    private Double coinBalance = 0.0;
    private LoadToast loadToast;
    private ConfirmAlert confirmAlert;
    TextView mCoinBalance, mCoinUsdc, mCoinSymbol, mStockBalance;
    ImageView mCoinIcon;
    EditText mEditAmount;
    Button mBtnTransfer;
    TextView tvViewHistory;
    CheckBox mChkMargin;

    View mView;
    BottomCoinAdapter mAdapter;
    private RecyclerView recyclerView;
    private BottomSheetDialog dialog;

    private String CoinId;
    String stockBalance, coinUSD;

    private List<CoinInfo> coinList = new ArrayList<>();
    

    public Coin2StockFragment() {
    }


    public static Coin2StockFragment newInstance(String stockBalance, Double coinBalance, String coinUSD){
        Coin2StockFragment fragment = new Coin2StockFragment();
        fragment.stockBalance = stockBalance;
        fragment.coinBalance = coinBalance;
        fragment.coinUSD = coinUSD;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadToast = new LoadToast(getActivity());
        confirmAlert = new ConfirmAlert(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_coin2_stock, container, false);

        initComponents();

        initListeners();

        mCoinBalance.setText(new DecimalFormat("#,###.####").format(coinBalance));
        mCoinUsdc.setText("($ " + coinUSD+")");
        mStockBalance.setText("$ "+new DecimalFormat("#,###.##").format(Double.parseDouble(stockBalance)));

       return mView;

    }

    private void initListeners() {

//        mCoinIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getCoinAssets();
//            }
//        });

        mBtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEditAmount.getText().toString().isEmpty()) {
                    mEditAmount.setError("!");
                    return;
                }
                if(Double.parseDouble(mEditAmount.getText().toString()) > coinBalance) {
                    Toast.makeText(getContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mChkMargin.isChecked())
                    showMarginConfirmAlertDialog();
                else
                    showTransferConfirmAlertDialog();
            }
        });

        mAdapter.setListener(new BottomCoinAdapter.Listener() {
            @Override
            public void onSelectCoin(int position) {
                CoinInfo coin = coinList.get(position);
                CoinId = coin.getCoinId();
                CoinSymbol = coin.getCoinSymbol();
//                CoinBalance = coin.getCoinBalance();
                CoinUsdc = coin.getCoinUsdc();
//                mCoinBalance.setText(CoinBalance + " " + CoinSymbol);
                mCoinSymbol.setText(CoinSymbol);
                mCoinUsdc.setText("( $ "+CoinUsdc+" )");
                Picasso.with(getActivity())
                        .load(coin.getCoinIcon())
                        .placeholder(R.drawable.coin_bitcoin)
                        .error(R.drawable.coin_bitcoin)
                        .into(mCoinIcon);
                dialog.dismiss();
            }
        });

        tvViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Coin2StockHistoryActivity.class);
                intent.putExtra("kind", "COIN");
                startActivity(intent);
            }
        });

        mChkMargin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirm")
                            .setIcon(R.mipmap.ic_launcher_round)
                            .setMessage(SharedHelper.getKey(getContext(), "msgMarginAccountUsagePolicy"))
                            .setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mChkMargin.setChecked(false);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    private void initComponents() {
        mCoinBalance = mView.findViewById(R.id.coin_balance);
        mCoinSymbol = mView.findViewById(R.id.coin_symbol);
        mCoinUsdc = mView.findViewById(R.id.coin_amount);
        mStockBalance = mView.findViewById(R.id.stock_balance);
        mEditAmount = mView.findViewById(R.id.edit_transfer_amount);
        mBtnTransfer = mView.findViewById(R.id.btn_transfer_funds);
        mCoinIcon = mView.findViewById(R.id.coin_icon);
        mChkMargin = mView.findViewById(R.id.chk_margin);
        tvViewHistory = mView.findViewById(R.id.tv_view_history);

        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(dialogView);

        recyclerView = dialogView.findViewById(R.id.bottom_coins_list);
        mAdapter  = new BottomCoinAdapter(coinList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
    }

    private void showTransferConfirmAlertDialog() {

        confirmAlert.confirm("Are you sure transfer " + mEditAmount.getText()+"USDC ?")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        onTransferFunds();
                        confirmAlert.process();
                    }
                })
                .show();
    }

    private void showMarginConfirmAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getContext().getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher_round)
                .setMessage("Do you want to transfer " + mEditAmount.getText()+"USDC into your margin account?")
                .setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showTransferConfirmAlertDialog();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void onTransferFunds() {
//        loadToast.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", mEditAmount.getText().toString());
            jsonObject.put("type", 0);
            jsonObject.put("coin", "USDC");
            jsonObject.put("rate", 1);
            jsonObject.put("check_margin", mChkMargin.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.REQUEST_DEPOSIT_STOCK)
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
//                            loadToast.success();


                                try {
                                    mStockBalance.setText("$ " + response.getString("stock_balance"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    coinBalance = response.getDouble("usdc_balance");
                                    mCoinBalance.setText("$ " + response.getString("usdc_balance"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {

                                    mCoinUsdc.setText("$ " + response.getString("usdc_est_usd"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            confirmAlert.success(response.optString("message"));
                        }

                        @Override
                        public void onError(ANError error) {
//                            loadToast.error();
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            confirmAlert.error(error.getErrorBody());
                        }
                    });
    }


}
