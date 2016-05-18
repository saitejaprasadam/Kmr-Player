package com.prasadam.smartcast.audioPackages;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.smartcast.R;
import com.prasadam.smartcast.commonClasses.CommonVariables;
import com.prasadam.smartcast.commonClasses.ExtensionMethods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Created by Prasadam Saiteja on 2/28/2016.
 */
public class AudioExtensionMethods {

    public static int getSongIndex(List<Song> songsList, String songPath) {
        for (Song song: songsList)
            if(song.getData().equals(songPath))
                return songsList.indexOf(song);

        return -1;
    }

    public static void updateSongList(Context context) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC, null, null);
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
                String thisDuration = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
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

                songList.add(new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath));
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

        CommonVariables.fullSongsList =  songList;
    }

    public static void updateAlbumList(Context context) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        ArrayList<Album> albumArrayList = new ArrayList<>();

        if(musicCursor!=null && musicCursor.moveToFirst()){

            do {
                long key = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_KEY));
                String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                String thisSongCount = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
                String thisAlbumArt = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                albumArrayList.add(new Album(key, thisTitle, thisArtist, "temp", thisSongCount, thisAlbumArt));
            }
            while (musicCursor.moveToNext());
        }
        if (musicCursor != null) {
            musicCursor.close();
        }

        Collections.sort(albumArrayList, new Comparator<Album>() {
            public int compare(Album s1, Album s2) {
                if(ExtensionMethods.stringIsEmptyorNull(s1.getTitle()) || ExtensionMethods.stringIsEmptyorNull(s2.getTitle()))
                    return 1;

                return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
            }
        });

        CommonVariables.fullAlbumList = albumArrayList;
    }

    public static void songDetails(Context context, Song currentSongDetails, String albumPath) {

        StringBuilder content = new StringBuilder();
        File songFile = new File(currentSongDetails.getData());
        content.append(context.getString(R.string.song_location_text) + " : " + currentSongDetails.getData() + "\n\n");
        try
        {
            content.append(context.getString(R.string.file_size_text) + " : " + ExtensionMethods.readableFileSize(songFile.length()) + "\n\n");
            content.append(context.getString(R.string.duration_text) + " : " + ExtensionMethods.formatIntoHHMMSS(Integer.parseInt(currentSongDetails.getDuration())/ 1000) + "\n\n");
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
        songName = "\"" + songName + "\"";
        context.startActivity(Intent.createChooser(share, "Share " + songName  + " using"));
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
                        values.put(MediaStore.MediaColumns.TITLE, filename);
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

    public static ArrayList<Song> getSongList(Context context, String albumName) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Albums.ALBUM + "='" + albumName + "'", null, null);
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
                String thisDuration = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
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

                songList.add(new Song(thisId, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath));
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

    public static void shareAlbum(Context context, ArrayList<Song> songsList, String albumTitle){

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND_MULTIPLE);
        share.putExtra(Intent.EXTRA_SUBJECT, "Share Album");
        share.setType("audio/*");

        ArrayList<Uri> files = new ArrayList<>();

        for(Song song : songsList) {
            files.add(Uri.parse(song.getData()));
        }

        albumTitle = albumTitle.trim();
        if(albumTitle.length() > 20)
            albumTitle = "\"" + albumTitle.substring(0, 18) + "...\"";

        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        context.startActivity(Intent.createChooser(share, "Share " + albumTitle  + "album using"));

    }

    public static void updateLists(Context context){
        updateSongList(context);
        updateAlbumList(context);
    }
}
