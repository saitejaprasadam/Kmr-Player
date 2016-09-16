package com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NearbyDevicesAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/8/2016.
 */

public class QuickShareActivity extends AppCompatActivity{

    public static TextView NoDevicesTextView;
    public static NearbyDevicesAdapter QuickShareRecyclerviewAdapter;
    private RecyclerView quickShareRecyclerView;
    private BroadcastReceiver receiver;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_quick_share);

        final ArrayList<String> songsPathList = getIntent().getStringArrayListExtra(KeyConstants.INTENT_SONGS_PATH_LIST);
        NoDevicesTextView = (TextView) findViewById(R.id.no_devices_available_text_view);
        InitActionBarAndToolBar();
        DialogHelper.checkForNetworkState(this, (FloatingActionButton) findViewById(R.id.wifi_fab));
        wifiBroadCastReceiver();
        setRecyclerView(songsPathList);
    }
    public void onDestroy(){
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
        quickShareRecyclerView.setAdapter(null);
        QuickShareRecyclerviewAdapter = null;
        NoDevicesTextView = null;
    }

    private void wifiBroadCastReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DialogHelper.checkForNetworkState(QuickShareActivity.this, (FloatingActionButton) findViewById(R.id.wifi_fab));
            }
        };
        registerReceiver(receiver, filter);
    }

    private void InitActionBarAndToolBar() {
        ActivityHelper.setStatusBarTranslucent_PreLollipop(QuickShareActivity.this);
        ActivityHelper.setDisplayHome(this);
    }
    private void setRecyclerView(ArrayList<String> songsPathList) {
        QuickShareRecyclerviewAdapter = new NearbyDevicesAdapter(this);
        QuickShareRecyclerviewAdapter.setQuickShareSongPathList(songsPathList);
        quickShareRecyclerView = (RecyclerView) findViewById(R.id.quick_share_recycler_view);
        quickShareRecyclerView.setAdapter(QuickShareRecyclerviewAdapter);
        quickShareRecyclerView.addItemDecoration(new DividerItemDecoration(QuickShareActivity.this, LinearLayoutManager.VERTICAL));
        quickShareRecyclerView.setLayoutManager(new LinearLayoutManager(QuickShareActivity.this));
    }
    public static void updateAdapater(){
        try{
            if(QuickShareRecyclerviewAdapter != null)
                QuickShareRecyclerviewAdapter.notifyDataSetChanged();
        }
        catch (Exception ignore){}
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_nearby_info_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.action_events:
                ActivitySwitcher.launchEventsActivity(this);
                break;

            case R.id.action_nearby_info:
                DialogHelper.showNearbyInfo(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
