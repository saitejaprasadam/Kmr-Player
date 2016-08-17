package com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.GroupPlay.GroupPlayHelper;
import com.prasadam.kmrplayer.VerticalSlidingDrawerBaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * Created by Prasadam Saiteja on 6/24/2016.
 */

public class MusicPlayerExtensionMethods {

    public static void shufflePlay(Context mActivity, final ArrayList<Song> songsList){

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
            VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
        }

        PlayerConstants.SHUFFLE = true;
        if(SharedVariables.globalActivityContext != null)
            VerticalSlidingDrawerBaseActivity.changeButton();
    }

    public static void playSong(Context mActivity, final ArrayList<Song> songsList, int position){

        PlayerConstants.SONG_PAUSED = false;

        if(PlayerConstants.SHUFFLE){
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
            PlayerConstants.SONGS_LIST.clear();
            PlayerConstants.SONGS_LIST.add(songsList.get(position));
            PlayerConstants.SONG_NUMBER = 0;
            for (Song song : shuffledPlaylist) {
                if(!PlayerConstants.SONGS_LIST.contains(song))
                    PlayerConstants.SONGS_LIST.add(song);
            }
        }

        else{
            PlayerConstants.SONGS_LIST = songsList;
            PlayerConstants.SONG_NUMBER = position;
        }

        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), mActivity);
        if (!isServiceRunning) {
            Intent i = new Intent(mActivity, MusicService.class);
            mActivity.startService(i);
        }

        else{
            GroupPlayHelper.notifyGroupPlayClientsIfExists();
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
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

    public static void playSong(Context mActivity, final Song parsong, int position){

        PlayerConstants.SONG_PAUSED = false;
        final ArrayList<Song> songsList = new ArrayList<>();
        songsList.add(parsong);
        if(PlayerConstants.SHUFFLE){
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
            PlayerConstants.SONGS_LIST.clear();
            PlayerConstants.SONGS_LIST.add(songsList.get(position));
            PlayerConstants.SONG_NUMBER = 0;
            for (Song song : shuffledPlaylist) {
                if(!PlayerConstants.SONGS_LIST.contains(song))
                    PlayerConstants.SONGS_LIST.add(song);
            }
        }

        else{
            PlayerConstants.SONGS_LIST = songsList;
            PlayerConstants.SONG_NUMBER = position;
        }

        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), mActivity);
        if (!isServiceRunning) {
            Intent i = new Intent(mActivity, MusicService.class);
            mActivity.startService(i);
        }

        else{
            GroupPlayHelper.notifyGroupPlayClientsIfExists();
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
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
            VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
            VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public static void addToNowPlayingPlaylist(Context context, ArrayList<Song> songsToBeAdded, String message) {

        final MaterialDialog[] loading = new MaterialDialog[1];

        try{

            loading[0] = new MaterialDialog.Builder(SharedVariables.globalActivityContext)
                    .title(R.string.adding_songs_to_playlist)
                    .content(R.string.please_wait)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            ArrayList<Song> temp = new ArrayList<>(songsToBeAdded);
            for (Song songToBeAdded : temp){
                for (Song song : PlayerConstants.SONGS_LIST) {
                    if(song.getHashID().equals(songToBeAdded.getHashID())){
                        songsToBeAdded.remove(songToBeAdded);
                        break;
                    }
                }
            }

            for (Song song : songsToBeAdded)
                PlayerConstants.SONGS_LIST.add(song);
            loading[0].dismiss();
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
            VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter.notifyDataSetChanged();
        }

        catch (Exception ignored){}
        finally {
            if(loading[0] != null)
                loading[0].dismiss();
        }


    }

    public static void playNext(Context context, Song songToBeAdded) {

        if(songToBeAdded.getHashID().equals(MusicService.currentSong.getHashID())){
            Toast.makeText(context, "This is song is currently playing", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Song song : PlayerConstants.SONGS_LIST) {
            if(song.getHashID().equals(songToBeAdded.getHashID())){
                PlayerConstants.SONGS_LIST.remove(song);
                break;
            }
        }

        PlayerConstants.SONGS_LIST.add(PlayerConstants.SONG_NUMBER + 1, songToBeAdded);
        if(PlayerConstants.SHUFFLE)
            PlayerConstants.HASH_ID_CURRENT_PLAYLIST.add(songToBeAdded.getHashID());

        Toast.makeText(context, "Song will be played next", Toast.LENGTH_SHORT).show();
        VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
    }

    public static void playNext(Context context, ArrayList<Song> songsToBeAdded, String message) {

        for (Song songToBeAdded : songsToBeAdded){
            for (Song song : PlayerConstants.SONGS_LIST) {
                if(song.getHashID().equals(songToBeAdded.getHashID()) && !songToBeAdded.getHashID().equals(MusicService.currentSong.getHashID())){
                    PlayerConstants.SONGS_LIST.remove(song);
                    break;
                }
            }
        }

        int index = 1;
        for (Song songToBeAdded : songsToBeAdded){
            PlayerConstants.SONGS_LIST.add(PlayerConstants.SONG_NUMBER + index, songToBeAdded);
            if(PlayerConstants.SHUFFLE)
                PlayerConstants.HASH_ID_CURRENT_PLAYLIST.add(songToBeAdded.getHashID());
            index++;
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
    }
}
