package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.prasadam.kmrplayer.Activities.AlbumActivity;
import com.prasadam.kmrplayer.Activities.ArtistActivity;
import com.prasadam.kmrplayer.Activities.Playlist.MostPlayedSongsActivity;
import com.prasadam.kmrplayer.Activities.Playlist.PlaylistHelpers.MostPlayedSongsPieChartActivity;
import com.prasadam.kmrplayer.Activities.NetworkAcitivities.NearbyDevicesActivity;
import com.prasadam.kmrplayer.Activities.HelperActivities.ExpandedAlbumartActivity;
import com.prasadam.kmrplayer.Activities.NetworkAcitivities.QuickShareActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.Activities.HelperActivities.SearchActivity;
import com.prasadam.kmrplayer.Activities.HelperActivities.TagEditorActivity;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 6/24/2016.
 */

public class ActivitySwitcher {

    public static void jumpToAlbum(final Activity context, final String albumTitle) {
        Intent albumActivityIntent = new Intent(context, AlbumActivity.class);
        albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        albumActivityIntent.putExtra("albumTitle", albumTitle);
        context.startActivity(albumActivityIntent);
    }

    public static void jumpToArtist(final Context context, final String artistTitle){
        Intent albumActivityIntent = new Intent(context, ArtistActivity.class);
        albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        albumActivityIntent.putExtra("artist", artistTitle);
        context.startActivity(albumActivityIntent);
    }

    public static void jumpToAlbumWithTranscition(final Activity mActivity, final ImageView imageView, final String albumTitle){

        Intent albumActivityIntent = new Intent(mActivity, AlbumActivity.class);
        albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, "AlbumArtImageTranscition");
        albumActivityIntent.putExtra("albumTitle", albumTitle);
        mActivity.startActivity(albumActivityIntent, options.toBundle());
    }

    public static void ExpandedAlbumArtWithTranscition(final Activity mActivity, final ImageView imageView, final String albumArtLocation){
        Intent albumActivityIntent = new Intent(mActivity, ExpandedAlbumartActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, "AlbumArtImageTranscition");
        albumActivityIntent.putExtra("albumArtPath", albumArtLocation);
        mActivity.startActivity(albumActivityIntent, options.toBundle());
    }

    public static void launchTagEditor(final Activity mActivity, final long songID, final int position){
        Intent tagEditorIntent = new Intent(mActivity, TagEditorActivity.class);
        tagEditorIntent.putExtra("songID", String.valueOf(songID));
        tagEditorIntent.putExtra("position", position);
        mActivity.startActivityForResult(tagEditorIntent, KeyConstants.REQUEST_CODE_TAG_EDITOR);
    }

    public static void initEqualizer(final Context context) {
        Intent i = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicService.player.getAudioSessionId());
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
        context.startActivity(i);
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

    public static void launchMostPlayedActivity(MostPlayedSongsActivity mostPlayedSongsActivity) {
        Intent mostPlayedSongsPieChartIntent = new Intent(mostPlayedSongsActivity, MostPlayedSongsPieChartActivity.class);
        mostPlayedSongsActivity.startActivity(mostPlayedSongsPieChartIntent);
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
}
