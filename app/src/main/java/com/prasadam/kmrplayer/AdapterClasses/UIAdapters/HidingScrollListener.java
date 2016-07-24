package com.prasadam.kmrplayer.AdapterClasses.UIAdapters;/*
 * Created by Prasadam Saiteja on 7/23/2016.
 */

import android.support.v7.widget.RecyclerView;

public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    private static boolean hidden = false;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (hidden)
            onHide();
         else
            onShow();
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy > 70) {
            hidden = true;

        } else if (dy < -5) {
            hidden = false;
        }

        /*Log.d(String.valueOf(dx), String.valueOf(dy));
        if(dy < -1 && hidden){
            onShow();
            hidden = false;
        }

        else if(dy >= 0 && !hidden){
            onHide();
            hidden = true;
        }

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            //Log.d("HidingScrollListener", "Hide");
            //onHide();
            controlsVisible = false;
            scrolledDistance = 0;
        }

        else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            //Log.d("HidingScrollListener", "Show");
            //onShow();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
            scrolledDistance += dy;
        }*/
    }

    public abstract void onHide();
    public abstract void onShow();

}
