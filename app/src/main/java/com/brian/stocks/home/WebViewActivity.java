package com.brian.stocks.home;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class WebViewActivity extends Activity {
    private WebView webView;
    private boolean loading = true;
    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initComponents();

        loadToast = new LoadToast(this);
        loadToast.show();
        Bundle bundle = getIntent().getExtras();
        String uri = bundle.getString("uri");
        Log.d("webview url", uri);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.loadUrl(uri);

    }

    private void initComponents() {

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if(!loading) {
                String url = request.getUrl().toString();
                Intent intent = new Intent();
                intent.putExtra("response", url);
                setResult(RESULT_OK, intent);
                finish();

                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("zabo url finished", url);
            loading = false;
            loadToast.hide();
        }
    }

}

