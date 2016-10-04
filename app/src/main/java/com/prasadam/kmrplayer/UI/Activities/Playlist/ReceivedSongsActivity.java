package com.prasadam.kmrplayer.UI.Activities.Playlist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NetworkAdapter.ReceivedSongsAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.DatabaseHelper.db4oHelper;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.UI.Activities.BaseActivity.VerticalSlidingDrawerBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/22/2016.
 */

public class ReceivedSongsActivity extends VerticalSlidingDrawerBaseActivity{

    @BindView (R.id.transfer_recycler_view) RecyclerView recyclerView;
    @BindView (R.id.fragment_container) FrameLayout fragmentContainer;
    private ReceivedSongsAdapter recyclerViewAdapter;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_received_songs_layout);
        ButterKnife.bind(this);

        ActivityHelper.setBackButtonToCustomToolbarBar(ReceivedSongsActivity.this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome(this);

        if(SharedVariables.fullTransferList.size() == 0)
            SharedVariables.fullTransferList = db4oHelper.getTransferableSongObjects(this);

        InitRecyclerView();
    }
    public void onDestroy(){
        recyclerView = null;
        recyclerViewAdapter = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_received_songs_menu, menu);
        ActivityHelper.nearbyDevicesCount(this, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        switch (id){

            case android.R.id.home:
                finish();
                break;

            case R.id.action_equilzer:
                ActivitySwitcher.initEqualizer(ReceivedSongsActivity.this);
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(ReceivedSongsActivity.this);
                break;
        }
        return true;
    }

    private void InitRecyclerView() {

        recyclerViewAdapter = new ReceivedSongsAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerViewAdapter);

        if(SharedVariables.getFullTransferList().size() == 0)
            ActivityHelper.showEmptyFragment(this, "No songs received", fragmentContainer);
    }
}