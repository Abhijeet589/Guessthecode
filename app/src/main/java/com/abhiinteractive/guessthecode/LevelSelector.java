package com.abhiinteractive.guessthecode;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LevelSelector extends AppCompatActivity{

    LinearLayout level1View;
    TextView starsView, hintsView;
    ShowcaseView sv, sv2;
    int counter = 0;
    boolean svShown = false, sv2Shown = false;

    private RewardedVideoAd mAd;
    boolean adLoadAttempt = false;
    AdView adview;
    int NO_OF_STARS = 3;
    Animation animation[] = new Animation[NO_OF_STARS];
    private int totalStars; //Keeps track of total stars
    String currentHintsString;
    int currentHintsInt;
    int hintToast = 0;
    boolean resultSuccess = false;
    View.OnClickListener onClick, onClick2;
    ProgressBar pgbar2, pgbar3, pgbar4;
    private final int[] stars_in_level = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //Initially let all stars be 0, these values change later
    private final int[] best_chances = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final int[] starsForLevelArray = {R.id.stars_for_level_1, R.id.stars_for_level_2, R.id.stars_for_level_3,
            R.id.stars_for_level_4, R.id.stars_for_level_5, R.id.stars_for_level_6,
            R.id.stars_for_level_7, R.id.stars_for_level_8, R.id.stars_for_level_9,
            R.id.stars_for_level_10, R.id.stars_for_level_11, R.id.stars_for_level_12,
            R.id.stars_for_level_13, R.id.stars_for_level_14, R.id.stars_for_level_15,
            R.id.stars_for_level_16, R.id.stars_for_level_17, R.id.stars_for_level_18,
            R.id.stars_for_level_19, R.id.stars_for_level_20, R.id.stars_for_level_21,
            R.id.stars_for_level_22, R.id.stars_for_level_23, R.id.stars_for_level_24};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_selector);

        MainActivity.apiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                    PendingResult r = Games.Leaderboards.submitScoreImmediate(MainActivity.apiClient,
                            getString(R.string.leaderboard_most_stars),
                            totalStars);
                ResultCallback callback = new ResultCallback()
                {
                    @Override
                    public void onResult(@NonNull Result result)
                    {
                    }
                };
                r.setResultCallback(callback);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });

        //Load ads
        adview = findViewById(R.id.banner_ad);
        final AdRequest adrequest = new AdRequest.Builder().addTestDevice("C50478B9352C968A158C206EB2D1DA63").build();
        adview.loadAd(adrequest);

        //Files - recieve data
        try {
            InputStream inputStream = openFileInput("starsInfo.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                //Read stars from the file
                int i=0;
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stars_in_level[i++] = Integer.parseInt(receiveString);
                }

                inputStream.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            InputStream inputStream = openFileInput("best.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                //Read stars from the file
                int i=0;
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    best_chances[i++] = Integer.parseInt(receiveString);
                }
                inputStream.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        //Files - recieve hints
//        try {
//            InputStream inputStream = openFileInput("hints.txt");
//            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                currentHintsString = bufferedReader.readLine();
//                currentHintsInt = Integer.parseInt(currentHintsString);
//                inputStream.close();
//            }
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        updateHints();

        ImageView addHints = findViewById(R.id.add_hints);
        addHints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayWatchAdDialog();
            }
        });

        TextView currentHintsTV = findViewById(R.id.hints);
        currentHintsTV.setText("Hints: " + currentHintsInt);

        //Calculate total number of stars
        calcStars();

        //Set the level data
        initializeLevelData();

        //Get the stars for each level from the stars_in_level array and display them as star icons
        LinearLayout starsInLevel; //This changes to next level with each iteration
        for(int i=0; i<stars_in_level.length; i++){
            for(int j=0; j<3; j++) {
                starsInLevel = findViewById(starsForLevelArray[i]);
                if (stars_in_level[i] > j) {
                    ImageView starFilled = new ImageView(this);
                    starFilled.setLayoutParams(new LinearLayout.LayoutParams(0, 100, 1));
                    starFilled.setImageResource(R.drawable.star_filled_small);
                    starsInLevel.addView(starFilled);
                } else {
                    ImageView starEmpty = new ImageView(this);
                    starEmpty.setLayoutParams(new LinearLayout.LayoutParams(0, 100, 1));
                    starEmpty.setImageResource(R.drawable.star_empty_small);
                    starsInLevel.addView(starEmpty);
                }
            }
        }

        //Just initializing to use later
        pgbar2 = findViewById(R.id.set_2_progress_bar);
        pgbar3 = findViewById(R.id.set_3_progress_bar);
        pgbar4 = findViewById(R.id.set_4_progress_bar);

        //Check if sets are unlocked after getting all the star data
        checkForSetUnlock();

        if(ShowcaseViewManager.GetString("showcases.txt", 2, this).equals("0")) {
            showCaseView();
            ShowcaseViewManager.SetString("showcases.txt", 2, "1", LevelSelector.this);
        }
    }

    @Override
    protected void onStart() {
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.loadAd("ca-app-pub-6229326724546843/4605319762", new AdRequest.Builder().addTestDevice("C50478B9352C968A158C206EB2D1DA63").build());
        mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                if(adLoadAttempt){
                    Toast.makeText(LevelSelector.this, "Ad loaded", Toast.LENGTH_SHORT).show();
                    adLoadAttempt = false;
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {
                hintToast=2;
            }

            @Override
            public void onRewardedVideoAdClosed() {
                mAd.loadAd("ca-app-pub-6229326724546843/4605319762", new AdRequest.Builder().addTestDevice("C50478B9352C968A158C206EB2D1DA63").build());
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                currentHintsInt++;
                //Files - write hints data
                try{
                    FileOutputStream fos = openFileOutput("hints.txt", MODE_PRIVATE);
                    fos.write(String.valueOf(currentHintsInt).getBytes());
                }catch (IOException e){
                    e.printStackTrace();
                }
                updateHints();
                hintToast = 1;
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }
        });
        super.onStart();
    }

    @Override
    protected void onResume() {

        if(MainActivity.apiClient.isConnected()){
            MainActivity.apiClient.connect();
        }

        //Files - recieve hints
        try {
            InputStream inputStream = openFileInput("hints.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                currentHintsString = bufferedReader.readLine();
                currentHintsInt = Integer.parseInt(currentHintsString);
                inputStream.close();
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        mAd.resume();
        adview.resume();

        if(hintToast==1){
            Toast.makeText(LevelSelector.this, "Hint added successfully", Toast.LENGTH_SHORT).show();
            hintToast=0;
        }
        else if(hintToast==2){
            Toast.makeText(LevelSelector.this, "Watch complete ad to get hint", Toast.LENGTH_SHORT).show();
            hintToast=0;
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        mAd.pause();
        adview.pause();
        super.onPause();
    }

    //Write to file whenever this activity stops
    @Override
    protected void onStop() {
        super.onStop();
        //Files - write stars data
        try{
            FileOutputStream fos = openFileOutput("starsInfo.txt", MODE_PRIVATE);
            for(int i=0; i<24; i++){
                StringBuilder sb = new StringBuilder().append(stars_in_level[i]).append("\n");
                fos.write(String.valueOf(sb).getBytes());
            }
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Error while storing stars data", Toast.LENGTH_SHORT).show();
        }

        //Files - write best chances data
        try{
            FileOutputStream fos = openFileOutput("best.txt", MODE_PRIVATE);
            for(int i=0; i<24; i++){
                StringBuilder sb = new StringBuilder().append(best_chances[i]).append("\n");
                fos.write(String.valueOf(sb).getBytes());
            }
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Error while storing best score", Toast.LENGTH_SHORT).show();
        }

        //Files - write hints data
        try{
            FileOutputStream fos = openFileOutput("hints.txt", MODE_PRIVATE);
            fos.write(String.valueOf(currentHintsInt).getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(svShown) {
            sv.hide();
            svShown = false;
        }
        else if(sv2Shown) {
            sv2.hide();
            sv2Shown = false;
        }
        else
            super.onBackPressed();
    }

    //All things to do when game activity returns back how many stars were scored
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Activity.RESULT_OK){
            //Get result intent values
            int level = data.getIntExtra("level", 0);
            int stars = data.getIntExtra("stars",0);
            int chances = data.getIntExtra("chances", 0);

            if(best_chances[level-1]==0)
                best_chances[level-1]=chances;
            if(chances<best_chances[level-1]) {
                best_chances[level - 1] = chances;
            }
            //starsInLevel points to the stars linear layout of the level that just ended
            LinearLayout starsInLevel = findViewById(starsForLevelArray[level-1]);
            if(stars_in_level[level-1]<stars) {
                //If you scored more than previous result, remove those stars and replace by new ones
                starsInLevel.removeAllViews();
                for(int i=0; i<NO_OF_STARS; i++) {
                    animation[i] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink_in);
                    animation[i].setStartOffset(i*500);
                }
                for (int i = 0; i < 3; i++) {
                    if (i < stars) {
                        ImageView starFilled = new ImageView(this);
                        starFilled.setLayoutParams(new LinearLayout.LayoutParams(0, 100, 1));
                        starFilled.setImageResource(R.drawable.star_filled_small);
                        starsInLevel.addView(starFilled);
                        starFilled.startAnimation(animation[i]);
                    } else {
                        ImageView starEmpty = new ImageView(this);
                        starEmpty.setLayoutParams(new LinearLayout.LayoutParams(0, 100, 1));
                        starEmpty.setImageResource(R.drawable.star_empty_small);
                        starsInLevel.addView(starEmpty);
                        starEmpty.startAnimation(animation[i]);
                    }
                    //Put new stars in the stats for each level array
                    stars_in_level[level - 1] = stars;
                }
            }
            resultSuccess = true;
        }
        //Calculate stars again and check set unlock everytime a result is obtained
        updateHints();
        calcStars();
        checkForSetUnlock();

        /*if(svShown){
            sv.hide();
        }*/
        if(ShowcaseViewManager.GetString("showcases.txt", 3, this).equals("0")) {
            showCaseView2();
            ShowcaseViewManager.SetString("showcases.txt", 3, "1", LevelSelector.this);
        }
    }

    private void updateHints(){
        //Files - recieve hints
        try {
            InputStream inputStream = openFileInput("hints.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                currentHintsString = bufferedReader.readLine();
                currentHintsInt = Integer.parseInt(currentHintsString);
                inputStream.close();
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        TextView hintstv = findViewById(R.id.hints);
        hintstv.setText("Hints: " + String.valueOf(currentHintsInt));
    }

    private void initializeLevelData(){
        LinearLayout level1 = findViewById(R.id.level_1);
        level1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(svShown){
                    sv.hide();
                    svShown = false;
                }
                displayDialog(1,2, "ABCD", 4, 3, 2, best_chances[0]);
            }
        });

        LinearLayout level2 = findViewById(R.id.level_2);
        level2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(2,2, "ABCDEF", 6, 4, 3, best_chances[1]);
            }
        });

        LinearLayout level3 = findViewById(R.id.level_3);
        level3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(3,3, "ABCD",6,5,3, best_chances[2]);
            }
        });

        LinearLayout level4 = findViewById(R.id.level_4);
        level4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(4,2, "ABCDEFGHI",8,6,4, best_chances[3]);
            }
        });

        LinearLayout level5 = findViewById(R.id.level_5);
        level5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(5,2, "ABCDEF12345",9,7,5, best_chances[4]);
            }
        });

        LinearLayout level6 = findViewById(R.id.level_6);
        level6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(6,3, "ABCDE",8,6,4, best_chances[5]);
            }
        });

        LinearLayout level7 = findViewById(R.id.level_7);
        level7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(7,3, "ABC123",9,7,4, best_chances[6]);
            }
        });

        LinearLayout level8 = findViewById(R.id.level_8);
        level8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(8,3, "ABCDEFG",10,8,5, best_chances[7]);
            }
        });

        LinearLayout level9 = findViewById(R.id.level_9);
        level9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(9,4, "ABCD",6,5,3, best_chances[8]);
            }
        });

        LinearLayout level10 = findViewById(R.id.level_10);
        level10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(10,3, "ABCDEFGHI",11,8,5, best_chances[9]);
            }
        });

        LinearLayout level11 = findViewById(R.id.level_11);
        level11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(11,4, "12345",7,6,4, best_chances[10]);
            }
        });

        LinearLayout level12 = findViewById(R.id.level_12);
        level12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(12,3, "0123456789",12,9,6, best_chances[11]);
            }
        });

        LinearLayout level13 = findViewById(R.id.level_13);
        level13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(13,4, "ABCDEF",8,6,4, best_chances[12]);
            }
        });

        LinearLayout level14 = findViewById(R.id.level_14);
        level14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(14,4, "ABCD1234",11,8,5, best_chances[13]);
            }
        });

        LinearLayout level15 = findViewById(R.id.level_15);
        level15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(15,5, "ABCDE",8,6,3, best_chances[14]);
            }
        });

        LinearLayout level16 = findViewById(R.id.level_16);
        level16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(16,4, "0123456789",13,9,6, best_chances[15]);
            }
        });

        LinearLayout level17 = findViewById(R.id.level_17);
        level17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(17,5, "ABCDEF",9,7,4, best_chances[16]);
            }
        });

        LinearLayout level18 = findViewById(R.id.level_18);
        level18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(18,5, "ABCDEFGH",11,9,6, best_chances[17]);
            }
        });

        LinearLayout level19 = findViewById(R.id.level_19);
        level19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(19,6, "ABCDEF",8,6,4, best_chances[18]);
            }
        });

        LinearLayout level20 = findViewById(R.id.level_20);
        level20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(20,5, "0123456789",13,9,7, best_chances[19]);
            }
        });

        LinearLayout level21 = findViewById(R.id.level_21);
        level21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(21,6, "ABCDEFGH",11,8,5, best_chances[20]);
            }
        });

        LinearLayout level22 = findViewById(R.id.level_22);
        level22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(22,7, "ABCDEFG",7,5,4, best_chances[21]);
            }
        });

        LinearLayout level23 = findViewById(R.id.level_23);
        level23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(23,6, "0123456789",14,10,7, best_chances[22]);
            }
        });

        LinearLayout level24 = findViewById(R.id.level_24);
        level24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(24,7, "0123456789",15,12,9, best_chances[23]);
            }
        });

    }

    private void calcStars(){
        totalStars = 0;
        for(int i=0; i<stars_in_level.length; i++){
            totalStars = totalStars + stars_in_level[i];
        }
        TextView starsCount = findViewById(R.id.stars_count);
        starsCount.setText("Stars: " + String.valueOf(totalStars));

        MainActivity.apiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                PendingResult r = Games.Leaderboards.submitScoreImmediate(MainActivity.apiClient,
                        getString(R.string.leaderboard_most_stars),
                        totalStars);
                Log.i("ResultCall", r.toString());
                ResultCallback callback = new ResultCallback()
                {
                    @Override
                    public void onResult(@NonNull Result result) {}
                };
                r.setResultCallback(callback);
                //Toast.makeText(LevelSelector.this, "Score updated", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });

    }

    private void checkForSetUnlock(){

        LinearLayout set4 = findViewById(R.id.set_4_layout);
        LinearLayout set3 = findViewById(R.id.set_3_layout);
        LinearLayout set2 = findViewById(R.id.set_2_layout);
        LinearLayout set4substitute = findViewById(R.id.set_4_substitute);
        LinearLayout set3substitute = findViewById(R.id.set_3_substitute);
        LinearLayout set2substitute = findViewById(R.id.set_2_substitute);

        pgbar2.setProgress(totalStars);
        pgbar3.setProgress(totalStars);
        pgbar4.setProgress(totalStars);

        if(totalStars<36){
            set4.setVisibility(View.GONE);
            set4substitute.setVisibility(View.VISIBLE);
            if(totalStars<24){
                set3.setVisibility(View.GONE);
                set3substitute.setVisibility(View.VISIBLE);
                if(totalStars<12){
                    set2.setVisibility(View.GONE);
                    set2substitute.setVisibility(View.VISIBLE);
                    pgbar3.setVisibility(View.GONE);
                    pgbar4.setVisibility(View.GONE);
                }
                else{
                    set2.setVisibility(View.VISIBLE);
                    set2substitute.setVisibility(View.GONE);
                    pgbar3.setVisibility(View.VISIBLE);
                }
            }
            else{
                set3.setVisibility(View.VISIBLE);
                set3substitute.setVisibility(View.GONE);
                pgbar4.setVisibility(View.VISIBLE);
            }
        }
        else{
            set4.setVisibility(View.VISIBLE);
            set4substitute.setVisibility(View.GONE);
        }
    }

    private void displayDialog(final int level, final int digits, final String alphabets, final int oneStarReq, final int twoStarReq, final int threeStarReq, final int bestChances){
        final Dialog levelInfoDialog = new Dialog(this);
        levelInfoDialog.setContentView(R.layout.level_info_dialog);

        TextView levelTV = levelInfoDialog.findViewById(R.id.dialog_level_number);
        levelTV.setText("Level " + String.valueOf(level));

        TextView digitsTV = levelInfoDialog.findViewById(R.id.dialog_level_digits);
        digitsTV.setText(String.valueOf(digits));

        TextView alphabetsTV = levelInfoDialog.findViewById(R.id.dialog_level_alphabets);
        alphabetsTV.setText(String.valueOf(alphabets));

        TextView oneStarReqTV = levelInfoDialog.findViewById(R.id.one_star_requirement);
        oneStarReqTV.setText(String.valueOf(oneStarReq));

        TextView twoStarReqTV = levelInfoDialog.findViewById(R.id.two_star_requirement);
        twoStarReqTV.setText(String.valueOf(twoStarReq));

        TextView threeStarReqTV = levelInfoDialog.findViewById(R.id.three_star_requirement);
        threeStarReqTV.setText(String.valueOf(threeStarReq));

        TextView bestScore = levelInfoDialog.findViewById(R.id.dialog_level_best);
        bestScore.setText(String.valueOf(bestChances + " chances"));
        if(bestChances==0)
            levelInfoDialog.findViewById(R.id.best_linear_layout).setVisibility(View.GONE);

        Button ok = (Button)levelInfoDialog.findViewById(R.id.level_end_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startLevel1 = new Intent(LevelSelector.this, GameActivity.class);
                startLevel1.putExtra("mode", "levels");
                startLevel1.putExtra("level", level);
                startLevel1.putExtra("Digits", digits);
                startLevel1.putExtra("Alphabets", alphabets);
                startLevel1.putExtra("oneStar", oneStarReq);
                startLevel1.putExtra("twoStar", twoStarReq);
                startLevel1.putExtra("threeStar", threeStarReq);
                startLevel1.putExtra("best", bestChances);
                startActivityForResult(startLevel1, 0);
                levelInfoDialog.dismiss();
            }
        });

        levelInfoDialog.show();
    }

    private void displayWatchAdDialog(){

        final Dialog watchAd = new Dialog(this);
        watchAd.setContentView(R.layout.dialog_get_ad);

        Button ok = (Button) watchAd.findViewById(R.id.watch_ad_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAd.isLoaded()) {
                    mAd.show();
                    watchAd.dismiss();
                }
                else {
                    Toast.makeText(LevelSelector.this, "Wait for ad to load", Toast.LENGTH_SHORT).show();
                    adLoadAttempt = true;
                }
            }
        });

        watchAd.show();
    }

    //First launch showcase that asks to start the level
    private void showCaseView(){
        level1View = findViewById(R.id.level_1);

        sv = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(level1View))
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Let's start level 1")
                .setOnClickListener(onClick)
                .build();
        sv.setButtonText("Close");
        svShown = true;

        onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv.hide();
            }
        };
    }

    //Showcase that displays after coming back from doing a level
    private void showCaseView2(){
        starsView = (TextView) findViewById(R.id.stars_count);
        hintsView = (TextView) findViewById(R.id.hints);

        sv2Shown = true;

        onClick2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (counter) {
                    case 0:
                        sv2.setShowcase(new ViewTarget(hintsView), true);
                        sv2.setContentTitle("There's hints to help if you get stuck");
                        sv2.setContentText("This shows the hints you currently have. You can get more by watching ads by clicking on the '+' button");
                        break;

                    case 1:
                        sv2.setTarget(Target.NONE);
                        sv2.setStyle(R.style.CustomShowcaseTheme);
                        sv2.setContentTitle("You're all set");
                        sv2.setContentText("Get as many stars as you can! Challenge your friends, compete in the leaderboard, and have fun! Use contact us from help for any feedback and suggestions.");
                        sv2.setButtonText("Close");
                        break;

                    case 2:
                        sv2.hide();
                        sv2Shown = false;
                        break;
                }
                counter++;
            }
        };

        String contentTitleText;
        if(resultSuccess)
            contentTitleText = "Great! You just got some stars";
        else
            contentTitleText = "Stars";
        sv2 = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(starsView))
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentText("Stars signify how good are you at guessing, you need to earn stars to unlock next set of levels")
                .setContentTitle(contentTitleText)
                .setOnClickListener(onClick2)
                .build();

        sv2.setButtonText(getString(R.string.next));
    }


}
