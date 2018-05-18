package com.abhiinteractive.guessthecode;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    LinearLayout makeYourGuessBox;
    TextView historyNo;
    ImageView historyCorrectPos, historyWrongPos, historyWrongChar;
    ShowcaseView sv, sv2, sv3;
    boolean svShown = false, sv2Shown = false, sv3shown = false;
    int counter = 0;

    private RewardedVideoAd mAd2;
    boolean adLoadAttempt = false;
    AdView adview2;
    boolean levelComplete = false;
    int stars = 0;
    int hintToast = 0;
    private int noOfDigits;
    private int chances;
    View.OnClickListener onClick, onClick2, onClick3;
    private final String[] randomNumber = new String[7]; //Holds random numbers as string
    private final Random random = new Random();
    private final EditText[] etReference = new EditText[7]; //Holds as many EditText's as noOfDigits
    private final String[] userCharacter = new String[7]; //Holds user entered numbers
    private String alphabetSet; //The alphabet set as single string
    private TextView submitBtn;
    private String[] alphabets; //The alphabet set as individual characters
    private final ArrayList<List> arrayList = new ArrayList<>();
    private String mode;
    private int oneStarReq, twoStarReq, threeStarReq;
    private final int NO_OF_STARS = 3;
    private LinearLayout starLL;
    private int level;
    private TextView chancesTextView; //THe TV on top left
    TextView optionsMenuButton;
    private boolean lockToast = true;
    StringBuilder possibleHints = new StringBuilder();
    private boolean gotHint = false;
    String currentHintsString;
    int bestChances;
    int currentHintsInt;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerAdapter mArrayAdapter;
    Animation[] animation = new Animation[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        //Load banner ad
        adview2 = findViewById(R.id.banner_ad);
        final AdRequest adrequest = new AdRequest.Builder().addTestDevice("C50478B9352C968A158C206EB2D1DA63").build();
        adview2.loadAd(adrequest);

        mAd2 = MobileAds.getRewardedVideoAdInstance(this);
        mAd2.loadAd("ca-app-pub-6229326724546843/6552115812", new AdRequest.Builder().addTestDevice("C50478B9352C968A158C206EB2D1DA63").build());
        mAd2.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                if (adLoadAttempt) {
                    Toast.makeText(GameActivity.this, "Ad loaded", Toast.LENGTH_SHORT).show();
                    adLoadAttempt = false;
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {
                hintToast = 2;
            }

            @Override
            public void onRewardedVideoAdClosed() {
                mAd2.loadAd("ca-app-pub-6229326724546843/6552115812", adrequest);
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                hintToast = 1;
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }
        });

        //Get extras from Intent
        Bundle bundle = getIntent().getExtras();
        mode = bundle.getString("mode");
        level = bundle.getInt("level");
        noOfDigits = bundle.getInt("Digits");
        oneStarReq = bundle.getInt("oneStar");
        twoStarReq = bundle.getInt("twoStar");
        threeStarReq = bundle.getInt("threeStar");
        alphabetSet = bundle.getString("Alphabets");
        bestChances = bundle.getInt("best");
        final int charsInAlphabetSet = alphabetSet.length();
        char[] alphabetsChar = alphabetSet.toCharArray();
        alphabets = new String[charsInAlphabetSet];
        for (int i = 0; i < charsInAlphabetSet; i++) {
            alphabets[i] = String.valueOf(alphabetsChar[i]);
        }
        //Just initializing to use later
        chancesTextView = findViewById(R.id.chances_text_view);

        LinearLayout etLayout = findViewById(R.id.edit_text_layout);
        //Initialize the edit text's
        for (int i = 0; i < noOfDigits; i++) {
            EditText et = new EditText(this);
            et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            if (noOfDigits == i + 1) {
                params.setMargins((int) getResources().getDimension(R.dimen.margin_in_dp_for_et), 0, (int) getResources().getDimension(R.dimen.margin_in_dp_for_et), 0);
            } else {
                params.setMargins((int) getResources().getDimension(R.dimen.margin_in_dp_for_et), 0, 0, 0);
            }
            et.setLayoutParams(params);
            et.setBackground(getResources().getDrawable(R.drawable.edit_text_layout));
            et.setGravity(Gravity.CENTER);
            et.setSelectAllOnFocus(true);
            et.setTextSize(getResources().getDimension(R.dimen.text_size_10_sp));
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            etReference[i] = et;
            etLayout.addView(et);

        }

        /*//HashSet to select random characters
        HashSet<Character> hs = new HashSet<>();
        //Fill hashset with as many digits as required
        while (hs.size() != noOfDigits) {
            hs.add(alphabetSet.charAt(random.nextInt(charsInAlphabetSet)));
        }
        //Iterator to get elements out of hashset
        Iterator iterator = hs.iterator();
        //Get elements from hashset and put in an array
        for (int i = 0; i < noOfDigits; i++) {
            randomNumber[i] = iterator.next().toString();
        }*/
        ArrayList randomSelectedList = new ArrayList();
        Iterator itr;
        boolean repeat;

        while (noOfDigits != randomSelectedList.size()) {
            String rc = String.valueOf(alphabetSet.charAt(random.nextInt(charsInAlphabetSet)));
            if (randomSelectedList.size() == 0) {
                randomSelectedList.add(rc);
                continue;
            }
            itr = randomSelectedList.iterator();
            repeat = false;
            while (itr.hasNext()) {
                if (rc.equals(itr.next().toString())) {
                    repeat = true;
                }
            }
            if (repeat) {
                continue;
            } else {
                randomSelectedList.add(rc);
            }
        }

        Iterator iterator = randomSelectedList.iterator();
        for (int i = 0; i < noOfDigits; i++) {
            randomNumber[i] = iterator.next().toString();
        }

        //Adding listener to the ETs
        for (int j = 0; j < noOfDigits; j++) {
            final int k = j;
            etReference[j].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (etReference[k].getText().toString().length() == 1) {
                        if (noOfDigits == k + 1 && !lookForEmptyET()) {
                            CommonFunctions.hideKeyboard(getCurrentFocus());
                        } else if (!lookForEmptyET()) {
                            CommonFunctions.hideKeyboard(getCurrentFocus());
                        } else {
                            if (noOfDigits != k + 1) {
                                if (etReference[k + 1].getText().toString().length() == 0) {
                                    etReference[k + 1].selectAll();
                                    etReference[k + 1].requestFocus();
                                }
                            } else {
                                lookForEmptyET();
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    boolean wrongChar = false;
                    for (int m = 0; m < charsInAlphabetSet; m++) {
                        if (alphabets[m].equalsIgnoreCase(etReference[k].getEditableText().toString())) {
                            wrongChar = false;
                            break;
                        } else wrongChar = true;
                    }
                    if (wrongChar && !etReference[k].getText().toString().isEmpty()) {
                        Toast toast = Toast.makeText(GameActivity.this, "Invalid character\nAllowed characters: " + alphabetSet, Toast.LENGTH_LONG);
                        toast.show();
                        etReference[k].setText("");
                        etReference[k].requestFocus();
                    }
                }
            });
            //The lock mechanism
            //TODO Fix the cursor moving to beginning of ET
            etReference[j].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(etReference[k].isFocusable() && !etReference[k].getText().toString().equals("")) {
                        etReference[k].setFocusable(false);
                        etReference[k].setCursorVisible(false);
                        etReference[k].setLongClickable(true);
                        etReference[k].setTextColor(getResources().getColor(android.R.color.darker_gray));
                    } else if (!etReference[k].isFocusable()) {
                        etReference[k].setFocusable(true);
                        etReference[k].setCursorVisible(true);
                        etReference[k].setFocusableInTouchMode(true);
                        etReference[k].setLongClickable(true);
                        etReference[k].setTextColor(getResources().getColor(android.R.color.black));
                        etReference[k].setSelection(etReference[k].getText().length());
                    }
                    return false;
                }
            });
        }

        //Set the onClickListener for Submit
        submitBtn = findViewById(R.id.submit_button);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        //Set the onClickListener for Options Menu
        optionsMenuButton = findViewById(R.id.options_button);
        optionsMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sv3shown)
                    sv3.hide();
                displayOptionsMenuDialog();
            }
        });

        updateCurrentStars(chances, oneStarReq, twoStarReq, threeStarReq);

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

        if(ShowcaseViewManager.GetString("showcases.txt", 4, this).equals("0")) {
            showCaseView1();
            ShowcaseViewManager.SetString("showcases.txt", 4, "1", GameActivity.this);
        }

        if(mode.equals("casual")){
            findViewById(R.id.current_stars_linear_layout).setVisibility(View.GONE);
        }

        //Set Adapter and linear layout manager for the recycler view.
        mRecyclerView = findViewById(R.id.past_answers);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mArrayAdapter = new RecyclerAdapter(arrayList);
        mRecyclerView.setAdapter(mArrayAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adview2.pause();
        //Files - write hints data
        try{
            FileOutputStream fos = openFileOutput("hints.txt", MODE_PRIVATE);
            fos.write(String.valueOf(currentHintsInt).getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
        mAd2.pause();
    }

    @Override
    protected void onResume() {
        if(hintToast==1){
            getHint();
            hintToast=0;
        }
        else if(hintToast==2){
            Toast.makeText(GameActivity.this, "Watch complete ad to get hint", Toast.LENGTH_SHORT).show();
            hintToast=0;
        }
        super.onResume();
        adview2.resume();
        mAd2.resume();
    }

    @Override
    protected void onDestroy() {
        mAd2.destroy();
        super.onDestroy();
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
        else if(sv3shown) {
            sv3.hide();
            sv3shown = false;
        }
        else if(levelComplete){
            Intent starsGained = new Intent();
            starsGained.putExtra("level", level);
            starsGained.putExtra("chances", chances);
            starsGained.putExtra("stars", stars);
            setResult(Activity.RESULT_OK, starsGained);
            finish();
        }
        else
            super.onBackPressed();
    }
    /*-----------------------------------------SUBMIT--------------------------------------------*/
    private void submit() {
        int correctPosition = 0;
        int wrongPositionCorrectNumber = 0;
        int wrongChar;

        //Get all numbers from ET's and store in userCharacter Array as Strings
        for (int i = 0; i < noOfDigits; i++) {
            userCharacter[i] = etReference[i].getEditableText().toString();
        }

        //Handles correct position
        for (int i = 0; i < noOfDigits; i++) {
            if (userCharacter[i].equalsIgnoreCase(randomNumber[i])) {
                correctPosition++;
            }
        }

        //Handles wrong number correct position
        for (int i = 0; i < noOfDigits; i++) {
            if (Arrays.asList(randomNumber).contains(userCharacter[i].toUpperCase()) && !userCharacter[i].equalsIgnoreCase(randomNumber[i])) {
                wrongPositionCorrectNumber++;
            }
        }

        wrongChar = noOfDigits - correctPosition - wrongPositionCorrectNumber;

        //Conditions to check if ET's are not empty and not equal
        boolean condition = true; //condition to check equal numbers - becomes false if same characters
        boolean condition2 = true; //condition for empty ET's - becomes false if ET empty
        for (int i = 0; i < noOfDigits; i++) {
            for (int j = 0; j < noOfDigits; j++) {
                //Check every ET with every other ET for similar numbers
                if ((userCharacter[i].equalsIgnoreCase(userCharacter[j]))) {
                    if (i == j)
                        continue;
                    condition = false;
                    break;
                }
            }
            //Check if any ET is empty, if yes, make condition false and break out
            condition2 = !userCharacter[i].isEmpty();
            if (!condition2) {
                break;
            }
        }

        //And add to list if above conditions are false, else toast the error
        if (condition && condition2) {
            //Add to history
            arrayList.add(new List(noOfDigits, userCharacter, correctPosition, wrongPositionCorrectNumber, wrongChar));
            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
            chances++;
            //Make each ET empty after successful submission
            for (int i = 0; i < noOfDigits; i++) {
                if(etReference[i].isFocusable()) {
                    etReference[i].setText("");
                }
            }
            chancesTextView.setText("Chances: " + chances);
            updateCurrentStars(chances, oneStarReq, twoStarReq, threeStarReq);

            gotHint = false;

            if(svShown) {
                sv.hide();
                svShown = false;
            }
            if(ShowcaseViewManager.GetString("showcases.txt", 5, this).equals("0")) {
                showCaseView2();
                ShowcaseViewManager.SetString("showcases.txt", 5, "1", GameActivity.this);
            }

            if(arrayList.size()>=2) {
                SharedPreferences prefs = getSharedPreferences("LockToast", MODE_PRIVATE);
                if (prefs.getInt("timesToast", 0)<2 && arrayList.get(arrayList.size() - 1).getCorrectPosition() >= 1 && arrayList.get(arrayList.size() - 2).getCorrectPosition() >= 1 && lockToast && correctPosition!=noOfDigits) {
                    int timesToast = prefs.getInt("timesToast", 0);
                    Toast.makeText(this, "Tap and hold a character to lock it if you are sure of it's position", Toast.LENGTH_LONG).show();
                    lockToast = false;
                    ++timesToast;
                    prefs.edit().putInt("timesToast", timesToast).commit();
                }
            }
        } else if (!condition2) {
            Toast toast = Toast.makeText(this, "Please enter a character", Toast.LENGTH_SHORT);
            toast.show();
        } else if (!condition) {
            Toast toast = Toast.makeText(this, "No two numbers can be the same", Toast.LENGTH_LONG);
            toast.show();
        }

        //If noOfDigits and correct position are equal - correct guess
        if (correctPosition == noOfDigits) {
            //level_end_dialog
            levelComplete = true;
            displayLevelEndDialog(chances, oneStarReq, twoStarReq, threeStarReq);
        }

        //arrayAdapter.notifyDataSetChanged();
        mArrayAdapter.notifyDataSetChanged();
    }

    /*-------------------------------LEVEL END DIALOG--------------------------------------------*/
    private void displayLevelEndDialog(final int chances, final int oneStarReq, final int twoStarReq, final int threeStarReq) {
        final Dialog levelEndDialog = new Dialog(this);
        levelEndDialog.setContentView(R.layout.level_end_dialog);

        TextView levelTV = levelEndDialog.findViewById(R.id.dialog_level_chances);
        levelTV.setText("Correct! You took " + chances + " chances to guess the code");

        TextView bestChancesTV = levelEndDialog.findViewById(R.id.dialog_level_best);
        bestChancesTV.setText("Previous best: " + bestChances);
        if(bestChances==0)
            bestChancesTV.setVisibility(View.GONE);

        TextView newBestTV = levelEndDialog.findViewById(R.id.dialog_level_newbest);
        if(chances<bestChances){
            newBestTV.setText("New best!!!");
        }
        else
            newBestTV.setVisibility(View.GONE);
        if (chances <= threeStarReq)
            stars = 3;
        else if (chances <= twoStarReq)
            stars = 2;
        else if (chances <= oneStarReq)
            stars = 1;
        else
            stars = 0;

        for(int i=0; i<NO_OF_STARS; i++) {
            animation[i] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink_in);
            animation[i].setStartOffset(i*500);
        }
        starLL = levelEndDialog.findViewById(R.id.stars_linear_layout);
        for (int i = 0; i < NO_OF_STARS; i++) {
            if (i < stars) {
                ImageView starFilled = new ImageView(this);
                starFilled.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                starFilled.setImageResource(R.drawable.star_flled);
                starLL.addView(starFilled);
                starFilled.startAnimation(animation[i]);
            } else {
                ImageView starEmpty = new ImageView(this);
                starEmpty.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                starEmpty.setImageResource(R.drawable.star_empty);
                starLL.addView(starEmpty);
                starEmpty.startAnimation(animation[i]);
            }
        }

        Button ok = (Button) levelEndDialog.findViewById(R.id.level_end_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ShowcaseViewManager.GetString("showcases.txt", 6, GameActivity.this).equals("0")) {
                    levelEndDialog.dismiss();
                }
                else {
                    Intent starsGained = new Intent();
                    starsGained.putExtra("level", level);
                    starsGained.putExtra("chances", chances);
                    starsGained.putExtra("stars", stars);
                    setResult(Activity.RESULT_OK, starsGained);
                    finish();
                }
            }
        });

        final Button share = levelEndDialog.findViewById(R.id.share_button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                if(mode.equals("casual")){
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "I took " + (chances) + " chances to guess the code in casual mode with " + noOfDigits + " digits and " + alphabetSet.length() + " characters in set. Can you do better? Download now: https://play.google.com/store/apps/details?id=com.abhiinteractive.guessthecode");
                }
                else {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "I took " + (chances) + " chances to guess the code in level " + level + " and got " + stars + " stars. Can you do better? \nDownload now: https://play.google.com/store/apps/details?id=com.abhiinteractive.guessthecode");
                }
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share using"));
            }
        });

        if(mode.equals("casual")){
            starLL.setVisibility(View.GONE);
            newBestTV.setVisibility(View.GONE);
            bestChancesTV.setVisibility(View.GONE);
        }

        levelEndDialog.show();
    }

    /*--------------------------------LEVEL INFO DIALOG------------------------------------------*/
    private void displayLevelInfoDialog(final int level, final int digits, final String alphabets, final int oneStarReq, final int twoStarReq, final int threeStarReq) {
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

        Button ok = (Button) levelInfoDialog.findViewById(R.id.level_end_ok);
        ok.setText("Ok");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelInfoDialog.dismiss();
            }
        });

        if(mode.equals("casual")){
            levelTV.setText("Info");
            LinearLayout chancesRequiredFor = levelInfoDialog.findViewById(R.id.chances_required_for_layout);
            chancesRequiredFor.setVisibility(View.GONE);
        }

        levelInfoDialog.show();
    }

    /*---------------------------------GIVE UP DIALOG---------------------------------------------*/
    private void displayGiveUpDialog(final int noOfDigits, String r[]) {
        final Dialog giveUpDialog = new Dialog(this);
        giveUpDialog.setContentView(R.layout.give_up_dialog);

        TextView correctAns = giveUpDialog.findViewById(R.id.correct_answer);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < noOfDigits; i++) {
            sb.append(r[i]);
        }
        correctAns.setText("The correct code was: \n" + sb.toString());

        for (int i = 0; i < noOfDigits; i++) {
            etReference[i].setText(randomNumber[i]);
            etReference[i].setEnabled(false);
        }
        submitBtn.setText("Exit");
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button reviewAns = (Button) giveUpDialog.findViewById(R.id.review_answers_button);
        reviewAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveUpDialog.dismiss();
            }
        });

        Button tryAgain = (Button) giveUpDialog.findViewById(R.id.try_again_button);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
                giveUpDialog.dismiss();
            }
        });

        Button mainMenu = giveUpDialog.findViewById(R.id.menu_button);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        giveUpDialog.show();
    }

    /*--------------------------------OPTIONS MENU DIALOG-----------------------------------------*/
    private void displayOptionsMenuDialog() {
        final Dialog optionsMenuDialog = new Dialog(this);
        optionsMenuDialog.setContentView(R.layout.dialog_options_menu);

        Button getHintButton = optionsMenuDialog.findViewById(R.id.options_get_hint);
        String s1 = "GET HINT";
        String s2;
        if(currentHintsInt>0) {
            s2 = "\n(" + currentHintsInt + " hints available)";
        }
        else{
            s2 = "\n(No hints left. Click to watch ad instead)";
        }
        SpannableString ss1 = new SpannableString(s1);
        SpannableString ss2 = new SpannableString(s2);
        ss2.setSpan(new AbsoluteSizeSpan((int)CommonFunctions.convertDpToPixel(15)), 0, s2.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss2.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getHintButton.setText(TextUtils.concat(ss1, " ", ss2));
        getHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!gotHint) {
                    if(currentHintsInt>0) {
                        optionsMenuDialog.dismiss();
                        getHint();
                    }
                    else{
                        if (mAd2.isLoaded()) {
                            optionsMenuDialog.dismiss();
                            mAd2.show();
                        }
                        else {
                            Toast.makeText(GameActivity.this, "Wait for ad to load", Toast.LENGTH_SHORT).show();
                            adLoadAttempt = true;
                        }
                        }
                }
                else{
                    Toast.makeText(GameActivity.this, "Make atleast 1 guess before getting another hint", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button viewLevelInfoDialog = optionsMenuDialog.findViewById(R.id.options_view_level_info);
        viewLevelInfoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsMenuDialog.dismiss();
                displayLevelInfoDialog(level, noOfDigits, alphabetSet, oneStarReq, twoStarReq, threeStarReq);
            }
        });

        Button giveUpButton = optionsMenuDialog.findViewById(R.id.options_give_up);
        giveUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsMenuDialog.dismiss();
                displayGiveUpDialog(noOfDigits, randomNumber);
            }
        });

        optionsMenuDialog.show();
    }

    /*---------------------------------OTHER METHODS-----------------------------------------------*/
    private void getHint(){
        gotHint = true;
        if(arrayList.size()>0) {
            String usersNumber = arrayList.get(arrayList.size() - 1).getNum();
            String userNoArray[] = new String[noOfDigits];
            possibleHints.delete(0, possibleHints.capacity());
            char userNoCharArray[] = usersNumber.toCharArray();
            for(int i=0; i<noOfDigits; i++){
                userNoArray[i] = String.valueOf(userNoCharArray[i]);
            }
            boolean add;
            for(int i=0; i<usersNumber.length(); i++){
                add = true;
                for(int j=0; j<usersNumber.length(); j++){
                    if(userNoArray[j].equalsIgnoreCase(randomNumber[i])){
                        add = false;
                    }
                }
                if(add) {
                    possibleHints.append(randomNumber[i]);
                }
            }
        }
        else if(arrayList.size()==0){
            possibleHints.delete(0, possibleHints.capacity());
            for(int i=0; i<noOfDigits; i++){
                possibleHints.append(randomNumber[i]);
            }
        }
        else{
            Toast.makeText(GameActivity.this, "Some error while taking hint", Toast.LENGTH_SHORT).show();
        }

        if(possibleHints.length()!=0) {
            String possibleHintsString = possibleHints.toString();
            int hintCharIndex = random.nextInt(possibleHintsString.length());
            String correctPosHintChar = possibleHintsString.substring(hintCharIndex, hintCharIndex+1);
            Toast.makeText(GameActivity.this, correctPosHintChar + " is present somewhere", Toast.LENGTH_LONG).show();
            if(currentHintsInt>0) {
                currentHintsInt--;
            }
        }
        else{
            possibleHints.delete(0, possibleHints.capacity());
            for(int i=0; i<noOfDigits; i++){
                for(int j=0; j<noOfDigits; j++) {
                    if (userCharacter[i].equalsIgnoreCase(randomNumber[j])) {
                        if(i==j)
                            continue;
                        possibleHints.append(randomNumber[i]);
                    }
                }
            }
            String possibleHintsString = possibleHints.toString();
            int hintCharIndex = random.nextInt(possibleHintsString.length());
            String correctPosHintChar = possibleHintsString.substring(hintCharIndex, hintCharIndex+1);
            int correctPosIndex = 0;
            for(int i=0; i<noOfDigits; i++){
                if(correctPosHintChar.equalsIgnoreCase(randomNumber[i])){
                    correctPosIndex = i;
                }
            }
            etReference[correctPosIndex].setText(correctPosHintChar);
            etReference[correctPosIndex].setFocusable(false);
            etReference[correctPosIndex].setCursorVisible(false);
            etReference[correctPosIndex].setLongClickable(true);
            etReference[correctPosIndex].setTextColor(getResources().getColor(android.R.color.darker_gray));
            Toast.makeText(GameActivity.this, correctPosHintChar + " is at position " + (correctPosIndex+1) , Toast.LENGTH_LONG).show();
            if(currentHintsInt>0) {
                currentHintsInt--;
            }
        }
    }
    private boolean lookForEmptyET() {
        for (int i = 0; i < noOfDigits; i++) {
            if (etReference[i].getText().toString().length() == 0) {
                etReference[i].requestFocus();
                return true;
            }
        }
        return false;
    }
    private void updateCurrentStars(int chances, int oneStar, int twoStar, int threeStar) {
        int stars;
        if (chances > threeStar) {
            stars = 2;
            if (chances > twoStar) {
                stars = 1;
                if (chances > oneStar) {
                    stars = 0;
                }
            }
        } else {
            stars = 3;
        }
        LinearLayout currentStarsLL = findViewById(R.id.current_stars_linear_layout);
        currentStarsLL.removeAllViews();
        for (int i = 0; i < 3; i++) {
            if (stars > i) {
                ImageView starFilled = new ImageView(this);
                starFilled.setLayoutParams(new LinearLayout.LayoutParams((int)CommonFunctions.convertDpToPixel(32), (int)CommonFunctions.convertDpToPixel(32)));
                starFilled.setScaleType(ImageView.ScaleType.FIT_XY);
                starFilled.setImageResource(R.drawable.star_flled);
                currentStarsLL.addView(starFilled);
            } else {
                ImageView starEmpty = new ImageView(this);
                starEmpty.setLayoutParams(new LinearLayout.LayoutParams((int)CommonFunctions.convertDpToPixel(32), (int)CommonFunctions.convertDpToPixel(32)));
                starEmpty.setScaleType(ImageView.ScaleType.FIT_XY);
                starEmpty.setImageResource(R.drawable.star_empty);
                currentStarsLL.addView(starEmpty);

            }
        }
    }

    //ShowcaseView that first comes, asking user to enter number
    private void showCaseView1(){
        svShown = true;
        makeYourGuessBox = findViewById(R.id.make_your_guess_box);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int reqWidth = displayMetrics.widthPixels;
        makeYourGuessBox.measure(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        int reqHeight= makeYourGuessBox.getMeasuredHeight();

        sv = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(makeYourGuessBox))
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Make your guess!")
                .setContentText("Let's start by entering some code, one character per box, you do it!")
                .setOnClickListener(onClick)
                .setShowcaseDrawer(new CustomShowcaseView(getResources(), reqWidth, reqHeight, false))
                .build();
        sv.setButtonText("Hide");

        onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv.hide();
                svShown = false;
            }
        };
    }

    //ShowcaseView that explains the 3 columns of history
    private void showCaseView2(){
        sv2Shown = true;
        historyNo = findViewById(R.id.no_text_view);
        historyCorrectPos = findViewById(R.id.correct_position_text_view);
        historyWrongPos = findViewById(R.id.wrong_position_text_view);
        historyWrongChar = findViewById(R.id.wrong_char_text_view);

        historyNo.measure(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        int reqWidth= historyCorrectPos.getMeasuredWidth();
        int reqHeight= historyCorrectPos.getMeasuredHeight();

        onClick2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (counter) {
                    case 0:
                        sv2.setShowcase(new ViewTarget(historyCorrectPos), true);
                        sv2.setContentTitle("Correct character and correct position");
                        sv2.setContentText("Number of characters of your code that are at the correct position");
                        break;

                    case 1:
                        sv2.setShowcase(new ViewTarget(historyWrongPos), true);
                        sv2.setContentTitle("Correct character but wrong position");
                        sv2.setContentText("Number of characters of your code that are present in the code but at the wrong place");
                        break;

                    case 2:
                        sv2.setShowcase(new ViewTarget(historyWrongChar), true);
                        sv2.setContentTitle("Not present");
                        sv2.setContentText("Number of characters of your code that are not present in the code");
                        break;

                    case 3:
                        sv2.setTarget(Target.NONE);
                        sv2.setContentTitle("Let's see an example");
                        sv2.setContentText("Press next to continue");
                        break;

                    case 4:
                        sv2.hide();
                        sv2Shown = false;
                        if(ShowcaseViewManager.GetString("showcases.txt", 6, GameActivity.this).equals("0")) {
                            showCaseView3();
                            ShowcaseViewManager.SetString("showcases.txt", 6, "1", GameActivity.this);
                        }
                        Intent intent = new Intent(GameActivity.this, ExampleActivity.class);
                        startActivity(intent);
                        break;
                }
                counter++;
            }
        };

        sv2 = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(historyNo))
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("History")
                .setContentText("Your guess. Latest guesses appear at the bottom")
                .setOnClickListener(onClick2)
                .setShowcaseDrawer(new CustomShowcaseView(getResources(), reqWidth, reqHeight, true))
                .build();
        sv2.setButtonText(getString(R.string.next));

        /*Button button = (Button) LayoutInflater.from(this).inflate(R.layout.showcase_button, sv2, false);
        RelativeLayout.LayoutParams secondParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        secondParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        secondParams.setMargins((int)CommonFunctions.convertDpToPixel(12), 20, 20, (int)CommonFunctions.convertDpToPixel(12));
        button.setText("See example");
        button.setPadding((int)CommonFunctions.convertDpToPixel(10), (int)CommonFunctions.convertDpToPixel(10), (int)CommonFunctions.convertDpToPixel(10), (int)CommonFunctions.convertDpToPixel(10));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, ExampleActivity.class);
                startActivity(intent);
            }
        });
        sv2.addView(button, secondParams);*/
    }

    //ShowcaseView that showcases the options menu
    private void showCaseView3(){

        sv3shown = true;
        sv3 = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(optionsMenuButton))
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Options menu")
                .setContentText("You can view level info using the options menu, or take a hint if you get stuck\n\nGo ahead and guess the correct code!")
                .setOnClickListener(onClick3)
                .build();
        sv3.setButtonText("Done");

        onClick3 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv3.hide();
                sv3shown = false;
            }
        };
    }

}



