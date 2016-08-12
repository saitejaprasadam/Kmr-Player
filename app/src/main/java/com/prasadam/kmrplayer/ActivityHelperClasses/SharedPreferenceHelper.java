package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/*
 * Created by Prasadam Saiteja on 7/28/2016.
 */

public class SharedPreferenceHelper {

    public static String getSongsListSortMethod(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(SharedPreferenceKeyConstants.SONGS_SORT_METHOD, SharedPreferenceKeyConstants.SONGS_SORT_METHOD_ENUM.SONGS_SORT_BY_NAME_ASC.toString());
    }

    public static void setClientTransferRequestAlwaysAccept(Context context, String MacAddress, boolean state){

        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.SOCKETS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Set<String> set = sharedpreferences.getStringSet(SharedPreferenceKeyConstants.SOCKETS_ACCEPT_TRANSFERS_BY_DEFUALT_MAC_ADDRESS, null);

        if(set == null)
            set = new HashSet<>();

        if(state)
            set.add(MacAddress);

        else
            set.remove(MacAddress);

        editor.putStringSet(SharedPreferenceKeyConstants.SOCKETS_ACCEPT_TRANSFERS_BY_DEFUALT_MAC_ADDRESS, set);
        editor.apply();
    }

    public static boolean getClientTransferRequestAlwaysAccept(Context context, String MacAddress){

        if(MacAddress == null)
            return false;

        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.SOCKETS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        Set<String> set = sharedpreferences.getStringSet(SharedPreferenceKeyConstants.SOCKETS_ACCEPT_TRANSFERS_BY_DEFUALT_MAC_ADDRESS, null);

        if(set == null)
            return false;

        else if(set.contains(MacAddress))
                return true;

        return false;
    }
}
