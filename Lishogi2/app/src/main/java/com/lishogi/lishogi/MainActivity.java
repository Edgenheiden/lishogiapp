package com.lishogi.lishogi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Well, this was fun. There's a lot of mess and redundant code here.
 * Tested on my Pixel 3a and Samsung Galaxy S6, both seemed to work pretty well.
 * Only major issue is that the app reloads when you leave, i.e. if you switch to
 * another app then back to Lishogi. It's a bit annoying but not too important. I
 * did try to fix it but I couldn't get it to work properly.
 */

public class MainActivity extends AppCompatActivity {


    private WebView webView;
    //private ImageView splash;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //hideSystemUI();
        //showSystemUI();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //webView = new WebView(this);
        webView = findViewById(R.id.webviewview);
        //splash = findViewById(R.id.splash);
        WebSettings settings = webView.getSettings();


        // Disable long press, because... reasons
        // (may need to re-enable? I think long press is registered as right click,
        // so perhaps for arrows and shadow drops? Mobile lichess doesn't have it so I suppose
        // it's probably fine)
        webView.setLongClickable(true);

        // What does this do?
        webView.setOnLongClickListener(v -> true);

        // Background image (not sure if this is needed really)
        //webView.setBackgroundResource(R.drawable.ic_splash);
        //webView.setBackgroundResource(R.drawable.ic_splash);
        webView.setBackgroundColor(Color.TRANSPARENT);

        // Misc. settings
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setSaveFormData(true);
        settings.setSavePassword(true);
        settings.setAllowFileAccess(true);

        // Activity activity activity activity
        final Activity activity = this;

        // For cookies... obviously... still not sure if this is needed; I put this here just to
        // try to fix some stuff because signing in wasn't working on my tablet
        CookieSyncManager.createInstance(this);

        // Webview stuff
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation") // AS says this is redundant, so... yeah...
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Toast for errors, probably better to replace with some other screen
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, for use in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageFinished(WebView webview, String url) {
                super.onPageFinished(webview, url);

                injectCSS();

                /* // Yeah, this stuff is for the splash screen animation which I later dumped
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.setAlpha(0f);
                        webView.setVisibility(View.VISIBLE);
                        webView.animate()
                                .alpha(1f)
                                .setDuration(getResources().getInteger(
                                        android.R.integer.config_shortAnimTime))
                                .setListener(null);
                        splash.animate()
                                .alpha(0f)
                                .setDuration(getResources().getInteger(
                                        android.R.integer.config_shortAnimTime))
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        splash.setVisibility(View.GONE);
                                    }
                                });
                    }
                }, 0);
                //}, 1750);*/


            }
        });

        // Also for splash screen
        // (redundant; later Android versions automatically implement a "splash screen")
        //splash.setVisibility(View.VISIBLE);
        //webView.setVisibility(View.GONE);

        // Load Lishogi
        webView.loadUrl("https://www.lishogi.org");

        // More cookie stuff
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        CookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.getInstance().setAcceptCookie(true);
        cookieManager.getCookie("https://www.lishogi.org");

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
        {
            cookieManager.setAcceptThirdPartyCookies( webView, true );
        }

        //setContentView(webView);

    }

    // Allows going back to previous page when back button is pressed
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    // To remove blue highlight on clickable div elements
    private void injectCSS() {
        try {
            InputStream inputStream = getAssets().open("style.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // More redundant stuff until eof



    /*

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    */

    /*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                 );
    }
    */
    /*
    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_VISIBLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    */

    /*
    @Override
    protected void onPause() {
        super.onPause();
        if (webView.getVisibility() == View.VISIBLE) {
            urlsave = webView.getUrl();
            editor.putString("lasturl", urlsave);
            editor.commit();
        }
    }
     */
}
