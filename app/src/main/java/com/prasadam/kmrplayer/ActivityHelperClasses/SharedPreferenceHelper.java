package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
 * Created by Prasadam Saiteja on 7/28/2016.
 */

public class SharedPreferenceHelper {

    public static String getSongsListSortMethod(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(SharedPreferenceKeyConstants.SONGS_SORT_METHOD, SharedPreferenceKeyConstants.SONGS_SORT_METHOD_ENUM.SONGS_SORT_BY_NAME_ASC.toString());
    }

    public static void setClientTransferRequestAlwaysAccept(Context context, String MacAddress, boolean state) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.SOCKETS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Set<String> set = sharedpreferences.getStringSet(SharedPreferenceKeyConstants.SOCKETS_ACCEPT_TRANSFERS_BY_DEFUALT_MAC_ADDRESS, null);

        if (set == null)
            set = new HashSet<>();

        if (state)
            set.add(MacAddress);

        else
            set.remove(MacAddress);

        editor.putStringSet(SharedPreferenceKeyConstants.SOCKETS_ACCEPT_TRANSFERS_BY_DEFUALT_MAC_ADDRESS, set);
        editor.apply();
    }
    public static boolean getClientTransferRequestAlwaysAccept(Context context, String MacAddress) {

        if (MacAddress == null)
            return false;

        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.SOCKETS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        Set<String> set = sharedpreferences.getStringSet(SharedPreferenceKeyConstants.SOCKETS_ACCEPT_TRANSFERS_BY_DEFUALT_MAC_ADDRESS, null);

        if (set == null)
            return false;

        else if (set.contains(MacAddress))
            return true;

        return false;
    }

    public static void setSongsListSharedPreference(final Context context) {
                SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.KMR_PLAYER_DEFAULT_SHARED_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                String hashIDPlaylist = "";
                for(String string : PlayerConstants.get_hash_id_current_playlist())
                    hashIDPlaylist = hashIDPlaylist + string + ",";

                String playlist = "";
                for (Song song : PlayerConstants.getPlaylist())
                    playlist = playlist + String.valueOf(song.getID()) + ",";

                editor.putString(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, playlist);
                editor.putString(SharedPreferenceKeyConstants.HASH_SONG_ID_KEY, hashIDPlaylist);
                editor.apply();
    }
    public static ArrayList<Song> getSongsListSharedPreference(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.KMR_PLAYER_DEFAULT_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        String Ids = sharedpreferences.getString(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, null);
        String HashIds = sharedpreferences.getString(SharedPreferenceKeyConstants.HASH_SONG_ID_KEY, null);

        if (Ids == null)
            return null;

        String[] songsIds = Ids.split(",", -1);
        ArrayList<Song> songsList = new ArrayList<>();
        for (String id : songsIds){
            try{
                songsList.add(AudioExtensionMethods.getSongFromID(context, Long.parseLong(id)));
            }
            catch (Exception ignored){}
        }

        if(HashIds != null){
            String[] hashId = HashIds.split(",", -1);
            for (String id : hashId){
                PlayerConstants.add_hash_id_current_playlist(id);
            }
        }

        return songsList;
    }

    public static void setLastPlayingSongPosition(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(SharedPreferenceKeyConstants.LAST_PLAYED_SONG_POSITION_KEY, PlayerConstants.SONG_NUMBER);
        editor.apply();
    }
    public static int getLastPlayingSongPosition(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(SharedPreferenceKeyConstants.LAST_PLAYED_SONG_POSITION_KEY, 0);
    }

    public static void setShuffle(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(SharedPreferenceKeyConstants.SHUFFLE_KEY, PlayerConstants.getShuffleState());
        editor.apply();
    }
    public static void getShuffle(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, Context.MODE_PRIVATE);
        PlayerConstants.setShuffleState(sharedpreferences.getBoolean(SharedPreferenceKeyConstants.SHUFFLE_KEY, false));
    }

    public static void setLoop(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SharedPreferenceKeyConstants.LOOP_KEY, PlayerConstants.getPlayBackState().toString());
        editor.apply();
    }
    public static void getLoop(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, Context.MODE_PRIVATE);
        String loopState = sharedpreferences.getString(SharedPreferenceKeyConstants.LOOP_KEY, PlayerConstants.PLAYBACK_STATE_ENUM.OFF.toString());
        PlayerConstants.setPlayBackState(PlayerConstants.PLAYBACK_STATE_ENUM.toMyEnum(loopState));
    }

    public static void setDuration(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(SharedPreferenceKeyConstants.CURRENT_PLAYING_SONG_DURATION_KEY, MusicService.player.getCurrentPosition());
        editor.apply();
    }
    public static void getDuration(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SharedPreferenceKeyConstants.DEFAULT_SONGS_PLAYLIST_KEY, Context.MODE_PRIVATE);
        MusicService.player.seekTo((int) sharedpreferences.getLong(SharedPreferenceKeyConstants.CURRENT_PLAYING_SONG_DURATION_KEY, 0));
    }
}
