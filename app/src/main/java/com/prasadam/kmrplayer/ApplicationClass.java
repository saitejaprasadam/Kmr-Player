package com.prasadam.kmrplayer;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/*
 * Created by Prasadam Saiteja on 8/14/2016.
 */

public class ApplicationClass extends Application{

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
