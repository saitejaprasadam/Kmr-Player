package com.prasadam.kmrplayer.SharedClasses;

import android.content.Context;
import android.media.audiofx.Equalizer;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Album;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Artist;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;

import java.util.ArrayList;

/*
 * Created by Prasadam saiteja on 3/14/2016.
 */

public class SharedVariables {

    public static void Initializers(Context context) {
        Fresco.initialize(context);
        //LastFm.initializeLastFm();
    }

    public static Equalizer equalizer;
    public static Context globalActivityContext = null;
    public static volatile ArrayList<Song> fullSongsList = new ArrayList<>();
    public static volatile ArrayList<Artist> fullArtistList = new ArrayList<>();
    public static volatile ArrayList<Album> fullAlbumList = new ArrayList<>();
}