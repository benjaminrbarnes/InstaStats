package west.districtr.instastats;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    ArrayList<String> photoIDs;
    HashMap<String, Integer> likeCountHM;
    String url;

    TextView userLike1;
    TextView userLike2;
    TextView userLike3;
    TextView userLike4;
    TextView userLike5;
    TextView userLike6;
    TextView userLike7;
    TextView userLike8;
    TextView userLike9;
    TextView userLike10;
    TextView userLike11;
    TextView userLike12;
    TextView userLike13;
    TextView userLike14;
    TextView userLike15;
    TextView userLike16;
    TextView userLike17;
    TextView userLike18;
    TextView userLike19;
    TextView userLike20;
    TextView userLike21;
    TextView userLike22;
    TextView userLike23;
    TextView userLike24;
    TextView userLike25;

    TextView[] tableArr;

    ProgressBar progBar;
    String requestToken;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_likes);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        userLike1 = (TextView) findViewById(R.id.LikeTV1);
        userLike2 = (TextView) findViewById(R.id.LikeTV2);
        userLike3 = (TextView) findViewById(R.id.LikeTV3);
        userLike4 = (TextView) findViewById(R.id.LikeTV4);
        userLike5 = (TextView) findViewById(R.id.LikeTV5);
        userLike6 = (TextView) findViewById(R.id.LikeTV6);
        userLike7 = (TextView) findViewById(R.id.LikeTV7);
        userLike8 = (TextView) findViewById(R.id.LikeTV8);
        userLike9 = (TextView) findViewById(R.id.LikeTV9);
        userLike10 = (TextView) findViewById(R.id.LikeTV10);
        userLike11 = (TextView) findViewById(R.id.LikeTV11);
        userLike12 = (TextView) findViewById(R.id.LikeTV12);
        userLike13 = (TextView) findViewById(R.id.LikeTV13);
        userLike14 = (TextView) findViewById(R.id.LikeTV14);
        userLike15 = (TextView) findViewById(R.id.LikeTV15);
        userLike16 = (TextView) findViewById(R.id.LikeTV16);
        userLike17 = (TextView) findViewById(R.id.LikeTV17);
        userLike18 = (TextView) findViewById(R.id.LikeTV18);
        userLike19 = (TextView) findViewById(R.id.LikeTV19);
        userLike20 = (TextView) findViewById(R.id.LikeTV20);
        userLike21 = (TextView) findViewById(R.id.LikeTV21);
        userLike22 = (TextView) findViewById(R.id.LikeTV22);
        userLike23 = (TextView) findViewById(R.id.LikeTV23);
        userLike24 = (TextView) findViewById(R.id.LikeTV24);
        userLike25 = (TextView) findViewById(R.id.LikeTV25);

        tableArr = new TextView[]{userLike1,userLike2,userLike3,userLike4,userLike5,userLike6,
                userLike7,userLike8,userLike9,userLike10,userLike11,userLike12,userLike13,
                userLike14,userLike15,userLike16,userLike17,userLike18,userLike19,userLike20,
                userLike21,userLike22,userLike23,userLike24,userLike25};

        requestToken = prefs.getString("API_ACCESS_TOKEN", null);
        userID = prefs.getString("API_USER_ID", null);

        photoIDs = new ArrayList<String>();
        likeCountHM = new HashMap<String, Integer>();

        progBar = (ProgressBar) findViewById(R.id.LikesProgressBar);
        progBar.setVisibility(View.GONE);

        Button calcLikes = (Button) findViewById(R.id.CalculatePictureLikesButton);
        calcLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "https://api.instagram.com/v1/users/" + userID + "/media/recent/?access_token=" + requestToken;
                // start progress spinner
                progBar.setVisibility(View.VISIBLE);
                new Thread(new backgroundTask()).start();
            }
        });
    }

    public synchronized void updateTable(final Object[] sortedHMArray, final int numOfValid, final int numOfEmpty){
        /*
        A method that updates the UI with the data received from
        the background task
         */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < numOfValid; ++i){
                    Object e = sortedHMArray[i];
                    tableArr[i].setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                            + ((Map.Entry<String, Integer>) e).getValue());
                }
                for(int i = 0; i < numOfEmpty; ++i){
                    tableArr[i].setVisibility(View.GONE);
                }
                progBar.setVisibility(View.GONE);
            }
        });

    }
    private void determineTable(HashMap hashMap){
        /*
        because the user might not have 25 unique likers over their photos,
        we have to case check and just show the unique likers they do have.
        So we determine if they have 25 here, and then we call updateTable
        to actually update the correct amount of rows on the table, and make
        empty ones disappear
         */
        Object[] hashMapArray = hashMap.entrySet().toArray();
        // sorting
        Arrays.sort(hashMapArray, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
                        ((Map.Entry<String, Integer>) o1).getValue());
            }
        });
        // printing to logcat for debugging purposes
        for (Object e : hashMapArray) {
            System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
                    + ((Map.Entry<String, Integer>) e).getValue());
        }
        int hashMapArrayLen = hashMapArray.length;
        System.out.println("leng :" + hashMapArrayLen);
        if(hashMapArrayLen >= 25){
            hashMapArrayLen = 25;
        }
        updateTable(hashMapArray, hashMapArrayLen, 25 - hashMapArrayLen);
    }


    private class backgroundTask implements Runnable{
        /*
        A class that does all of the computation on a thread
        so that we don't stall up the main thread. Used this instead
        of Async task specifically because Async Task is asynchronous
        and we want it to be synchronized
         */
        @Override
        public synchronized void run() {
            try {
                // api url to get a specific users recent posts
                JSONObject jObject = new APICall().execute(url).get();
                System.out.println(jObject.toString());
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
            // Since we can only update the UI from main thread, we do that
            // with this function
            determineTable(likeCountHM);
        }
    }
}