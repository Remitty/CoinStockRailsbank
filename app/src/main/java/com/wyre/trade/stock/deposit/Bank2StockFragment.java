package com.wyre.trade.stock.deposit;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.PlaidConnect;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class Bank2StockFragment extends Fragment {
    View mView;
    String stockBalance, bankBalance;
    Double usd = 0.0;

    TextView mStockBalance, mBankBalance;
    EditText mEditAmount;
    CheckBox mChkMargin;
    Button mBtnTransfer, btnConnectBank;
    TextView tvViewHistory;

    private LoadToast loadToast;

    public Bank2StockFragment() {
        // Required empty public constructor
    }

    public static Bank2StockFragment newInstance(String mStockBalance, String usdBalance) {
        Bank2StockFragment fragment = new Bank2StockFragment();
        fragment.stockBalance = mStockBalance;
        fragment.bankBalance = usdBalance;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadToast = new LoadToast(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_bank2_stock, container, false);

        initComponents();
        initListeners();

        mStockBalance.setText("$ " + new DecimalFormat("#,###.##").format(Double.parseDouble(stockBalance)));
        if(bankBalance.equals("No wallet"))
            mBankBalance.setText(bankBalance);
        else {
            usd = Double.parseDouble(bankBalance);
            mBankBalance.setText(new DecimalFormat("#,###.##").format(usd));
        }

        return mView;
    }

    private void initComponents() {
        mStockBalance = mView.findViewById(R.id.stock_balance);
        mBankBalance = mView.findViewById(R.id.bank_balance);
        mEditAmount = mView.findViewById(R.id.edit_transfer_amount);
        mBtnTransfer = mView.findViewById(R.id.btn_transfer_funds);
        mChkMargin = mView.findViewById(R.id.chk_margin);
        tvViewHistory = mView.findViewById(R.id.tv_view_history);
        btnConnectBank = mView.findViewById(R.id.btn_connect_bank);
    }

    private void initListeners() {
        btnConnectBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlaidConnect(getActivity()).openPlaid();
            }
        });
        mBtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(bankBalance.equals("No wallet")) {
//                    Toast.makeText(getContext(), "No wallet", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if(mEditAmount.getText().toString().equals("")) {
                    mEditAmount.setError("!");
                    return;
                }
//                if(usd == 0) {
//                    Toast.makeText(getContext(), "No balance", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(Double.parseDouble(mEditAmount.getText().toString()) > usd) {
//                    Toast.makeText(getContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if(mChkMargin.isChecked())
                    showMarginConfirmAlertDialog();
                else
                    showTransferConfirmAlertDialog();
            }
        });

        tvViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Coin2StockHistoryActivity.class);
                intent.putExtra("kind", "Bank");
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

    private void showTransferConfirmAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getContext().getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher_round)
                .setMessage("Are you sure transfer $ " + mEditAmount.getText()+"?")
                .setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onTransferFunds();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showMarginConfirmAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getContext().getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher_round)
                .setMessage("Do you want to transfer $ " + mEditAmount.getText()+" into your margin account?")
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
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void onTransferFunds() {
        loadToast.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", mEditAmount.getText().toString());
            jsonObject.put("type", 1);
            jsonObject.put("rate", 1);
            jsonObject.put("coin", "Bank");
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
                            loadToast.success();

                                mStockBalance.setText("$ " + response.optString("stock_balance"));
                                mBankBalance.setText("$ " + response.optString("usd_balance"));


                                Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setIcon(R.mipmap.ic_launcher_round)
                                    .setTitle("Alert")
                                    .setMessage(error.getErrorBody())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!new PlaidConnect(getActivity()).myPlaidResultHandler.onActivityResult(requestCode, resultCode, data)) {
//            Log.i(MainActivityJava.class.getSimpleName(), "Not handled");
        }
    }

}
