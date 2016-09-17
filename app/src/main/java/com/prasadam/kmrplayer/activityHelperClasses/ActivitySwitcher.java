package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.UI.Activities.AlbumActivity;
import com.prasadam.kmrplayer.UI.Activities.ArtistActivity;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.EventsActivity;
import com.prasadam.kmrplayer.UI.Activities.HelperActivities.ExpandedAlbumartActivity;
import com.prasadam.kmrplayer.UI.Activities.HelperActivities.SearchActivity;
import com.prasadam.kmrplayer.UI.Activities.HelperActivities.TagEditorActivity;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.NearbyDevicesActivity;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.QuickShareActivity;
import com.prasadam.kmrplayer.UI.Activities.SettingsActivity;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 6/24/2016.
 */

public class ActivitySwitcher {

    public static void jumpToAlbum(final Activity context, final Long songID) {

        Intent albumActivityIntent = new Intent(context, AlbumActivity.class);
        albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        albumActivityIntent.putExtra("albumID", AudioExtensionMethods.getAlubmID(context, songID));
        context.startActivityForResult(albumActivityIntent, KeyConstants.REQUEST_CODE_DELETE_ALBUM);
    }
    public static void jumpToAlbumWithTranscition(final Activity mActivity, final ImageView imageView, final Long albumID){

        Intent albumActivityIntent = new Intent(mActivity, AlbumActivity.class);
        albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, "AlbumArtImageTranscition");
        albumActivityIntent.putExtra("albumID", albumID);
        mActivity.startActivityForResult(albumActivityIntent, KeyConstants.REQUEST_CODE_DELETE_ALBUM, options.toBundle());
    }
    public static void jumpToArtist(final Context context, final long artistID){
        Intent albumActivityIntent = new Intent(context, ArtistActivity.class);
        albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        albumActivityIntent.putExtra("artistID", artistID);
        context.startActivity(albumActivityIntent);
    }
    public static void ExpandedAlbumArtWithTranscition(final Activity mActivity, final ImageView imageView, final String albumArtLocation){
        Intent albumActivityIntent = new Intent(mActivity, ExpandedAlbumartActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, "AlbumArtImageTranscition");
        albumActivityIntent.putExtra("albumArtPath", albumArtLocation);
        mActivity.startActivity(albumActivityIntent, options.toBundle());
    }

    public static void launchTagEditor(final Activity mActivity, final long songID, final String songHashID){
        Intent tagEditorIntent = new Intent(mActivity, TagEditorActivity.class);
        tagEditorIntent.putExtra("songID", String.valueOf(songID));
        tagEditorIntent.putExtra("songHashID", songHashID);
        mActivity.startActivityForResult(tagEditorIntent, KeyConstants.REQUEST_CODE_TAG_EDITOR);
    }
    public static void initEqualizer(final Context context) {

        try{
            Intent i = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicService.player.getAudioSessionId());
            i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
            context.startActivity(i);
        }
        catch (ActivityNotFoundException exception){
            Toast.makeText(context, "No stock equilzer found", Toast.LENGTH_SHORT).show();
        }
    }
    public static void launchEventsActivity(final Context context){
        context.startActivity(new Intent(context, EventsActivity.class));
    }

    public static void jumpToAvaiableDevies(final Context context) {
        Intent avaiableDevices = new Intent(context, NearbyDevicesActivity.class);
        context.startActivity(avaiableDevices);
    }
    public static void jumpToQuickShareActivity(final Context context, final ArrayList<Song> songsList){
        Intent quickShareIntent = new Intent(context, QuickShareActivity.class);
        ArrayList<String> songsPath = new ArrayList<>();
        for (Song song : songsList){
            songsPath.add(song.getData());
        }
        quickShareIntent.putStringArrayListExtra(KeyConstants.INTENT_SONGS_PATH_LIST, songsPath);
        context.startActivity(quickShareIntent);
    }
    public static void jumpToQuickShareActivity(final Context context, final Song song){
        Intent quickShareIntent = new Intent(context, QuickShareActivity.class);
        ArrayList<String> songsPath = new ArrayList<>();
        songsPath.add(song.getData());
        quickShareIntent.putStringArrayListExtra(KeyConstants.INTENT_SONGS_PATH_LIST, songsPath);
        context.startActivity(quickShareIntent);
    }

    public static void launchSearchActivity(final Context context){
        Intent searchAcrivityIntent = new Intent(context, SearchActivity.class);
        searchAcrivityIntent.setAction(Intent.ACTION_SEARCH);
        context.startActivity(searchAcrivityIntent);
    }
    public static void launchAboutActivity(Context context){

        new LibsBuilder()
                .withAutoDetect(true)
                .withLicenseShown(true)
                .withVersionShown(true)
                .withActivityTitle(context.getResources().getString(R.string.about_text))
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutAppName(context.getResources().getString(R.string.app_name))
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withAboutDescription(context.getResources().getString(R.string.app_description_text))
                .start(context);
    }
    public static void launchSettings(Context context) {
        Intent settingsIntent = new Intent(context, SettingsActivity.class);
        context.startActivity(settingsIntent);
    }
    public static void launchMarket(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Unable to find play store", Toast.LENGTH_LONG).show();
        }
    }
}
