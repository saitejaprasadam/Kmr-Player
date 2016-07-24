package com.prasadam.kmrplayer.AdapterClasses.UIAdapters;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

/*
 * Created by Prasadam Saiteja on 3/22/2016.
 */
public class ObservableScrollViewAdapter implements ObservableScrollViewCallbacks {

    private Context context;
    private ActionBar actionBar;

    public ObservableScrollViewAdapter(Context context){
        this.context = context;
        if(context != null) {
            actionBar = ((AppCompatActivity) context).getSupportActionBar();
            if (actionBar != null)
                actionBar.setShowHideAnimationEnabled(true);
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        if (scrollState == ScrollState.UP) {
            if (actionBar.isShowing()) {
                actionBar.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!actionBar.isShowing()) {
                actionBar.show();
            }
        }
    }
}
