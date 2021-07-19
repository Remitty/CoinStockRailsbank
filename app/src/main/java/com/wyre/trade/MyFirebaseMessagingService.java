package com.wyre.trade;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("fcm token", token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fcm_token", token).apply();
    }

}


