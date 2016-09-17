package com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

/*
 * Created by Prasadam Saiteja on 7/5/2016.
 */

public class NearbyDevicesActivity extends AppCompatActivity{

    public static NearbyDevicesAdapter nearbyDevicesRecyclerviewAdapter;
    public static TextView NoDevicesTextView;
    private RecyclerView NearbyRecyclerView;
    private BroadcastReceiver receiver;
    private Handler currentSongPlayingRequestHandler;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_nearby_devices);

        NoDevicesTextView = (TextView) findViewById(R.id.no_devices_available_text_view);
        ActivityHelper.setDisplayHome(this);
        ActivityHelper.setStatusBarTranslucent_PreLollipop(NearbyDevicesActivity.this);
        DialogHelper.checkForNetworkState(this, (FloatingActionButton) findViewById(R.id.wifi_fab));
        wifiBroadCastReceiver();
        setRecyclerView();
        setCurrentSongPlayingHandler();
    }

    public void onDestroy(){
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        currentSongPlayingRequestHandler = null;
        NearbyRecyclerView.setAdapter(null);
        nearbyDevicesRecyclerviewAdapter = null;
        NoDevicesTextView = null;
        super.onDestroy();
    }

    private void setRecyclerView() {

        nearbyDevicesRecyclerviewAdapter = new NearbyDevicesAdapter(this);
        NearbyRecyclerView = (RecyclerView) findViewById(R.id.available_devices_recycler_view);
        NearbyRecyclerView.setAdapter(nearbyDevicesRecyclerviewAdapter);
        NearbyRecyclerView.addItemDecoration(new DividerItemDecoration(NearbyDevicesActivity.this, LinearLayoutManager.VERTICAL));
        NearbyRecyclerView.setLayoutManager(new LinearLayoutManager(NearbyDevicesActivity.this));
    }
    public static void updateAdapater(){
        try{
            Handler mainThread = new Handler(Looper.getMainLooper());
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    if(nearbyDevicesRecyclerviewAdapter != null)
                        nearbyDevicesRecyclerviewAdapter.notifyDataSetChanged();
                }
            });
        }
        catch (Exception ignore){}
    }
    private void wifiBroadCastReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {DialogHelper.checkForNetworkState(NearbyDevicesActivity.this, (FloatingActionButton) findViewById(R.id.wifi_fab));}
        };
        registerReceiver(receiver, filter);
    }
    private void setCurrentSongPlayingHandler() {

        if(currentSongPlayingRequestHandler == null){
            currentSongPlayingRequestHandler = new Handler();
            currentSongPlayingRequestHandler.post(new Runnable() {
                @Override
                public void run() {

                    for (NSD serverObject : NSDClient.devicesList){
                        SocketExtensionMethods.requestForCurrentSongPlaying(NearbyDevicesActivity.this, serverObject.GetClientNSD());
                    }

                    if(currentSongPlayingRequestHandler != null)
                        currentSongPlayingRequestHandler.postDelayed(this, 8000);
                }
            });
        }
    }

    @Override
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