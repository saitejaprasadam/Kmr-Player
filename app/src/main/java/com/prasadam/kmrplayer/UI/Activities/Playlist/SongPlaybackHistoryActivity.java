package com.prasadam.kmrplayer.UI.Activities.Playlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.SongsAdapter.UnifedSongAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.ListenerClasses.SongsSearchListener;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;
import com.prasadam.kmrplayer.UI.Activities.BaseActivity.VerticalSlidingDrawerBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 5/29/2016.
 */

public class SongPlaybackHistoryActivity extends VerticalSlidingDrawerBaseActivity {

    @BindView (R.id.history_recycler_view) RecyclerView recyclerView;
    @BindView (R.id.root_layout) FrameLayout rootLayout;
    @BindView (R.id.fragment_container) FrameLayout fragmentContainer;

    private Menu mOptionsMenu;
    private SongsArrayList songsList;
    private UnifedSongAdapter songHistoryActivityrecyclerViewAdapter;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_song_playback_history_layout);
        ButterKnife.bind(this);

        ActivityHelper.setBackButtonToCustomToolbarBar(SongPlaybackHistoryActivity.this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome(this);

        final MaterialDialog loading = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_populate_list_text)
                .progress(true, 0)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                songsList = new SongsArrayList(AudioExtensionMethods.getSongPlayBackHistory(SongPlaybackHistoryActivity.this)) {
                    @Override
                    public void notifyDataSetChanged() {
                        if(songHistoryActivityrecyclerViewAdapter != null)
                            songHistoryActivityrecyclerViewAdapter.notifyDataSetChanged();
                    }
                    public void notifyItemRemoved(int index) {
                        if(songHistoryActivityrecyclerViewAdapter != null)
                        songHistoryActivityrecyclerViewAdapter.notifyItemRemoved(index);
                    }
                    public void notifyItemInserted(int index) {
                        if(songHistoryActivityrecyclerViewAdapter != null)
                            songHistoryActivityrecyclerViewAdapter.notifyItemInserted(index);
                    }
                    public void notifyItemChanged(int index) {
                        if(songHistoryActivityrecyclerViewAdapter != null)
                            songHistoryActivityrecyclerViewAdapter.notifyItemChanged(index);
                    }
                };

                if(songsList.size() == 0){
                    ActivityHelper.showEmptyFragment(SongPlaybackHistoryActivity.this, getResources().getString(R.string.no_play_history), fragmentContainer);
                    loading.dismiss();
                }

                else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            songHistoryActivityrecyclerViewAdapter = new UnifedSongAdapter(SongPlaybackHistoryActivity.this, songsList);
                            if (!ExtensionMethods.isTablet(SongPlaybackHistoryActivity.this)) {
                                if (!ExtensionMethods.isLandScape(SongPlaybackHistoryActivity.this))    //Mobile Portrait
                                    recyclerView.setLayoutManager(new LinearLayoutManager(SongPlaybackHistoryActivity.this));

                                if (ExtensionMethods.isLandScape(SongPlaybackHistoryActivity.this))    //Mobile Landscape
                                    recyclerView.setLayoutManager(new GridLayoutManager(SongPlaybackHistoryActivity.this, 2, GridLayoutManager.VERTICAL, false));
                            } else {
                                if (!ExtensionMethods.isLandScape(SongPlaybackHistoryActivity.this))    //Tablet Portrait
                                    recyclerView.setLayoutManager(new GridLayoutManager(SongPlaybackHistoryActivity.this, 2, GridLayoutManager.VERTICAL, false));

                                if (ExtensionMethods.isLandScape(SongPlaybackHistoryActivity.this))    //Tablet Landscape
                                    recyclerView.setLayoutManager(new GridLayoutManager(SongPlaybackHistoryActivity.this, 3, GridLayoutManager.VERTICAL, false));
                            }

                            recyclerView.setAdapter(songHistoryActivityrecyclerViewAdapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(SongPlaybackHistoryActivity.this, LinearLayoutManager.VERTICAL));
                            loading.dismiss();
                            ActivityHelper.setShuffleFAB(SongPlaybackHistoryActivity.this, rootLayout, recyclerView, songsList);
                            setSearchListener();
                        }
                    });
                }
            }
        }).start();
    }
    public void onDestroy() {
        recyclerView.setAdapter(null);
        songHistoryActivityrecyclerViewAdapter = null;
        songsList = null;
        super.onDestroy();
    }

    public void setSearchListener(){
        MenuItem searchItem = mOptionsMenu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setActionView(searchItem, searchView);
        SongsSearchListener searchListener = new SongsSearchListener(SongPlaybackHistoryActivity.this, songsList, recyclerView, songHistoryActivityrecyclerViewAdapter);
        searchView.setOnQueryTextListener(searchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_recently_added_songs_menu, menu);
        ActivityHelper.nearbyDevicesCount(this, menu);
        mOptionsMenu = menu;
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        switch (id){

            case android.R.id.home:
                finish();
                break;

            case R.id.action_equilzer:
                ActivitySwitcher.initEqualizer(SongPlaybackHistoryActivity.this);
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(SongPlaybackHistoryActivity.this);
                break;
        }
        return true;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityHelper.onActivityResultMethod(this, requestCode, resultCode, data, songsList);
    }
}
