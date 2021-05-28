package com.wyre.trade.usdc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;
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
import com.wyre.trade.R;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.HomeActivity;
import com.wyre.trade.home.adapters.AutoUserAdapter;
import com.wyre.trade.usdc.adapters.UserContactAdapter;
import com.wyre.trade.model.ContactUser;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SendUsdcActivity extends AppCompatActivity {
    TextView tvBalance;

    RecyclerView contactListView;
    Button btnPay, tvViewHistory;
//    TextView tvViewHistory;

    TextView tvTo;
    EditText editAmount;

    private BottomSheetDialog dialog;

    ArrayList<ContactUser> users = new ArrayList();
    ArrayList<ContactUser> usersTemp = new ArrayList();

    String selectedUserEmail, amount;
    Double usdcBalance =0.0;

    AutoUserAdapter userAdapter;
    UserContactAdapter userContactAdapter;


    private LoadToast loadToast;
    ConfirmAlert confirmAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_usdc);

        if(getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);
        confirmAlert = new ConfirmAlert(SendUsdcActivity.this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        tvBalance = findViewById(R.id.usdc_balance);


        btnPay = findViewById(R.id.btn_pay);

        tvViewHistory = findViewById(R.id.tv_view_history);

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

        tvViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendUsdcActivity.this, SendUsdcHistoryActivity.class));
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = editAmount.getText().toString();
                if(TextUtils.isEmpty(amount) || amount.startsWith(".")){
                    editAmount.setError("!");
                    return;
                }
                if(TextUtils.isEmpty(tvTo.getText().toString())){
                    tvTo.setError("!");
                    return;
                }
                if(Double.parseDouble(amount) == 0) {
                    editAmount.setError("!");
                    return;
                }
                if(usdcBalance < Double.parseDouble(amount)){
                    confirmAlert.alert("Insufficient balance");
                    return;
                }
                    dialog.dismiss();

                    confirmAlert.confirm("You pay " + amount + "usdc to " + tvTo.getText().toString())
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sendCoin();
                                    confirmAlert.process();
                                }
                            }).show();
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
                            Log.d("errorm", "" + error.getErrorBody());
                        }
                    });
    }

    private void setData(JSONObject response) {
        users.clear();

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

            userContactAdapter.notifyDataSetChanged();

            usdcBalance = response.getDouble("usdc_balance");
            tvBalance.setText(new DecimalFormat("###,###.####").format(usdcBalance));
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }



    private void sendCoin() {
//        loadToast.show();
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
//                            loadToast.success();
//                            Toast.makeText(getBaseContext(), "Sent successfully.", Toast.LENGTH_SHORT).show();
                            setData(response);
                            confirmAlert.success("Sent successfully.");

                        }

                        @Override
                        public void onError(ANError error) {
//                            loadToast.error();
                            // handle error
                            confirmAlert.error(error.getErrorBody());
//                            Toast.makeText(getBaseContext(), error.getErrorBody(), Toast.LENGTH_SHORT).show();
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
