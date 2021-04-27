package com.wyre.trade.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wyre.trade.R;

import net.steamcrafted.loadtoast.LoadToast;

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

