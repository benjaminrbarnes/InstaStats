package west.districtr.instastats;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebAuth extends Activity {
    /*
    A class that opens a webview for the user to authenticate their account
    with my application so that I can make API requests. After they authenticate,
    it will reroute them to my website with an auth token on the end, which we parse
    and save to the shared preferences, and then send them back to the home page.
     */
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_auth);
        prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        // need to fix on back pressed

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://benjaminbarnes.co/#")) {
                    String parts[] = url.split("=");
                    String request_token = parts[1];  //This is the request token.
                    editor.putString("API_ACCESS_TOKEN", request_token);
                    editor.commit();
                    Intent i = new Intent(WebAuth.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    return true;
                }
                // need to handle case where there was an error authenticating
                return false;
            }
        });
        myWebView.loadUrl("https://api.instagram.com/oauth/authorize/?client_id=fb02de9d159b4670a1933112cae454fd&redirect_uri=http://benjaminbarnes.co&response_type=token&scope=basic+likes+comments+relationships");
    }
}