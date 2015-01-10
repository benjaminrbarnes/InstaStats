package west.districtr.instastats;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebAuth extends Activity {
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_auth);
        prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://benjaminbarnes.co/#")) {
                    System.out.println(url);
                    String parts[] = url.split("=");
                    String request_token = parts[1];  //This is your request token.
                    System.out.println(request_token);
                    editor.putString("API_ACCESS_TOKEN", request_token);
                    editor.commit();
                    Intent i = new Intent(WebAuth.this, MainActivity.class);
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });
        /*
        below is the website that I take users to to authorize their accounts
        regardless of what happens, it will take them to the redirect uri, currently my
        website. after they are authorized, there will be a code on the end of my site
        which I use to do stuff with (assuming they authorized) or there will be an
        error code in which I tell them there was an error.

        The reason the load URL takes them to my SSLTol class is because above we set
        the web view to our specific web client so that SSL is tolerated, obviously
         */
        myWebView.loadUrl("https://api.instagram.com/oauth/authorize/?client_id=fb02de9d159b4670a1933112cae454fd&redirect_uri=http://benjaminbarnes.co&response_type=token&scope=basic+likes+comments+relationships");

    }
}