package com.prasadam.kmrplayer.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.Activities.MainActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

/*
 * Created by Prasadam Saiteja on 8/17/2016.
 */

public class NowPlayingWidget extends AppWidgetProvider {

    private static final String NOW_PLAYING_WIDGET_INTENT_LAUNCH = "NOW_PLAYING_WIDGET_INTENT_LAUNCH";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews;

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(SharedVariables.globalActivityContext);
        if(getPrefs.getBoolean("widget_now_expanded", false))
            remoteViews = new RemoteViews(SharedVariables.globalActivityContext.getPackageName(), R.layout.widget_now_playing_expanded);
        else
            remoteViews = new RemoteViews(SharedVariables.globalActivityContext.getPackageName(), R.layout.widget_now_playing);

        ComponentName watchWidget = new ComponentName(context, NowPlayingWidget.class);

        setWidgetDetails(context, remoteViews);
        openPlayerOnWidgetClick(context, remoteViews);
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int rows = getCellsForSize(minHeight);
        RemoteViews remoteViews;

        if (rows == 1){
            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(SharedVariables.globalActivityContext);
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean("widget_now_expanded", false);
            e.apply();
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_now_playing);
        }

        else{
            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(SharedVariables.globalActivityContext);
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean("widget_now_expanded", true);
            e.apply();
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_now_playing_expanded);
        }

        setWidgetDetails(context, remoteViews);
        openPlayerOnWidgetClick(context, remoteViews);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private void openPlayerOnWidgetClick(Context context, RemoteViews remoteViews) {
        Intent nIntent = new Intent(SharedVariables.globalActivityContext, MainActivity.class);
        nIntent.putExtra("notificationIntent", true);
        nIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_album_layout, pendingIntent);
    }
    private void setWidgetDetails(Context context, RemoteViews remoteViews) {

        Bitmap albumArt = AudioExtensionMethods.getBitMap(MusicService.currentSong.getAlbumArtLocation());
        if(albumArt != null)
            remoteViews.setImageViewBitmap(R.id.widget_now_playing_album_art, albumArt);
        else if(SharedVariables.globalActivityContext != null)
            remoteViews.setImageViewBitmap(R.id.widget_now_playing_album_art, AudioExtensionMethods.getBitMap(SharedVariables.globalActivityContext ,MusicService.currentSong.getAlbumArtLocation()));

        remoteViews.setTextViewText(R.id.widget_song_name, MusicService.currentSong.getTitle());
        remoteViews.setTextViewText(R.id.widget_artist_name, MusicService.currentSong.getArtist());

        remoteViews.setOnClickPendingIntent(R.id.widget_now_playing_album_art, getPendingSelfIntent(context, NOW_PLAYING_WIDGET_INTENT_LAUNCH));

        Intent previous = new Intent(MusicService.NOTIFY_PREVIOUS);
        Intent fav = new Intent(MusicService.NOTIFY_FAV);
        Intent pause = new Intent(MusicService.NOTIFY_PAUSE);
        Intent next = new Intent(MusicService.NOTIFY_NEXT);
        Intent play = new Intent(MusicService.NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(SharedVariables.globalActivityContext, 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_prev_button, pPrevious);

        if(MusicService.player != null && MusicService.player.isPlaying()){
            remoteViews.setImageViewBitmap(R.id.widget_play_pause_button, ((BitmapDrawable) SharedVariables.globalActivityContext.getResources().getDrawable(R.mipmap.ic_pause_black_24dp)).getBitmap());
            PendingIntent pPause = PendingIntent.getBroadcast(SharedVariables.globalActivityContext, 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_play_pause_button, pPause);
        }
        else{
            remoteViews.setImageViewBitmap(R.id.widget_play_pause_button, ((BitmapDrawable) SharedVariables.globalActivityContext.getResources().getDrawable(R.mipmap.ic_play_arrow_black_24dp)).getBitmap());
            PendingIntent pPlay = PendingIntent.getBroadcast(SharedVariables.globalActivityContext, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_play_pause_button, pPlay);
        }

        if(MusicService.currentSong.getIsLiked(SharedVariables.globalActivityContext)){
            remoteViews.setImageViewBitmap(R.id.widget_fav_button, ((BitmapDrawable) SharedVariables.globalActivityContext.getResources().getDrawable(R.drawable.ic_favorite_red_24dp)).getBitmap());
        }

        else{
            remoteViews.setImageViewBitmap(R.id.widget_fav_button, ((BitmapDrawable) SharedVariables.globalActivityContext.getResources().getDrawable(R.mipmap.ic_favorite_border_black_24dp)).getBitmap());
        }

        PendingIntent pFav = PendingIntent.getBroadcast(SharedVariables.globalActivityContext, 0, fav, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_fav_button, pFav);

        PendingIntent pNext = PendingIntent.getBroadcast(SharedVariables.globalActivityContext, 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_next_button, pNext);
    }
    private static int getCellsForSize(int size) {
        int n = 2;
        while (70 * n - 30 < size) {
            ++n;
        }
        return n - 1;
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}