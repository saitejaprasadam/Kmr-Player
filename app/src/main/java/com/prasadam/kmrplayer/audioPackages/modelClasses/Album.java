package com.prasadam.kmrplayer.audioPackages.modelClasses;

/*
 * Created by Prasadam Saiteja on 3/25/2016.
 */
public class Album {

    private long key;
    private String title, artist, albumID, albumArtLocation, songCount;
    public int colorBoxLayoutColor, albumNameTextViewColor, artistNameTextViewColor;

    public Album(long key, String title, String artist, String albumID, String songCount, String albumArtLocation) {
        this.key = key;
        this.title = title;
        this.artist = artist;
        this.albumID = albumID;
        this.songCount = songCount;
        this.albumArtLocation = albumArtLocation;
    }

    public long getKey(){return key;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbumID(){return albumID;}
    public String getSongsCount(){return songCount;}
    public String getAlbumArtLocation(){return albumArtLocation;}

    public Boolean isColorSet(){

        if(colorBoxLayoutColor != 0 && albumNameTextViewColor != 0 && artistNameTextViewColor != 0)
            return true;

        return  false;
    }

}
