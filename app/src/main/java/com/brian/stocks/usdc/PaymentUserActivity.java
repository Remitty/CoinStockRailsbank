package com.brian.stocks.usdc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.ContactUser;
import com.brian.stocks.usdc.adapters.UserContactAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentUserActivity extends AppCompatActivity {

    private LoadToast loadToast;
    private EditText editName, editEmail;
    Button btnAddUser;

    RecyclerView contactListView;
    ArrayList<ContactUser> users = new ArrayList();
    UserContactAdapter userContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_user);

        if(getSupportActionBar() != null){
            // getSupportActionBar().setTitle("Payment User");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);

        editEmail = findViewById(R.id.edit_contact_email);
        editName = findViewById(R.id.edit_contact_name);
        btnAddUser = findViewById(R.id.btn_add_user);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                    sendContact();
            }
        });

        contactListView = findViewById(R.id.user_list);
        userContactAdapter  = new UserContactAdapter(users, true);
        contactListView.setLayoutManager(new LinearLayoutManager(this));
        contactListView.setAdapter(userContactAdapter);
        userContactAdapter.setListener(new UserContactAdapter.Listener() {
            @Override
            public void onSelect(int position) {
            }

            @Override
            public void onDelete(int position) {
                final ContactUser user = users.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentUserActivity.this);
                builder.setIcon(R.mipmap.ic_launcher_round)
                        .setTitle("Delete user")
                        .setMessage("Are you sure you want to delete " + user.getName() + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUser(user.getId());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        getData();
    }

    private boolean validate() {
        boolean flag = true;

        if (TextUtils.isEmpty(editEmail.getText().toString())) {
            editEmail.setError("!");
            flag = false;
        }
        if (TextUtils.isEmpty(editName.getText().toString())) {
            editName.setError("!");
            flag = false;
        }
        Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
        final Matcher m = p.matcher(editEmail.getText().toString());
        if (!m.find()) {
            editEmail.setError("Invalid email format");
            flag = false;
        }
        
        return flag;
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
        try {
            JSONArray userarray = response.getJSONArray("users");
            for(int i = 0; i < userarray.length(); i ++) {
                try {
                    ContactUser user = new ContactUser();
                    user.setData(userarray.getJSONObject(i));
                    users.add(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            userContactAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
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
        Log.d("add contact parmas", param.toString());
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.ADD_TRANSFER_COIN_CONTACT)
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
                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();

                            users.clear();
                            try {
                                JSONArray userarray = response.getJSONArray("users");
                                for(int i = 0; i < userarray.length(); i ++) {
                                    try {
                                        ContactUser user = new ContactUser();
                                        user.setData(userarray.getJSONObject(i));
                                        users.add(user);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                userContactAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (NullPointerException e) {
                                e.printStackTrace();
                            }

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


    private void deleteUser(String id) {
        loadToast.show();
        JSONObject param = new JSONObject();
        try {
            param.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("delete contact user parmas", param.toString());
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.REMOVE_TRANSFER_COIN_CONTACT)
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
                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();

                            users.clear();
                            try {
                                JSONArray userarray = response.getJSONArray("users");
                                for(int i = 0; i < userarray.length(); i ++) {
                                    try {
                                        ContactUser user = new ContactUser();
                                        user.setData(userarray.getJSONObject(i));
                                        users.add(user);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                userContactAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (NullPointerException e) {
                                e.printStackTrace();
                            }

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
