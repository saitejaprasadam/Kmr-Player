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
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

/*
 * Created by Prasadam Saiteja on 7/22/2016.
 */

public class QuickShuffleWidget extends AppWidgetProvider {

    private static final String SHUFFLE_WIDGET_ACTION = "SHUFFLE_WIDGET_ACTION";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_quick_shuffle);
        ComponentName watchWidget = new ComponentName(context, QuickShuffleWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.quick_shuffle_widget_image_view, getPendingSelfIntent(context, SHUFFLE_WIDGET_ACTION));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (SHUFFLE_WIDGET_ACTION.equals(intent.getAction())) {

            if (SharedVariables.fullSongsList.size() == 0){
                AudioExtensionMethods.updateSongList(context);
                Toast.makeText(context, context.getResources().getString(R.string.please_wait_quick_shuffle), Toast.LENGTH_SHORT).show();
            }

            if (SharedVariables.fullSongsList.size() > 0) {
                MusicPlayerExtensionMethods.shufflePlay(context, SharedVariables.fullSongsList);
                Toast.makeText(context, context.getResources().getString(R.string.Quick_shuffle_widget_text), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, context.getResources().getString(R.string.widget_no_songs_text), Toast.LENGTH_SHORT).show();
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
