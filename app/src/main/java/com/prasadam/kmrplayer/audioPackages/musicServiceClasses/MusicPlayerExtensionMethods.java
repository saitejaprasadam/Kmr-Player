package com.prasadam.kmrplayer.audioPackages.musicServiceClasses;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.prasadam.kmrplayer.MainActivity;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * Created by Prasadam Saiteja on 6/24/2016.
 */

public class MusicPlayerExtensionMethods {

    public static void shufflePlay(Activity mActivity, final ArrayList<Song> songsList){

        PlayerConstants.SONG_PAUSED = false;
        PlayerConstants.HASH_ID_CURRENT_PLAYLIST.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Song song: songsList) {
                    PlayerConstants.HASH_ID_CURRENT_PLAYLIST.add(song.getHashID());
                }
            }
        }).start();

        long seed = System.nanoTime();
        ArrayList<Song> shuffledPlaylist = new ArrayList<>(songsList);
        Collections.shuffle(shuffledPlaylist, new Random(seed));
        PlayerConstants.SONGS_LIST = shuffledPlaylist;
        PlayerConstants.SONG_NUMBER = 0;
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), mActivity);
        if (!isServiceRunning) {
            Intent i = new Intent(mActivity, MusicService.class);
            mActivity.startService(i);
        } else
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());

        PlayerConstants.SHUFFLE = true;
        MainActivity.changeButton();
    }

    public static void playSong(Activity mActivity, final ArrayList<Song> songsList, int position){
        PlayerConstants.SONG_PAUSED = false;
        PlayerConstants.SONGS_LIST = songsList;
        PlayerConstants.SONG_NUMBER = position;
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), mActivity);
        if (!isServiceRunning) {
            Intent i = new Intent(mActivity, MusicService.class);
            mActivity.startService(i);
        }

        else
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());

        PlayerConstants.HASH_ID_CURRENT_PLAYLIST.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Song song: songsList) {
                    PlayerConstants.HASH_ID_CURRENT_PLAYLIST.add(song.getHashID());
                }
            }
        }).start();
    }

    public static void startMusicService(Activity mActivity){
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), mActivity);
        if (!isServiceRunning) {
            Intent i = new Intent(mActivity, MusicService.class);
            mActivity.startService(i);
        }
    }

    public static void changeSong(Activity mActivity, int position){

        PlayerConstants.SONG_NUMBER = position;
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), mActivity);
        if (!isServiceRunning) {
            Intent i = new Intent(mActivity, MusicService.class);
            mActivity.startService(i);
        }

        else
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
    }
}
