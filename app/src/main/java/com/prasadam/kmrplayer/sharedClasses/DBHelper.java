package com.prasadam.kmrplayer.SharedClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 5/20/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smartcast.db";
    private static final String FAVORITES_TABLE_NAME = "favorites";
    private static final String HISTORY_TABLE_NAME = "history";
    private static final String CUSTOM_PLAYLIST_TABLE_NAME = "customplaylist";
    private static final String HASHID_TABLE_NAME = "songHashID";

    private static final String SONG_ID_COLUMN_NAME = "songID";
    private static final String ID_COLUMN_NAME = "hashID";
    private static final String PLAYLIST_NAME_COLUMN_NAME = "playlistName";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + FAVORITES_TABLE_NAME + "(hashID text primary key, isFav int)");
        sqLiteDatabase.execSQL("create table " + HISTORY_TABLE_NAME + "(hashID text)");
        sqLiteDatabase.execSQL("create table " + CUSTOM_PLAYLIST_TABLE_NAME + "(" + PLAYLIST_NAME_COLUMN_NAME + " text primary key)");
        sqLiteDatabase.execSQL("create table " + HASHID_TABLE_NAME + "(" + SONG_ID_COLUMN_NAME + " int, " + ID_COLUMN_NAME + " text primary key)");
    }
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CUSTOM_PLAYLIST_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public String getSongHashID(Context context, long songID){

        SQLiteDatabase rdb = this.getReadableDatabase();
        Cursor cursor = rdb.rawQuery("select " + ID_COLUMN_NAME + " from " + HASHID_TABLE_NAME + " where " + SONG_ID_COLUMN_NAME + " = " + songID, null);

        if(cursor != null && cursor.moveToFirst()){
            String hashID = cursor.getString(cursor.getColumnIndex(ID_COLUMN_NAME));
            cursor.close();
            rdb.close();
            return hashID;
        }

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media._ID + " = " + songID, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()) {
            do {
                String thisData = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                try{

                    File file = new File(thisData);
                    byte[] bFile;

                    if(file.length() < 50000)
                        bFile = new byte[(int) file.length()];

                    else
                        bFile = new byte[50000];

                    RandomAccessFile fileInputStream = new RandomAccessFile(file, "r");
                    fileInputStream.seek(fileInputStream.length() - bFile.length);
                    fileInputStream.read(bFile, 0, bFile.length);
                    fileInputStream.close();

                    MessageDigest messageDigest  = MessageDigest.getInstance("SHA-1");
                    byte[] digest = messageDigest.digest(bFile);
                    String songHashID = new BigInteger(1, digest).toString(32);

                    Log.d(thisData, songHashID);
                    SQLiteDatabase wdb = this.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(ID_COLUMN_NAME, songHashID);
                    cv.put(SONG_ID_COLUMN_NAME, songID);

                    Cursor tempCursor = rdb.rawQuery("select " + SONG_ID_COLUMN_NAME + " from " + HASHID_TABLE_NAME + " where " + ID_COLUMN_NAME + " = '" + songHashID + "'", null);
                    if(tempCursor != null && tempCursor.moveToFirst())
                        wdb.update(HASHID_TABLE_NAME, cv, ID_COLUMN_NAME + " = '" + songHashID + "'", null);

                    else
                        wdb.insert(HASHID_TABLE_NAME, null, cv);

                    rdb.close();
                    wdb.close();

                    return songHashID;
                }

                catch (Exception ignored){}

            } while (musicCursor.moveToNext());
            musicCursor.close();
        }

        rdb.close();
        return null;
    }

    public long getSongID(String songHashID) {

        SQLiteDatabase rdb = this.getReadableDatabase();
        Cursor cursor = rdb.rawQuery("select " + SONG_ID_COLUMN_NAME + " from " + HASHID_TABLE_NAME + " where " + ID_COLUMN_NAME + " = '" + songHashID + "'", null);

        if(cursor != null && cursor.moveToFirst()){
            long songID = cursor.getLong(cursor.getColumnIndex(SONG_ID_COLUMN_NAME));
            cursor.close();
            rdb.close();
            return songID;
        }


        return 0;
    }

    public void setFavorite(String songHashID, boolean isFav){

        SQLiteDatabase rdb = this.getReadableDatabase();
        SQLiteDatabase wdb = this.getWritableDatabase();

        Cursor cur =  rdb.rawQuery( "select * from " + FAVORITES_TABLE_NAME +" where " + ID_COLUMN_NAME + " = '" + songHashID + "'", null );
        ContentValues contentValues = new ContentValues();
        if(isFav)
            contentValues.put("isFav", 1);
        else
            contentValues.put("isFav", 0);

        if(cur.getCount() > 0)
        {
            wdb.update(FAVORITES_TABLE_NAME, contentValues, ID_COLUMN_NAME + " = '" + songHashID + "'", null);
            cur.close();
            rdb.close();
            wdb.close();
            return;
        }

        contentValues.put(ID_COLUMN_NAME, songHashID);
        wdb.insert(FAVORITES_TABLE_NAME, null, contentValues);

        cur.close();
        rdb.close();
        wdb.close();
        return;
    }

    public boolean isFavorite(String songHashID){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur =  db.rawQuery( "select * from " + FAVORITES_TABLE_NAME +" where " + ID_COLUMN_NAME + " = '" + songHashID + "'", null );

        if(cur.moveToFirst()){
            if(cur.getInt(cur.getColumnIndex("isFav")) == 1)
            {
                cur.close();
                db.close();
                return true;
            }
        }

        cur.close();
        db.close();
        return false;
    }

    public ArrayList<String> getFavoriteSongsList(){

        ArrayList<String> songsHashID = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select " + FAVORITES_TABLE_NAME + ".hashID , (select count(" + HISTORY_TABLE_NAME + ".hashID) from " + HISTORY_TABLE_NAME + " where " + HISTORY_TABLE_NAME + ".hashID = " + FAVORITES_TABLE_NAME + ".hashID) as RepeatCount from " + FAVORITES_TABLE_NAME + " where isFav = 1 order by RepeatCount desc", null);

        if(cur != null && cur.moveToFirst()){
            do {
                songsHashID.add(cur.getString(cur.getColumnIndex(ID_COLUMN_NAME)));
            }while(cur.moveToNext());
            cur.close();
        }

        db.close();
        return songsHashID;
    }

    public void addSongToHistory(String songHashID){

        SQLiteDatabase wdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COLUMN_NAME, songHashID);

        wdb.insert(HISTORY_TABLE_NAME, null, contentValues);
        wdb.close();
    }

    public ArrayList<Long> getSongPlayingHistory() {

        SQLiteDatabase rdb = this.getReadableDatabase();
        Cursor cursor =  rdb.rawQuery( "select hashID from " + HISTORY_TABLE_NAME, null );
        ArrayList<Long> songsID = new ArrayList<>();

        if(cursor != null && cursor.moveToLast()){
            do {
                String songHashID = cursor.getString(cursor.getColumnIndex(ID_COLUMN_NAME));
                long songID = getSongID(songHashID);

                if(!songsID.contains(songID))
                    songsID.add(songID);

                if(songsID.size() > 40){
                    cursor.close();
                    rdb.close();
                    return songsID;
                }

            }while(cursor.moveToPrevious());

            cursor.close();
            rdb.close();
            return songsID;
        }

        return songsID;
    }

    public ArrayList<Song> getMostPlayedSongsList(Context context) {

        SQLiteDatabase rdb = this.getReadableDatabase();
        ArrayList<Song> mostPlayedSongs = new ArrayList<>();
        Cursor cursor =  rdb.rawQuery( "select count(*)," + ID_COLUMN_NAME +" from " + HISTORY_TABLE_NAME + " group by " + ID_COLUMN_NAME + " having count(*) > 1 order by count(*) desc", null );

        if(cursor != null && cursor.moveToFirst()){
            ContentResolver musicResolver = context.getContentResolver();
            do{
                String songHashID = cursor.getString(cursor.getColumnIndex(ID_COLUMN_NAME));
                long songID = getSongID(songHashID);

                Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media._ID + " = " + songID , null, null);
                if(musicCursor!=null && musicCursor.moveToFirst()){

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
                    Cursor albumArtCursor = musicResolver
                            .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID }, null);

                    if (albumArtCursor != null) {
                        if (albumArtCursor.moveToFirst())
                            albumArtPath = albumArtCursor.getString(0);
                        albumArtCursor.close();
                    }

                    Song song = new Song(songID, thisTitle, thisArtist, thisArtistID, thisAlbum, thisAlbumID, thisDuration, thisdata, albumArtPath, songHashID);
                    song.repeatCount = cursor.getInt(0);
                    mostPlayedSongs.add(song);

                    if(mostPlayedSongs.size() > 40){
                        musicCursor.close();
                        cursor.close();
                        rdb.close();
                        return mostPlayedSongs;
                    }
                }
            }while(cursor.moveToNext());
            cursor.close();
        }

        rdb.close();
        return mostPlayedSongs;
    }

    public boolean createCustomPlaylist(String playlistName) {

        playlistName = playlistName.trim();
        SQLiteDatabase rdb = this.getReadableDatabase();
        Cursor curos = rdb.rawQuery("select * from sqlite_master where type='table' and name=?", new String[]{playlistName});

        if(curos.getCount() == 0){
            SQLiteDatabase wdb = this.getWritableDatabase();
            wdb.execSQL("create table '" + playlistName +"' (" + ID_COLUMN_NAME + " text primary key)");

            ContentValues contentValues = new ContentValues();
            contentValues.put(PLAYLIST_NAME_COLUMN_NAME, playlistName);

            wdb.insert(CUSTOM_PLAYLIST_TABLE_NAME, null, contentValues);
            curos.close();
            wdb.close();
            rdb.close();
            return true;
        }

        curos.close();
        rdb.close();
        return false;
    }

    public ArrayList<String> getCustomPlaylistNames() {

        ArrayList<String> customPlaylistNames = new ArrayList<>();
        SQLiteDatabase rdb = this.getReadableDatabase();
        Cursor cursor = rdb.rawQuery("select " + PLAYLIST_NAME_COLUMN_NAME + " from " + CUSTOM_PLAYLIST_TABLE_NAME, null);

        if(cursor != null && cursor.moveToFirst()){
            do{
                customPlaylistNames.add(cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME_COLUMN_NAME)));
            }while(cursor.moveToNext());
            cursor.close();
        }

        rdb.close();
        return customPlaylistNames;
    }

    public int getSongCountInPlaylist(String playlistName) {

        SQLiteDatabase rdb = this.getReadableDatabase();

        try{
            Cursor cursor = rdb.rawQuery("select count(*) from '" + playlistName + "'", null);
            if(cursor != null && cursor.moveToFirst()){
                int count = cursor.getInt(0);
                cursor.close();
                rdb.close();
                return count;
            }
        }

        catch (Exception e){
            return 0;
        }

        rdb.close();
        return 0;
    }

    public boolean addSongToPlaylist(String playlistName, String songHashID) {

        try{
            SQLiteDatabase wdb = this.getWritableDatabase();
            wdb.execSQL("insert into '" + playlistName + "' values('" + songHashID + "')");
            wdb.close();
            return true;
        }

        catch (Exception e){
            return false;
        }
    }

    public ArrayList<String> getAlbumArtsForPlaylistCover(Context context, String playlistName) {

        SQLiteDatabase rdb = this.getReadableDatabase();
        ArrayList<String> albumartPath = new ArrayList<>();
        ArrayList<String> albumIDs = new ArrayList<>();
        Cursor cursor = rdb.rawQuery("select '" + playlistName + "'.hashID , (select count(" + HISTORY_TABLE_NAME + ".hashID) from " + HISTORY_TABLE_NAME + " where " + HISTORY_TABLE_NAME + ".hashID = '" + playlistName + "'.hashID) as RepeatCount from '" + playlistName + "' order by RepeatCount desc", null);

        if(cursor != null && cursor.moveToFirst()){
            do{
                String songHashID = cursor.getString(cursor.getColumnIndex(ID_COLUMN_NAME));
                long songID = getSongID(songHashID);

                ContentResolver musicResolver = context.getContentResolver();
                Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media._ID + " = " + songID, null, null);

                if(musicCursor != null && musicCursor.moveToFirst()){

                    String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    if(albumIDs.contains(albumID))
                        continue;

                    Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                    Cursor albumCursor = musicResolver
                            .query(albumUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID }, null);

                    if (albumCursor != null) {
                        if (albumCursor.moveToFirst()) {
                            albumartPath.add(albumCursor.getString(0));
                            albumIDs.add(albumID);
                            if(albumartPath.size() == 4){
                                musicCursor.close();
                                albumCursor.close();
                                cursor.close();
                                rdb.close();
                                return albumartPath;
                            }

                        }
                        albumCursor.close();
                    }
                }
            }while (cursor.moveToNext());
            cursor.close();
        }

        rdb.close();
        return albumartPath;
    } //need to change

    public boolean renamePlaylist(String oldName, String newName) {

        try{
            SQLiteDatabase wdb = this.getWritableDatabase();
            SQLiteDatabase rdb = this.getReadableDatabase();
            Cursor cursor = rdb.rawQuery("select * from sqlite_master where type='table' and name=?", new String[]{newName});

            if(cursor.getCount() > 0){
                rdb.close();
                wdb.close();
                cursor.close();
                return false;
            }


            wdb.execSQL("alter table \'" + oldName + "\' rename to '" + newName + "'");
            wdb.execSQL("update " + CUSTOM_PLAYLIST_TABLE_NAME + " set " + PLAYLIST_NAME_COLUMN_NAME + " = \'" + newName + "\' where " + PLAYLIST_NAME_COLUMN_NAME + " = \'" + oldName + "\'");
            rdb.close();
            wdb.close();
            cursor.close();
            return true;
        }

        catch (Exception e){
            return false;
        }
    }

    public ArrayList<String> getSongsListFromCustomPlaylist(String playlistName) {

        ArrayList<String> songsHashID = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select \'" + playlistName + "\'.hashID , (select count(" + HISTORY_TABLE_NAME + ".hashID) from " + HISTORY_TABLE_NAME + " where " + HISTORY_TABLE_NAME + ".hashID = '" + playlistName + "'.hashID) as RepeatCount from '" + playlistName + "' order by RepeatCount desc", null);

        if(cursor != null && cursor.moveToFirst()){
            do {
                songsHashID.add(cursor.getString(cursor.getColumnIndex(ID_COLUMN_NAME)));
            }while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return songsHashID;
    }

    public long getLastPlayedSong() {
        SQLiteDatabase rdb = this.getReadableDatabase();
        Cursor cursor =  rdb.rawQuery( "select hashID from " + HISTORY_TABLE_NAME, null );

        if(cursor != null && cursor.moveToLast()) {
            String songHashID = cursor.getString(cursor.getColumnIndex(ID_COLUMN_NAME));
            long songID = getSongID(songHashID);
            cursor.close();
            rdb.close();
            return songID;
        }

        rdb.close();
        return 0;

    }
}