package com.wyre.trade.stock.deposit;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.wyre.trade.R;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.payment.AddPaypalActivity;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Paypal2StockFragment extends Fragment {
    View mView;
    String stockBalance;
    JSONObject paypal;

    TextView mStockBalance, tvpaypal;
    EditText mEditAmount;
    CheckBox mChkMargin;
    Button mBtnTransfer;
    TextView tvViewHistory, tvAdd;

    public static final int PAYPAL_REQUEST_CODE = 123;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;


    private LoadToast loadToast;
    private ConfirmAlert confirmAlert;

    public Paypal2StockFragment() {
        // Required empty public constructor
    }

    public static Paypal2StockFragment newInstance(String mStockBalance, JSONObject paypal) {
        Paypal2StockFragment fragment = new Paypal2StockFragment();
        fragment.stockBalance = mStockBalance;
        fragment.paypal = paypal;
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
        mView = inflater.inflate(R.layout.activity_paypal2_stock, container, false);

        initComponents();
        initListeners();

        mStockBalance.setText("$ " + new DecimalFormat("#,###.##").format(Double.parseDouble(stockBalance)));
        if(!paypal.optString("paypal").isEmpty()) {
            tvpaypal.setText(paypal.optString("paypal"));
            tvAdd.setText("Edit");
        }

        return mView;
    }

    private void initComponents() {
        mStockBalance = mView.findViewById(R.id.stock_balance);
        tvpaypal = mView.findViewById(R.id.tv_paypal);
        mEditAmount = mView.findViewById(R.id.edit_transfer_amount);
        mBtnTransfer = mView.findViewById(R.id.btn_transfer_funds);
        mChkMargin = mView.findViewById(R.id.chk_margin);
        tvViewHistory = mView.findViewById(R.id.tv_view_history);
        tvAdd = mView.findViewById(R.id.tv_add_paypal);
    }

    private void initListeners() {
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPaypalActivity.class);
                if(!paypal.optString("paypal").isEmpty())
                    intent.putExtra("paypal", paypal.optString("paypal"));
                startActivity(intent);
            }
        });
        mBtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paypal.optString("paypal").isEmpty()) {
                    Toast.makeText(getContext(), "No paypal", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mEditAmount.getText().toString().equals("")) {
                    mEditAmount.setError("!");
                    return;
                }

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
                intent.putExtra("kind", "Paypal");
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
        confirmAlert.confirm("Are you sure transfer $ " + mEditAmount.getText()
                + "?\nYou must hold " + SharedHelper.getKey(getActivity(), "token_amount_for_stock_deposit_payment") + " PEPE to process this transaction."
                +"\nFee is "+SharedHelper.getKey(getActivity(), "stock_deposit_from_card_fee_percent"))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        handlePaypal();
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
//        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", mEditAmount.getText().toString());
            jsonObject.put("type", 3); //deposit from paypal
            jsonObject.put("rate", 1);
            jsonObject.put("coin", paypal.optString("paypal"));
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
                            mStockBalance.setText("$ " + response.optString("stock_balance"));

                                confirmAlert.success(response.optString("message"));
//                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            confirmAlert.error(error.getErrorBody());

                        }
                    });
    }

    private void handlePaypal() {
//        loadToast.show();
        PayPalConfiguration
                config = null;
        try {
            config = new PayPalConfiguration()
                    // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                    // or live (ENVIRONMENT_PRODUCTION)
//                    .environment(CONFIG_ENVIRONMENT)
                    .environment(paypal.getString("mode"))
//                    .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                    .clientId(paypal.getString("client_id"))
                    .merchantName(paypal.getString("merchant_name"));
            Intent intent = new Intent(getActivity(), PayPalService.class);

            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            //Creating a paypalpayment
            PayPalPayment payment = new PayPalPayment(new BigDecimal(mEditAmount.getText().toString()), paypal.getString("currency"), "wyretrade",
                    PayPalPayment.PAYMENT_INTENT_SALE);
//
//            //Creating Paypal Payment activity intent
            Intent intent1 = new Intent(getActivity(), PaymentActivity.class);
//
//            //putting the paypal configuration to the intent
            intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//
//            //Puting paypal payment to the intent
            intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
//
//            //Starting the intent activity for result
//            //the request code will be used on the method onActivityResult
            startActivityForResult(intent1, PAYPAL_REQUEST_CODE);
        } catch (JSONException e) {
//            loadToast.error();
            confirmAlert.error("Network error");
            e.printStackTrace();
        } catch (NumberFormatException e) {
//            loadToast.error();
            confirmAlert.error("Network error");
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
//                        paymentId = confirm.toJSONObject()
//                                .getJSONObject("response").getString("id");
                        onTransferFunds();

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
                loadToast.hide();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                loadToast.error();
            }
        }

    }

}
