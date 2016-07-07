package com.prasadam.kmrplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.AlbumInnerLayoutSongRecyclerViewAdapter;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.NearbyDevicesRecyclerViewAdapter;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.DividerItemDecoration;
import com.prasadam.kmrplayer.socketClasses.NSDClient;

import butterknife.ButterKnife;

import static com.prasadam.kmrplayer.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 7/5/2016.
 */

public class NearbyDevicesActivity extends AppCompatActivity{

    public static NearbyDevicesRecyclerViewAdapter nearbyDevicesRecyclerviewAdapter;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_available_devices);
        ButterKnife.bind(this);

        if(getSupportActionBar() != null ){
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_chevron_left_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setStatusBarTranslucent(NearbyDevicesActivity.this);
        setRecyclerView();
    }

    private void setRecyclerView() {

        nearbyDevicesRecyclerviewAdapter = new NearbyDevicesRecyclerViewAdapter(NearbyDevicesActivity.this, this);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.available_devices_recycler_view);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(nearbyDevicesRecyclerviewAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(NearbyDevicesActivity.this, LinearLayoutManager.VERTICAL));
                recyclerView.setLayoutManager(new LinearLayoutManager(NearbyDevicesActivity.this));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        if (id == android.R.id.home)
            finish();

        return false;
    }
}
