package com.abhiinteractive.guessthecode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class ExampleActivity extends AppCompatActivity {

    ShowcaseView sv;
    int counter = 0;
    boolean svShown = false;
    LinearLayout guess0, guess1, guess2, guess3, guess4, guess5;
    View.OnClickListener onClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_activity);

        Button exitExample = findViewById(R.id.exit_example);
        exitExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button showExpl = findViewById(R.id.show_explaination_again);
        showExpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCaseView1();
            }
        });

        showCaseView1();
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

    private void showCaseView1() {
        svShown = true;
        guess0 = findViewById(R.id.example_row_title);
        guess1 = findViewById(R.id.example_row_1);
        guess2 = findViewById(R.id.example_row_2);
        guess3 = findViewById(R.id.example_row_3);
        guess4 = findViewById(R.id.example_row_4);
        guess5 = findViewById(R.id.example_row_5);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int reqWidth = displayMetrics.widthPixels;
        guess1.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int reqHeight = guess1.getMeasuredHeight();

        onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (counter) {
                    case 0:
                        sv.setShowcase(new ViewTarget(guess1), true);
                        sv.setContentTitle("Guess 1");
                        sv.setContentText("Starting off with something - abc. 2 digits at wrong position, let's find out which and their correct position");
                        counter++;
                        break;

                    case 1:
                        sv.setShowcase(new ViewTarget(guess2), true);
                        sv.setContentTitle("Guess 2");
                        sv.setContentText("Swapped a and b to see if that changes any of the number. But nope, still both digits incorrect place");
                        counter++;
                        break;

                    case 2:
                        sv.setShowcase(new ViewTarget(guess3), true);
                        sv.setContentTitle("Guess 3");
                        sv.setContentText("Now swapped a and c from the first guess. Bingo! So it's confirmed a and c were the 2 initally at wrong place and now at correct place. Now let's find the third digit");
                        counter++;
                        break;

                    case 3:
                        sv.setShowcase(new ViewTarget(guess4), true);
                        sv.setContentTitle("Guess 4");
                        sv.setContentText("The other digit has to be d or e since those are the only unused characters from the character set. Tried d, but that isn't it");
                        counter++;
                        break;

                    case 4:
                        sv.setShowcase(new ViewTarget(guess5), true);
                        sv.setContentTitle("Guess 5");
                        sv.setContentText("So it has to be e, yay!");
                        sv.setButtonText("Close");
                        counter++;
                        break;

                    case 5:
                        sv.hide();
                        svShown = false;
                        counter=0;
                        break;
                }
            }
        };

        sv = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(guess0))
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Example")
                .setContentText("Digits: 3\nAlphabet set: ABCDE\nPress next to see the first guess")
                .setOnClickListener(onClick)
                .setShowcaseDrawer(new CustomShowcaseView(getResources(), reqWidth, reqHeight, false))
                .build();
        sv.setButtonText(getString(R.string.next));

        Button button = (Button) LayoutInflater.from(this).inflate(R.layout.showcase_button, sv, false);
        RelativeLayout.LayoutParams secondParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        secondParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        secondParams.setMargins((int)CommonFunctions.convertDpToPixel(12), 20, 20, (int)CommonFunctions.convertDpToPixel(12));
        button.setText("Hide");
        button.setPadding((int)CommonFunctions.convertDpToPixel(10), (int)CommonFunctions.convertDpToPixel(10), (int)CommonFunctions.convertDpToPixel(10), (int)CommonFunctions.convertDpToPixel(10));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv.hide();
                svShown = false;
            }
        });
        sv.addView(button, secondParams);
    }


}
