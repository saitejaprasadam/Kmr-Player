package com.prasadam.kmrplayer.AudioPackages.modelClasses;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class Artist {

    private String artistTitle, songCount, albumCount;
    public String artistAlbumArt = null;
    public int colorBoxLayoutColor, artistNameTextViewColor;
    private long artistID;

    public Artist(String artistTitle, long artistID, String songCount, String albumCount, String artistAlbumArt) {
        this.artistTitle = artistTitle;
        this.artistID = artistID;
        this.songCount = songCount;
        this.albumCount = albumCount;
        this.artistAlbumArt = artistAlbumArt;
    }

    public long getArtistID(){ return artistID; }
    public String getArtistTitle(){
        return artistTitle;
    }
    public String getSongCount(){return songCount;}
    public String getAlbumCount(){return albumCount;}
    public Boolean isColorSet(){

        if(colorBoxLayoutColor != 0 && artistNameTextViewColor != 0)
            return true;

        return  false;
    }
}
