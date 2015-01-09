package west.districtr.instastats;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by My_Mac on 11/28/14.
 */
public class SSLTolerantWebViewClient extends WebViewClient {

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed(); // Ignore SSL certificate errors
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("http://benjaminbarnes.co/#")) {
            //StrictMode.ThreadPolicy policy = new StrictMode.
            //ThreadPolicy.Builder().permitAll().build();
            //StrictMode.setThreadPolicy(policy);
            System.out.println(url);
            String parts[] = url.split("=");
            String request_token = parts[1];  //This is your request token.
            System.out.println(request_token);
            MainActivity.AUTHTOKEN = request_token;
            //WebAuth.returnHome();
        }else{
            //url.startsWith("http://benjaminbarnes.co/?"))
            /*
            this will return something along the lines of
            http://benjaminbarnes.co/?error_reason=user_denied&error=access_denied&error_description=The+user+denied+your+request.
            */
            return false;
        }
        // returning true messes up behavior so for time being we are doing this
        return false;
    }

}
