package com.abhiinteractive.guessthecode;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CasualModeSelector extends AppCompatActivity{

    int noOfDigits;
    String characterSet;
    int characterSetLength;
    final int casualCharIds[] = {R.id.char_A, R.id.char_B, R.id.char_C, R.id.char_D, R.id.char_E, R.id.char_F,
            R.id.char_G, R.id.char_H, R.id.char_I, R.id.char_J, R.id.char_K, R.id.char_L, R.id.char_M,
            R.id.char_N, R.id.char_O, R.id.char_P, R.id.char_Q, R.id.char_R, R.id.char_S, R.id.char_T,
            R.id.char_U, R.id.char_V, R.id.char_W, R.id.char_X, R.id.char_Y, R.id.char_Z,
            R.id.char_0, R.id.char_1, R.id.char_2, R.id.char_3, R.id.char_4,
            R.id.char_5, R.id.char_6, R.id.char_7, R.id.char_8, R.id.char_9};
    final TextView[] charTVs = new TextView[casualCharIds.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casual_mode_selector);

        /*------SPINNER-----*/
        Spinner spinner = (Spinner) findViewById(R.id.no_of_digits_spinner);
        spinner.getBackground().setColorFilter(0xFFFFFF, PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.no_of_digits_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0:
                        noOfDigits = 2;
                        break;
                    case 1:
                        noOfDigits = 3;
                        break;
                    case 2:
                        noOfDigits = 4;
                        break;
                    case 3:
                        noOfDigits = 5;
                        break;
                    case 4:
                        noOfDigits = 6;
                        break;
                    case 5:
                        noOfDigits = 7;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                noOfDigits = 2;
            }
        });

        /*------TABLE LAYOUT-------*/
        for(int i=0; i<casualCharIds.length; i++) {
            charTVs[i] = findViewById(casualCharIds[i]);
        }
        for(int j=0; j<charTVs.length; j++){
            final int k = j;
            charTVs[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int cd = ((ColorDrawable) charTVs[k].getBackground()).getColor();
                    if(cd==getResources().getColor(R.color.teal500)){
                        charTVs[k].setBackgroundColor(getResources().getColor(R.color.teal300));
                    }
                    else if(cd==getResources().getColor(R.color.teal300)){
                        charTVs[k].setBackgroundColor(getResources().getColor(R.color.teal500));
                    }
                    Log.i("ch", String.valueOf(k));
                }
            });
        }

        final TextView start = findViewById(R.id.start_casual_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    private void start(){
        StringBuilder sb = new StringBuilder();
        for(int j=0; j<charTVs.length; j++){
            int cd = ((ColorDrawable) charTVs[j].getBackground()).getColor();
            if(cd==getResources().getColor(R.color.teal300)){
                charTVs[j].setBackgroundColor(getResources().getColor(R.color.teal500));
                sb.append(charTVs[j].getText().toString());
            }
        }
        characterSet = sb.toString();
        characterSetLength = characterSet.length();

        if(noOfDigits<=characterSetLength) {
            Intent startLevel1 = new Intent(CasualModeSelector.this, GameActivity.class);
            startLevel1.putExtra("mode", "casual");
            startLevel1.putExtra("level", 0);
            startLevel1.putExtra("Digits", noOfDigits);
            startLevel1.putExtra("Alphabets", characterSet);
            startLevel1.putExtra("oneStar", 1);
            startLevel1.putExtra("twoStar", 2);
            startLevel1.putExtra("threeStar", 3);
            startActivity(startLevel1);
        }
        else{
            Toast.makeText(CasualModeSelector.this, "Select atleast " + noOfDigits + " characters for " + noOfDigits + " number of digits", Toast.LENGTH_LONG).show();
        }
    }
}
