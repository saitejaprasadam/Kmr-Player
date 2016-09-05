package com.prasadam.kmrplayer.SharedClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/*
 * Created by use on 2/24/2016.
 */
public class PermissionHelper {

    public static void requestSystemAlertWindowPermission(Context context, Activity activity) {
        if(context.checkCallingOrSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 1);
    }

    public static boolean checkReadExternalStoragePermission(Context context){
        return (context.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
}
