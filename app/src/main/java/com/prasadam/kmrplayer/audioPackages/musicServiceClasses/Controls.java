package com.prasadam.kmrplayer.audioPackages.musicServiceClasses;

import android.content.Context;

import com.prasadam.kmrplayer.MainActivity;
import com.prasadam.kmrplayer.R;


/*
 * Created by Prasadam Saiteja on 5/31/2016.
 */

public class Controls {

    static String LOG_CLASS = "Controls";

    public static void playControl(Context context) {
        sendMessage(context.getResources().getString(R.string.play));
        PlayerConstants.SONG_PAUSED = false;
        MainActivity.updateNowPlayingUI(context);
    }

    public static void pauseControl(Context context) {
        sendMessage(context.getResources().getString(R.string.pause));
        PlayerConstants.SONG_PAUSED = true;
        MainActivity.updateNowPlayingUI(context);
    }

    public static void nextControl(Context context) {
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(PlayerConstants.SONGS_LIST.size() > 0 ){
            if(PlayerConstants.SONG_NUMBER < (PlayerConstants.SONGS_LIST.size()-1)){
                PlayerConstants.SONG_NUMBER++;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }else{
                PlayerConstants.SONG_NUMBER = 0;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
        PlayerConstants.SONG_PAUSED = false;
        MainActivity.updateNowPlayingUI(context);
    }

    public static void previousControl(Context context) {
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(PlayerConstants.SONGS_LIST.size() > 0 ){
            if(PlayerConstants.SONG_NUMBER > 0){
                PlayerConstants.SONG_NUMBER--;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }else{
                PlayerConstants.SONG_NUMBER = PlayerConstants.SONGS_LIST.size() - 1;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
        PlayerConstants.SONG_PAUSED = false;
        MainActivity.updateNowPlayingUI(context);
    }

    private static void sendMessage(String message) {
        try{
            PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0, message));
        }catch(Exception e){}
    }
}

