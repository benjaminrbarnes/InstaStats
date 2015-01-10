package west.districtr.instastats;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        String userID = prefs.getString("API_USER_ID", null);
        String requestToken = prefs.getString("API_ACCESS_TOKEN", null);
        int sum = 0;
        int picSum = 0;

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
