package com.abhiinteractive.guessthecode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static GoogleApiClient apiClient;
    SharedPreferences prefs = null;
    ShowcaseView sv;
    boolean svShown = false;
    TextView startTextView, casualTextView, leaderboardTextView, howToPlayTextView, contactUsTV;
    View.OnClickListener onClick;
    private Button settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-6229326724546843~4529783246");

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "Failed to connect to google play", Toast.LENGTH_LONG).show();
                    }
                })
                .build();

        prefs = getSharedPreferences("com.abhiinteractive.guessthecode", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            try {
                FileOutputStream fos = openFileOutput("hints.txt", MODE_PRIVATE);
                int initialStars = 3;
                fos.write(String.valueOf(initialStars).getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream fos = openFileOutput("showcases.txt", MODE_PRIVATE);
                fos.write(String.valueOf("0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0").getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        //OnClickListener for Start button
        startTextView = (TextView) findViewById(R.id.start_game_button);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (svShown) {
                    sv.hide();
                    svShown = false;
                }
                Intent start = new Intent(MainActivity.this, LevelSelector.class);
                startActivity(start);
            }
        });

        //OnClickListener for Casual button
        casualTextView = (TextView) findViewById(R.id.casual_button);
        casualTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start = new Intent(MainActivity.this, CasualModeSelector.class);
                startActivity(start);
            }
        });

        //OnClickListener for Leaderboard button
        leaderboardTextView = (TextView) findViewById(R.id.leaderboard_button);
        leaderboardTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(apiClient!=null && apiClient.isConnected()) {
                    startActivityForResult(
                            Games.Leaderboards.getLeaderboardIntent(apiClient,
                                    getString(R.string.leaderboard_most_stars)), 1);
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_LONG).show();
                }
            }
        });

        //OnClickListener for How to Play button
        howToPlayTextView = (TextView) findViewById(R.id.how_to_play_button);
        howToPlayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowcaseViewManager.SetString("showcases.txt", 1, "0", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 2, "0", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 3, "0", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 4, "0", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 5, "0", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 6, "0", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 7, "0", MainActivity.this);
                showcaseView();
            }
        });

        //OnClickListener for Contact us button
        contactUsTV = (TextView) findViewById(R.id.contact_us_button);
        final String[] adresses = {"abhiinteractive@gmail.com"};
        contactUsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact = new Intent(Intent.ACTION_SENDTO);
                contact.setData(Uri.parse("mailto:")); // only email apps should handle this
                contact.putExtra(Intent.EXTRA_EMAIL, adresses);
                if (contact.resolveActivity(getPackageManager()) != null) {
                    startActivity(contact);
                }
            }
        });

        //OnClickListener for Settings button
        /*settingsBtn = findViewById(R.id.contact_us_button);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(start);
            }
        });*/

        if (ShowcaseViewManager.GetString("showcases.txt", 1, this).equals("0")) {
            showcaseView();
            ShowcaseViewManager.SetString("showcases.txt", 1, "1", MainActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        if(svShown) {
            sv.hide();
            svShown = false;
        }
        else
            super.onBackPressed();
    }

    //Initial showcase, asking to press on start
    private void showcaseView(){

        svShown = true;
        sv = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(startTextView))
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Welcome to the app")
                .setContentText("Let's get started")
                .setOnClickListener(onClick)
                .build();
        sv.setButtonText("Hide");

        Button button = (Button) LayoutInflater.from(this).inflate(R.layout.showcase_button, sv, false);
        RelativeLayout.LayoutParams secondParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        secondParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        secondParams.setMargins((int)CommonFunctions.convertDpToPixel(12), 20, 20, (int)CommonFunctions.convertDpToPixel(12));
        button.setText("Skip Tutorial");
        button.setPadding((int)CommonFunctions.convertDpToPixel(10), (int)CommonFunctions.convertDpToPixel(10), (int)CommonFunctions.convertDpToPixel(10), (int)CommonFunctions.convertDpToPixel(10));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv.hide();
                svShown = false;
                ShowcaseViewManager.SetString("showcases.txt", 1, "1", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 2, "1", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 3, "1", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 4, "1", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 5, "1", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 6, "1", MainActivity.this);
                ShowcaseViewManager.SetString("showcases.txt", 7, "1", MainActivity.this);
            }
        });
        sv.addView(button, secondParams);

        onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv.hide();
                svShown = false;
            }
        };
    }
}
