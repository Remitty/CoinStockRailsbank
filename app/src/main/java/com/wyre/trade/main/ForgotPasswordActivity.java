package com.wyre.trade.main;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailId;
    private TextView submit, back;
    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        emailId = findViewById(R.id.registered_emailid);
        submit = findViewById(R.id.forgot_button);
        back = findViewById(R.id.backToLoginBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForgotPasswordRequest();
            }
        });
    }

    private void sendForgotPasswordRequest() {
            loadToast.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", emailId.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            AndroidNetworking.post(URLHelper.FORGOT_PASSWORD)
                    .addJSONObjectBody(jsonObject)// posting json
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();
                            emailId.setText("");
                            JSONObject user = response.optJSONObject("user");
                            String otp = user.optString("otp");
                            String id = user.optString("id");
                            Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                            intent.putExtra("otp", otp);
                            intent.putExtra("user_id", id);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Log.d("errorb", "" + error.getErrorBody());
                            Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errord", "" + error.getErrorDetail());
                            Log.d("errorc", "" + error.getErrorCode());
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
    }
}
