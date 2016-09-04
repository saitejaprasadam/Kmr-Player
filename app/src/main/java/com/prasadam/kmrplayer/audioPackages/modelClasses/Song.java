package com.prasadam.kmrplayer.AudioPackages.modelClasses;

import android.content.Context;

import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;

import static com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods.isSongFavorite;
import static com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods.setSongFavorite;

/*
 * Created by saiteja prasadam on 2/14/2016.
 */

public class Song{

    private long id, duration;
    private String title, artist, album, data, albumArtLocation, hashID;
    public int repeatCount;

    public Song(long songID, String songTitle, String songArtist, String songAlbum, long songDuration, String songData, String albumArtLocation, String hashID) {

        this.id = songID;
        this.title = songTitle;
        this.artist = songArtist;
        this.album = songAlbum;
        this.duration = songDuration;
        this.data = songData;
        this.albumArtLocation = albumArtLocation;
        this.hashID = hashID;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum(){return album;}
    public long getDuration(){return duration;}
    public String getData(){return data;}
    public String getAlbumArtLocation(){return albumArtLocation;}
    public boolean getIsLiked(Context context) { return isSongFavorite(context, hashID); }
    public void setIsLiked(Context context, boolean value) {
        setSongFavorite(context, hashID, value);
    }
    public String getHashID(){return hashID;}
}