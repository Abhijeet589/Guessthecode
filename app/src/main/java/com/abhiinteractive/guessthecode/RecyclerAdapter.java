package com.abhiinteractive.guessthecode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

/**
 * Created by Abhijeet on 10-09-2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {
    private ArrayList<List> mList;
    private int lastPosition = -1;
    Context context;

    public RecyclerAdapter(ArrayList<List> list) {
        mList = list;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new RecyclerHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        List c = mList.get(position);
        holder.num1View.setText(String.valueOf(c.getNum()));
        holder.correctPositionView.setText(String.valueOf(c.getCorrectPosition()));
        holder.wrongPositionView.setText(String.valueOf(c.getWrongPositionCorrectNumber()));
        holder.wrongCharView.setText(String.valueOf(c.getWrongChar()));
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context ,android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
