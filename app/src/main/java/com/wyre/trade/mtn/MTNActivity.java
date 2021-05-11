package com.wyre.trade.mtn;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.wyre.trade.model.MTNTransactionItem;
import com.wyre.trade.mtn.adapter.MTNTransactionAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MTNActivity extends AppCompatActivity {

    LoadToast loadToast;
    ConfirmAlert confirmAlert;

    Button btnPay, btnTopup, btnConvert, btnConvert1;
    TextView tvBalance, tvCurrency, tvViewAll;
    TextView tvEmpty;
    RecyclerView mtnTransactionView;
    MTNTransactionAdapter mtnTransactionAdapter;
    ArrayList<MTNTransactionItem> transactions = new ArrayList<>();
    AlertDialog.Builder alert;
    String amount, to, currency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtn);

        if(getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        loadToast = new LoadToast(this);
        confirmAlert = new ConfirmAlert(MTNActivity.this);
        //loadToast.setBackgroundColor(R.color.colorBlack);
        initComponents();
        initListeners();
        alert = new AlertDialog.Builder(MTNActivity.this);
        alert.setIcon(R.mipmap.ic_launcher_round);
        getData();
    }

    private void initListeners() {
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.layout_mtn_payment, null);
                final EditText mtnAmount = view.findViewById(R.id.edit_mtn_pay_amount);
                final EditText mtnTo = view.findViewById(R.id.edit_mtn_pay_to);
                alert.setTitle("Disbursement")
                        .setView(view);

                final AlertDialog alertDialog = alert.create();
                alertDialog.show();
                Button btnCancel = view.findViewById(R.id.btn_mtn_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                Button btnOk = view.findViewById(R.id.btn_mtn_ok);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amount = mtnAmount.getText().toString();
                        to = mtnTo.getText().toString();
                        if(amount.isEmpty() || amount.startsWith(".")) {
                            mtnAmount.setError("!");
                            return;
                        }
                        if(Double.parseDouble(amount) == 0) {
                            mtnAmount.setError("!");
                            return;
                        }
                        if(to.isEmpty()) {
                            mtnTo.setError("!");
                            return;
                        }
                        Pattern p = Pattern.compile("^\\+[0-9]{10,13}$");
                        Matcher m = p.matcher(mtnTo.getText().toString());
                        if(!m.matches()) {
                            mtnTo.setError("Invalid phone number");
                            return;
                        }
                        alertDialog.dismiss();

                        confirmAlert.confirm("Are you sure you want to pay " + amount + "" + currency + " to " + to + "?")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        handlePay();
                                    }
                                }).show();
                    }
                });
            }
        });

        btnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.layout_mtn_payment, null);
                final EditText mtnAmount = view.findViewById(R.id.edit_mtn_pay_amount);
                final EditText mtnTo = view.findViewById(R.id.edit_mtn_pay_to);
                alert.setTitle("Collection")
                        .setView(view);
                final AlertDialog alertDialog = alert.create();
                alertDialog.show();
                Button btnOk = view.findViewById(R.id.btn_mtn_ok);
                Button btnCancel = view.findViewById(R.id.btn_mtn_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amount = mtnAmount.getText().toString();
                        to = mtnTo.getText().toString();
                        if(amount.isEmpty() || amount.startsWith(".")) {
                            mtnAmount.setError("!");
                            return;
                        }
                        if(Double.parseDouble(amount) == 0) {
                            mtnAmount.setError("!");
                            return;
                        }
                        if(to.isEmpty()) {
                            mtnTo.setError("!");
                            return;
                        }
                        Pattern p = Pattern.compile("^\\+[0-9]{10,13}$");
                        Matcher m = p.matcher(mtnTo.getText().toString());
                        if(!m.matches()) {
                            mtnTo.setError("Invalid phone number");
                            return;
                        }
                        alertDialog.dismiss();

                        confirmAlert.confirm("Are you sure you want to topup " + amount + "" + currency + "?")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        handleTopup();
                                    }
                                }).show();
                    }
                });
            }
        });

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.layout_mtn_payment, null);

                final EditText mtnAmount = view.findViewById(R.id.edit_mtn_pay_amount);
                final EditText mtnTo = view.findViewById(R.id.edit_mtn_pay_to);
                mtnTo.setVisibility(View.GONE);
                alert.setTitle("Convert")
                        .setView(view);
                final AlertDialog alertDialog = alert.create();
                alertDialog.show();
                Button btnOk = view.findViewById(R.id.btn_mtn_ok);
                Button btnCancel = view.findViewById(R.id.btn_mtn_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amount = mtnAmount.getText().toString();
                        if(amount.isEmpty() || amount.startsWith(".")) {
                            mtnAmount.setError("!");
                            return;
                        }
                        if(Double.parseDouble(amount) == 0) {
                            mtnAmount.setError("!");
                            return;
                        }

                        alertDialog.dismiss();

                        confirmAlert.confirm("Are you sure you want to convert " + amount + "" + currency + "?")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        handleConvert(0);
                                    }
                                }).show();
                    }
                });
            }
        });

        btnConvert1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.layout_mtn_payment, null);
                final EditText mtnAmount = view.findViewById(R.id.edit_mtn_pay_amount);
                final EditText mtnTo = view.findViewById(R.id.edit_mtn_pay_to);
                mtnTo.setVisibility(View.GONE);
                alert.setTitle("Collection")
                        .setView(view);
                final AlertDialog alertDialog = alert.create();
                alertDialog.show();
                Button btnOk = view.findViewById(R.id.btn_mtn_ok);
                Button btnCancel = view.findViewById(R.id.btn_mtn_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amount = mtnAmount.getText().toString();
                        if(amount.isEmpty() || amount.startsWith(".")) {
                            mtnAmount.setError("!");
                            return;
                        }
                        if(Double.parseDouble(amount) == 0) {
                            mtnAmount.setError("!");
                            return;
                        }

                        alertDialog.dismiss();
                        confirmAlert.confirm("Are you sure you want to convert " + amount +  "USDC ?")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        handleConvert(1);
                                    }
                                }).show();
                    }
                });
            }
        });

        tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MTNActivity.this, MtnTransactionActivity.class));
            }
        });
    }

    private void initComponents() {
        tvBalance = findViewById(R.id.mtn_balance);
        tvCurrency = findViewById(R.id.mtn_currency);
        tvEmpty = findViewById(R.id.mtn_no_transaction);
        tvViewAll = findViewById(R.id.view_all);

        mtnTransactionView = findViewById(R.id.mtn_transaction_view);
        mtnTransactionView.setLayoutManager(new LinearLayoutManager(this));
        mtnTransactionAdapter = new MTNTransactionAdapter(transactions);
        mtnTransactionView.setAdapter(mtnTransactionAdapter);

        btnPay = findViewById(R.id.btn_mtn_pay);
        btnTopup = findViewById(R.id.btn_mtn_topup);
        btnConvert = findViewById(R.id.btn_mtn_convert);
        btnConvert1 = findViewById(R.id.btn_mtn_convert1);
    }

    private void getData() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_MTN_SERVICE)
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
                        if(response.optBoolean("success")) {
                            try {
                                tvBalance.setText(response.getString("balance"));
                                currency = response.getString("currency");
                                tvCurrency.setText(currency);

                                transactions.clear();
                                JSONObject data = response.getJSONObject("transaction");
                                MTNTransactionItem item = new MTNTransactionItem();
                                item.setData(data);
                                transactions.add(item);
                                mtnTransactionAdapter.notifyDataSetChanged();

                                if(transactions.size() > 0) {
                                    tvEmpty.setVisibility(View.GONE);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
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

    private void handleTopup() {
//        loadToast.show();
        confirmAlert.process();
        JSONObject object = new JSONObject();
        try {
            object.put("amount", amount);
            object.put("to", to);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(URLHelper.REQUEST_MTN_TOPUP)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addJSONObjectBody(object)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
//                        loadToast.success();
                        if(response.optBoolean("success")) {
                            transactions.clear();
                            JSONObject data = null;
                            try {
                                data = response.getJSONObject("transaction");
                                MTNTransactionItem item = new MTNTransactionItem();
                                item.setData(data);
                                transactions.add(item);

                                mtnTransactionAdapter.notifyDataSetChanged();
                                if(transactions.size() > 0) {
                                    tvEmpty.setVisibility(View.GONE);
                                }

                                tvBalance.setText(response.getString("balance"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            confirmAlert.success(response.optString("message"));
                        } else {
                            confirmAlert.error(response.optString("message"));
                        }
//                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
//                        loadToast.error();
                        // handle error
                        confirmAlert.error(error.getErrorBody());
//                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
    }

    private void handlePay() {
//        loadToast.show();
        confirmAlert.process();
        JSONObject object = new JSONObject();
        try {
            object.put("amount", amount);
            object.put("to", to);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(URLHelper.REQUEST_MTN_PAY)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addJSONObjectBody(object)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
//                        loadToast.success();
                        if(response.optBoolean("success")) {
                            transactions.clear();
                            JSONObject data = null;
                            try {
                                data = response.getJSONObject("transaction");
                                MTNTransactionItem item = new MTNTransactionItem();
                                item.setData(data);
                                transactions.add(item);
                                mtnTransactionAdapter.notifyDataSetChanged();
                                if(transactions.size() > 0) {
                                    tvEmpty.setVisibility(View.GONE);
                                }

                                tvBalance.setText(response.getString("balance"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            confirmAlert.success(response.optString("message"));
                        } else {
                            confirmAlert.error(response.optString("message"));
//                        Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
//                        loadToast.error();
                        // handle error
                        confirmAlert.error(error.getErrorBody());
//                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
    }

    private void handleConvert(int type) {
//        loadToast.show();
        confirmAlert.process();
        JSONObject object = new JSONObject();
        try {
            object.put("amount", amount);
            object.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(URLHelper.REQUEST_MTN_CONVERT)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addJSONObjectBody(object)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
//                        loadToast.success();
                        if(response.optBoolean("success")) {
                            transactions.clear();
                            JSONObject data = null;
                            try {
                                data = response.getJSONObject("transaction");
                                MTNTransactionItem item = new MTNTransactionItem();
                                item.setData(data);
                                transactions.add(item);
                                mtnTransactionAdapter.notifyDataSetChanged();
                                if(transactions.size() > 0) {
                                    tvEmpty.setVisibility(View.GONE);
                                }
                                tvBalance.setText(response.getString("balance"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            confirmAlert.success(response.optString("message"));
                        } else
                            confirmAlert.error(response.optString("message"));
//                        Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
//                        loadToast.error();
                        confirmAlert.error(error.getErrorBody());
//                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MTNActivity.this, HomeActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
