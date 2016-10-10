package com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NetworkAdapter.RequestsAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.DatabaseHelperClasses.db4oHelper;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.UI.Activities.BaseActivity.VerticalSlidingDrawerBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/14/2016.
 */

public class RequestsActivity extends VerticalSlidingDrawerBaseActivity {

    @BindView(R.id.events_recycler_view) RecyclerView eventRecyclerView;
    @BindView(R.id.fragment_container) FrameLayout fragmentContainer;
    public static RequestsAdapter eventsAdapter;
    private static NotificationManager notificationManager;
    private static int requestNotificationID = 13435;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_requests_layout);
        ButterKnife.bind(this);
        eventsAdapter = new RequestsAdapter(this);

        if(notificationManager != null)
            notificationManager.cancel(requestNotificationID);
        ActivityHelper.setBackButtonToCustomToolbarBar(RequestsActivity.this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome(this);

        if (SharedVariables.fullEventsList.size() == 0)
            SharedVariables.fullEventsList = db4oHelper.getRequestObjects(this);

        InitRecyclerView();
    }
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("request notification", false) && notificationManager != null)
            notificationManager.cancel(requestNotificationID);
    }
    public void onDestroy() {
        eventsAdapter = null;
        notificationManager = null;
        super.onDestroy();
    }
    public void onBackPressed() {
        super.onBackPressed();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_events_menu, menu);
        ActivityHelper.nearbyDevicesCount(this, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(this);
                break;

            case R.id.action_events_info:
                DialogHelper.showEventsInfo(this);
                break;
        }
        return true;
    }

    private void InitRecyclerView() {

        if (SharedVariables.fullEventsList.size() == 0)
            ActivityHelper.showEmptyFragment(this, "No requests", fragmentContainer);
        else {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            eventRecyclerView.setLayoutManager(mLayoutManager);
            eventRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            eventRecyclerView.setAdapter(eventsAdapter);
        }
    }
    public static void eventNotifyDataSetChanged() {
        try {
            if (eventsAdapter != null) {
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        eventsAdapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (Exception ignored) {
        }
    }

    public static void pushRequestObjectAndNotify(Context context, IRequest request) {
        db4oHelper.pushRequestObject(context, request);
        showRequestNotification(context, request);
    }
    private static void showRequestNotification(Context context, IRequest request) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_turned_in_not_white_24dp)
                .setColor(context.getResources().getColor(R.color.teal))
                .setContentTitle(context.getResources().getString(R.string.new_request_received))
                .setContentText(context.getResources().getString(R.string.new_request) + KeyConstants.SPACE + request.getClientName())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent resultIntent = new Intent(context, RequestsActivity.class);
        resultIntent.putExtra("request notification", true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(RequestsActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestNotificationID, mBuilder.build());
    }
}