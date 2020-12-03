package com.brian.stocks.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.profile.ProfileCompleteActivity;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText mUserNameEditText, mFirstNameEditText, mLastNameEditText, mEmailEditText, mPasswordEditText;
    private TextView mSigninButton;
    private LoadToast loadToast;

    String device_token="stocks", device_UDID="stocks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        loadToast = new LoadToast(this);
        loadToast.setText("Creating Account");

//        mUserNameEditText = findViewById(R.id.username);
        mFirstNameEditText = findViewById(R.id.firstname);
        mLastNameEditText = findViewById(R.id.lastname);
        mEmailEditText = findViewById(R.id.email);
        mPasswordEditText = findViewById(R.id.password);
        mSigninButton = findViewById(R.id.already_user);
        Button mSignUpButton = findViewById(R.id.signup);

        Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
        final Matcher m = p.matcher(mEmailEditText.getText().toString());

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirstNameEditText.getText().toString().isEmpty())
                    mFirstNameEditText.setError("!");
                else if (mLastNameEditText.getText().toString().isEmpty())
                    mLastNameEditText.setError("!");
                else if (mEmailEditText.getText().toString().isEmpty())
                    mEmailEditText.setError("!");
                else if (!m.matches()) {
                    mEmailEditText.setError("Invalid email format");
                }
                else if (mPasswordEditText.getText().toString().isEmpty())
                    mPasswordEditText.setError("!");
                else checkMailAlreadyExit();
            }
        });

        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

    }

    private void doSignUp() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_type", "android");
            jsonObject.put("device_id", device_UDID);
            jsonObject.put("device_token", "" + device_token);
            jsonObject.put("first_name", mFirstNameEditText.getText().toString());
            jsonObject.put("last_name", mLastNameEditText.getText().toString());
            jsonObject.put("email", mEmailEditText.getText().toString());
            jsonObject.put("password", mPasswordEditText.getText().toString());
            jsonObject.put("user_type", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(URLHelper.register)
                .addJSONObjectBody(jsonObject)// posting json
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("response", "" + response.toString());
                        loadToast.success();
                        JSONObject user = response.optJSONObject("data");
                        SharedHelper.putKey(getBaseContext(), "access_token", user.optString("access_token"));
                        SharedHelper.putKey(getBaseContext(), "is_completed", "false");
                        Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_LONG).show();
                        SharedHelper.putKey(getBaseContext(), "fullName", mFirstNameEditText.getText().toString() + " " + mLastNameEditText.getText().toString());
                        startActivity(new Intent(getApplicationContext(), ProfileCompleteActivity.class));
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("errorb", "" + error.getErrorBody());
                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errord", "" + error.getErrorDetail());
                        Log.d("errorc", "" + error.getErrorCode());
                        Log.d("errorm", "" + error.getMessage());
                        loadToast.error();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

        }
        return super.onOptionsItemSelected(item);
    }

    public void checkMailAlreadyExit(){
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", mEmailEditText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(URLHelper.CHECK_MAIL_ALREADY_REGISTERED)
                .addJSONObjectBody(jsonObject)// posting json
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        doSignUp();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("errorb", "" + error.getErrorBody());
                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errord", "" + error.getErrorDetail());
                        Log.d("errorc", "" + error.getErrorCode());
                        Log.d("errorm", "" + error.getMessage());
                        loadToast.error();
                    }
                });
    }
}

