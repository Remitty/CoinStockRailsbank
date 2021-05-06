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

        cvcNo = stripeWidget.getCard().getCVC();
        cardNo = stripeWidget.getCard().getNumber();
        month = stripeWidget.getCard().getExpMonth();
        year = stripeWidget.getCard().getExpYear();

        Stripe stripe = new Stripe(AddCardActivity.this, stripPubKey);
        Card card = stripeWidget.getCard();
        loadToast.show();
        stripe.createToken(card, new ApiResultCallback<Token>() {
            @Override
            public void onSuccess(@NonNull Token token) {
                sendAddCard(token.getId());
            }

            @Override
            public void onError(@NonNull Exception e) {
                loadToast.error();
            }
        });
    }

    private void sendAddCard(String token) {
        loadToast.show();
        AndroidNetworking.post(URLHelper.REQUEST_CARD)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addBodyParameter("stripe_token", token)
                .addBodyParameter("user_type", "0")
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddCardActivity.this);
                        builder.setIcon(R.mipmap.ic_launcher_round)
                                .setTitle("Alert")
                                .setMessage(error.getErrorBody())
                                .setPositiveButton("Ok", null)
                                .show();
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
