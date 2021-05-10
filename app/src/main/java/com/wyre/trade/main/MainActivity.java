package com.wyre.trade.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.wyre.trade.SharedPrefs;
import com.wyre.trade.home.HomeActivity;


public class MainActivity extends AppCompatActivity {

    private SharedPrefs sharedPrefs;
    private TextView mAgoraTextView;
    private Button mSigninButton;
    private Button mSignupButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        sharedPrefs = new SharedPrefs(getApplicationContext());

        checkingLogin();
    }

    private void checkingLogin() {

        if (sharedPrefs.getLogedInKey() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        else startActivity(new Intent(getApplicationContext(), SignInActivity.class));
    }
}
