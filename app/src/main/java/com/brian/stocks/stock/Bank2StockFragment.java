package com.brian.stocks.stock;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.BankInfo;
import com.brian.stocks.model.TransferInfo;
import com.brian.stocks.stock.adapter.StockTransferAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Bank2StockFragment extends Fragment {
    View mView;
    String stockBalance, bankBalance;

    RecyclerView mTransferListView;
    StockTransferAdapter mTransferAdapter;

    TextView mStockBalance, mBankBalance;
    EditText mEditAmount;
    CheckBox mChkMargin;
    Button mBtnTransfer;
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
        else
            mBankBalance.setText(new DecimalFormat("#,###.##").format(Double.parseDouble(bankBalance)));

        return mView;
    }

    private void initComponents() {
        mStockBalance = mView.findViewById(R.id.stock_balance);
        mBankBalance = mView.findViewById(R.id.bank_balance);
        mEditAmount = mView.findViewById(R.id.edit_transfer_amount);
        mBtnTransfer = mView.findViewById(R.id.btn_transfer_funds);
        mTransferListView = mView.findViewById(R.id.list_transfer_view);
        mChkMargin = mView.findViewById(R.id.chk_margin);
    }

    private void initListeners() {
        mBtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bankBalance.equals("No wallet"))
                    Toast.makeText(getContext(), "No wallet", Toast.LENGTH_SHORT).show();

                if(mEditAmount.getText().toString().equals("")) {
                    mEditAmount.setError("!");
                    return;
                }
//                if(Double.parseDouble(mEditAmount.getText().toString()) > Double.parseDouble(CoinUsdc)) {
//                    Toast.makeText(getContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if(mChkMargin.isChecked())
                    showMarginConfirmAlertDialog();
                else
                    showTransferConfirmAlertDialog();
            }
        });
    }

    private void showTransferConfirmAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getContext().getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
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
                .setIcon(R.mipmap.ic_launcher)
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
            jsonObject.put("amount", mEditAmount.getText());
            jsonObject.put("type", 1);
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
                            loadToast.success();

//                            stocksList.clear();
                            if(response.optBoolean("success")) {
//                                JSONArray stocks = response.optJSONArray("stock_transfer");
//                                for (int i = 0; i < stocks.length(); i++) {
//                                    try {
//                                        Log.d("transferitem", stocks.get(i).toString());
//                                        stocksList.add(new TransferInfo((JSONObject) stocks.get(i)));
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }

                                mStockBalance.setText("$ " + response.optString("stock_balance"));
                                mBankBalance.setText("$ " + response.optString("usd_balance"));

//                                mTransferAdapter.notifyDataSetChanged();
                            }

                            if(getContext() != null)
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
