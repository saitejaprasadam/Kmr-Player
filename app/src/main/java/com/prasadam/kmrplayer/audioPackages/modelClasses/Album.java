package com.prasadam.kmrplayer.AudioPackages.modelClasses;

/*
 * Created by Prasadam Saiteja on 3/25/2016.
 */
public class Album {

    private String title, artist, albumArtLocation;
    public int colorBoxLayoutColor, albumNameTextViewColor, artistNameTextViewColor;
    private Long ID;

    public Album(String title, String artist, String albumArtLocation, Long ID) {
        this.title = title;
        this.artist = artist;
        this.albumArtLocation = albumArtLocation;
        this.ID = ID;
    }

    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public Long getID(){
        return ID;
    }
    public String getAlbumArtLocation(){return albumArtLocation;}

    public Boolean isColorSet(){
        return colorBoxLayoutColor != 0 && albumNameTextViewColor != 0 && artistNameTextViewColor != 0;
    }
}
