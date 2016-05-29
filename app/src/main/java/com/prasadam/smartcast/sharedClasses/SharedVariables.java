package com.prasadam.smartcast.sharedClasses;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.prasadam.smartcast.audioPackages.modelClasses.Album;
import com.prasadam.smartcast.audioPackages.modelClasses.Song;

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

    public static ArrayList<Album> fullAlbumList = new ArrayList<>();
}
