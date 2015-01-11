package west.districtr.instastats;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class PictureLikes extends Activity {
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    ArrayList<String> photoIDs;
    HashMap<String, Integer> likeCountHM;
    String url;

    TextView lastTimeCalc;
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

    RadioGroup RadioButtonGroup;
    RadioButton SelectedRadioButton;

    int numOfPhotosToCheck;

    ProgressBar progBar;
    String requestToken;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_likes);

        prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        lastTimeCalc = (TextView) findViewById(R.id.LastTimeCalculated);
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

        RadioButtonGroup = (RadioGroup) findViewById(R.id.RadioButtonGroup);

        tableArr = new TextView[]{userLike1, userLike2, userLike3, userLike4, userLike5, userLike6,
                userLike7, userLike8, userLike9, userLike10, userLike11, userLike12, userLike13,
                userLike14, userLike15, userLike16, userLike17, userLike18, userLike19, userLike20,
                userLike21, userLike22, userLike23, userLike24, userLike25};

        requestToken = prefs.getString("API_ACCESS_TOKEN", null);
        userID = prefs.getString("API_USER_ID", null);

        lastTimeCalc.setText(prefs.getString("LAST_DATE_CALC", "Never"));

        progBar = (ProgressBar) findViewById(R.id.LikesProgressBar);
        progBar.setVisibility(View.GONE);

        Button calcLikes = (Button) findViewById(R.id.CalculatePictureLikesButton);
        calcLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoIDs = new ArrayList<String>();
                likeCountHM = new HashMap<String, Integer>();

                url = "https://api.instagram.com/v1/users/" + userID + "/media/recent/?access_token=" + requestToken;

                int selectedId = RadioButtonGroup.getCheckedRadioButtonId();
                SelectedRadioButton = (RadioButton) findViewById(selectedId);
                // the getText call will either return 20,40,60,80,100, so we make sure it is a string
                // then turn it into an integer, and divide it by 20 to get a number 1 thu 5
                // the reason we do this is because we accumulate likes by a for loop and each
                // iteration of the for loop covers 20 pictures. I.E. 2 pages is 40 pictures
                numOfPhotosToCheck = (Integer.parseInt((String) SelectedRadioButton.getText()))/20;
                String sharedPrefKey = String.valueOf(numOfPhotosToCheck);
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
                    Toast toast = Toast.makeText(getApplicationContext(), "You can only calculate likes for each radio button once every 10 minutes. " + left +
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
    }

    private void determineTable(HashMap hashMap) {
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
        if (hashMapArrayLen >= 25) {
            hashMapArrayLen = 25;
        }
        updateTable(hashMapArray, hashMapArrayLen, 25 - hashMapArrayLen);
    }

    public synchronized void updateTable(final Object[] sortedHMArray, final int numOfValid, final int numOfEmpty) {
        /*
        A method that updates the UI with the data received from
        the background task
         */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < numOfValid; i++) {
                    Object e = sortedHMArray[i];
                    System.out.println(sortedHMArray[i].toString());
                    tableArr[i].setText(((Map.Entry<String, Integer>) e).getKey() + " : "
                            + ((Map.Entry<String, Integer>) e).getValue());
                }
                for (int i = numOfValid; i < numOfEmpty + 1; ++i) {
                    System.out.println("empty :" + tableArr[i]);
                    tableArr[i].setVisibility(View.GONE);
                }
                java.util.Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
                String todayString = formatter.format(today);
                editor.putString("LAST_DATE_CALC", todayString);
                editor.commit();
                lastTimeCalc.setText(prefs.getString("LAST_DATE_CALC", "Never"));
                progBar.setVisibility(View.GONE);

            }
        });
    }

    private class backgroundTask implements Runnable {
        /*
        A class that does all of the computation on a thread
        so that we don't stall up the main thread. Used this instead
        of Async task specifically because Async Task is asynchronous
        and we want it to be synchronized
         */
        @Override
        public synchronized void run() {
            try {
                for (int k = 0; k < numOfPhotosToCheck; ++k) {
                    // the json array photos is 20 pictures under data. we put them into an
                    // array and then iterate through it, getting their specific id's and adding
                    // them to an array list because we go back through the array list and
                    // look at them specifically by their id's so that we can see the unique likes
                    JSONObject jObject = new APICall().execute(url).get();
                    JSONArray photos = jObject.getJSONArray("data");
                    for (int i = 0; i < photos.length(); ++i) {
                        JSONObject photo = photos.getJSONObject(i);
                        photoIDs.add(photo.getString("id"));
                    }
                    if (jObject.getString("pagination").toString().equals("{}")){
                        break;
                    }else{
                        url = jObject.getJSONObject("pagination").getString("next_url");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            // Since we can only update the UI from main thread, we do that
            // with this function
            determineTable(likeCountHM);
        }
    }
}