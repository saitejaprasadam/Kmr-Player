package com.prasadam.kmrplayer.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.R;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/22/2016.
 */

public class QuickFavoritePlayWidget extends AppWidgetProvider {

    private static final String FAVORITE_WIDGET_ACTION = "FAVORITE_WIDGET_ACTION";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_play_favorites);
        ComponentName watchWidget = new ComponentName(context, QuickFavoritePlayWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.favorites_widget_image_view, getPendingSelfIntent(context, FAVORITE_WIDGET_ACTION));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (FAVORITE_WIDGET_ACTION.equals(intent.getAction())) {

            Toast.makeText(context, context.getResources().getString(R.string.please_wait_quick_shuffle), Toast.LENGTH_SHORT).show();
            ArrayList<Song> FavoriteSongs = AudioExtensionMethods.getFavoriteSongsList(context);
            if(FavoriteSongs.size() > 0){
                MusicPlayerExtensionMethods.playSong(context, FavoriteSongs, 0);
                Toast.makeText(context, context.getResources().getString(R.string.Favorites_widget_text), Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(context, context.getResources().getString(R.string.widget_no_songs_text), Toast.LENGTH_SHORT).show();
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
