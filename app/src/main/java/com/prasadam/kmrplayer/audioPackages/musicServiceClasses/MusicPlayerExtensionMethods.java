package com.prasadam.kmrplayer.audioPackages.musicServiceClasses;

import android.app.Activity;
import android.content.Intent;

import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * Created by Prasadam Saiteja on 6/24/2016.
 */

public class MusicPlayerExtensionMethods {

    public static void shufflePlay(Activity mActivity, ArrayList<Song> songsList){

        PlayerConstants.SONG_PAUSED = false;
        long seed = System.nanoTime();
        ArrayList<Song> shuffledPlaylist = songsList;
        Collections.shuffle(shuffledPlaylist, new Random(seed));
        PlayerConstants.SONGS_LIST = shuffledPlaylist;
        PlayerConstants.SONG_NUMBER = 0;
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), mActivity);
        if (!isServiceRunning) {
            Intent i = new Intent(mActivity, MusicService.class);
            mActivity.startService(i);
        } else
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());

    }
}
