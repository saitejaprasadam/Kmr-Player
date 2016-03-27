package com.prasadam.smartcast.audioPackages;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prasadam Saiteja on 2/28/2016.
 */
public class AdditionalAudioPackageMethods {

    public static int getSongIndex(List<Song> songsList, String songPath) {
        for (Song song: songsList)
            if(song.getData().equals(songPath))
                return songsList.indexOf(song);

        return -1;
    }

    public static ArrayList<Song> getSongList(Context context, ArrayList<Song> songList) {

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

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
        musicCursor.close();
        return songList;
    }

    public static ArrayList<Album> getAlbumList(Context context) {

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
        musicCursor.close();
        return albumArrayList;
    }
}
