package com.prasadam.kmrplayer.audioPackages.modelClasses;

/*
 * Created by Prasadam Saiteja on 3/25/2016.
 */
public class Album {

    private String title, artist, albumArtLocation;
    public int colorBoxLayoutColor, albumNameTextViewColor, artistNameTextViewColor;

    public Album(String title, String artist, String albumArtLocation) {
        this.title = title;
        this.artist = artist;
        this.albumArtLocation = albumArtLocation;
    }

    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbumArtLocation(){return albumArtLocation;}

    public Boolean isColorSet(){

        if(colorBoxLayoutColor != 0 && albumNameTextViewColor != 0 && artistNameTextViewColor != 0)
            return true;

        return  false;
    }
}
