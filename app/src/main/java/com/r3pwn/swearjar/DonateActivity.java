package com.r3pwn.swearjar;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DonateActivity extends AppCompatActivity {

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor prefsEdit;
    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);


        sharedPrefs = getSharedPreferences("sentSMS", Context.MODE_PRIVATE);
        prefsEdit = sharedPrefs.edit();

        getSupportActionBar().setTitle("Donate");

        wv = (WebView)findViewById(R.id.donateWebView);

        final int dollarAmt = sharedPrefs.getInt("totalBill", 0);

        wv.loadUrl("https://give.specialolympics.org/page/contribute/default");

        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webview, String url) {
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript("document.getElementById(\"amt_other_text\").value = " + dollarAmt + ".00", null);
                view.evaluateJavascript("document.getElementById(\"full-gift-checkbox\").checked = \"checked\"", null);
            }
        });

        wv.getSettings().setJavaScriptEnabled(true);

        prefsEdit.putInt("totalBill", 0);
        prefsEdit.commit();
    }
}
