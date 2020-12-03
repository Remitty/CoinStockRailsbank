package com.brian.stocks.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.brian.stocks.home.adapters.AutoUserAdapter;
import com.brian.stocks.home.adapters.TransferCoinHistoryAdapter;
import com.brian.stocks.home.adapters.UserContactAdapter;
import com.brian.stocks.model.ContactUser;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransferCoinFragment extends Fragment {
    TextView tvBalance;
    TextView tvTo;
    EditText editAmount;
    RecyclerView payHistoryView, contactListView;
    Button btnPay;
    FloatingActionButton btnAddContact;

    View addContactView;
    EditText editName, editEmail;
    Button btnAddContact1;

    private BottomSheetDialog dialog;

    ArrayList<ContactUser> users = new ArrayList();
    ArrayList<ContactUser> usersTemp = new ArrayList();
    ArrayList<JSONObject> payHistory = new ArrayList();

    String selectedUserID, usdcBalance="0.0";

    AutoUserAdapter userAdapter;
    UserContactAdapter userContactAdapter;
    TransferCoinHistoryAdapter historyAdapter;

    private LoadToast loadToast;
    private TextView mtvUserName;

    public TransferCoinFragment() {
        // Required empty public constructor
    }

    public static TransferCoinFragment newInstance() {
        TransferCoinFragment fragment = new TransferCoinFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transfer_coin, container, false);

        loadToast = new LoadToast(getActivity());

        tvBalance = view.findViewById(R.id.usdc_balance);
        tvTo = view.findViewById(R.id.edit_pay_to);
        editAmount = view.findViewById(R.id.edit_pay_amount);
        payHistoryView = view.findViewById(R.id.pay_history_view);
        btnPay = view.findViewById(R.id.btn_pay);
        btnAddContact = view.findViewById(R.id.btn_add_contact);
        mtvUserName = view.findViewById(R.id.user_name);
        mtvUserName.setText(SharedHelper.getKey(getContext(), "fullName"));



        View dialogView = getLayoutInflater().inflate(R.layout.coins_bottom_sheet, null);
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(dialogView);
        contactListView = dialogView.findViewById(R.id.bottom_coins_list);
        userContactAdapter  = new UserContactAdapter(users);
        contactListView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactListView.setAdapter(userContactAdapter);
        userContactAdapter.setListener(new UserContactAdapter.Listener() {
            @Override
            public void onSelect(int position) {
                dialog.hide();
                selectedUserID = users.get(position).getEmail();
                tvTo.setText(users.get(position).getName());
            }
        });

//        userAdapter = new AutoUserAdapter(getActivity(), R.layout.item_user ,users);

        historyAdapter = new TransferCoinHistoryAdapter(payHistory);
        payHistoryView.setAdapter(historyAdapter);
        payHistoryView.setLayoutManager(new LinearLayoutManager(getActivity()));

        tvTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(users.size() == 0) {
                    Toast.makeText(getActivity(), "No contact list", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.show();
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Confirm Pay")
                            .setIcon(R.mipmap.ic_launcher_round)
                            .setMessage("Are you sure you want to pay " + editAmount.getText().toString() + "usdc to " + tvTo.getText().toString())
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendCoin();
                                }
                            })
                            .show();
                }
            }
        });

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(addContactView.getParent() != null) {
//                    ((ViewGroup)addContactView.getParent()).removeView(addContactView); // <- fix
//                }
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                View addContactView = getLayoutInflater().inflate(R.layout.add_contact, null);
                editEmail = addContactView.findViewById(R.id.edit_contact_email);
                editName = addContactView.findViewById(R.id.edit_contact_name);
                btnAddContact1 = addContactView.findViewById(R.id.btn_add_contact);

                btnAddContact1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        alert.
                    }
                });

                alert.setTitle("Add contact")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setView(addContactView)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(editEmail.getText().toString())) {
                                    editEmail.setError("!");
                                    return;
                                }
                                if (TextUtils.isEmpty(editName.getText().toString())) {
                                    editName.setError("!");
                                    return;
                                }
                                Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
                                final Matcher m = p.matcher(editEmail.getText().toString());
                                if (!m.find()) {
                                    editEmail.setError("Invalid email format");
                                    return;
                                }
                                sendContact();
//                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        getData();

        return view;
    }

    private boolean checkValidate() {
        boolean validate = true;
        if(!TextUtils.isEmpty(editAmount.getText().toString()) && Double.parseDouble(usdcBalance) < Double.parseDouble(editAmount.getText().toString())){
            Toast.makeText(getActivity(), "Insufficient funds", Toast.LENGTH_SHORT).show();
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

    private void getData() {
        loadToast.show();
        if(getContext() != null)
            AndroidNetworking.get(URLHelper.TRANSFER_COIN)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
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
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });
    }

    private void sendCoin() {
        loadToast.show();
        JSONObject param = new JSONObject();
        try {
            param.put("user", selectedUserID);
            param.put("amount", editAmount.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("transfer parmas", param.toString());
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.TRANSFER_COIN)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(param)
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
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });
    }


    private void sendContact() {
        loadToast.show();
        JSONObject param = new JSONObject();
        try {
            param.put("name", editName.getText().toString());
            param.put("email", editEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("transfer parmas", param.toString());
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.ADD_TRANSFER_COIN_CONTACT)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(param)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();
                            try {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                users.clear();
                                if(response.optBoolean("success")) {
                                    JSONArray userarray = response.getJSONArray("users");
                                    for (int i = 0; i < userarray.length(); i++) {
                                        try {
                                            ContactUser user = new ContactUser();
                                            user.setData(userarray.getJSONObject(i));
                                            user.setData(userarray.getJSONObject(i));
                                            users.add(user);
                                            usersTemp.add(user);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    userContactAdapter.notifyDataSetChanged();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
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
            tvBalance.setText(usdcBalance);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
