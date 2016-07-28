package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * Created by Prasadam Saiteja on 7/28/2016.
 */

public class SharedPreferenceHelper {

    public static String getSongsListSortMethod(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(SharedPreferenceKeyConstants.SONGS_SORT_METHOD, SharedPreferenceKeyConstants.SONGS_SORT_METHOD_ENUM.SONGS_SORT_BY_NAME_ASC.toString());
    }
}
