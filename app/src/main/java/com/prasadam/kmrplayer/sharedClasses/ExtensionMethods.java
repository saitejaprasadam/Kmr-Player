package com.prasadam.kmrplayer.SharedClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.UUID;

/*
 * Created by Prasadam Saiteja on 3/20/2016.
 */
public class ExtensionMethods {

    public static String formatIntoHHMMSS(int secsIn) {

        secsIn = secsIn / 1000;
        int hours = secsIn / 3600;
        int minutes = (secsIn % 3600) / 60;
        int seconds = (secsIn % 3600) % 60;

        if(hours == 0)
        return (minutes < 10 ? "0" : "") + minutes
                + ":" + (seconds< 10 ? "0" : "") + seconds;

        return ( (hours < 10 ? "0" : "") + hours
                + ":" + (minutes < 10 ? "0" : "") + minutes
                + ":" + (seconds< 10 ? "0" : "") + seconds );

    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
    public static boolean isLandScape(Context context){
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    public static boolean stringIsEmptyorNull(String str){
        return (str == null || str.isEmpty());
    }

    public static void setStatusBarTranslucent_PreLollipop(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintColor(ActivityHelper.getColor(activity, R.color.colorPrimaryDark));
            tintManager.setStatusBarTintEnabled(true);
        }
    }
    public static void setStatusBarTranslucent(Activity context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        view.getLayoutParams().height = getStatusBarHeight(context);
    }
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void scanMedia(Context context, String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);
        Log.d("sent broadcast message", "Scanning file");
    }

    public static String deviceName(Context context){
        String username = SharedPreferenceHelper.getUserName(context);
        if(username != null)
            return username;

        return getDeviceModelName();
    }
    public static String getTimeStamp(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    public static String getDeviceModelName(){
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer))
            return capitalize(model);
        else
            return capitalize(manufacturer) + " " + model;
    }
}
