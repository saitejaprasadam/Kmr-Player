package com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses;

import java.util.ArrayList;

import android.os.Handler;

import com.prasadam.kmrplayer.ActivityHelperClasses.SharedPreferenceHelper;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

/*
 * Created by Prasadam Saiteja on 5/31/2016.
 */

public class PlayerConstants {

    private static ArrayList<Song> SONGS_LIST = new ArrayList<>();
    private static ArrayList<String> HASH_ID_CURRENT_PLAYLIST = new ArrayList<>();
    private static boolean SHUFFLE = false;
    private static PLAYBACK_STATE_ENUM PLAY_BACK_STATE = PLAYBACK_STATE_ENUM.OFF;
    public static int SONG_NUMBER = 0;
    public static boolean SHOWING_PLAYLIST = false;
    public static boolean SONG_PAUSED = true;
    public static Handler SONG_CHANGE_HANDLER;
    public static Handler NOTIFICATION_HANDLER;
    public static Handler PLAY_PAUSE_HANDLER;

    public static boolean getIsPlayingState() {
        return MusicService.player != null && MusicService.player.isPlaying();
    }

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

    public static ArrayList<Song> getPlaylist() {return SONGS_LIST;}
    public static void setPlayList(ArrayList<Song> songsList) {
        SONGS_LIST = new ArrayList<>(songsList);
        SharedPreferenceHelper.setSongsListSharedPreference(SharedVariables.globalActivityContext);
    }
    public static void addSongToPlaylist(Song song) {
        SONGS_LIST.add(song);
        SharedPreferenceHelper.setSongsListSharedPreference(SharedVariables.globalActivityContext);
    }
    public static void addSongToPlaylist(ArrayList<Song> songsList) {
        SONGS_LIST.addAll(songsList);
        SharedPreferenceHelper.setSongsListSharedPreference(SharedVariables.globalActivityContext);
    }
    public static void removeSongFromPlaylist(Song song) {
        SONGS_LIST.remove(song);
        SharedPreferenceHelper.setSongsListSharedPreference(SharedVariables.globalActivityContext);
    }
    public static void removeSongFromPlaylist(int position) {
        SONGS_LIST.remove(position);
        SharedPreferenceHelper.setSongsListSharedPreference(SharedVariables.globalActivityContext);
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
    public static void setShuffleState(Boolean state) {
        SHUFFLE = state;
        SharedPreferenceHelper.setShuffle(SharedVariables.globalActivityContext);
    }

    public static PLAYBACK_STATE_ENUM getPlayBackState() {
        return PLAY_BACK_STATE;
    }
    public static void setPlayBackState(PLAYBACK_STATE_ENUM state) {
        PLAY_BACK_STATE = state;
        SharedPreferenceHelper.setLoop(SharedVariables.globalActivityContext);
    }
}