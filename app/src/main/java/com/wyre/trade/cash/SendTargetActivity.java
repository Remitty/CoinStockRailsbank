package com.wyre.trade.cash;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.cash.adapters.BankAdapter;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.BankInfo;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.wyre.trade.R;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SendTargetActivity extends AppCompatActivity {

    ImageButton btnAddBank, btnAddFriend;
    RecyclerView bankRecycler;
    List<BankInfo> bankList = new ArrayList<>();
    BankAdapter mBankAdapter;

    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        if(getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        mBankAdapter = new BankAdapter(bankList);
        mBankAdapter.setListener(new BankAdapter.Listener() {
            @Override
            public void onDelBank(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SendTargetActivity.this);
                builder.setTitle(R.string.app_name)
                        .setMessage("Are you sure you want to remove this bank?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                removeBank(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }

            @Override
            public void onGotoSend(int position) {
                BankInfo bankInfo = bankList.get(position);
                Intent intent = new Intent(SendTargetActivity.this, SendCashActivity.class);
                intent.putExtra("getter_id", bankInfo.getId());
                intent.putExtra("bank_currency", bankInfo.getCurrency());
                intent.putExtra("bank_name", bankInfo.getBankAlias());
                intent.putExtra("bank_type", bankInfo.getBankType());
                intent.putExtra("currency", getIntent().getStringExtra("currency"));
                intent.putExtra("currency_id", getIntent().getStringExtra("currency_id"));
                startActivity(intent);
            }
        });
        bankRecycler = findViewById(R.id.bank_recycler);
        bankRecycler.setAdapter(mBankAdapter);
        bankRecycler.setLayoutManager(new LinearLayoutManager(this));

        btnAddBank = findViewById(R.id.btn_add_bank);
        btnAddBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendTargetActivity.this, AddBankActivity.class));
            }
        });

        btnAddFriend = findViewById(R.id.btn_add_friend);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendTargetActivity.this, AddFriendActivity.class));
            }
        });

        getBankList();
    }

    private void removeBank(int position) {
        BankInfo bankInfo = bankList.get(position);
        loadToast.show();
        JSONObject param = new JSONObject();
        try {
            param.put("id", bankInfo.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("remove param", param.toString());
        AndroidNetworking.post(URLHelper.REQUEST_REMOVE_FRIEND_BANK)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addJSONObjectBody(param)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();

                        bankList.clear();
                        JSONArray data = response.optJSONArray("data");
                        for (int i = 0; i < data.length(); i ++) {
                            JSONObject object = data.optJSONObject(i);
                            BankInfo bankInfo = new BankInfo();
                            bankInfo.setData(object);
                            bankList.add(bankInfo);
                        }

                        mBankAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }

    private void getBankList() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.REQUEST_FRIEND_BANK_LIST)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();

                        bankList.clear();
                        JSONArray data = response.optJSONArray("data");
                        for (int i = 0; i < data.length(); i ++) {
                            JSONObject object = data.optJSONObject(i);
                            BankInfo bankInfo = new BankInfo();
                            bankInfo.setData(object);
                            bankList.add(bankInfo);
                        }

                        mBankAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
