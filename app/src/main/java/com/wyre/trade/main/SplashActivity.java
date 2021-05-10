package com.wyre.trade.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.SharedPrefs;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.home.HomeActivity;
import com.wyre.trade.profile.ProfileCompleteActivity;

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

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.parseColor("#3AE57F"));
        }

        mGetStartedButton = findViewById(R.id.getstarted);
        mWelcomeTextView = findViewById(R.id.welcome_text);
        mAgoraImageView = findViewById(R.id.agora_image);

        sharedPrefs = new SharedPrefs(this);

//        ActionBar actionBar = getActionBar();
//        actionBar.hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        mGetStartedButton.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom));
//        mWelcomeTextView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom));
//        mAgoraImageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_top));

        mWaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sharedPrefs.getLogedInKey() != null) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    return;
                }
                else {
//                    Log.d("profile completed", SharedHelper.getKey(this, "is_completed"));
                    if (SharedHelper.getKey(getBaseContext(), "is_completed").equals("false")) {
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
