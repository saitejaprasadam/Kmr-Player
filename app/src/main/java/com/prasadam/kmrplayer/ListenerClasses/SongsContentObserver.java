package com.prasadam.kmrplayer.ListenerClasses;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.Fragments.AlbumsFragment;
import com.prasadam.kmrplayer.Fragments.ArtistFragment;
import com.prasadam.kmrplayer.Fragments.SongsFragment;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

/*
 * Created by Prasadam Saiteja on 8/16/2016.
 */

public class SongsContentObserver extends ContentObserver {

    public SongsContentObserver(Handler handler) {
        super(handler);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }
    public void onChange(boolean selfChange, Uri uri) {
        Log.d("Song added", uri.toString());
    }
}
