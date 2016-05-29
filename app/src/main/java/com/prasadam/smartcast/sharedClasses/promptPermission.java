package com.prasadam.smartcast.sharedClasses;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by use on 2/24/2016.
 */
public class promptPermission {

    public void readExternalStorage(Activity context)
    {
        if (Build.VERSION.SDK_INT > 9) {
            int hasWriteContactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(context, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }
}
