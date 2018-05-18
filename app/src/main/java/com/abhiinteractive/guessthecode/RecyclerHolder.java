package com.abhiinteractive.guessthecode;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Abhijeet on 09-09-2017.
 */

public class RecyclerHolder extends RecyclerView.ViewHolder {
    public TextView num1View, correctPositionView, wrongPositionView, wrongCharView;
    public RecyclerHolder(View itemView) {
        super(itemView);
        num1View = itemView.findViewById(R.id.num_view);
        correctPositionView = itemView.findViewById(R.id.correct_position_view);
        wrongPositionView = itemView.findViewById(R.id.wrong_position_view);
        wrongCharView = itemView.findViewById(R.id.wrong_char_view);
    }


}
