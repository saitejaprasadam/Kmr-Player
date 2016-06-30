package com.prasadam.kmrplayer.audioPackages;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.TagEditorActivity;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Album;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Artist;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.sharedClasses.DBHelper;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static void updateSongList(Context context) {

        ArrayList<Song> songList = getSongList(context);

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song s1, Song s2) {
                return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
            }
        });

        SharedVariables.fullSongsList =  songList;
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

                albumArrayList.add(new Album(thisTitle, thisArtist, thisAlbumArt));
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

        SharedVariables.fullAlbumList = albumArrayList;
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

        SharedVariables.fullArtistList = artistArrayList;
    }

    public static void updateLists(Context context){
        updateSongList(context);
        updateAlbumList(context);
        updateArtistList(context);
    }

    public static void songDetails(Context context, Song currentSongDetails, String albumPath) {

        StringBuilder content = new StringBuilder();
        File songFile = new File(currentSongDetails.getData());
        content.append(context.getString(R.string.song_location_text) + " : " + currentSongDetails.getData() + "\n\n");
        try
        {
            content.append(context.getString(R.string.file_size_text) + " : " + ExtensionMethods.readableFileSize(songFile.length()) + "\n\n");
            content.append(context.getString(R.string.duration_text) + " : " + ExtensionMethods.formatIntoHHMMSS((int)currentSongDetails.getDuration()/ 1000) + "\n\n");
        }
        catch (Exception ignored){}


        MediaExtractor mex = new MediaExtractor();
        try {

            mex.setDataSource(currentSongDetails.getData());// the adresss location of the sound on sdcard.
            MediaFormat mf = mex.getTrackFormat(0);
            int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
            if(bitRate/1000 > 0)
                content.append(context.getString(R.string.bit_rate_text) + " : " + bitRate/1000 + " kb/s" + "\n\n");
            int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            content.append(context.getString(R.string.sampling_rate_text) + " : " + sampleRate + " Hz");

        } catch (IOException ignored) {}

        if(albumPath != null)
            new MaterialDialog.Builder(context)
                    .icon(Drawable.createFromPath(albumPath))
                    .title(currentSongDetails.getTitle())
                    .positiveText(context.getString(R.string.ok_text))
                    .content(content.toString())
                    .show();

        else
            new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.song_details_text))
                    .positiveText(context.getString(R.string.ok_text))
                    .content(content.toString())
                    .show();
    }

    public static void deleteSong(final Context context, final String songName, final String songLocation,final long songID) {

        new MaterialDialog.Builder(context)
                .content("Delete this song " + songName)
                .positiveText(R.string.delete_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        File file = new File(songLocation);
                        if(file.delete())
                            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + songID + "'", null);
                    }
                })
                .negativeText(R.string.cancel_text)
                .show();
    } //Prototype won't wait for material dialog result

    public static void sendSong(Context context ,String songName,Uri songLocation) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_STREAM, songLocation);

        songName = songName.trim();
        if(songName.length() > 20)
            songName = songName.substring(0, 18) + "...";

        context.startActivity(Intent.createChooser(share, "Share " + "\'" + songName + "\'"  + " using"));
    }

    public static void setSongAsRingtone(final Context context, final Song currentSongDetails) {

        new MaterialDialog.Builder(context)
                .content("Set song as ringtone : " + currentSongDetails.getTitle())
                .positiveText(R.string.set_text)
                .negativeText(R.string.cancel_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        byte[] buffer = null;
                        File songFile = new File(currentSongDetails.getData());
                        InputStream fIn = null;
                        try {
                            fIn = new FileInputStream(songFile);
                        } catch (FileNotFoundException e) {
                            Toast.makeText(context, "Error setting ringtone", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int size = 0;

                        try {
                            size = fIn.available();
                            buffer = new byte[size];
                            fIn.read(buffer);
                            fIn.close();
                        } catch (IOException e) {
                            Toast.makeText(context, "Error setting ringtone", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/media/";
                        String filename = "ringtone.mp3";

                        boolean exists = (new File(path)).exists();
                        if (!exists) {
                            new File(path).mkdirs();
                        }

                        FileOutputStream save;
                        try {
                            save = new FileOutputStream(path + filename);
                            save.write(buffer);
                            save.flush();
                            save.close();
                        } catch (FileNotFoundException e) {
                            Toast.makeText(context, "Error setting ringtone", Toast.LENGTH_SHORT).show();
                            return;
                        } catch (IOException e) {
                            Toast.makeText(context, "Error setting ringtone", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path + filename)));

                        File k = new File(path, filename);

                        ContentValues values = new ContentValues();
                        values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
                        values.put(MediaStore.MediaColumns.TITLE, currentSongDetails.getTitle() + " ");
                        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
                        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);

                        Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
                        Uri newUri = context.getContentResolver().insert(uri, values);
                        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
                        context.getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath()), values);
                        Toast.makeText(context, "Ringtone set to " + currentSongDetails.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }).show();


    }

    public static void ShoutOut(Context context, Song currentSongDetails, String albumPath) {

        if(albumPath != null)
            new MaterialDialog.Builder(context)
                    .icon(Drawable.createFromPath(albumPath))
                    .maxIconSize(120)
                    .title(currentSongDetails.getTitle())
                    .customView(R.layout.activity_shout_out_layout, true)
                    .positiveText(R.string.post_text)
                    .negativeText(R.string.cancel_text)
                    .show();

    }

    public static ArrayList<Song> getSongList(Context context, String albumName){

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Albums.ALBUM + "=\"" + albumName + "\"", null, null);
        ArrayList<Song> songList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //add songs to list
            do {
                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtistID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String thisAlbumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
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

                Song song = new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId));

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
                String thisArtistID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String thisAlbumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
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

                songList.add(new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
            }
            while (musicCursor.moveToPrevious());
        }
        if (musicCursor != null) {
            musicCursor.close();
        }

        return songList;
    }

    public static Bitmap getBitMap(Context context, String absolutePath) {
        if(absolutePath == null)
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art);

        return BitmapFactory.decodeFile(absolutePath);
    }

    public static void shareAlbum(Context context, ArrayList<Song> songsList, String albumTitle){

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND_MULTIPLE);
        share.setType("audio/*");

        ArrayList<Uri> files = new ArrayList<>();

        for(Song song : songsList) {
            files.add(Uri.parse(song.getData()));
        }

        albumTitle = albumTitle.trim();
        if(albumTitle.length() > 20)
            albumTitle =  albumTitle.substring(0, 18) + "...";

        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        context.startActivity(Intent.createChooser(share, "Share " + "\'" + albumTitle  + "\'" +  " album using"));

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
                String thisArtistID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String thisAlbumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
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
                songList.add(new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
            }
            while (musicCursor.moveToPrevious());
        }
        if (musicCursor != null) {
            musicCursor.close();
        }

        return songList;
    }

    public static void launchTagEditor(Context context, long songID){
        Intent tagEditorIntent = new Intent(context, TagEditorActivity.class);
        tagEditorIntent.putExtra("songID", String.valueOf(songID));
        context.startActivity(tagEditorIntent);
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
                String thisArtistID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String thisAlbumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
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

                favSongsList.add(new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath, AudioExtensionMethods.generateHashID(context, thisId)));
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
                String thisArtistID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String thisAlbumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
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
                songPlaybackHistoryList.add(new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
            }
        }

        return songPlaybackHistoryList;
    }

    public static ArrayList<Song> getMostPlayedSongsList(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getMostPlayedSongsList(context);
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

    public static void renamePlaylist(final Context context, final String oldName) {

        new MaterialDialog.Builder(context)
                .title(R.string.enter_a_new_name_text)
                .inputRangeRes(3, 20, R.color.colorAccentGeneric)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                        String newName = String.valueOf(input);
                        if(newName.equals(oldName))
                            Toast.makeText(context , "Provide a different name!!!", Toast.LENGTH_SHORT).show();

                        else{
                            DBHelper dbHelper = new DBHelper(context);
                            if(dbHelper.renamePlaylist(oldName, newName)){
                                Toast.makeText(context, "Name changed successfully", Toast.LENGTH_SHORT).show();

                            }

                            else
                                Toast.makeText(context, "Playlist with same name already exists", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).show();
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
                String thisArtistID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String thisAlbumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
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

                songsList.add(new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
            }
        }

        return songsList;
    }

    public static long getSongID(Context context, String songHashID) {

        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getSongID(songHashID);
    }

    public static String getArtistAlbumArt(Context context, String artistName){

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media.ARTIST + "=\"" + artistName + "\"", null, null);

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

            do {
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                String thisSongCount = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                String thisAlbumCount = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));

                musicCursor.close();
                return new Artist(thisArtist, thisSongCount, thisAlbumCount, AudioExtensionMethods.getArtistAlbumArt(context, thisArtist));

            } while (musicCursor.moveToNext());
        }

        musicCursor.close();
        return null;
    }

    public static ArrayList<Song> getSongListFromArtist(Context context, String artistTitle) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media.ARTIST + "=\"" + artistTitle + "\"", null, null);
        ArrayList<Song> songList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToFirst()) {

            do {
                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtistID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String thisAlbumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
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

                songList.add(new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId)));
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

                    albumCursor.close();
                    albumList.add(thisTitle);
                    albumArrayList.add(new Album(thisTitle, thisArtist, thisAlbumArt));
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

    public static void shareAllSongsFromCurrentArtist(Context context, ArrayList<Song> songsList, String artistTitle) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND_MULTIPLE);
        share.setType("audio/*");

        ArrayList<Uri> files = new ArrayList<>();

        for(Song song : songsList) {
            files.add(Uri.parse(song.getData()));
        }

        artistTitle = artistTitle.trim();
        if(artistTitle.length() > 20)
            artistTitle =  artistTitle.substring(0, 18) + "...";

        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        context.startActivity(Intent.createChooser(share, "Share all songs of " + "\'" + artistTitle  + "\'" +  " artist using"));
    }

    public static String getAlbumArtistTitle(Context context, String albumTitle) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Albums.ALBUM + "=\"" + albumTitle + "\"", null, null);

        if(musicCursor != null && musicCursor.moveToFirst()){

            String artistName = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
            musicCursor.close();
            return artistName;
        }

        return null;
    }

    public static Song getLastPlayedSong(Context context){
        long songID = new DBHelper(context).getLastPlayedSong();
        if(songID == 0)
            return null;
        return getSongFromID(context, songID);
    }

    public static Song getSongFromID(Context context, long songID){

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media._ID + "=\"" + songID + "\"", null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
                long thisId = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String thisArtistID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String thisAlbumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
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
                return new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath, generateHashID(context, thisId));
            }

        return null;
    }

}