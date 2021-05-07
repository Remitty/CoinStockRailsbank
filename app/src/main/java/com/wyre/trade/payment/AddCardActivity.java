package com.wyre.trade.payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.wyre.trade.R;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.stock.deposit.StockDepositActivity;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONObject;

public class AddCardActivity extends AppCompatActivity {
    LoadToast loadToast;
    CardInputWidget stripeWidget;
    Button btnAdd;
    String stripPubKey;
    String cvcNo, cardNo;
    int month, year;
    int withdrawal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        loadToast = new LoadToast(this);

        if(getIntent() != null) {
            stripPubKey = getIntent().getStringExtra("stripe_pub_key");
            if(getIntent().hasExtra("withdrawal"))
                withdrawal = getIntent().getIntExtra("withdrawal", 0);
        }

        btnAdd = findViewById(R.id.btn_add_card);
        stripeWidget = findViewById(R.id.stripe_widget);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stripeWidget.getCard() != null && !stripeWidget.getCard().getNumber().isEmpty()) {
                    checkoutStripe();
                }
            }
        });
    }

    private void checkoutStripe() {
//        Stripe stripe = new Stripe(AddCardActivity.this, stripPubKey);
        Card card = stripeWidget.getCard();
        if(card.validateCard()) {
            cvcNo = stripeWidget.getCard().getCVC();
            cardNo = stripeWidget.getCard().getNumber();
            month = stripeWidget.getCard().getExpMonth();
            year = stripeWidget.getCard().getExpYear();

            sendAddCard("");
//            loadToast.show();
//            stripe.createToken(card, new ApiResultCallback<Token>() {
//                @Override
//                public void onSuccess(@NonNull Token token) {
//
//                    sendAddCard(token.getId());
//                }
//
//                @Override
//                public void onError(@NonNull Exception e) {
//                    loadToast.error();
//                }
//            });
        } else {
//            Toast.makeText(getBaseContext(), "Invalid card", Toast.LENGTH_SHORT).show();
            ConfirmAlert alert = new ConfirmAlert(AddCardActivity.this);
            alert.error("Invalid card");
        }

    }

    private void sendAddCard(String token) {
        loadToast.show();
        AndroidNetworking.post(URLHelper.REQUEST_CARD)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addBodyParameter("no", cardNo)
                .addBodyParameter("month", month+"")
                .addBodyParameter("year", year+"")
                .addBodyParameter("cvc", cvcNo+"")
                .addBodyParameter("user_type", "0")
                .addBodyParameter("withdrawal", String.valueOf(withdrawal))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToast.success();
                        Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        ConfirmAlert alert = new ConfirmAlert(AddCardActivity.this);
                        alert.error(error.getErrorBody());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(AddCardActivity.this, StockDepositActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
