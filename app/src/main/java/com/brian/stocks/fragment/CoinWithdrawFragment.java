package com.brian.stocks.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class CoinWithdrawFragment extends Fragment {
    private Button mBtnWithdraw;
    private EditText mWalletAddress, mEditAmount;
    private View mView;
    private LoadToast loadToast;
    private static String CoinSymbol, CoinBalance, CoinUsdc;
    TextView mCoinBalance, mCoinUsdc;
    public CoinWithdrawFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CoinWithdrawFragment newInstance(String symbol, String balance, String usdc) {
        CoinWithdrawFragment fragment = new CoinWithdrawFragment();
        CoinSymbol = symbol;
        CoinBalance = balance;
        CoinUsdc = usdc;
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
        mView = inflater.inflate(R.layout.fragment_coin_withdraw, container, false);
        mCoinBalance = mView.findViewById(R.id.coin_balance);
        mCoinUsdc = mView.findViewById(R.id.coin_amount);
        mBtnWithdraw = mView.findViewById(R.id.btn_coin_withdraw);
        mWalletAddress = mView.findViewById(R.id.edit_withdraw_wallet_address);
        mEditAmount = mView.findViewById(R.id.edit_coin_withdraw_amount);

        mCoinBalance.setText(CoinBalance);
        mCoinUsdc.setText("( $ "+CoinUsdc+" )");

        mBtnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mWalletAddress.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please input receive address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mEditAmount.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please input coin amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Double.parseDouble(mEditAmount.getText().toString()) > Double.parseDouble(CoinBalance)){
                    Toast.makeText(getContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
                    return;
                }
                doWithdraw();
            }
        });

        return mView;
    }

    private void doWithdraw() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("currency", CoinSymbol);
            jsonObject.put("amount", mEditAmount.getText());
            jsonObject.put("address", mWalletAddress.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(getContext() != null)
        AndroidNetworking.post(URLHelper.COIN_WITHDRAW)
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
