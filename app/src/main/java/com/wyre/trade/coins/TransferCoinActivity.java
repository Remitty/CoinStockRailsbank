package com.wyre.trade.coins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.wyre.trade.R;
import com.wyre.trade.usdc.PaymentUserActivity;
import com.wyre.trade.usdc.SendUsdcActivity;

import net.steamcrafted.loadtoast.LoadToast;

public class TransferCoinActivity extends AppCompatActivity {

    private LoadToast loadToast;

    LinearLayout sendingLayout, addLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_coin);

        sendingLayout = findViewById(R.id.ll_send_usdc);
        addLayout = findViewById(R.id.ll_add_contact);
        sendingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(usdcBalance > 0)
                startActivity(new Intent(TransferCoinActivity.this, SendUsdcActivity.class));
//                else Toast.makeText(getContext(), "No balance", Toast.LENGTH_SHORT).show();
            }
        });

        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransferCoinActivity.this, PaymentUserActivity.class));
            }
        });
    }
}
