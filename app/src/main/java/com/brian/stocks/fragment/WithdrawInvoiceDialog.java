package com.brian.stocks.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brian.stocks.R;

public class WithdrawInvoiceDialog extends DialogFragment {

    private Listener mListener;
    private int mView;
    private String ReqAmount, Rate, WithdrawFee, Total;
    private View rootView;
    private Button btnCancel, btnAccept;
    private TextView mReqAmount, mRate, mWithdrawFee, mTotal;

    public WithdrawInvoiceDialog() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public WithdrawInvoiceDialog(int view, String reqAmount, String rate, String withdrawFee, String total) {
        mView = view;
        ReqAmount = reqAmount;
        Rate = rate;
        WithdrawFee = withdrawFee;
        Total = total;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(mView, null);

        mReqAmount = view.findViewById(R.id.invoice_req_amount);
        mRate = view.findViewById(R.id.invoice_rate);
        mWithdrawFee = view.findViewById(R.id.invoice_withdraw_fee);
        mTotal = view.findViewById(R.id.invoice_total);

        btnAccept = view.findViewById(R.id.invoice_accept);
        btnCancel = view.findViewById(R.id.invoice_cancel);

        mReqAmount.setText(ReqAmount);
        mRate.setText(Rate);
        mWithdrawFee.setText(WithdrawFee);
        mTotal.setText(Total);

        builder.setView(view);
        // Add action buttons
//                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // sign in the user ...
//                        mListener.onOk();
//                    }
//                });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOk();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        // TODO: Update argument type and name
        void onOk();
        void onCancel();
    }
}
