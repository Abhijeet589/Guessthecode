package com.abhiinteractive.guessthecode;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ShowcaseViewManager{

    //Line 1: Showcaseview in main activity
    //Line 2: ShowCaseView1 in level selector
    //Line 3: ShowCaseView2 in level selector

    static String[] showCase = new String[7];

    public static String GetString(String fileName, int lineNo, Context context){
        String text, returnText="";
        try {

            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\n");
                }

                inputStream.close();
                text = stringBuilder.toString();

                showCase = text.split("\n");
                returnText = showCase[lineNo-1];
            }
        }
        catch (FileNotFoundException e) {
            Log.e("abc", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("abc", "Can not read file: " + e.toString());
        }

        return returnText;
    }

    public static void SetString( String fileName, int lineNo, String value, Context context){
        try {
            if(value != null) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter
                        (context.openFileOutput(fileName, Context.MODE_PRIVATE));

                StringBuilder text = new StringBuilder();
                showCase[lineNo-1] = value;
                for(int i=0; i<7; i++){
                    text.append(showCase[i]).append("\n");
                }

                outputStreamWriter.write(text.toString());
                outputStreamWriter.close();
            }
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    public static void logData(){
        Log.i("abc", showCase[0]+""+showCase[1]);
    }
}
