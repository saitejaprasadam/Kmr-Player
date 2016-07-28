package com.prasadam.kmrplayer.AudioPackages.musicServiceClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.prasadam.kmrplayer.MainActivity;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.SocketClasses.GroupPlay.GroupPlayHelper;

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
        } else{
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            MainActivity.updateAlbumAdapter();
        }

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

        else{
            GroupPlayHelper.notifyGroupPlayClientsIfExists();
            MainActivity.updateAlbumAdapter();
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
        }


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

    public static void addToNowPlayingPlaylist(Context context, Song songToBeAdded) {
        boolean found = false;
        for (Song song : PlayerConstants.SONGS_LIST) {
            if(song.getHashID().equals(songToBeAdded.getHashID())){
                found = true;
                break;
            }
        }
        if(found)
            Toast.makeText(context, "Song already present in now playing playlist", Toast.LENGTH_SHORT).show();
        else{
            PlayerConstants.SONGS_LIST.add(songToBeAdded);
            Toast.makeText(context, "Song added to now playing playlist", Toast.LENGTH_SHORT).show();
            MainActivity.updateAlbumAdapter();
            MainActivity.recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public static void playNext(Context context, Song songToBeAdded) {
        int index = 0;
        for (Song song : PlayerConstants.SONGS_LIST) {
            if(song.getHashID().equals(songToBeAdded.getHashID())){
                if(index < PlayerConstants.SONGS_LIST.size())
                    PlayerConstants.SONGS_LIST.remove(index);
                break;
            }
            index++;
        }

        PlayerConstants.SONGS_LIST.add(PlayerConstants.SONG_NUMBER + 1, songToBeAdded);
        Toast.makeText(context, "Song will be played next", Toast.LENGTH_SHORT).show();
        MainActivity.updateAlbumAdapter();
    }
}
