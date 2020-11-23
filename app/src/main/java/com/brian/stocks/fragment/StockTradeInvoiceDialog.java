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

public class StockTradeInvoiceDialog extends DialogFragment {

    private Listener mListener;
    private int mView;
    private String Ticker, Qty, Price, Fee, Total, Type;
    private View rootView;
    private Button btnCancel, btnAccept;
    private TextView mTicker, mQty, mPrice,mFee, mType, mTotal;

    public StockTradeInvoiceDialog() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public StockTradeInvoiceDialog(int view, String ticker, String qty, String price, String fees, String type, String total) {
        mView = view;
        Ticker = ticker;
        Qty = qty;
        Price = price;
        Fee = fees;
        Type = type;
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

        mTicker = view.findViewById(R.id.invoice_ticker);
        mQty = view.findViewById(R.id.invoice_qty);
        mPrice = view.findViewById(R.id.invoice_price);
        mFee = view.findViewById(R.id.invoice_fee);
        mType = view.findViewById(R.id.invoice_type);
        mTotal = view.findViewById(R.id.invoice_total);

        btnAccept = view.findViewById(R.id.invoice_accept);
        btnCancel = view.findViewById(R.id.invoice_cancel);

        mTicker.setText(Ticker);
        mQty.setText(Qty);
        mPrice.setText(Price);
        mFee.setText(Fee);
        mType.setText(Type);
        mTotal.setText(Total);

        builder.setView(view);
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
