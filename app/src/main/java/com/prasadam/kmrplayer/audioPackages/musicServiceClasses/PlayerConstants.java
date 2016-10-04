package com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses;

import android.content.Context;
import android.os.Handler;

import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;
import com.prasadam.kmrplayer.UI.Activities.BaseActivity.VerticalSlidingDrawerBaseActivity;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 5/31/2016.
 */

public class PlayerConstants {

    private static SongsArrayList SONGS_LIST = new SongsArrayList() {
        @Override
        public void notifyDataSetChanged() {
            if(VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter != null)
                VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void notifyItemRemoved(int index) {
            if(VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter != null)
                VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter.notifyItemRemoved(index);
        }

        @Override
        public void notifyItemInserted(int index) {
            if(VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter != null)
                VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter.notifyItemInserted(index);
        }

        @Override
        public void notifyItemChanged(int index) {
            if(VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter != null)
                VerticalSlidingDrawerBaseActivity.NowPlayingPlaylistRecyclerViewAdapter.notifyItemChanged(index);
        }
    };
    private static ArrayList<String> HASH_ID_CURRENT_PLAYLIST = new ArrayList<>();
    private static boolean SHUFFLE = false;
    private static PLAYBACK_STATE_ENUM PLAY_BACK_STATE = PLAYBACK_STATE_ENUM.LOOP;
    public static int SONG_NUMBER = 0;
    public static boolean SHOWING_PLAYLIST = false;
    public static boolean SONG_PAUSED = true;
    public static Handler SONG_CHANGE_HANDLER;
    public static Handler NOTIFICATION_HANDLER;
    public static Handler PLAY_PAUSE_HANDLER;
    public static Handler UPDATE_NOW_PLAYING_UI;

    public static ArrayList<IRequest> groupListeners = new ArrayList<>();
    public static IRequest parentGroupListener = null;

    public enum PLAYBACK_STATE_ENUM {
        LOOP, SINGLE_LOOP, OFF;

        public static PLAYBACK_STATE_ENUM toMyEnum (String myEnumString) {
            try {
                return valueOf(myEnumString);
            } catch (Exception ex) {
                return OFF;
            }
        }
    }

    public static SongsArrayList getPlayList(){ return SONGS_LIST; }
    public static void setPlayList(Context context, ArrayList<Song> songsList) {
        SONGS_LIST.setArrayList(songsList);
        SharedPreferenceHelper.setSongsListSharedPreference(context);
    }
    public static void addSongToPlaylist(Context context, Song song) {
        SONGS_LIST.add(song);
        SharedPreferenceHelper.setSongsListSharedPreference(context);
    }
    public static void setSongToPlaylist(Context context, int index, Song song){
        SONGS_LIST.set(index, song);
        SharedPreferenceHelper.setSongsListSharedPreference(context);
    }
    public static void addSongToPlaylist(Context context, ArrayList<Song> songsList) {
        SONGS_LIST.addAll(songsList);
        SharedPreferenceHelper.setSongsListSharedPreference(context);
    }
    public static void removeSongFromPlaylist(Context context, Song song) {
        SONGS_LIST.remove(song);
        SharedPreferenceHelper.setSongsListSharedPreference(context);
    }
    public static void removeSongFromPlaylist(Context context, int position) {
        SONGS_LIST.remove(position);
        SharedPreferenceHelper.setSongsListSharedPreference(context);
    }
    public static void clearPlaylist() {
        SONGS_LIST.clear();
    }
    public static int getPlaylistSize() {return SONGS_LIST.size();}

    public static ArrayList<String> get_hash_id_current_playlist() {
        return HASH_ID_CURRENT_PLAYLIST;
    }
    public static void add_hash_id_current_playlist(String id) {
        HASH_ID_CURRENT_PLAYLIST.add(id);
    }
    public static void clear_hash_id_current_playlist() {
        HASH_ID_CURRENT_PLAYLIST.clear();
    }

    public static boolean getShuffleState() {
        return SHUFFLE;
    }
    public static void setShuffleState(Context context, Boolean state) {
        SHUFFLE = state;
        SharedPreferenceHelper.setShuffle(context);
    }

    public static PLAYBACK_STATE_ENUM getPlayBackState() {
        return PLAY_BACK_STATE;
    }
    public static void setPlayBackState(Context context, PLAYBACK_STATE_ENUM state) {
        PLAY_BACK_STATE = state;
        SharedPreferenceHelper.setLoop(context);
    }
}