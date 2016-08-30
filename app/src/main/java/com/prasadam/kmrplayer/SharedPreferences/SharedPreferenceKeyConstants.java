package com.prasadam.kmrplayer.SharedPreferences;

/*
 * Created by Prasadam Saiteja on 7/28/2016.
 */

public class SharedPreferenceKeyConstants {

    public static final String SETTINGS_SHARED_PREFERENCE = "KMR_PLAYER_SETTINGS_SHARED_PREFERENCE";
    public static final String KMR_PLAYER_DEFAULT_SHARED_PREFERENCE = "KMR_PLAYER_DEFAULT_SHARED_PREFERENCE";
    public static final String SOCKETS_SHARED_PREFERENCE = "KMR_PLAYER_SOCKETS_SHARED_PREFERENCE";

    public static final String SOCKETS_ACCEPT_TRANSFERS_BY_DEFUALT_MAC_ADDRESS = "SOCKETS_ACCEPT_TRANSFERS_BY_DEFUALT_MAC_ADDRESS";
    public static final String DEFAULT_SONGS_PLAYLIST_KEY = "DEFAULT_SONGS_PLAYLIST_KEY";
    public static final String CURRENT_PLAYING_SONG_DURATION_KEY = "CURRENT_PLAYING_SONG_DURATION_KEY";
    public static final String HASH_SONG_ID_KEY = "HASH_SONG_ID_KEY";
    public static final String LAST_PLAYED_SONG_POSITION_KEY = "LAST_PLAYED_SONG_POSITION_KEY";
    public static final String SHUFFLE_KEY = "SHUFFLE_KEY";
    public static final String SONGS_SORT_METHOD = "SONGS_SORT_METHOD";
    public static final String LOOP_KEY = "LOOP_KEY";

    public enum SONGS_SORT_METHOD_ENUM{
        SONGS_SORT_BY_NAME_ASC, SONGS_SORT_BY_NAME_DSC,
    }
}
