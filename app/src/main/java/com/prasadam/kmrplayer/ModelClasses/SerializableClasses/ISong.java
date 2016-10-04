package com.prasadam.kmrplayer.ModelClasses.SerializableClasses;

import com.prasadam.kmrplayer.ModelClasses.Song;

/*
 * Created by Prasadam Saiteja on 10/3/2016.
 */

public class ISong extends Song{

    private static final long serialVersionUID = 123456789L;

    public ISong(Song song) {
        super(song);
    }
    public ISong(String hashID, long id) {
        super(hashID, id);
    }
}
