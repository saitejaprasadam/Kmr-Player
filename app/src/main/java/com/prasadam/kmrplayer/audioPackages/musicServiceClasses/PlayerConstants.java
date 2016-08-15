package com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses;

import java.util.ArrayList;
import android.os.Handler;

import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;

/*
 * Created by Prasadam Saiteja on 5/31/2016.
 */

public class PlayerConstants {

    public static ArrayList<Song> SONGS_LIST = new ArrayList<>();
    public static ArrayList<String> HASH_ID_CURRENT_PLAYLIST = new ArrayList<>();
    public static boolean SHOWING_PLAYLIST = false;
    public static int SONG_NUMBER = 0;
    public static boolean SONG_PAUSED = true;
    public static boolean SHUFFLE = false;
    public static Handler SONG_CHANGE_HANDLER;
    public static Handler NOTIFICATION_HANDLER;
    public static Handler PLAY_PAUSE_HANDLER;
    public static PLAYBACK_STATE_ENUM PLAY_BACK_STATE = PLAYBACK_STATE_ENUM.OFF;

    public static boolean getIsPlayingState() {
        return MusicService.player != null && MusicService.player.isPlaying();
    }
    public enum PLAYBACK_STATE_ENUM{
        LOOP, SINGLE_LOOP, OFF
    }
}
