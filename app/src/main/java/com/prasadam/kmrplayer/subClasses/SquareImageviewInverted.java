package com.prasadam.kmrplayer.SubClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/*
 * Created by Prasadam Saiteja on 6/21/2016.
 */

public class SquareImageviewInverted extends ImageView{


    public SquareImageviewInverted(Context context) {
        super(context);
    }

    public SquareImageviewInverted(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageviewInverted(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        setMeasuredDimension(height, height);
    }

}
