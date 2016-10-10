package com.prasadam.kmrplayer.ListenerClasses;

import android.support.v7.widget.RecyclerView;

/*
 * Created by Prasadam Saiteja on 7/23/2016.
 */

public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static boolean hidden = false;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (hidden){
            //onHide();
        }

         else
            onShow();
    }
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy > 70)
            hidden = true;
         else if (dy < -5)
            hidden = false;
    }

    public abstract void onHide();
    public abstract void onShow();
}
