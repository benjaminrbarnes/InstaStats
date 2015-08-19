package west.districtr.instastats;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class TotalLikes extends Activity {
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    TextView numOfPhotoLikesTV;
    TextView numOfPhotosTV;

    ProgressBar progBar;
    TextView lastTimeCalc;

    String url;
    String userID;
    String requestToken;
    int picSum;
    int sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_likes);
        prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        userID = prefs.getString("API_USER_ID", null);
        requestToken = prefs.getString("API_ACCESS_TOKEN", null);

        progBar = (ProgressBar) findViewById(R.id.TotalProgressBar);
        progBar.setVisibility(View.GONE);

        numOfPhotoLikesTV = (TextView) findViewById(R.id.NumberOfLikesTV);
        numOfPhotosTV = (TextView) findViewById(R.id.NumberOfPhotosTV);

        lastTimeCalc = (TextView) findViewById(R.id.TotalLastTimeCalc);

        lastTimeCalc.setText(prefs.getString("TOTAL_LAST_TIME_CALC", "Never"));

        numOfPhotoLikesTV.setText(prefs.getString("NUM_OF_PHOTO_LIKES", "0 likes"));
        numOfPhotosTV.setText(prefs.getString("NUM_OF_PHOTOS", "0 photos"));

        Button calcLikes = (Button) findViewById(R.id.CalculateLikesButton);
        calcLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharedPrefKey = "TOTAL_LIKE_TIME_KEY";
                long currTime = Calendar.getInstance().getTimeInMillis();
                long savedValue = prefs.getLong(sharedPrefKey,1);
                if(savedValue == 1){
                    // falls in here if they have never calculated likes today
                    editor.putLong(sharedPrefKey, currTime);
                    editor.commit();
                    progBar.setVisibility(View.VISIBLE);
                    new Thread(new backgroundTask()).start();
                }else if(currTime - savedValue < 600000){
                    // falls into here if they calculated likes less than 10 minutes ago
                    long timeLeft = TimeUnit.MILLISECONDS.toMinutes(currTime - savedValue);
                    String left = String.valueOf(10 - timeLeft);
                    Toast toast = Toast.makeText(getApplicationContext(), "You can only calculate total likes once every 10 minutes. " + left +
                            " minutes left", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    // falls into here if they have calculated likes but not in the last 10 minutes
                    progBar.setVisibility(View.VISIBLE);
                    editor.putLong(sharedPrefKey, currTime);
                    editor.commit();
                    new Thread(new backgroundTask()).start();
                }
            }
        });

        Button twitterShare = (Button) findViewById(R.id.TwitterShareButton);
        twitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // http://stackoverflow.com/questions/2077008/android-intent-for-twitter-application
                String[] likeSumArr = numOfPhotoLikesTV.getText().toString().split(" ");
                String[] photoSumArr = numOfPhotosTV.getText().toString().split(" ");
                // Create intent using ACTION_VIEW and a normal Twitter url:
                String tweetUrl =
                        String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                                urlEncode(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(likeSumArr[0])) + " total likes from my most recent "
                                        + NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(photoSumArr[0])) + " Instagram photos. Calculated with InstaStats"),
                                urlEncode("https://www.google.fi/"));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

                // Narrow down to official Twitter app, if available:
                List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                        intent.setPackage(info.activityInfo.packageName);
                    }
                }
                startActivity(intent);
            }
        });
    }

    public void updateTextView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                java.util.Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
                String todayString = formatter.format(today);
                // we want it to remember last time it calculated each radio button
                editor.putString("TOTAL_LAST_TIME_CALC", todayString);
                editor.commit();
                lastTimeCalc.setText(prefs.getString("TOTAL_LAST_TIME_CALC", "Never"));
                editor.putString("NUM_OF_PHOTOS", picSum + " photos");
                editor.putString("NUM_OF_PHOTO_LIKES", sum + " likes");
                editor.commit();
                numOfPhotosTV.setText(picSum + " photos");
                numOfPhotoLikesTV.setText(sum + " likes");
                progBar.setVisibility(View.GONE);
            }
        });
    }

    private class backgroundTask implements Runnable{
        @Override
        public void run() {
            try {
                url = "https://api.instagram.com/v1/users/" + userID + "/media/recent/?access_token=" + requestToken;
                JSONObject jObject =  new APICall().execute(url).get();
                JSONArray photos;
                JSONObject pag = jObject.getJSONObject("pagination");
                do {
                    /* while this repeats the above code, we have to do it because we
                     will need to reassign these with the new url each iteration */
                    jObject =  new APICall().execute(url).get();
                    photos = jObject.getJSONArray("data");
                    if(!(pag.toString().equals("{}"))){
                        String nextURL = pag.getString("next_url");
                        url = nextURL;
                    }
                    // optimization
                    int numOfPhotos = photos.length();
                    // below sums up picture likes
                    for (int i = 0; i < numOfPhotos; ++i) {
                        // increments through array of photos and adds up likes
                        // based off each one
                        JSONObject photo = photos.getJSONObject(i);
                        JSONObject likes = photo.getJSONObject("likes");
                        sum += Integer.parseInt(likes.getString("count"));
                        picSum++;
                        System.out.println("Sum of " + picSum + " pictures: " + sum);
                    }
                    // make next url the next one
                    pag = jObject.getJSONObject("pagination");
                }while(!(pag.toString().equals("{}")));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e){
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            updateTextView();
        }
    }

    public static String urlEncode(String s) {
        // http://stackoverflow.com/questions/2077008/android-intent-for-twitter-application
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
}
