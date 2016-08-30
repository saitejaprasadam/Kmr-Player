package com.prasadam.kmrplayer;

import android.app.Application;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.splunk.mint.Mint;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 8/14/2016.
 */

public class SmartCastApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        Mint.initAndStartSession(this, "2e54b9a6");
        SharedPreferenceHelper.getShuffle(this);
        SharedPreferenceHelper.getLoop(this);

        ArrayList<Song> arrayList = SharedPreferenceHelper.getSongsListSharedPreference(this);
        if(arrayList != null)
            PlayerConstants.setPlayList(this, arrayList);

        int position = SharedPreferenceHelper.getLastPlayingSongPosition(this);
        if(position < PlayerConstants.getPlaylistSize()) {
            PlayerConstants.SONG_NUMBER = position;
            MusicService.currentSong = PlayerConstants.getPlaylist().get(PlayerConstants.SONG_NUMBER);
        }
    }
}
