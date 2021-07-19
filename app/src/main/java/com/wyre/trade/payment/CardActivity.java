package com.wyre.trade.payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.braintreepayments.cardform.view.CardForm;
import com.wyre.trade.R;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.Card;
import com.wyre.trade.payment.adapters.CardAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CardActivity extends AppCompatActivity {
    LoadToast loadToast;
    ConfirmAlert confirmAlert;

    Button btnAdd;
    CardAdapter mAdapter;
    RecyclerView cardView;
    ArrayList<Card> cardList = new ArrayList<Card>();
    int withdrawal = 0;

    String cvcNo, cardNo, month, year;

    CardForm cardForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        if(getIntent().hasExtra("withdrawal"))
            withdrawal = getIntent().getIntExtra("withdrawal", 0);

        loadToast = new LoadToast(this);
        confirmAlert = new ConfirmAlert(CardActivity.this);

        cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
//                .cardholderName(CardForm.FIELD_REQUIRED)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
//                .mobileNumberExplanation("SMS is required on this number")
//                .actionLabel("Purchase")
                .setup(CardActivity.this);

        btnAdd = findViewById(R.id.btn_add_card);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardForm.isValid()) {
                    checkCard();
                } else {
                    Toast.makeText(getBaseContext(), "Please fill valid card", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardView = findViewById(R.id.card_view);

        mAdapter = new CardAdapter(CardActivity.this, cardList);
        cardView.setAdapter(mAdapter);
        cardView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setListener(new CardAdapter.Listener() {
            @Override
            public void OnDelete(final int position) {

                confirmAlert.confirm("Are you sure you want to delete this card?")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                deleteCard(position);
                                confirmAlert.delete();
                            }
                        })
                        .show();
            }
        });

        getCard();
    }

    private void checkCard() {

            cvcNo = cardForm.getCvv();
            cardNo = cardForm.getCardNumber();
            month = cardForm.getExpirationMonth();
            year = cardForm.getExpirationYear();

            sendAddCard("");

    }

    private void sendAddCard(String token) {
//        loadToast.show();
        confirmAlert.show();
        confirmAlert.process();
        AndroidNetworking.post(URLHelper.REQUEST_CARD)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addBodyParameter("no", cardNo)
                .addBodyParameter("month", month)
                .addBodyParameter("year", year)
                .addBodyParameter("cvc", cvcNo)
                .addBodyParameter("user_type", "0")
                .addBodyParameter("withdrawal", String.valueOf(withdrawal))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        loadToast.success();
//                        Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        confirmAlert.success(response.optString("message"));
                        getCard();
                    }

                    @Override
                    public void onError(ANError error) {
//                        loadToast.error();
                        confirmAlert.error(error.getErrorBody());
                    }
                });
    }

    private void deleteCard(final int position) {
//        loadToast.show();
        AndroidNetworking.delete(URLHelper.REQUEST_CARD+"/{id}")
                .addPathParameter("id", cardList.get(position).getCardId())
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addQueryParameter("withdrawal", String.valueOf(withdrawal))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        loadToast.success();
                            confirmAlert.success("Deleted successfully");
//                        Toast.makeText(getBaseContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();

                        cardList.remove(position);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(ANError error) {
//                        loadToast.error();
                        // handle error
//                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                        confirmAlert.error(error.getErrorBody());
                    }
                });
    }

    private void getCard() {
        loadToast.show();
            AndroidNetworking.get(URLHelper.REQUEST_CARD)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                    .addQueryParameter("withdrawal", String.valueOf(withdrawal))
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

                            mAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
