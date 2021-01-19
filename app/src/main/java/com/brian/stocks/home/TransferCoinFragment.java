package com.brian.stocks.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

    RecyclerView payHistoryView;
    Button btnSend;
    Button btnAddContact;

    EditText editName, editEmail;
    Button btnAddContact1;

    ArrayList<JSONObject> payHistory = new ArrayList();
    TransferCoinHistoryAdapter historyAdapter;

    String usdcBalance="0.0";


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

        payHistoryView = view.findViewById(R.id.pay_history_view);
        btnSend = view.findViewById(R.id.btn_usdc_send);
        btnAddContact = view.findViewById(R.id.btn_add_contact);
        mtvUserName = view.findViewById(R.id.user_name);
        mtvUserName.setText(SharedHelper.getKey(getContext(), "fullName"));

        historyAdapter = new TransferCoinHistoryAdapter(payHistory);
        payHistoryView.setAdapter(historyAdapter);
        payHistoryView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SendUsdcActivity.class));
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

                alert.setTitle("Add contact")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setView(addContactView)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(editEmail.getText().toString())) {
                                    editEmail.setError("!");
                                    Toast.makeText(getContext(), "Please fill into form.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (TextUtils.isEmpty(editName.getText().toString())) {
                                    editName.setError("!");
                                    Toast.makeText(getContext(), "Please fill into form.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
                                final Matcher m = p.matcher(editEmail.getText().toString());
                                if (!m.find()) {
                                    editEmail.setError("Invalid email format");
                                    Toast.makeText(getContext(), "Invalid email format.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                sendContact();
//                                dialog.dismiss();
                            }
                        });
                final AlertDialog dialog = alert.create();
                dialog.show();

                btnAddContact1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        getData();

        return view;
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

        try {
            usdcBalance = String.format("%.4f", Double.parseDouble(response.getString("usdc_balance")));
            tvBalance.setText(usdcBalance);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
