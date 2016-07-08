package com.prasadam.kmrplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.NearbyDevicesRecyclerViewAdapter;
import com.prasadam.kmrplayer.sharedClasses.DividerItemDecoration;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.util.ArrayList;

import static com.prasadam.kmrplayer.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 7/8/2016.
 */

public class QuickShareActivity extends AppCompatActivity{

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_quick_share);
        final ArrayList<String> songsPathList = getIntent().getStringArrayListExtra(KeyConstants.INTENT_SONGS_PATH_LIST);

        InitActionBarAndToolBar();
        setRecyclerView(songsPathList);
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }

    private void InitActionBarAndToolBar() {
        setStatusBarTranslucent(QuickShareActivity.this);

        if(getSupportActionBar() != null ){
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_chevron_left_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    private void setRecyclerView(ArrayList<String> songsPathList) {
        NearbyDevicesRecyclerViewAdapter nearbyDevicesRecyclerviewAdapter = new NearbyDevicesRecyclerViewAdapter(QuickShareActivity.this, this);
        nearbyDevicesRecyclerviewAdapter.setQuickShareSongPathList(songsPathList);
        final RecyclerView quickShareRecyclerView = (RecyclerView) findViewById(R.id.quick_share_recycler_view);
        quickShareRecyclerView.setAdapter(nearbyDevicesRecyclerviewAdapter);
        quickShareRecyclerView.addItemDecoration(new DividerItemDecoration(QuickShareActivity.this, LinearLayoutManager.VERTICAL));
        quickShareRecyclerView.setLayoutManager(new LinearLayoutManager(QuickShareActivity.this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
