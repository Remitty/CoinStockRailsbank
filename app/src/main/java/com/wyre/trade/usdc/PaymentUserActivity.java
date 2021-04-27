package com.wyre.trade.usdc;

import androidx.appcompat.app.AppCompatActivity;

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
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentUserActivity extends AppCompatActivity {

    private LoadToast loadToast;
    private EditText editName, editEmail;
    Button btnAddUser;
    TextView tvViewUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_user);

        if(getSupportActionBar() != null){
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);

        editEmail = findViewById(R.id.edit_contact_email);
        editName = findViewById(R.id.edit_contact_name);
        btnAddUser = findViewById(R.id.btn_add_user);
        tvViewUsers = findViewById(R.id.view_users);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                    sendContact();
            }
        });


        tvViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentUserActivity.this, PaymentUserListActivity.class));
            }
        });
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
