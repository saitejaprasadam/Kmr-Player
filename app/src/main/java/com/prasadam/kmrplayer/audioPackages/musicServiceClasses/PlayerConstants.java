package com.prasadam.kmrplayer.audioPackages.musicServiceClasses;

import java.util.ArrayList;
import android.os.Handler;

import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;

/*
 * Created by Prasadam Saiteja on 5/31/2016.
 */

public class PlayerConstants {

    public static ArrayList<Song> SONGS_LIST = new ArrayList<>();
    public static int SONG_NUMBER = 0;
    public static boolean SONG_PAUSED = true;
    public static boolean SONG_CHANGED = false;
    public static Handler SONG_CHANGE_HANDLER;
    public static Handler PLAY_PAUSE_HANDLER;
    public static Handler PROGRESSBAR_HANDLER;
}
