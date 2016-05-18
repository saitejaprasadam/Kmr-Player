package com.prasadam.smartcast.commonClasses;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.prasadam.smartcast.audioPackages.Album;
import com.prasadam.smartcast.audioPackages.Song;

import java.util.ArrayList;

/*
 * Created by Prasadam saiteja on 3/14/2016.
 */

public class CommonVariables {

    public static void Initializers(Context context)
    {
        Fresco.initialize(context);
    }

    public static ArrayList<Song> fullSongsList = new ArrayList<>();

    public static ArrayList<Album> fullAlbumList = new ArrayList<>();
}
