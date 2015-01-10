package west.districtr.instastats;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class PictureLikes extends Activity {
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_likes);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        final ProgressBar progBar = (ProgressBar) findViewById(R.id.LikesProgressBar);
        progBar.setVisibility(View.VISIBLE);

        Button calcLikes = (Button) findViewById(R.id.CalculatePictureLikesButton);
        calcLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                TextView userLike1 = (TextView) findViewById(R.id.LikeTV1);
                TextView userLike2 = (TextView) findViewById(R.id.LikeTV2);
                TextView userLike3 = (TextView) findViewById(R.id.LikeTV3);
                TextView userLike4 = (TextView) findViewById(R.id.LikeTV4);
                TextView userLike5 = (TextView) findViewById(R.id.LikeTV5);
                TextView userLike6 = (TextView) findViewById(R.id.LikeTV6);
                TextView userLike7 = (TextView) findViewById(R.id.LikeTV7);
                TextView userLike8 = (TextView) findViewById(R.id.LikeTV8);
                TextView userLike9 = (TextView) findViewById(R.id.LikeTV9);
                TextView userLike10 = (TextView) findViewById(R.id.LikeTV10);
                TextView userLike11 = (TextView) findViewById(R.id.LikeTV11);
                TextView userLike12 = (TextView) findViewById(R.id.LikeTV12);
                TextView userLike13 = (TextView) findViewById(R.id.LikeTV13);
                TextView userLike14 = (TextView) findViewById(R.id.LikeTV14);
                TextView userLike15 = (TextView) findViewById(R.id.LikeTV15);

                String requestToken = prefs.getString("API_ACCESS_TOKEN", null);
                String userID = prefs.getString("API_USER_ID", null);

                ArrayList<String> photoIDs = new ArrayList<String>();
                HashMap<String, Integer> likeCountHM = new HashMap<String, Integer>();

                try {
                    // api url to get a specific users recent posts
                    String url = "https://api.instagram.com/v1/users/" + userID + "/media/recent/?access_token=" + requestToken;
                    JSONObject jObject = new APICall().execute(url).get();
                    // the json array photos is 20 pictures under data. we put them into an
                    // array and then iterate through it, getting their specific id's and adding
                    // them to an array list because we go back through the array list and
                    // look at them specifically by their id's so that we can see the unique likes
                    JSONArray photos = jObject.getJSONArray("data");
                    for (int i = 0; i < photos.length(); ++i) {
                        JSONObject photo = photos.getJSONObject(i);
                        photoIDs.add(photo.getString("id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e){
                    e.printStackTrace();
                } catch (ExecutionException e){
                    e.printStackTrace();
                }


                for (String s : photoIDs) {
                    // this goes through all the photo id's in the array list that we added earlier
                    // we do this because to get specific info about a picture, we have to make a
                    // unique api request for each picture
                    try {
                        // api url call for getting info about a specific picture, in our case we want to
                        // see who all liked the picture
                        String url = "https://api.instagram.com/v1/media/" + s + "/likes?access_token=" + requestToken;
                        JSONObject jObject = new APICall().execute(url).get();
                        JSONArray likes = jObject.getJSONArray("data");
                        for (int k = 0; k < likes.length(); k++) {
                            JSONObject like = likes.getJSONObject(k);
                            if (likeCountHM.containsKey(like.getString("username"))) {
                                likeCountHM.put(like.getString("username"), likeCountHM.get(like.getString("username")) + 1);
                            } else {
                                likeCountHM.put(like.getString("username"), 1);
                            }
                        }
                        System.out.println(likeCountHM.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    } catch (ExecutionException e){
                        e.printStackTrace();
                    }
                }
                System.out.println(likeCountHM.toString());
                Object[] a = likeCountHM.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
                                ((Map.Entry<String, Integer>) o1).getValue());
                    }
                });
                for (Object e : a) {
                    System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
                            + ((Map.Entry<String, Integer>) e).getValue());
                }


                Object e = a[0];
                userLike1.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[1];
                userLike2.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[2];
                userLike3.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[3];
                userLike4.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[4];
                userLike5.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[5];
                userLike6.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[6];
                userLike7.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[7];
                userLike8.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[8];
                userLike9.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[9];
                userLike10.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[10];
                userLike11.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[11];
                userLike12.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[12];
                userLike13.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[13];
                userLike14.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());
                e = a[14];
                userLike15.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                        + ((Map.Entry<String, Integer>) e).getValue());

                progBar.setVisibility(View.GONE);
            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture_likes, menu);
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

class BackgroundTask extends AsyncTask<String, Void, HashMap>{
    @Override
    protected HashMap doInBackground(String... params) {
        String url = params[0];
        String requestToken = params[1];
        ArrayList<String> photoIDs = new ArrayList<String>();
        HashMap<String, Integer> likeCountHM = new HashMap<String, Integer>();
        try {
            // api url to get a specific users recent posts
            JSONObject jObject = new APICall().execute(url).get();
            // the json array photos is 20 pictures under data. we put them into an
            // array and then iterate through it, getting their specific id's and adding
            // them to an array list because we go back through the array list and
            // look at them specifically by their id's so that we can see the unique likes
            JSONArray photos = jObject.getJSONArray("data");
            for (int i = 0; i < photos.length(); ++i) {
                JSONObject photo = photos.getJSONObject(i);
                photoIDs.add(photo.getString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        }


        for (String s : photoIDs) {
            // this goes through all the photo id's in the array list that we added earlier
            // we do this because to get specific info about a picture, we have to make a
            // unique api request for each picture
            try {
                // api url call for getting info about a specific picture, in our case we want to
                // see who all liked the picture
                String photoURL = "https://api.instagram.com/v1/media/" + s + "/likes?access_token=" + requestToken;
                JSONObject jObject = new APICall().execute(photoURL).get();
                JSONArray likes = jObject.getJSONArray("data");
                for (int k = 0; k < likes.length(); k++) {
                    JSONObject like = likes.getJSONObject(k);
                    if (likeCountHM.containsKey(like.getString("username"))) {
                        likeCountHM.put(like.getString("username"), likeCountHM.get(like.getString("username")) + 1);
                    } else {
                        likeCountHM.put(like.getString("username"), 1);
                    }
                }
                System.out.println(likeCountHM.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            } catch (ExecutionException e){
                e.printStackTrace();
            }
        }
        return likeCountHM;
    }
}
