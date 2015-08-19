package west.districtr.instastats;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * A class that creates an Async task which allows us to make an API
 * call that is not run on the UI thread.
 *
 * Read documentation if you need an explanation for AsyncTask:
 * http://developer.android.com/reference/android/os/AsyncTask.html
 */
public class APICall extends AsyncTask<String, Integer, JSONObject> {

    protected JSONObject doInBackground(String... urls) {
        JSONObject jObject = null;
        String url = urls[0];
        try {
            URL apiURL = new URL(url);
            HttpsURLConnection apiConnection = (HttpsURLConnection) apiURL.openConnection();
            apiConnection.setRequestMethod("GET");
            apiConnection.setRequestProperty("Content-length", "0");
            apiConnection.setUseCaches(false);
            apiConnection.setAllowUserInteraction(false);
            apiConnection.setConnectTimeout(10000);
            apiConnection.setReadTimeout(1000000);
            apiConnection.connect();

            int status = apiConnection.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    jObject = new JSONObject(sb.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject;
    }
}
