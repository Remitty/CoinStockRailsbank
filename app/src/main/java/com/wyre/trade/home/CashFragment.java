package com.wyre.trade.home;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.cash.AddBankActivity;
import com.wyre.trade.cash.AddFriendActivity;
import com.wyre.trade.cash.InfoActivity;
import com.wyre.trade.home.adapters.BankTransactionAdapter;
import com.wyre.trade.cash.SendTargetActivity;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.adapters.CashBalancePagerAdapter;
import com.wyre.trade.model.BankInfo;
import com.wyre.trade.model.BankTransaction;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wyre.trade.R;
import com.kassisdion.library.ViewPagerWithIndicator;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CashFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    View mView;

    LinearLayout addLayout, sendLayout, bankLayout, contactLayout, accountLayout;
    TextView tvNoWallet;
    RecyclerView transactionsView;

    Spinner addCurrencySpinner;

    CashBalancePagerAdapter cashAdapter;
    ArrayList<BankInfo> currencies = new ArrayList<>();
    ArrayList<BankTransaction> transactions = new ArrayList<>();

    ArrayList<String> CURRENCIES = new ArrayList<>();

    ArrayAdapter<String> currencyAdapter;
    BankTransactionAdapter transactionAdapter;

    AlertDialog alertDialog;

    private ViewPager mViewPager;
    private ViewPagerWithIndicator mViewPagerWithIndicator;

    private LoadToast loadToast;

    public CashFragment() {
        // Required empty public constructor
    }

    public static CashFragment newInstance() {
        CashFragment fragment = new CashFragment();
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
        mView = inflater.inflate(R.layout.fragment_cash, container, false);
        loadToast = new LoadToast(getActivity());

        initComponents();

        String[] ADD_CURRENCIES = {"", "USD", "EUR", "GBP", "SGD"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_add_currency_popup, ADD_CURRENCIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addCurrencySpinner.setAdapter(adapter);

        addCurrencySpinner.setOnItemSelectedListener(this);

        transactionAdapter = new BankTransactionAdapter(transactions);
        transactionsView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionsView.setAdapter(transactionAdapter);

        mViewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        mViewPagerWithIndicator = (ViewPagerWithIndicator) mView.findViewById(R.id.viewPagerWithIndicator);
        cashAdapter = new CashBalancePagerAdapter(getActivity(), currencies);
        mViewPager.setAdapter(cashAdapter);
        mViewPagerWithIndicator.setViewPager(mViewPager);

        initListeners();

        getBankData();

//        showAlert();

        return mView;

    }

    private void showAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setIcon(R.mipmap.ic_launcher_round)
                .setTitle("Coming soon")
                .setMessage(" Make sure no one can access")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void initComponents() {
        addLayout = mView.findViewById(R.id.layout_add);
        sendLayout = mView.findViewById(R.id.layout_send);
        bankLayout = mView.findViewById(R.id.layout_bank);
        contactLayout = mView.findViewById(R.id.layout_contact);
        accountLayout = mView.findViewById(R.id.layout_account);

        tvNoWallet = mView.findViewById(R.id.no_wallet);

        addCurrencySpinner = mView.findViewById(R.id.add_currency_spinner);

        transactionsView = mView.findViewById(R.id.money_transaction);
    }

    private void initListeners() {
        sendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currencies.size() == 0){
                    Toast.makeText(getContext(), "Please add currency", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), SendTargetActivity.class);
                intent.putExtra("currency", currencies.get(mViewPager.getCurrentItem()).getCurrency());
                intent.putExtra("currency_id", currencies.get(mViewPager.getCurrentItem()).getCurrencyID());
                startActivity(intent);
            }
        });

        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addCurrencySpinner.performClick();
                addCurrencySpinner.setSelection(0, false);
            }
        });

        contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
            }
        });

        bankLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddBankActivity.class));
            }
        });

        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), InfoActivity.class));
            }
        });

    }

    private void getBankData() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        if(getContext() != null)
            AndroidNetworking.get(URLHelper.GET_BANK_DETAIL)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("info Edit ProfileGet", "" + response);
                            loadToast.success();
                            currencies.clear();
                            JSONArray currencies_array = response.optJSONArray("currencies");
                            for(int i = 0; i < currencies_array.length(); i ++) {
                                BankInfo bankInfo = new BankInfo();
                                bankInfo.setData(currencies_array.optJSONObject(i));
                                currencies.add(bankInfo);
                            }
                            cashAdapter.notifyDataSetChanged();

                            if(currencies.size() > 0 )
                                mViewPagerWithIndicator.setVisibility(View.VISIBLE);
                            else
                                tvNoWallet.setVisibility(View.VISIBLE);

                            JSONArray transactions_array = response.optJSONArray("transactions");

                            for(int i = 0; i < transactions_array.length(); i ++) {
                                BankTransaction transaction = new BankTransaction();
                                transaction.setData(transactions_array.optJSONObject(i));
                                transactions.add(transaction);
                            }

                            transactionAdapter.notifyDataSetChanged();

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

    private void sendAddCurrency(int currency) {
        loadToast.show();
        JSONObject object = new JSONObject();
        try {
            object.put("currency", currency);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("add currency params", object.toString());
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.REQUEST_ADD_BANK)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(object)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();

                            if(response.optBoolean("success")){

                                currencies.clear();
                                JSONArray currencies_array = response.optJSONArray("currencies");
                                for(int i = 0; i < currencies_array.length(); i ++) {
                                    BankInfo bankInfo = new BankInfo();
                                    bankInfo.setData(currencies_array.optJSONObject(i));
                                    currencies.add(bankInfo);
                                }
                                cashAdapter.notifyDataSetChanged();

                                if(currencies.size() > 0 ) {
                                    mViewPagerWithIndicator.setVisibility(View.VISIBLE);
                                    tvNoWallet.setVisibility(View.GONE);
                                }
                                else
                                    tvNoWallet.setVisibility(View.VISIBLE);
                            }

                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
        if (parent.getId() == R.id.add_currency_spinner) {
            String currency="";

            if(position == 1) { //USD
                currency = "USD";
            }
            if(position == 2) { //EUR
                currency = "EUR";
            }
            if(position == 3) { //GBP
                currency = "GBP";
            }
            if(position == 4) { //SGD
                currency = "SGD";
            }
            if(position != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add currency")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setMessage("Are you sure you want to add " + currency +"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendAddCurrency(position);
                            }
                        })
                        .setNegativeButton("No", null);
                builder.show();
//                addCurrencySpinner.setSelection(0);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

