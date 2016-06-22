package com.prasadam.kmrplayer.audioPackages.modelClasses;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class Artist {

    private String artistTitle, songCount, albumCount, artistID;
    private long key;
    public String artistAlbumArt = null;
    public int colorBoxLayoutColor, artistNameTextViewColor;

    public Artist(String artistTitle){
        this.artistTitle = artistTitle;
    }

    public Artist(){}

    public Artist(long key, String artistID, String artistTitle, String songCount, String albumCount, String artistAlbumArt) {
        this.key = key;
        this.artistID = artistID;
        this.artistTitle = artistTitle;
        this.songCount = songCount;
        this.albumCount = albumCount;
        this.artistAlbumArt = artistAlbumArt;
    }

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
