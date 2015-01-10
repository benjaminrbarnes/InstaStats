package west.districtr.instastats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class WebAuth extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_auth);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        SSLTolerantWebViewClient mySSLTolWebViewClient = new SSLTolerantWebViewClient();
        myWebView.setWebViewClient(mySSLTolWebViewClient);

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
        mySSLTolWebViewClient.shouldOverrideUrlLoading(myWebView, myWebView.getUrl());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_auth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
