package com.prasadam.smartcast.commonClasses;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Prasadam saiteja on 3/14/2016.
 */
public class CommonVariables {

    public static void Initializers(Context context)
    {
        Fresco.initialize(context);
    }
}
