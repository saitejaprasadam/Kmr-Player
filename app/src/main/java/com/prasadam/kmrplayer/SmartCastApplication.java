package com.prasadam.kmrplayer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 8/14/2016.
 */

public class SmartCastApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        setMusicPlaylist_and_Settings();
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
