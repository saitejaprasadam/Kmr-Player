package com.prasadam.kmrplayer.activityHelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import com.prasadam.kmrplayer.AlbumActivity;
import com.prasadam.kmrplayer.ArtistActivity;

/*
 * Created by Prasadam Saiteja on 6/24/2016.
 */

public class ActivitySwitcher {

    public static void jumpToAlbum(Context context, String albumTitle) {
        Intent albumActivityIntent = new Intent(context, AlbumActivity.class);
        albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        albumActivityIntent.putExtra("albumTitle", albumTitle);
        context.startActivity(albumActivityIntent);
    }

    public static void jumpToArtist(Context context, String artistTitle){
        Intent albumActivityIntent = new Intent(context, ArtistActivity.class);
        albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        albumActivityIntent.putExtra("artist", artistTitle);
        context.startActivity(albumActivityIntent);
    }

    public static void jumpToAlbumWithTranscition(Activity mActivity, ImageView imageView, String albumTitle){

        Intent albumActivityIntent = new Intent(mActivity, AlbumActivity.class);
        albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, "AlbumArtImageTranscition");
        albumActivityIntent.putExtra("albumTitle", albumTitle);
        mActivity.startActivity(albumActivityIntent, options.toBundle());
    }
}
