package west.districtr.instastats;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class TotalLikes extends Activity {
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_likes);
        final SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        TextView numOfPhotoLikesTV = (TextView) findViewById(R.id.NumberOfLikesTV);
        TextView numOfPhotosTV = (TextView) findViewById(R.id.NumberOfPhotosTV);

        int sum = 0;
        int picSum = 0;

        Button twitterShare = (Button) findViewById(R.id.TwitterShareButton);
        twitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent using ACTION_VIEW and a normal Twitter url:
                String tweetUrl =
                        String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                                urlEncode(" total likes from my most recent Instagram photos. Calculated with InstaStats"), urlEncode("https://www.google.fi/"));
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

        String userID = prefs.getString("API_USER_ID", null);
        String requestToken = prefs.getString("API_ACCESS_TOKEN", null);
        System.out.println(userID + " : user id");
        System.out.println(requestToken + " : request Token");


        try {
            String url = "https://api.instagram.com/v1/users/" + userID + "/media/recent/?access_token=" + requestToken;
            JSONObject jObject =  new APICall().execute(url).get();
            JSONArray photos;
            JSONObject pag = jObject.getJSONObject("pagination");

            do {
                // while this repeats the above code, we have to do it because we
                // will need to reassign these with the new url each iteration
                jObject =  new APICall().execute(url).get();
                photos = jObject.getJSONArray("data");
                String nextURL = pag.getString("next_url");
                url = nextURL;

                // program optimization
                int numOfPhotos = photos.length();

                // below sums up picture likes
                for (int i = 0; i < numOfPhotos; ++i) {
                    // increments through array of photos and adds up likes
                    // based off each one
                    JSONObject photo = photos.getJSONObject(i);
                    JSONObject likes = photo.getJSONObject("likes");
                    sum += Integer.parseInt(likes.getString("count"));
                    picSum++;
                    numOfPhotosTV.setText(picSum + " photos");
                    numOfPhotoLikesTV.setText(sum + " likes");
                    System.out.println("Sum of " + picSum + " pictures: " + sum);
                }
                // make next url the next one
                pag = jObject.getJSONObject("pagination");
            }while(!(pag.toString().equals("{}")));
            System.out.print("pag is empty : " + pag.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_total_likes, menu);
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
