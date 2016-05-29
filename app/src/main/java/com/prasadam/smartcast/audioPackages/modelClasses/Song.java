package com.prasadam.smartcast.audioPackages.modelClasses;

/*
 * Created by use on 2/14/2016.
 */

import android.content.Context;

import static com.prasadam.smartcast.audioPackages.AudioExtensionMethods.isSongFavorite;
import static com.prasadam.smartcast.audioPackages.AudioExtensionMethods.setSongFavorite;

public class Song
{
    private long id;
    private String title, artist, album, duration, data, albumArtLocation, artistID, albumID;
    private boolean liked;
    public int repeatCount;

    public Song(long songID, String songTitle, String songArtist, String artistID, String songAlbum, String albumID, String songDuration, String songData, String albumArtLocation) {
        this.id=songID;
        this.title=songTitle;
        this.artist=songArtist;
        this.album = songAlbum;
        this.duration = songDuration;
        this.data = songData;
        this.albumArtLocation = albumArtLocation;
        this.artistID = artistID;
        this.albumID = albumID;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum(){return album;}
    public String getDuration(){return duration;}
    public String getData(){return data;}
    public String getAlbumArtLocation(){return albumArtLocation;}
    public String getArtistID(){return artistID;}
    public String getAlbumID(){return albumID;}
    public boolean getIsLiked(Context context) { return isSongFavorite(context, id); }
    public void setIsLiked(Context context, boolean value) { setSongFavorite(context, id, value); }
}