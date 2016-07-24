package com.prasadam.kmrplayer.Widgets;/*
 * Created by Prasadam Saiteja on 7/22/2016.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.prasadam.kmrplayer.MainActivity;
import com.prasadam.kmrplayer.R;

public class QuickShuffleWidget extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quick_shuffle);
        views.setOnClickPendingIntent(R.id.quick_shuffle, pendingIntent);
        appWidgetManager.updateAppWidget(1, views);
    }
}
