package com.prasadam.kmrplayer.SubClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/*
 * Created by Prasadam Saiteja on 9/5/2016.
 */

public class SquaredView extends View {

    public SquaredView(Context context) {
        super(context);
    }

    public SquaredView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquaredView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
