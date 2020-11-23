package com.brian.stocks.stock.stockwithdraw;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class StockWithdrawFragment extends Fragment {
    LoadToast loadToast;
    private Button mBtnWithdraw;
    private EditText mWalletAddress, mEditAmount;
    TextView mStockBalance, mUSDCRate;
    RadioGroup radioGroup;
    AppCompatRadioButton radioButton;
    String StockBalance = "0", USDCRate="0", DAIRate="0", WithdrawFee="0";
    
    public StockWithdrawFragment() {
        // Required empty public constructor
    }

    public static StockWithdrawFragment newInstance(String stockBalance, String USDCRate) {
        StockWithdrawFragment fragment = new StockWithdrawFragment();
        fragment.StockBalance = stockBalance;
        fragment.USDCRate = USDCRate;

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
        View view = inflater.inflate(R.layout.fragment_stock_withdraw, container, false);

        loadToast = new LoadToast(getActivity());
        
        mStockBalance = view.findViewById(R.id.stock_balance);
        mUSDCRate = view.findViewById(R.id.stock_usdc_rate);
        mBtnWithdraw = view.findViewById(R.id.btn_coin_withdraw);
        mWalletAddress = view.findViewById(R.id.edit_withdraw_wallet_address);
        mEditAmount = view.findViewById(R.id.edit_coin_withdraw_amount);
        radioGroup = view.findViewById(R.id.rdg_withdraw_coins);

//        CoinBalance = SharedHelper.getKey(getContext(), "coin_balance");

        mBtnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mEditAmount.getText().toString().equals("")) {
                    mEditAmount.setText("!");
                    return;
                }
                if(Double.parseDouble(mEditAmount.getText().toString()) > Double.parseDouble(USDCRate)){
                    Toast.makeText(getContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(Double.parseDouble(mEditAmount.getText().toString()) < Double.parseDouble(WithdrawFee)){
//                    Toast.makeText(getContext(), "You have to request at least "+WithdrawFee, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                int selectedId = radioGroup.getCheckedRadioButtonId();
//                if(selectedId < 0){
//                    Toast.makeText(getContext(), "Please select withdraw coin", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                radioButton = findViewById(selectedId);
                showInvoiceDialog();
            }
        });

        mStockBalance.setText("$ "+StockBalance);
        mUSDCRate.setText("(" + USDCRate+" USDC" + ")");

//        getBalances();

        return view;
    }

    private void showInvoiceDialog() {
        BigDecimal amount = new BigDecimal(mEditAmount.getText().toString());
        BigDecimal rate;
        rate = new BigDecimal(USDCRate);

        if(amount.compareTo(rate) > 1) {
            Toast.makeText(getContext(), "Insufficient funds", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Double.parseDouble(amount.toString()) <= 7.5) {
            Toast.makeText(getContext(), "You have to request amount than 7.5USDC at least.", Toast.LENGTH_SHORT).show();
            return;
        }

        Double total = Double.parseDouble(amount.toString()) - 7.5;
//        if(radioButton.getText().toString().equals("USDC")){
//        }
//        else{
//            rate = new BigDecimal(DAIRate);
//        }
//        BigDecimal withdraw_fee = new BigDecimal(WithdrawFee);
//        String total = amount.multiply(rate).subtract(withdraw_fee).toString();

//        final WithdrawInvoiceDialog dialog = new WithdrawInvoiceDialog(R.layout.dialog_withdraw_invoice, "$ "+mEditAmount.getText().toString(), "$1 = "+rate+radioButton.getText().toString(), "$ "+WithdrawFee, "$ "+total);
//        dialog.setListener(new WithdrawInvoiceDialog.Listener() {
//
//            @Override
//            public void onOk() {
//                dialog.dismiss();
//                doWithdraw();
//            }
//
//            @Override
//            public void onCancel() {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show(getSupportFragmentManager(), "deposit");
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to withdraw " + amount + "USDC ?" + " Fee is 7.5USDC. Total is " + total + "USDC.")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doWithdraw();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // dismiss the dialog
                                dialogInterface.dismiss();

                                // dismiss the bottomsheet
                            }
                        }).show();
    }

    private void doWithdraw() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("currency", "USDC");
            jsonObject.put("amount", mEditAmount.getText());
            jsonObject.put("address", mWalletAddress.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(URLHelper.STOCK_WITHDRAW)
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
                            StockBalance = response.optString("stock_balance");
                            mStockBalance.setText("$ "+StockBalance);
                            USDCRate = response.optString("usdc_balance");
                            mUSDCRate.setText("(" + USDCRate+" USDC" + ")");
                        }
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

    private void getBalances() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        if(getContext() != null)
            AndroidNetworking.get(URLHelper.STOCK_WITHDRAW)
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

                            StockBalance = response.optString("stock_balance");
                            USDCRate = response.optString("stock2usdc");
                            mStockBalance.setText("$ "+StockBalance);
                            mUSDCRate.setText("(" + USDCRate+" USDC" + ")");
                            WithdrawFee = response.optString("withdraw_fee");
                            DAIRate = response.optString("dai_rate");
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
