package com.prasadam.kmrplayer.sharedClasses;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Album;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Artist;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.audioPackages.modelClasses.SongIdentification;

import java.util.ArrayList;

/*
 * Created by Prasadam saiteja on 3/14/2016.
 */

public class SharedVariables {

    public static void Initializers(Context context)
    {
        Fresco.initialize(context);
    }

    public static ArrayList<Song> fullSongsList = new ArrayList<>();
    public static ArrayList<Artist> fullArtistList = new ArrayList<>();
    public static ArrayList<Album> fullAlbumList = new ArrayList<>();
    public static ArrayList<SongIdentification> songIdentifications = new ArrayList<>();
}
