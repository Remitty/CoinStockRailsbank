package com.wyre.trade.usdc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.ContactUser;
import com.wyre.trade.usdc.adapters.UserContactAdapter;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PaymentUserListActivity extends AppCompatActivity {
    private LoadToast loadToast;

    RecyclerView contactListView;
    ArrayList<ContactUser> users = new ArrayList();
    UserContactAdapter userContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_user_list);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentUserListActivity.this);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
