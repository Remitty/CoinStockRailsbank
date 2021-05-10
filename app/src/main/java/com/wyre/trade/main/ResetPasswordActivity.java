package com.wyre.trade.main;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ResetPasswordActivity extends AppCompatActivity {
    EditText editOTP, editNewPW, editConfirmNewPW;
    Button btnSend;
    String otp, userID;
    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        if(getIntent() != null){
            otp = getIntent().getStringExtra("otp");
            userID = getIntent().getStringExtra("user_id");
        }

        editOTP = findViewById(R.id.edit_otp);
        editNewPW = findViewById(R.id.edit_new_password);
        editConfirmNewPW = findViewById(R.id.edit_new_password_confirm);
        btnSend = findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                    sendNewPassword();
            }
        });
    }

    private void sendNewPassword() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", editNewPW.getText().toString());
            jsonObject.put("password_confirmation", editConfirmNewPW.getText().toString());
            jsonObject.put("id", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(URLHelper.RESET_PASSWORD)
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
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

    private boolean validate() {
        boolean validate = true;

        if(editOTP.getText().toString().equals("")){
            validate = false;
            editOTP.setError("!");
        }

        if(!editOTP.getText().toString().equals(this.otp)){
            validate = false;
            editOTP.setError("Not match!");
        }

        if(editNewPW.getText().toString().equals("")){
            validate = false;
            editNewPW.setError("!");
        }

        if(editNewPW.getText().toString().length() < 6){
            validate = false;
            editNewPW.setError("At least 6 characters");
        }

        if(editConfirmNewPW.getText().toString().equals("")){
            validate = false;
            editConfirmNewPW.setError("!");
        }

        if(!editConfirmNewPW.getText().toString().equals(editNewPW.getText().toString())){
            validate = false;
            editConfirmNewPW.setError("Not match password");
        }

        return validate;
    }
}
