package com.prasadam.kmrplayer.UI.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.Controls;
import com.prasadam.kmrplayer.BuildConfig;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.PermissionHelper;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceKeyConstants;

/*
 * Created by Prasadam Saiteja on 8/28/2016.
 */

public class SettingsActivity extends AppCompatActivity{

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_chevron_left_white_24dp);
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        ExtensionMethods.setStatusBarTranslucent_PreLollipop(SettingsActivity.this);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preferences);

            InitPreferences();
            SetPreferencesOnClickListeners();
            Key_album_art_update_method();
        }
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            switch (key){
                case SharedPreferenceKeyConstants.KEY_BLUR_ALBUM_ART:
                    Controls.updateNotification();
                    break;

                case SharedPreferenceKeyConstants.KEY_ALBUM_ART:{
                    Key_album_art_update_method();
                    Controls.updateNotification();
                }break;

                case SharedPreferenceKeyConstants.KEY_LOCKSCREEN_META_DATA:{
                    key_lockscreen_meta_data_update_method();
                    Controls.updateNotification();
                }break;

            }
        }

        private void InitPreferences() {
            (findPreference(SharedPreferenceKeyConstants.KEY_VERSION)).setSummary("v"+ BuildConfig.VERSION_NAME);

            if(!ActivityHelper.hasSoftNavBar(getResources())){
                SwitchPreference coloredNavBarSwitchPref = (SwitchPreference) findPreference(SharedPreferenceKeyConstants.KEY_COLORED_NAV_BAR);
                coloredNavBarSwitchPref.setEnabled(false);
                coloredNavBarSwitchPref.setTitle(coloredNavBarSwitchPref.getTitle() + " " + getResources().getString(R.string.no_available_on_your_device));
            }
        }
        private void Key_album_art_update_method() {
            SwitchPreference blurAlbumArtSwitchPref = (SwitchPreference) findPreference(SharedPreferenceKeyConstants.KEY_BLUR_ALBUM_ART);
            if(SharedPreferenceHelper.getLockScreenAlbumArtState(getActivity()))
                blurAlbumArtSwitchPref.setEnabled(true);
            else
                blurAlbumArtSwitchPref.setEnabled(false);
        }
        private void key_lockscreen_meta_data_update_method() {
            SwitchPreference LockScreenAlbumArtSwitchPref = (SwitchPreference) findPreference(SharedPreferenceKeyConstants.KEY_ALBUM_ART);
            SwitchPreference blurAlbumArtSwitchPref = (SwitchPreference) findPreference(SharedPreferenceKeyConstants.KEY_BLUR_ALBUM_ART);
            if(SharedPreferenceHelper.getLockScreenMetaDataState(getActivity())){
                LockScreenAlbumArtSwitchPref.setEnabled(true);
                blurAlbumArtSwitchPref.setEnabled(true);
            }

            else{
                LockScreenAlbumArtSwitchPref.setEnabled(false);
                blurAlbumArtSwitchPref.setEnabled(false);
            }
        }
        private void SetPreferencesOnClickListeners(){

            (findPreference(SharedPreferenceKeyConstants.KEY_REPORT_BUG)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DialogHelper.reportBug(getActivity());
                    return true;
                }
            });

            (findPreference(SharedPreferenceKeyConstants.KEY_STICKY_NOTIFICATION)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Controls.updateNotification();
                    return true;
                }
            });

            (findPreference(SharedPreferenceKeyConstants.KEY_VERSION)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivitySwitcher.launchAboutActivity(getActivity());
                    return true;
                }
            });

            (findPreference(SharedPreferenceKeyConstants.KEY_EQUALIZER)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivitySwitcher.initEqualizer(getActivity());
                    return true;
                }
            });

            (findPreference(SharedPreferenceKeyConstants.KEY_RATE)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivitySwitcher.launchMarket(getActivity());
                    return true;
                }
            });

            (findPreference(SharedPreferenceKeyConstants.KEY_USERNAME)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(o.toString().trim().length() >= 4 && o.toString().trim().length() <= 25){
                        Toast.makeText(getActivity(), "Reboot application to apply changes", Toast.LENGTH_LONG).show();
                        return true;
                    }


                    else
                        Toast.makeText(getActivity(), "Username reverted, recommeded to use length between 4 and 25", Toast.LENGTH_LONG).show();
                        return false;
                }
            });

            (findPreference(SharedPreferenceKeyConstants.KEY_FLOATING_NOTIFICATIONS)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    PermissionHelper.requestSystemAlertWindowPermission(getActivity(), getActivity());
                    return true;
                }
            });
        }
    }
}