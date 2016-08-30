package com.prasadam.kmrplayer.UI.Activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;

/*
 * Created by Prasadam Saiteja on 8/28/2016.
 */

public class SettingsActivity extends AppCompatActivity{

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        ExtensionMethods.setStatusBarTranslucent(SettingsActivity.this);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preferences);
        }
    }
}
