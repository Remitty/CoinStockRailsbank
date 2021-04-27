package com.wyre.trade.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.SharedPrefs;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private SharedPrefs sharedPrefs;
    private EditText mUserNameEditText, mPasswordEditText;
    private TextView mSignupButton, forgotPassword;
    private LoadToast loadToast;
    String device_token="stocks", device_UDID="stocks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

//        ActionBar actionBar = getSupportActionBar();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Objects.requireNonNull(actionBar).setTitle("Login");
//        }

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                Window window=getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.colorRedCrayon));
            }
        }

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#3AE57F"));
        }

        mUserNameEditText = findViewById(R.id.login_emailid);
        mPasswordEditText = findViewById(R.id.login_password);
        mSignupButton = findViewById(R.id.createAccount);
        forgotPassword = findViewById(R.id.forgot_password);

        Button mSigninButton = findViewById(R.id.loginBtn);
        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        sharedPrefs = new SharedPrefs(getApplicationContext());
        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
                final Matcher m = p.matcher(mUserNameEditText.getText().toString());

                if (mUserNameEditText.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), "enter your email", Toast.LENGTH_SHORT).show();
                else if (!m.matches()) {
                    mUserNameEditText.setError("Invalid email format");
                }
                else if (mPasswordEditText.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), "enter your password", Toast.LENGTH_SHORT).show();
                else
                    doSignIn();
            }
        });

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

    }

    private void doSignIn() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", mPasswordEditText.getText().toString());
            jsonObject.put("email", mUserNameEditText.getText().toString());
            jsonObject.put("device_type", "android");
            jsonObject.put("device_id", device_UDID);
            jsonObject.put("device_token", device_token);
            jsonObject.put("user_type", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(URLHelper.login)
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
                        if(response.optBoolean("success")){
                            try {
                                response = response.getJSONObject("user");
                                String key = response.optString("access_token");
                                sharedPrefs.savePref(key);
                                SharedHelper.putKey(getBaseContext(), "access_token", key);
                                SharedHelper.putKey(getBaseContext(), "userId", response.optString("id"));
                                SharedHelper.putKey(getBaseContext(), "is_completed", response.optString("isCompleteProfile"));
                                SharedHelper.putKey(getBaseContext(), "loggedIn", "true");
                                SharedHelper.putKey(getBaseContext(), "email", mUserNameEditText.getText().toString());
                                SharedHelper.putKey(getBaseContext(), "password", mPasswordEditText.getText().toString());
                                String first_name = response.optString("first_name");
                                String last_name = response.optString("last_name");
                                SharedHelper.putKey(getBaseContext(), "fullName", first_name + " " + last_name);
                                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return;
                        }

                        try {
                            Toast.makeText(getBaseContext(), response.getString("message"), Toast.LENGTH_LONG ).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        Toast.makeText(getApplicationContext(), "wrong username or password", Toast.LENGTH_SHORT).show();
                        // handle error
                        Log.d("errorb", "" + error.getErrorBody());
                        Log.d("errord", "" + error.getErrorDetail());
                        Log.d("errorc", "" + error.getErrorCode());
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }

}
