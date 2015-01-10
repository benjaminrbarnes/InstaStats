package west.districtr.instastats;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static String AUTHTOKEN = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        final EditText edittext = (EditText) findViewById(R.id.likeText);
        edittext.setKeyListener(null);

        editor.putString("API_USER_ID", null);
        editor.putString("API_ACCESS_TOKEN", "192392253.fb02de9.cf7d9aecd00f40af84aeb31002fea256");
        editor.commit();


        // idea to see if there is an auth token present in shared prefs,
        // and if so, we know the user has already authenticated
        // if not, we need to start the intent to go to the webview page to
        // authenticate them
        if(prefs.getString("API_ACCESS_TOKEN", null) == null){
            Intent i = new Intent(MainActivity.this, WebAuth.class);
            startActivity(i);
        }

        // need to thread this correctly; below practice is running on UI thread; BAD
        //StrictMode.ThreadPolicy policy = new StrictMode.
                //ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        Button totalLikes = (Button) findViewById(R.id.TotalLikesButton);
        totalLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getString("API_USER_ID", null) == null) {
                    // if there is no user id in shared prefs, split access token
                    // to find it
                    String[] authParts = (prefs.getString("API_ACCESS_TOKEN", null)).split("\\.");
                    System.out.println("This is what we are saving as api_user_id : " + authParts[0]);
                    editor.putString("API_USER_ID",authParts[0]);
                    editor.commit();
                }
                Intent i = new Intent(MainActivity.this, TotalLikes.class);
                startActivity(i);
            }
        });

        Button getLikeButton = (Button) findViewById(R.id.button2);
        getLikeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*
                This button gathers the statistics of who has liked your 20
                most recent pictures
                 */


                // need to put this somewhere else, but for time being, we do it here
                // this basically requires the user to click on "get likes" before closing
                // app or else auth will be lost and app will crash
                if (prefs.getString("API_ACCESS_TOKEN", null) == null && AUTHTOKEN != null) {
                    // we have an auth, it just isnt saved, so when the user hits this button,
                    // it will save it into our saved preferences
                    editor.putString("API_ACCESS_TOKEN", AUTHTOKEN);
                    editor.commit();
                    System.out.println("Auth token just commited as " + prefs.getString("API_ACCESS_TOKEN", null));
                }

                if (prefs.getString("API_ACCESS_TOKEN", null) == null && AUTHTOKEN == null) {
                    // this happens if user has not authenticated and they dont have a saved
                    // auth token

                    // make this into a toast message, and then start the intent for the
                    // authentication page
                    System.out.println("You need to authenticate first!");
                } else {
                    // falls into here if it is saved, we don't care about auth token being null or not
                    // at this point
                    System.out.println("Auth token is saved as " + prefs.getString("API_ACCESS_TOKEN", null));
                    // first we have to see if we know their user id, or else we can't make
                    // the API call!
                    if (prefs.getString("API_USER_ID", null) == null) {
                        // if there is no user id in shared prefs, split access token
                        // to find it
                        String[] authParts = prefs.getString("API_ACCESS_TOKEN", null).split("\\.");
                        System.out.println("This is what we are saving as api_user_id : " + authParts[0]);
                        editor.putString("API_USER_ID",authParts[0]);
                        editor.commit();
                    }
                    // now we have the user id for sure, so we continue onto counting their pictures!


                    // these should be gone before production
                    //String userID = "30846955";
                    //String userID2 = "1641965654";
                    //String accToken = "30846955.fb02de9.8d609643b18147d0a6de77c28747754f";
                    //String accToken2 = "1641965654.fb02de9.40582667abb34e8d820715ffdcabd366";
                    //chris 1342339113.fb02de9.ba0421955f7045a6ba440d8d49c285c3
                    // chris id  = 1342339113
                    // andy 192392253.fb02de9.cf7d9aecd00f40af84aeb31002fea256
                    // andy id 192392253

                    String requestToken = prefs.getString("API_ACCESS_TOKEN", null);
                    String userID = prefs.getString("API_USER_ID", null);

                    // two variables below are used to accumulate likes of all pictures
                    //int sum = 0;
                    //int picSum = 0;

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
                        //JSONObject pag = jObject.getJSONObject("pagination");
                        //String urlString = pag.getString("next_url");
                        // below sums up picture likes
                            /*
                            for (int i = 0; i < photos.length(); ++i) {
                                JSONObject photo = photos.getJSONObject(i);
                                JSONObject likes = photo.getJSONObject("likes");
                                JSONArray likeData = likes.getJSONArray("data");
                                // to prevent taking forever, we will only accumulate
                                // the likes if picture has less than 100 likes
                                //System.out.println(hm.toString());
                                System.out.println(likes.getString("count"));
                                sum += Integer.parseInt(likes.getString("count"));
                                picSum++;
                                System.out.println("Sum of " + picSum + " pictures: " + sum);
                            }
                            */
                        //firstURL = new URL(urlString);
                        //System.out.println(hm.toString());
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

                    edittext.setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                            + ((Map.Entry<String, Integer>) e).getValue());
                }
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


