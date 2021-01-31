package com.brian.stocks.usdc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.brian.stocks.home.HomeActivity;
import com.brian.stocks.home.adapters.AutoUserAdapter;
import com.brian.stocks.usdc.adapters.TransferCoinHistoryAdapter;
import com.brian.stocks.usdc.adapters.UserContactAdapter;
import com.brian.stocks.model.ContactUser;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SendUsdcActivity extends AppCompatActivity {
    TextView tvBalance;

    RecyclerView payHistoryView, contactListView;
    Button btnPay;

    TextView tvTo;
    EditText editAmount;

    private BottomSheetDialog dialog;

    ArrayList<ContactUser> users = new ArrayList();
    ArrayList<ContactUser> usersTemp = new ArrayList();
    ArrayList<JSONObject> payHistory = new ArrayList();

    String selectedUserEmail, usdcBalance="0.0", amount;

    AutoUserAdapter userAdapter;
    UserContactAdapter userContactAdapter;
    TransferCoinHistoryAdapter historyAdapter;

    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_usdc);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pay USDC");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        tvBalance = findViewById(R.id.usdc_balance);

        payHistoryView = findViewById(R.id.pay_history_view);
        btnPay = findViewById(R.id.btn_pay);

        tvTo = findViewById(R.id.edit_pay_to);
        editAmount = findViewById(R.id.edit_pay_amount);

        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        contactListView = dialogView.findViewById(R.id.bottom_coins_list);
        userContactAdapter  = new UserContactAdapter(users, false);
        contactListView.setLayoutManager(new LinearLayoutManager(this));
        contactListView.setAdapter(userContactAdapter);
        userContactAdapter.setListener(new UserContactAdapter.Listener() {
            @Override
            public void onSelect(int position) {
                dialog.hide();
                selectedUserEmail = users.get(position).getEmail();
                tvTo.setText(users.get(position).getName());
            }

            @Override
            public void onDelete(int position) {

            }
        });

//        userAdapter = new AutoUserAdapter(getActivity(), R.layout.item_user ,users);

        historyAdapter = new TransferCoinHistoryAdapter(payHistory);
        payHistoryView.setAdapter(historyAdapter);
        payHistoryView.setLayoutManager(new LinearLayoutManager(this));

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate()) {
                    dialog.dismiss();
                    AlertDialog.Builder alert1 = new AlertDialog.Builder(SendUsdcActivity.this);
                    alert1.setTitle("Confirm Pay")
                            .setIcon(R.mipmap.ic_launcher_round)
                            .setMessage("Are you sure you want to pay " + editAmount.getText().toString() + "usdc to " + tvTo.getText().toString())
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    amount = editAmount.getText().toString();
                                    sendCoin();
                                }
                            })
                            .show();
                }
            }
        });

        tvTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(users.size() == 0) {
                    Toast.makeText(getBaseContext(), "No contact list", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.show();
            }
        });

        getData();
    }

    private void getData() {
        loadToast.show();
        if(getBaseContext() != null)
            AndroidNetworking.get(URLHelper.TRANSFER_COIN)
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

                            setData(response);

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

    private void setData(JSONObject response) {
        users.clear();
        payHistory.clear();
        try {
            JSONArray userarray = response.getJSONArray("users");
            for(int i = 0; i < userarray.length(); i ++) {
                try {
                    ContactUser user = new ContactUser();
                    user.setData(userarray.getJSONObject(i));
                    users.add(user);
                    usersTemp.add(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JSONArray payhistory = response.getJSONArray("pay_history");
            for(int i = 0; i < payhistory.length(); i ++) {
                try {
                    payHistory.add(payhistory.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            userContactAdapter.notifyDataSetChanged();
            historyAdapter.notifyDataSetChanged();

            usdcBalance = String.format("%.4f", Double.parseDouble(response.getString("usdc_balance")));
            tvBalance.setText(new DecimalFormat("###,###.####").format(Double.parseDouble(response.getString("usdc_balance"))));
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean validate() {
        boolean validate = true;
        if(!TextUtils.isEmpty(editAmount.getText().toString()) && Double.parseDouble(usdcBalance) < Double.parseDouble(editAmount.getText().toString())){
            Toast.makeText(getBaseContext(), "Insufficient funds", Toast.LENGTH_SHORT).show();
            validate = false;
        }
        if(TextUtils.isEmpty(editAmount.getText().toString())){
            editAmount.setError("!");
            validate = false;
        }
        if(TextUtils.isEmpty(tvTo.getText().toString())){
            tvTo.setError("!");
            validate = false;
        }

        return validate;
    }

    private void sendCoin() {
        loadToast.show();
        JSONObject param = new JSONObject();
        try {
            param.put("user", selectedUserEmail);
            param.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("transfer parmas", param.toString());
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.TRANSFER_COIN)
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
                            Toast.makeText(getBaseContext(), "Sent successfully.", Toast.LENGTH_SHORT).show();
                            setData(response);

                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getBaseContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SendUsdcActivity.this, HomeActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
