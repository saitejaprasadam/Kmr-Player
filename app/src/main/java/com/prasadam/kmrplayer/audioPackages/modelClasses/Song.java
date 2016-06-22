package com.prasadam.kmrplayer.audioPackages.modelClasses;

/*
 * Created by use on 2/14/2016.
 */

import android.content.Context;

import static com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods.isSongFavorite;
import static com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods.setSongFavorite;

public class Song
{
    private long id, duration;
    private String title, artist, album, data, albumArtLocation, artistID, albumID, hashID;
    private boolean liked;
    public int repeatCount;

    public Song(long songID, String songTitle, String songArtist, String artistID, String songAlbum, String albumID, long songDuration, String songData, String albumArtLocation, String hashID) {

        this.id=songID;
        this.title=songTitle;
        this.artist=songArtist;
        this.album = songAlbum;
        this.duration = songDuration;
        this.data = songData;
        this.albumArtLocation = albumArtLocation;
        this.artistID = artistID;
        this.albumID = albumID;
        this.hashID = hashID;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum(){return album;}
    public long getDuration(){return duration;}
    public String getData(){return data;}
    public String getAlbumArtLocation(){return albumArtLocation;}
    public String getArtistID(){return artistID;}
    public String getAlbumID(){return albumID;}
    public boolean getIsLiked(Context context) { return isSongFavorite(context, hashID); }
    public void setIsLiked(Context context, boolean value) { setSongFavorite(context, hashID, value); }
    public String getHashID(){return hashID;};
}