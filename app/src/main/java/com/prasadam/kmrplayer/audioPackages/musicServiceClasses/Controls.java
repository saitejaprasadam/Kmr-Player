package com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses;

import android.content.Context;

import com.prasadam.kmrplayer.Fragments.SongsFragment;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.SocketClasses.GroupPlay.GroupPlayHelper;
import com.prasadam.kmrplayer.Activities.VerticalSlidingDrawerBaseActivity;

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
        if(PlayerConstants.getPlaylistSize() > 0 ){
            if(PlayerConstants.SONG_NUMBER < (PlayerConstants.getPlaylistSize() - 1)){
                PlayerConstants.SONG_NUMBER++;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                PlayerConstants.SONG_PAUSED = false;
            }else {
                if(PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.LOOP || PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.SINGLE_LOOP){
                    PlayerConstants.SONG_NUMBER = 0;
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                    PlayerConstants.SONG_PAUSED = false;
                }
                else{
                    MusicService.player.seekTo(0);
                    sendMessage(context.getResources().getString(R.string.pause));
                }

            }
        }
    }
    public static void previousControl(Context context) {
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(PlayerConstants.getPlaylistSize() > 0 ){
            if(PlayerConstants.SONG_NUMBER > 0){
                PlayerConstants.SONG_NUMBER--;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            } else{
                if(PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.LOOP || PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.SINGLE_LOOP){
                    PlayerConstants.SONG_NUMBER = PlayerConstants.getPlaylistSize() - 1;
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                }
                else{
                    MusicService.player.seekTo(0);
                    pauseControl(context);
                }
            }
        }
        PlayerConstants.SONG_PAUSED = false;
    }

    public static void favControl(Context context) {
        MusicService.currentSong.setIsLiked(context, !MusicService.currentSong.getIsLiked(context));
        PlayerConstants.NOTIFICATION_HANDLER.sendEmptyMessage(0);
        VerticalSlidingDrawerBaseActivity.updateSongLikeStatus(context);
        SongsFragment.recyclerViewAdapter.notifyDataSetChanged();
        VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter.notifyDataSetChanged();
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

        if(PlayerConstants.getShuffleState()){
            long seed = System.nanoTime();
            ArrayList<Song> shuffledPlaylist = new ArrayList<>(PlayerConstants.getPlaylist());
            Collections.shuffle(shuffledPlaylist, new Random(seed));
            PlayerConstants.clearPlaylist();
            PlayerConstants.addSongToPlaylist(MusicService.currentSong);
            PlayerConstants.SONG_NUMBER = 0;
            ArrayList<Song> tempList = new ArrayList<>();
            for (Song song : shuffledPlaylist)
                if(!PlayerConstants.getPlaylist().contains(song))
                    tempList.add(song);
            PlayerConstants.addSongToPlaylist(tempList);
        }

        else{
            ArrayList<Song> tempArrayList = new ArrayList<>();
            for (String hashID : PlayerConstants.get_hash_id_current_playlist()) {
                for(Song song: PlayerConstants.getPlaylist()){
                    if(hashID.equals(song.getHashID()))
                        tempArrayList.add(song);
                }
            }

            PlayerConstants.setPlayList(tempArrayList);
            int index = 0;
            for (Song song: PlayerConstants.getPlaylist()){
                if(MusicService.currentSong.getHashID().equals(song.getHashID())){
                    PlayerConstants.SONG_NUMBER = index;
                    break;
                }
                index++;
            }
        }

        VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
    }
}