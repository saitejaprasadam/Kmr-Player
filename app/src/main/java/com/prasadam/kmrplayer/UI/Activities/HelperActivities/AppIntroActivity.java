package com.prasadam.kmrplayer.UI.Activities.HelperActivities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.R;

/*
 * Created by Prasadam Saiteja on 8/12/2016.
 */

public class AppIntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance(getString(R.string.app_name), getString(R.string.app_description_text), R.mipmap.launcher_icon_big, ActivityHelper.getColor(this, R.color.launcher_background_color)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.permissions_title_text), getString(R.string.permissions_description_text), R.mipmap.permissions, ActivityHelper.getColor(this, R.color.launch_screen_red_palette_color)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.cluster_title_text), getString(R.string.cluster_description_text), R.mipmap.launch_screen_cluster, ActivityHelper.getColor(this, R.color.launch_screen_yellow_palette_color)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.alpha_version_title_text), getString(R.string.alpha_version_description_text), R.mipmap.launch_screen_alpha, ActivityHelper.getColor(this, R.color.launch_screen_green_palette_color)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.all_set_text), getString(R.string.get_started_text), R.mipmap.launch_screen_completed, ActivityHelper.getColor(this, R.color.launch_screen_blue_palette_color)));

        //askForPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW}, 2);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            showStatusBar(true);
        else
            showStatusBar(false);
        showDoneButton(true);
        skipButtonEnabled = false;
    }
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
