package com.wyre.trade.main;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONObject;

public class SupportActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView imgEmail;
    ImageView imgPhone;
    ImageView imgWeb;

    String phone = "";
    String email = "";
    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        if(getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        imgEmail = (ImageView) findViewById(R.id.img_mail);
        imgPhone = (ImageView) findViewById(R.id.img_phone);
        imgWeb = (ImageView) findViewById(R.id.img_web);

        imgEmail.setOnClickListener(this);
        imgPhone.setOnClickListener(this);
        imgWeb.setOnClickListener(this);

        getHelp();
    }

    private void getHelp() {
        loadToast.show();

        AndroidNetworking.get(URLHelper.HELP)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(this,"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        if (response.optBoolean("success")) {
                            phone=response.optString("contact_number");
                            email=response.optString("contact_email");
                        }
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
    public void onClick(View v) {
        if (v == imgEmail) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + "-" + "Help");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello team");
            startActivity(Intent.createChooser(intent, "Send Email"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
