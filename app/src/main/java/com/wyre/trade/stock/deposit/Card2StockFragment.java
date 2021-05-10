package com.wyre.trade.stock.deposit;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wyre.trade.R;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.Card;
import com.wyre.trade.payment.AddCardActivity;
import com.wyre.trade.payment.CardActivity;
import com.wyre.trade.stock.adapter.BottomCardAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Card2StockFragment extends Fragment {
    View mView;
    String stockBalance, stripe_pub_key;
    Card mCard;

    TextView mStockBalance, tvCardId;
    EditText mEditAmount;
    CheckBox mChkMargin;
    Button mBtnTransfer;
    TextView tvViewHistory, tvChangeCard, tvEditCard;
    LinearLayout llAddCard;

    BottomSheetDialog dialog;
    BottomCardAdapter mBottomAdapter;
    RecyclerView recyclerView;
    ArrayList<Card> cardList = new ArrayList<Card>();


    private LoadToast loadToast;
    private ConfirmAlert confirmAlert;

    public Card2StockFragment() {
        // Required empty public constructor
    }

    public static Card2StockFragment newInstance(String mStockBalance, Card card, String stripe_pub_key) {
        Card2StockFragment fragment = new Card2StockFragment();
        fragment.stockBalance = mStockBalance;
        fragment.mCard = card;
        fragment.stripe_pub_key = stripe_pub_key;
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
        mView = inflater.inflate(R.layout.fragment_card2_stock_fragemnt, container, false);

        initComponents();
        initListeners();

        mStockBalance.setText("$ " + new DecimalFormat("#,###.##").format(Double.parseDouble(stockBalance)));
        if(mCard != null) {
            tvCardId.setText("XXXX - XXXX - XXXX - " + mCard.getLastFour());
            llAddCard.setVisibility(View.GONE);
        } else {
            tvEditCard.setVisibility(View.GONE);
            tvChangeCard.setVisibility(View.GONE);
        }

        initBottomSheet();

        return mView;
    }

    private void initComponents() {
        mStockBalance = mView.findViewById(R.id.stock_balance);
        tvCardId = mView.findViewById(R.id.tv_card_id);
        mEditAmount = mView.findViewById(R.id.edit_transfer_amount);
        mBtnTransfer = mView.findViewById(R.id.btn_transfer_funds);
        mChkMargin = mView.findViewById(R.id.chk_margin);
        tvViewHistory = mView.findViewById(R.id.tv_view_history);
        tvEditCard = mView.findViewById(R.id.tv_edit_card);
        tvChangeCard = mView.findViewById(R.id.tv_change_card);
        llAddCard = mView.findViewById(R.id.ll_add_card);
    }

    private void initListeners() {
        tvEditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardActivity.class);
                intent.putExtra("stripe_pub_key", stripe_pub_key);
                startActivity(intent);
            }
        });
        llAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCardActivity.class);
                intent.putExtra("stripe_pub_key", stripe_pub_key);
                startActivity(intent);
            }
        });
        tvChangeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCard();
            }
        });
        mBtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCard == null) {
                    Toast.makeText(getContext(), "No card", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mEditAmount.getText().toString().equals("")) {
                    mEditAmount.setError("!");
                    return;
                }

                if(Double.parseDouble(mEditAmount.getText().toString()) > 500) {
                    ConfirmAlert alert = new ConfirmAlert(getActivity());
                    alert.alert("Limit is $500");
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
                intent.putExtra("kind", "Card");
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

    private void initBottomSheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(dialogView);

        recyclerView = dialogView.findViewById(R.id.bottom_coins_list);
        mBottomAdapter  = new BottomCardAdapter(cardList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mBottomAdapter);

        mBottomAdapter.setListener(new BottomCardAdapter.Listener() {
            @Override
            public void onSelectCard(int position) {
                mCard = cardList.get(position);
                tvCardId.setText("XXXX - XXXX - XXXX - " + mCard.getLastFour());

                dialog.dismiss();
            }
        });
    }

    private void showTransferConfirmAlertDialog() {
        confirmAlert.confirm("Are you sure transfer $ " + mEditAmount.getText()
                +"? "
//                + "\nYou must hold " + SharedHelper.getKey(getActivity(), "token_amount_for_stock_deposit_payment") + " PEPE to process this transaction."
                +"\nFee is "+SharedHelper.getKey(getActivity(), "stock_deposit_from_card_fee_percent")
                +"%.\nDaily limit is $"+SharedHelper.getKey(getActivity(), "stock_deposit_from_card_daily_limit")
                +".")
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

    private void getCard() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.REQUEST_CARD)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getActivity(),"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToast.success();

                        cardList.clear();

                        JSONArray cards = response.optJSONArray("cards");
                        for(int i = 0; i < cards.length(); i ++) {
                            try {
                                cardList.add(new Card(cards.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(cards.length() > 0) {
                            mBottomAdapter.notifyDataSetChanged();
                            dialog.show();
                        } else {
                            Toast.makeText(getContext(), "No card", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getActivity(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
    }

    private void onTransferFunds() {
//        loadToast.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", mEditAmount.getText().toString());
            jsonObject.put("type", 2); //deposit from card
            jsonObject.put("rate", 1);
            jsonObject.put("coin", mCard.getLastFour());
            jsonObject.put("cardId", mCard.getCardId());
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
                            stockBalance = response.optString("stock_balance");
                            mStockBalance.setText("$ " + new DecimalFormat("#,###.##").format(Double.parseDouble(stockBalance)));

                            confirmAlert.success(response.optString("message"));
//                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Log.d("card depost", error.getErrorBody());
                            confirmAlert.error(error.getErrorBody());

                        }
                    });
    }

}
