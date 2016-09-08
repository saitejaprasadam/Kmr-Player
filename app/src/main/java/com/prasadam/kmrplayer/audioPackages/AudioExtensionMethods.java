package com.prasadam.kmrplayer.AudioPackages;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Album;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Artist;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.DBHelper;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * Created by Prasadam Saiteja on 2/28/2016.
 */

public class AudioExtensionMethods {

    public static String generateHashID(Context context,long songID){

        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getSongHashID(context, songID);
    }
    public static Long getAlubmID(Context context, Long songID){

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media._ID + "=\"" + songID + "\"", null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            Long albumID = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            musicCursor.close();
            return albumID;
        }
        return null;
    }
    public static long getSongID(Context context, String songHashID) {

        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getSongID(songHashID);
    }
    public static Song getSongFromID(Context context, long songID){

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media._ID + "=\"" + songID + "\"", null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            long thisDuration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String thisdata = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String albumArtPath = null;

            musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            Cursor cursor = musicResolver
                    .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID}, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    albumArtPath = cursor.getString(0);
                }
                cursor.close();
            }

            musicCursor.close();
            return new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId));
        }

        return null;
    }
    public static Song getSongFromPath(Context context, String fileName) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media.DATA + "=\"" + fileName + "\"", null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            long thisDuration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String thisdata = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String albumArtPath = null;

            musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            Cursor cursor = musicResolver
                    .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID}, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    albumArtPath = cursor.getString(0);
                }
                cursor.close();
            }

            musicCursor.close();
            return new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId));
        }

        return null;
    }

    public static void updateSongList(Context context) {

        ArrayList<Song> songList = getSongList(context);

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song s1, Song s2) {
                return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
            }
        });

        SharedVariables.fullSongsList.addAll(songList);
    }
    public static void updateAlbumList(Context context) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        ArrayList<Album> albumArrayList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToFirst()){

            do {
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                String thisAlbumArt = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                Long thisID = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Albums._ID));

                albumArrayList.add(new Album(thisTitle, thisArtist, thisAlbumArt, thisID));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }

        Collections.sort(albumArrayList, new Comparator<Album>() {
            public int compare(Album s1, Album s2) {
                if(ExtensionMethods.stringIsEmptyorNull(s1.getTitle()) || ExtensionMethods.stringIsEmptyorNull(s2.getTitle()))
                    return 1;

                return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
            }
        });

        SharedVariables.fullAlbumList.addAll(albumArrayList);
    }
    public static void updateArtistList(Context context) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        ArrayList<Artist> artistArrayList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToFirst()) {

            do {
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                String thisSongCount = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                String thisAlbumCount = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));

                artistArrayList.add(new Artist(thisArtist, thisSongCount, thisAlbumCount, AudioExtensionMethods.getArtistAlbumArt(context, thisArtist)));

            } while (musicCursor.moveToNext());
            musicCursor.close();
        }

        SharedVariables.fullArtistList.addAll(artistArrayList);
    }
    public static void updateLists(Context context){
        updateSongList(context);
        updateAlbumList(context);
        updateArtistList(context);
    }

    public static void setSongAsRingtone(final Context context, final Song currentSongDetails) {

        new MaterialDialog.Builder(context)
                .content("Set song as ringtone : " + currentSongDetails.getTitle())
                .positiveText(R.string.set_text)
                .negativeText(R.string.cancel_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        final ContentResolver resolver = context.getContentResolver();
                        final Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSongDetails.getID());
                        try {
                            final ContentValues values = new ContentValues(2);
                            values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, "1");
                            values.put(MediaStore.Audio.AudioColumns.IS_ALARM, "1");
                            resolver.update(uri, values, null, null);
                        } catch (final UnsupportedOperationException ingored) {
                            return;
                        }

                        final String[] projection = new String[] {
                                BaseColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.TITLE
                        };

                        final String selection = BaseColumns._ID + "=" + currentSongDetails.getID();
                        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
                                selection, null, null);
                        try {
                            if (cursor != null && cursor.getCount() == 1) {
                                cursor.moveToFirst();
                                Settings.System.putString(resolver, Settings.System.RINGTONE, uri.toString());
                                Toast.makeText(context, "Ringtone set to " + currentSongDetails.getTitle(), Toast.LENGTH_SHORT).show();
                            }
                        } finally {
                            if (cursor != null)
                                cursor.close();
                        }
                    }
                }).show();
    }

    public static ArrayList<Song> getSongList(Context context){

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC, null, null);
        ArrayList<Song> songList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToLast()){
            //add songs to list
            do {
                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long thisDuration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String thisdata = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String albumArtPath = null;

                musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor cursor = musicResolver
                        .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID}, null);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        albumArtPath = cursor.getString(0);
                    }
                    cursor.close();
                }
                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
            }
            while (musicCursor.moveToPrevious());
        }
        if (musicCursor != null) {
            musicCursor.close();
        }

        return songList;
    }
    public static ArrayList<Song> getSongList(Context context, Long albumID){

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media.ALBUM_ID + "=\"" + albumID + "\"", null, null);
        ArrayList<Song> songList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //add songs to list
            do {
                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String thisAlbumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                long thisDuration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String thisdata = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String albumArtPath = null;

                musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor cursor = musicResolver
                        .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ thisAlbumID}, null);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        albumArtPath = cursor.getString(0);
                    }
                    cursor.close();
                }

                Song song = new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId));

                songList.add(song);
            }
            while (musicCursor.moveToNext());
        }
        if (musicCursor != null) {
            musicCursor.close();
        }

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song s1, Song s2) {
                return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
            }
        });

        return songList;
    }
    public static ArrayList<Song> getRecentlyAddedSongs(Context context){

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC, null, null);
        ArrayList<Song> songList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToLast()){
            //add songs to list
            do {
                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long thisDuration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String thisdata = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String albumArtPath = null;

                musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor cursor = musicResolver
                        .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID}, null);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        albumArtPath = cursor.getString(0);
                    }
                    cursor.close();
                }

                if(songList.size() > 40)
                    break;

                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
            }
            while (musicCursor.moveToPrevious());
        }
        if (musicCursor != null) {
            musicCursor.close();
        }

        return songList;
    }
    public static ArrayList<Song> getMostPlayedSongsList(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getMostPlayedSongsList(context);
    }

    public static Bitmap getBitMap(Context context, String absolutePath) {
        if(absolutePath == null)
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art);

        return BitmapFactory.decodeFile(absolutePath);
    }
    public static Bitmap getBitMap(String absolutePath) {
        if(absolutePath == null)
            return null;

        return BitmapFactory.decodeFile(absolutePath);
    }

    public static void addToPlaylist(final Context context, final String songHashID){

        ArrayList<String> playlistNames = getCustomPlaylistNames(context);
        new MaterialDialog.Builder(context)
                .title("Choose playlist")
                .items(playlistNames)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        DBHelper dbHelper = new DBHelper(context);
                        if(dbHelper.addSongToPlaylist(String.valueOf(text), songHashID))
                            Toast.makeText(context, R.string.song_added_to_playlist_text, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, R.string.error_adding_song_to_playlist_text, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
    public static void addToPlaylist(final Context context, final ArrayList<Song> songsList){

        final MaterialDialog[] loading = new MaterialDialog[1];

        try{
            ArrayList<String> playlistNames = getCustomPlaylistNames(context);
            new MaterialDialog.Builder(context)
                    .title("Choose playlist")
                    .items(playlistNames)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                            loading[0] = new MaterialDialog.Builder(context)
                                    .title(R.string.adding_songs_to_playlist)
                                    .content(R.string.please_wait)
                                    .cancelable(false)
                                    .progress(true, 0)
                                    .show();

                            DBHelper dbHelper = new DBHelper(context);
                            for (Song song : songsList)
                                dbHelper.addSongToPlaylist(String.valueOf(text), song.getHashID());

                            loading[0].dismiss();
                            Toast.makeText(context, R.string.songs_added_to_playlist_text, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }

        catch (Exception ignored){}
        finally {
            if(loading[0] != null)
                loading[0].dismiss();
        }
    }
    public static boolean removeFromPlaylist(Activity context, String playlist,Song currentSong) {
        DBHelper dbHelper = new DBHelper(context);
        if(dbHelper.removeSongFromPlaylist(playlist, currentSong.getHashID())){
            Toast.makeText(context, R.string.song_removed_from_playlist_text, Toast.LENGTH_SHORT).show();
            return true;
        }

        else{
            Toast.makeText(context, R.string.error_removing_song_from_playlist_text, Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public static void setSongFavorite(Context context, String songHashID, Boolean isFav){

        DBHelper dbhelper = new DBHelper(context);
        dbhelper.setFavorite(songHashID, isFav);
    }
    public static boolean isSongFavorite(Context context, String songHashID){
        DBHelper dbhelper = new DBHelper(context);
        return dbhelper.isFavorite(songHashID);
    }
    public static ArrayList<Song> getFavoriteSongsList(Context context) {

        ArrayList<Song> favSongsList = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context);
        ArrayList<String> favSongsID = dbHelper.getFavoriteSongsList();

        if(favSongsID.size() == 0)
            return  favSongsList;

        ContentResolver musicResolver = context.getContentResolver();

        for (String songHashID : favSongsID) {

            long songID = getSongID(context, songHashID);

            Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media._ID + " = " + songID, null, null);
            if(musicCursor!=null && musicCursor.moveToFirst()){

                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long thisDuration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String thisdata = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String albumArtPath = null;

                musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor cursor = musicResolver
                        .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID}, null);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        albumArtPath = cursor.getString(0);
                    }
                    cursor.close();
                }

                favSongsList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisdata, albumArtPath, AudioExtensionMethods.generateHashID(context, thisId)));
            }
        }

        return favSongsList;
    }

    public static void addSongToHistory(Context context, String songHashID) {

        DBHelper dbHelper = new DBHelper(context);
        dbHelper.addSongToHistory(songHashID);
    }
    public static ArrayList<Song> getSongPlayBackHistory(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        ArrayList<Long> songPlayingHistoryIDList = dbHelper.getSongPlayingHistory();
        ArrayList<Song> songPlaybackHistoryList = new ArrayList<>();

        if(songPlayingHistoryIDList.size() == 0)
            return songPlaybackHistoryList;

        ContentResolver musicResolver = context.getContentResolver();

        for (Long songID : songPlayingHistoryIDList) {

            Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media._ID + " = " + songID, null, null);
            if(musicCursor!=null && musicCursor.moveToFirst()){

                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long thisDuration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String thisdata = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String albumArtPath = null;

                musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor cursor = musicResolver
                        .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID}, null);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        albumArtPath = cursor.getString(0);
                    }
                    cursor.close();
                }
                musicCursor.close();
                songPlaybackHistoryList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
            }
        }

        return songPlaybackHistoryList;
    }

    public static boolean createNewCustomPlaylist(Context context,String playlistName) {
        DBHelper dbhelper = new DBHelper(context);
        return dbhelper.createCustomPlaylist(playlistName);
    }
    public static ArrayList<String> getCustomPlaylistNames(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getCustomPlaylistNames();
    }
    public static int getPlaylistSongCount(Context context, String playlistName) {
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getSongCountInPlaylist(playlistName);
    }
    public static ArrayList<String> getAlbumArtsForPlaylistCover(Context context, String playlistName) {

        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getAlbumArtsForPlaylistCover(context, playlistName);
    }
    public static ArrayList<Song> getSongsListFromCustomPlaylist(Context context, String playlistName) {

        DBHelper dbHelper = new DBHelper(context);
        ArrayList<Song> songsList = new ArrayList<>();

        ArrayList<String> songsHashID =  dbHelper.getSongsListFromCustomPlaylist(playlistName);
        if(songsHashID.size() == 0)
            return songsList;

        ContentResolver musicResolver = context.getContentResolver();

        for (String songHashID : songsHashID) {

            long songID = getSongID(context, songHashID);
            Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media._ID + " = " + songID, null, null);
            if(musicCursor!=null && musicCursor.moveToFirst()){

                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long thisDuration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String thisdata = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                musicCursor.close();
                String albumArtPath = null;

                musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor cursor = musicResolver
                        .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID}, null);

                if (cursor != null) {
                    if (cursor.moveToFirst())
                        albumArtPath = cursor.getString(0);
                    cursor.close();
                }

                songsList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
            }
        }

        return songsList;
    }

    public static String getArtistAlbumArt(Context context, String artistName){

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media.ARTIST + "=\'" + artistName + "\'", null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()) {
            do {
                musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                Cursor cursor = musicResolver
                        .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID}, null);

                if (cursor != null) {
                    if (cursor.moveToFirst()){
                        String albumart = cursor.getString(0);
                        if(albumart != null){
                            musicCursor.close();
                            cursor.close();
                            return albumart;
                        }
                    }


                }
            }while (musicCursor.moveToNext());
            musicCursor.close();
        }

        return null;
    }
    public static Artist getArtist(Context context, String artistName) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Artists.ARTIST + "=\"" + artistName + "\"", null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()) {
            String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
            String thisSongCount = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
            String thisAlbumCount = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));

            musicCursor.close();
            return new Artist(thisArtist, thisSongCount, thisAlbumCount, AudioExtensionMethods.getArtistAlbumArt(context, thisArtist));
        }

        return null;
    }
    public static ArrayList<Song> getSongListFromArtist(Context context, String artistTitle) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media.ARTIST + "=\'" + artistTitle + "\'", null, null);
        ArrayList<Song> songList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToFirst()) {

            do {
                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long thisDuration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String thisdata = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String albumArtPath = null;

                musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor cursor = musicResolver
                        .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{albumID}, null);

                if (cursor != null) {
                    if (cursor.moveToFirst())
                        albumArtPath = cursor.getString(0);
                    cursor.close();
                }

                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song s1, Song s2) {
                return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
            }
        });

        return songList;
    }
    public static ArrayList<Album> getAlbumListFromArtist(Context context, String artistTitle) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.ARTIST + "=\"" + artistTitle + "\"", null, null);
        ArrayList<Album> albumArrayList = new ArrayList<>();
        ArrayList<String> albumList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToFirst()){

            do {
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor albumCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Albums.ALBUM + "=\"" + thisTitle + "\"", null, null);

                if(albumCursor != null && albumCursor.moveToFirst() && !albumList.contains(thisTitle)){


                    String thisArtist = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                    String thisAlbumArt = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    Long thisID = albumCursor.getLong(albumCursor.getColumnIndex(MediaStore.Audio.Albums._ID));

                    albumCursor.close();
                    albumList.add(thisTitle);
                    albumArrayList.add(new Album(thisTitle, thisArtist, thisAlbumArt, thisID));
                }

            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }

        Collections.sort(albumArrayList, new Comparator<Album>() {
            public int compare(Album s1, Album s2) {
                if(ExtensionMethods.stringIsEmptyorNull(s1.getTitle()) || ExtensionMethods.stringIsEmptyorNull(s2.getTitle()))
                    return 1;

                return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
            }
        });
        return albumArrayList;
    }
    public static String getAlbumArtistTitle(Context context, Long albumID) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Albums._ID + "=\"" + albumID + "\"", null, null);

        if(musicCursor != null && musicCursor.moveToFirst()){

            String artistName = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
            musicCursor.close();
            return artistName;
        }

        return null;
    }

    public static ArrayList<Song> getSongListForSearch(String songName){

        ArrayList<Song> result = new ArrayList<>();

        for (Song song : SharedVariables.fullSongsList) {
            if(song.getTitle().toLowerCase().contains(songName.toLowerCase()))
                result.add(song);
            if(result.size() > 15)
                return result;
        }

        return result;
    }
    public static ArrayList<Album> getAlbumListForSearch(String albumName){
        ArrayList<Album> result = new ArrayList<>();

        for (Album album : SharedVariables.fullAlbumList) {
            if(album.getTitle().toLowerCase().contains(albumName.toLowerCase()))
                result.add(album);
            if(result.size() > 15)
                return result;
        }
        return result;
    }
    public static ArrayList<Artist> getArtistListForSearch(String artistName){
        ArrayList<Artist> result = new ArrayList<>();

        for (Artist artitst : SharedVariables.fullArtistList) {
            if(artitst.getArtistTitle().toLowerCase().contains(artistName.toLowerCase()))
                result.add(artitst);
            if(result.size() > 15)
                return result;
        }
        return result;
    }

    public static void RemoveSongFromContentResolver(Context context, long id) {
        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + id + "'", null);
    }
}