package com.prasadam.kmrplayer.activityHelperClasses;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.R;

/*
 * Created by Prasadam Saiteja on 7/14/2016.
 */

public class DialogHelper {

    public static void showNearbyInfo(Context context){
        new MaterialDialog.Builder(context)
                .content(R.string.nearby_info_text)
                .show();
    }

}
