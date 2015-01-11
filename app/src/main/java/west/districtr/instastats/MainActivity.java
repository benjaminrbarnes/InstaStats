package west.districtr.instastats;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends Activity {
    public static final String PREFS_NAME = "MyPrefsFile";

    // these should be gone before production
    //String userID = "30846955";
    //String userID2 = "1641965654";
    //String accToken = "30846955.fb02de9.8d609643b18147d0a6de77c28747754f";
    //String accToken2 = "1641965654.fb02de9.40582667abb34e8d820715ffdcabd366";
    //chris 1342339113.fb02de9.ba0421955f7045a6ba440d8d49c285c3
    // chris id  = 1342339113
    // andy 192392253.fb02de9.cf7d9aecd00f40af84aeb31002fea256
    // andy id 192392253

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        // if you want to skip authentication or force a user login
        editor.putString("API_USER_ID", "192392253");
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

        java.util.Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMd");
        String todayString = formatter.format(today);
        if(prefs.getString("DATE", "null").equals("null")){
            editor.putString("DATE", todayString);
            editor.commit();
        }else if(prefs.getString("DATE", "null") != todayString){
            editor.putString("DATE", todayString);
            editor.commit();
        }

        Button clearData = (Button) findViewById(R.id.ClearDataButton);
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("API_ACCESS_TOKEN", null);
                editor.putString("API_USER_ID", null);
                editor.commit();
            }
        });

        Button totalLikes = (Button) findViewById(R.id.TotalLikesButton);
        totalLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getString("API_ACCESS_TOKEN", "null").equals("null")){
                    Toast toast = Toast.makeText(getApplicationContext(), "You need to authenticate first!", Toast.LENGTH_LONG);
                    toast.show();
                    Intent j = new Intent(MainActivity.this, WebAuth.class);
                    startActivity(j);
                }
                if (prefs.getString("API_USER_ID", null) == null) {
                    // if there is no user id in shared prefs, split access token
                    // to find it
                    //String[] authParts = (prefs.getString("API_ACCESS_TOKEN", null)).split("\\.");
                    //System.out.println("This is what we are saving as api_user_id : " + authParts[0]);
                    //editor.putString("API_USER_ID",authParts[0]);
                    //editor.commit();
                }else {
                    Intent i = new Intent(MainActivity.this, TotalLikes.class);
                    startActivity(i);
                }
            }
        });

        Button getLikeButton = (Button) findViewById(R.id.PhotoLikesButton);
        getLikeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (prefs.getString("API_USER_ID", null) == null) {
                    // if there is no user id in shared prefs, split access token
                    // to find it
                    String[] authParts = (prefs.getString("API_ACCESS_TOKEN", null)).split("\\.");
                    System.out.println("This is what we are saving as api_user_id : " + authParts[0]);
                    editor.putString("API_USER_ID",authParts[0]);
                    editor.commit();
                }
                Intent i = new Intent(MainActivity.this, PictureLikes.class);
                startActivity(i);

                /*
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
                    /*


                    */
                //}
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


