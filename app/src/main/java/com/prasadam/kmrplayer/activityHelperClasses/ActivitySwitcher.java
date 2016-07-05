package com.prasadam.kmrplayer.activityHelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.Equalizer;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import com.prasadam.kmrplayer.AlbumActivity;
import com.prasadam.kmrplayer.ArtistActivity;
import com.prasadam.kmrplayer.ExpandedAlbumartActivity;
import com.prasadam.kmrplayer.TagEditorActivity;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.MusicService;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

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

    public static void ExpandedAlbumArtWithTranscition(Activity mActivity, ImageView imageView, String albumArtLocation){
        Intent albumActivityIntent = new Intent(mActivity, ExpandedAlbumartActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, "AlbumArtImageTranscition");
        albumActivityIntent.putExtra("albumArtPath", albumArtLocation);
        mActivity.startActivity(albumActivityIntent, options.toBundle());
    }

    public static void launchTagEditor(Activity mActivity, long songID, int position){
        Intent tagEditorIntent = new Intent(mActivity, TagEditorActivity.class);
        tagEditorIntent.putExtra("songID", String.valueOf(songID));
        tagEditorIntent.putExtra("position", position);
        mActivity.startActivityForResult(tagEditorIntent, SharedVariables.TAG_EDITOR_REQUEST_CODE);
    }

    public static void initEqualizer(Context context) {
        if(SharedVariables.equalizer == null){
            SharedVariables.equalizer = new Equalizer(0, MusicService.player.getAudioSessionId());
            SharedVariables.equalizer.setEnabled(true);
        }

        Intent i = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicService.player.getAudioSessionId());
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
        context.startActivity(i);
    }

}
