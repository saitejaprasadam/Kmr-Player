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

public class QuickMostPlayedWidget extends AppWidgetProvider {

    private static final String MOST_PLAYED_WIDGET_ACTION = "MOST_PLAYED_WIDGET_ACTION";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_quick_most_played);
        ComponentName watchWidget = new ComponentName(context, QuickMostPlayedWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.most_played_widget_image_view, getPendingSelfIntent(context, MOST_PLAYED_WIDGET_ACTION));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (MOST_PLAYED_WIDGET_ACTION.equals(intent.getAction())) {
            Toast.makeText(context, context.getResources().getString(R.string.please_wait_quick_shuffle), Toast.LENGTH_SHORT).show();
            ArrayList<Song> MostPlayedSongs = AudioExtensionMethods.getMostPlayedSongsList(context);
            if(MostPlayedSongs.size() > 0){
                MusicPlayerExtensionMethods.playSong(context, MostPlayedSongs, 0);
                Toast.makeText(context, context.getResources().getString(R.string.Most_played_widget_text), Toast.LENGTH_SHORT).show();
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
