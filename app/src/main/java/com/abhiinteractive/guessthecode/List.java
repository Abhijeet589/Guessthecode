package com.abhiinteractive.guessthecode;

class List {

    private final String mNum;
    private final int mCorrectPosition;
    private final int mWrongPositionCorrectNumber;
    private final int mWrongChar;

    private final StringBuilder sb = new StringBuilder();
    List(int noOfDigits, String num[], int correctPosition, int wrongPositionCorrectNumber, int wrongChar){
        for(int i=0; i<noOfDigits; i++){
            sb.append(num[i]);
        }
        mNum = sb.toString();
        mCorrectPosition = correctPosition;
        mWrongPositionCorrectNumber = wrongPositionCorrectNumber;
        mWrongChar = wrongChar;
    }

    String getNum(){
        return mNum;
    }

    int getCorrectPosition(){
        return mCorrectPosition;
    }

    int getWrongPositionCorrectNumber(){
        return mWrongPositionCorrectNumber;
    }

    int getWrongChar(){
        return mWrongChar;
    }
}
