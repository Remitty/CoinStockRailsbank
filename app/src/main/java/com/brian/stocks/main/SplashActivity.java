package com.brian.stocks.main;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.stocks.R;
import com.brian.stocks.SharedPrefs;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.home.HomeActivity;
import com.brian.stocks.profile.ProfileCompleteActivity;

public class SplashActivity extends AppCompatActivity {

    private Button mGetStartedButton;
    private TextView mWelcomeTextView;
    private ImageView mAgoraImageView;
    private SharedPrefs sharedPrefs;
    private Handler mWaitHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mGetStartedButton = findViewById(R.id.getstarted);
        mWelcomeTextView = findViewById(R.id.welcome_text);
        mAgoraImageView = findViewById(R.id.agora_image);

        sharedPrefs = new SharedPrefs(this);

//        ActionBar actionBar = getActionBar();
//        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGetStartedButton.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom));
        mWelcomeTextView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom));
        mAgoraImageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_top));

        mWaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sharedPrefs.getLogedInKey() != null) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    return;
                }
                else {
//                    Log.d("profile completed", SharedHelper.getKey(this, "is_completed"));
                    if (SharedHelper.getKey(getBaseContext(), "is_completed").toString().equals("false")) {
                        startActivity(new Intent(getApplicationContext(), ProfileCompleteActivity.class));
                        return;
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        return;
                    }
                }

//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }, 2000);
    }


    public void getStarted(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
