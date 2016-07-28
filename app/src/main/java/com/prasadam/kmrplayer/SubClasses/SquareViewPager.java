package com.prasadam.kmrplayer.SubClasses;/*
 * Created by Prasadam Saiteja on 7/28/2016.
 */

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class SquareViewPager extends ViewPager{

    public SquareViewPager(Context context) {super(context);}
    public SquareViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
