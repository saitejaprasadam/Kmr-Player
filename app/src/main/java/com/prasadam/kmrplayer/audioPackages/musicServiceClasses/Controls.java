package com.prasadam.kmrplayer.AudioPackages.musicServiceClasses;

import android.content.Context;

import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.SocketClasses.GroupPlay.GroupPlayHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * Created by Prasadam Saiteja on 5/31/2016.
 */

public class Controls {

    public static void playControl(Context context) {
        GroupPlayHelper.notifyGroupPlayClientsIfExists();
        sendMessage(context.getResources().getString(R.string.play));
        PlayerConstants.SONG_PAUSED = false;
    }

    public static void pauseControl(Context context){
        sendMessage(context.getResources().getString(R.string.pause));
        PlayerConstants.SONG_PAUSED = true;
    }

    public static void nextControl(Context context) {
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(PlayerConstants.SONGS_LIST.size() > 0 ){
            if(PlayerConstants.SONG_NUMBER < (PlayerConstants.SONGS_LIST.size() - 1)){
                PlayerConstants.SONG_NUMBER++;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                PlayerConstants.SONG_PAUSED = false;
            }else {
                if(PlayerConstants.PLAY_BACK_STATE == PlayerConstants.PLAYBACK_STATE_ENUM.LOOP || PlayerConstants.PLAY_BACK_STATE == PlayerConstants.PLAYBACK_STATE_ENUM.SINGLE_LOOP){
                    PlayerConstants.SONG_NUMBER = 0;
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                    PlayerConstants.SONG_PAUSED = false;
                }
                else{
                    sendMessage(context.getResources().getString(R.string.pause));
                    PlayerConstants.SONG_PAUSED = true;
                }

            }
        }
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
    }

    private static void sendMessage(String message) {
        try{
            PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0, message));
        }catch(Exception ignored){}
    }

    public static void setLoop(boolean loop){
        MusicService.player.setLooping(loop);
    }

    public static void shuffleMashUpMethod() {

        if(PlayerConstants.SHUFFLE){
            long seed = System.nanoTime();
            ArrayList<Song> shuffledPlaylist = new ArrayList<>(PlayerConstants.SONGS_LIST);
            Collections.shuffle(shuffledPlaylist, new Random(seed));
            PlayerConstants.SONGS_LIST.clear();
            PlayerConstants.SONGS_LIST.add(MusicService.currentSong);
            PlayerConstants.SONG_NUMBER = 0;
            for (Song song : shuffledPlaylist) {
                if(!PlayerConstants.SONGS_LIST.contains(song))
                    PlayerConstants.SONGS_LIST.add(song);
            }
        }

        else{
            ArrayList<Song> tempArrayList = new ArrayList<>();
            for (String hashID : PlayerConstants.HASH_ID_CURRENT_PLAYLIST) {
                for(Song song: PlayerConstants.SONGS_LIST){
                    if(hashID.equals(song.getHashID()))
                        tempArrayList.add(song);
                }
            }
            PlayerConstants.SONGS_LIST = tempArrayList;
            int index = 0;
            for (Song song: PlayerConstants.SONGS_LIST){
                if(MusicService.currentSong.getHashID().equals(song.getHashID())){
                    PlayerConstants.SONG_NUMBER = index;
                    break;
                }
                index++;
            }
        }
    }
}

