package com.wyre.trade.payment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.Card;
import com.wyre.trade.payment.adapters.CardAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity {
    LoadToast loadToast;
    Button btnAdd;
    CardAdapter mAdapter;
    RecyclerView cardView;
    ArrayList<Card> cardList = new ArrayList<Card>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        loadToast = new LoadToast(this);

        btnAdd = findViewById(R.id.btn_add_card);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardActivity.this, AddCardActivity.class);
                intent.putExtra("stripe_pub_key", getIntent().getStringExtra("stripe_pub_key"));
                startActivity(intent);
            }
        });

        cardView = findViewById(R.id.card_view);

        mAdapter = new CardAdapter(this, cardList);
        cardView.setAdapter(mAdapter);
        cardView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setListener(new CardAdapter.Listener() {
            @Override
            public void OnDelete(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
                builder.setIcon(R.mipmap.ic_launcher_round)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to delete this card?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCard(position);
                            }
                        })
                        .show();
            }
        });

        getCard();
    }

    private void deleteCard(final int position) {
        loadToast.show();
        AndroidNetworking.delete(URLHelper.REQUEST_CARD+"/{id}")
                .addPathParameter("id", cardList.get(position).getCardId())
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToast.success();

                        Toast.makeText(getBaseContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();

                        cardList.remove(position);
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

    private void getCard() {
        loadToast.show();
            AndroidNetworking.get(URLHelper.REQUEST_CARD)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
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
