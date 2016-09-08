package com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.UI.Activities.VerticalSlidingDrawerBaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * Created by Prasadam Saiteja on 6/24/2016.
 */

public class MusicPlayerExtensionMethods {

    public static void shufflePlay(Context context, final ArrayList<Song> songsList){

        PlayerConstants.SONG_PAUSED = false;
        PlayerConstants.clear_hash_id_current_playlist();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Song song: songsList) {
                    PlayerConstants.add_hash_id_current_playlist(song.getHashID());
                }
            }
        }).start();

        long seed = System.nanoTime();
        ArrayList<Song> shuffledPlaylist = new ArrayList<>(songsList);
        Collections.shuffle(shuffledPlaylist, new Random(seed));
        PlayerConstants.setPlayList(context, shuffledPlaylist);
        PlayerConstants.SONG_NUMBER = 0;
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), context);
        if (!isServiceRunning) {
            Intent i = new Intent(context, MusicService.class);
            context.startService(i);
        } else{
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
        }

        PlayerConstants.setShuffleState(context, true);
        VerticalSlidingDrawerBaseActivity.changeButton();
    }
    public static void playSong(Context context, final ArrayList<Song> songsList, int position){

        PlayerConstants.SONG_PAUSED = false;

        if(PlayerConstants.getShuffleState()){
            PlayerConstants.clear_hash_id_current_playlist();
            for (Song s: songsList)
                PlayerConstants.add_hash_id_current_playlist(s.getHashID());

            long seed = System.nanoTime();
            ArrayList<Song> shuffledPlaylist = new ArrayList<>(songsList);
            Collections.shuffle(shuffledPlaylist, new Random(seed));
            PlayerConstants.clearPlaylist();
            PlayerConstants.addSongToPlaylist(context, songsList.get(position));
            PlayerConstants.SONG_NUMBER = 0;
            ArrayList<Song> tempList = new ArrayList<>();
            for (Song song : shuffledPlaylist)
                if(!PlayerConstants.getPlayList().contains(song))
                    tempList.add(song);
            PlayerConstants.addSongToPlaylist(context, tempList);
        }

        else{
            PlayerConstants.clear_hash_id_current_playlist();
            for (Song song: songsList)
                PlayerConstants.add_hash_id_current_playlist(song.getHashID());
            PlayerConstants.setPlayList(context, songsList);
            PlayerConstants.SONG_NUMBER = position;
        }

        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), context);
        if (!isServiceRunning) {
            Intent i = new Intent(context, MusicService.class);
            context.startService(i);
        }

        else{

            if(MusicService.currentSong == null)
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());

            else{
                if(!MusicService.currentSong.getHashID().equals(PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getHashID()))
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                else
                    Toast.makeText(context, "now playing list updated", Toast.LENGTH_SHORT).show();
            }

            VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
        }
    }

    public static void startMusicService(Activity mActivity){
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), mActivity);
        if (!isServiceRunning) {
            Intent i = new Intent(mActivity, MusicService.class);
            mActivity.startService(i);
        }
        //HearingHelper.StartHearingHelper();
    }

    public static void changeSong(Context mActivity, int position){

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
        for (Song song : PlayerConstants.getPlayList()) {
            if(song.getHashID().equals(songToBeAdded.getHashID())){
                found = true;
                break;
            }
        }
        if(found)
            Toast.makeText(context, "Song already present in now playing playlist", Toast.LENGTH_SHORT).show();
        else{
            PlayerConstants.addSongToPlaylist(context, songToBeAdded);
            Toast.makeText(context, "Song added to now playing playlist", Toast.LENGTH_SHORT).show();
            VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
        }
    }
    public static void addToNowPlayingPlaylist(Context context, ArrayList<Song> songsToBeAdded, String message) {

        final MaterialDialog[] loading = new MaterialDialog[1];

        try{

            loading[0] = new MaterialDialog.Builder(context)
                    .title(R.string.adding_songs_to_playlist)
                    .content(R.string.please_wait)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            ArrayList<Song> temp = new ArrayList<>(songsToBeAdded);
            for (Song songToBeAdded : temp){
                for (Song song : PlayerConstants.getPlayList()) {
                    if(song.getHashID().equals(songToBeAdded.getHashID())){
                        songsToBeAdded.remove(songToBeAdded);
                        break;
                    }
                }
            }

            PlayerConstants.addSongToPlaylist(context, songsToBeAdded);
            loading[0].dismiss();
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
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

        for (Song song : PlayerConstants.getPlayList()) {
            if(song.getHashID().equals(songToBeAdded.getHashID())){
                PlayerConstants.removeSongFromPlaylist(context, song);
                break;
            }
        }

        PlayerConstants.getPlayList().add(PlayerConstants.SONG_NUMBER + 1, songToBeAdded);
        if(PlayerConstants.getShuffleState())
            PlayerConstants.add_hash_id_current_playlist(songToBeAdded.getHashID());

        Toast.makeText(context, "Song will be played next", Toast.LENGTH_SHORT).show();
        VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
    }
    public static void playNext(Context context, ArrayList<Song> songsToBeAdded, String message) {

        for (Song songToBeAdded : songsToBeAdded){
            for (Song song : PlayerConstants.getPlayList()) {
                if(song.getHashID().equals(songToBeAdded.getHashID()) && !songToBeAdded.getHashID().equals(MusicService.currentSong.getHashID())){
                    PlayerConstants.removeSongFromPlaylist(context, song);
                    break;
                }
            }
        }

        int index = 1;
        for (Song songToBeAdded : songsToBeAdded){
            PlayerConstants.getPlayList().add(PlayerConstants.SONG_NUMBER + index, songToBeAdded);
            if(PlayerConstants.getShuffleState())
                PlayerConstants.add_hash_id_current_playlist(songToBeAdded.getHashID());
            index++;
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
    }
}
