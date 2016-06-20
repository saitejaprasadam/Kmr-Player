package com.prasadam.kmrplayer.audioPackages.modelClasses;

/*
 * Created by Prasadam Saiteja on 6/2/2016.
 */

public class SongIdentification {

    private long songID;
    private String songHashID;

    public SongIdentification(long songID, String songHashID){
        this.songID = songID;
        this.songHashID = songHashID;
    }

    public long getSongID(){ return songID;}
    public String getSongHashID(){ return songHashID;}
}
