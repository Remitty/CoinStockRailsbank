package com.wyre.trade;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class MyApplication extends Application {
    SharedPrefs sharedPrefs;
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());

//        Places.initialize(getApplicationContext(), getString(R.string.google_map_key));
    }
}
