package west.districtr.instastats;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;


public class MainActivity extends Activity {
    private AdView adView;
    private String adViewString = "ca-app-pub-1660316413319998/8835794669";

    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Button clearData;
    Button totalLikes;
    Button getLikeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AD
        // http://www.androidbegin.com/tutorial/integrating-new-google-admob-banner-interstitial-ads/
        //AdView adView = (AdView) this.findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder()
                // below two lines allow us to run a test ad on my phone to
                // prevent us from infringing on googles ad policy
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("D9BD95E6048C651DFE0A0F5D9A46A73F")
                //.build();
        //adView.loadAd(adRequest);

        prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        // if you want to skip authentication or force a user login
        //editor.putString("API_USER_ID", "192392253");
        //editor.putString("API_ACCESS_TOKEN", "192392253.fb02de9.cf7d9aecd00f40af84aeb31002fea256");
        //editor.commit();

        // idea to see if there is an auth token present in shared prefs,
        // and if so, we know the user has already authenticated
        // if not, we need to start the intent to go to the webview page to
        // authenticate them
        if (prefs.getString("API_ACCESS_TOKEN", null) == null) {
            Intent i = new Intent(MainActivity.this, WebAuth.class);
            startActivity(i);
        }

        clearData = (Button) findViewById(R.id.ClearDataButton);
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //editor.putString("API_ACCESS_TOKEN", "null");
                //editor.putString("API_USER_ID", "null");
                editor.clear();
                editor.commit();
            }
        });

        totalLikes = (Button) findViewById(R.id.TotalLikesButton);
        totalLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getString("API_ACCESS_TOKEN", "null").equals("null")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "You need to authenticate first!", Toast.LENGTH_LONG);
                    toast.show();
                    Intent i = new Intent(MainActivity.this, WebAuth.class);
                    startActivity(i);
                } else if (prefs.getString("API_USER_ID", "null").equals("null")) {
                    // if there is no user id in shared prefs, split access token
                    // to find it
                    String[] authParts = (prefs.getString("API_ACCESS_TOKEN", null)).split("\\.");
                    editor.putString("API_USER_ID", authParts[0]);
                    editor.commit();
                    Intent i = new Intent(MainActivity.this, TotalLikes.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, TotalLikes.class);
                    startActivity(i);
                }
            }
        });

        getLikeButton = (Button) findViewById(R.id.PhotoLikesButton);
        getLikeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (prefs.getString("API_ACCESS_TOKEN", "null").equals("null")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "You need to authenticate first!", Toast.LENGTH_LONG);
                    toast.show();
                    Intent i = new Intent(MainActivity.this, WebAuth.class);
                    startActivity(i);
                } else if (prefs.getString("API_USER_ID", "null").equals("null")) {
                    // if there is no user id in shared prefs, split access token
                    // to find it
                    String[] authParts = (prefs.getString("API_ACCESS_TOKEN", null)).split("\\.");
                    editor.putString("API_USER_ID", authParts[0]);
                    editor.commit();
                } else {
                    Intent i = new Intent(MainActivity.this, PictureLikes.class);
                    startActivity(i);
                }
            }
        });
    }
    /*
    @Override
    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    //  Called before the activity is destroyed.
    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
    */
}


