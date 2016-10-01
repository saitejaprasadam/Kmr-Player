package com.prasadam.kmrplayer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.DatabaseHelper.db4oHelper;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 8/14/2016.
 */

public class kmrPlayer extends Application{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "gfQomdrCg4EUIwnB74H20UtKw";
    private static final String TWITTER_SECRET = "iVVMb27aUGFVaoruwHW4TO4HfucfMctz35b6KpiYhfe7DxY322";

    @Override
    public void onCreate() {
        super.onCreate();
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET)
        // , new Twitter(authConfig)

        initDB4OFiles();
        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserName(ExtensionMethods.deviceName(this));
        Crashlytics.setUserIdentifier(ExtensionMethods.getDeviceModelName());

        //Firebase.setAndroidContext(this);
        //Firebase myFirebaseRef = new Firebase("https://kmr-player-950e3.firebaseio.com/");

        setMusicPlaylist_and_Settings();
    }

    private void initDB4OFiles() {
        try{
            SharedVariables.fullEventsList = db4oHelper.getEventObjects(this);
            SharedVariables.fullTransferList = db4oHelper.getTransferableSongObjects(this);
        }

        catch (Exception ignored){}
    }
    private void setMusicPlaylist_and_Settings() {
        SharedPreferenceHelper.getShuffle(this);
        SharedPreferenceHelper.getLoop(this);

        ArrayList<Song> arrayList = SharedPreferenceHelper.getSongsListSharedPreference(this);
        if(arrayList != null)
            PlayerConstants.setPlayList(this, arrayList);

        int position = SharedPreferenceHelper.getLastPlayingSongPosition(this);
        if(position < PlayerConstants.getPlaylistSize()) {
            PlayerConstants.SONG_NUMBER = position;
            MusicService.currentSong = PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER);
        }
    }
}
