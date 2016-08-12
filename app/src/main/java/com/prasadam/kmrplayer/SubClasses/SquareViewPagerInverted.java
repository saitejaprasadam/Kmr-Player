package com.prasadam.kmrplayer.SubClasses;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/*
 * Created by Prasadam Saiteja on 8/8/2016.
 */

public class SquareViewPagerInverted extends ViewPager{

    public SquareViewPagerInverted(Context context) {super(context);}
    public SquareViewPagerInverted(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        setMeasuredDimension(height, height);
    }
}
