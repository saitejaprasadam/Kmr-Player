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
import com.prasadam.kmrplayer.UI.Activities.VerticalSlidingDrawerBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
/*
 * Created by Prasadam Saiteja on 5/27/2016.
 */

public class RecentlyAddedActivity extends VerticalSlidingDrawerBaseActivity {

    @Bind (R.id.recently_added_playlist_recycler_view) RecyclerView recentlyAddedRecyclerView;
    @Bind (R.id.root_layout) FrameLayout rootLayout;
    @Bind(R.id.fragment_container) FrameLayout fragmentContainer;

    private SongsArrayList songsList;
    private UnifedSongAdapter RecentlyAddedAcitivityrecyclerViewAdapter;
    private Menu mOptionsMenu;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_recently_added_layout);
        ButterKnife.bind(this);

        ActivityHelper.setCustomActionBar(RecentlyAddedActivity.this);
        ExtensionMethods.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome(this);

        final MaterialDialog loading = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_populate_list_text)
                .progress(true, 0)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                songsList = new SongsArrayList(AudioExtensionMethods.getRecentlyAddedSongs(RecentlyAddedActivity.this)) {
                    @Override
                    public void notifyDataSetChanged() {
                        if(RecentlyAddedAcitivityrecyclerViewAdapter != null)
                            RecentlyAddedAcitivityrecyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void notifyItemRemoved(int index) {
                        if(RecentlyAddedAcitivityrecyclerViewAdapter != null)
                            RecentlyAddedAcitivityrecyclerViewAdapter.notifyItemRemoved(index);
                    }

                    @Override
                    public void notifyItemInserted(int index) {
                        if(RecentlyAddedAcitivityrecyclerViewAdapter != null)
                            RecentlyAddedAcitivityrecyclerViewAdapter.notifyItemInserted(index);
                    }

                    @Override
                    public void notifyItemChanged(int index) {
                        if(RecentlyAddedAcitivityrecyclerViewAdapter != null)
                            RecentlyAddedAcitivityrecyclerViewAdapter.notifyItemChanged(index);
                    }
                };

                if(songsList.size() == 0){
                    ActivityHelper.showEmptyFragment(RecentlyAddedActivity.this, getResources().getString(R.string.no_songs_text), fragmentContainer);
                    loading.dismiss();
                }

                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSearchListener();
                            RecentlyAddedAcitivityrecyclerViewAdapter = new UnifedSongAdapter(RecentlyAddedActivity.this, songsList);
                            if (!ExtensionMethods.isTablet(RecentlyAddedActivity.this)) {
                                if (!ExtensionMethods.isLandScape(RecentlyAddedActivity.this))    //Mobile Portrait
                                    recentlyAddedRecyclerView.setLayoutManager(new LinearLayoutManager(RecentlyAddedActivity.this));

                                if (ExtensionMethods.isLandScape(RecentlyAddedActivity.this))    //Mobile Landscape
                                    recentlyAddedRecyclerView.setLayoutManager(new GridLayoutManager(RecentlyAddedActivity.this, 2, GridLayoutManager.VERTICAL, false));
                            } else {
                                if (!ExtensionMethods.isLandScape(RecentlyAddedActivity.this))    //Tablet Portrait
                                    recentlyAddedRecyclerView.setLayoutManager(new GridLayoutManager(RecentlyAddedActivity.this, 2, GridLayoutManager.VERTICAL, false));

                                if (ExtensionMethods.isLandScape(RecentlyAddedActivity.this))    //Tablet Landscape
                                    recentlyAddedRecyclerView.setLayoutManager(new GridLayoutManager(RecentlyAddedActivity.this, 3, GridLayoutManager.VERTICAL, false));
                            }

                            recentlyAddedRecyclerView.setAdapter(RecentlyAddedAcitivityrecyclerViewAdapter);
                            recentlyAddedRecyclerView.addItemDecoration(new DividerItemDecoration(RecentlyAddedActivity.this, LinearLayoutManager.VERTICAL));
                            loading.dismiss();
                            ActivityHelper.setShuffleFAB(RecentlyAddedActivity.this, rootLayout, recentlyAddedRecyclerView, songsList);
                        }
                    });
                }
            }
        }).start();
    }
    public void onDestroy() {
        super.onDestroy();
        recentlyAddedRecyclerView.setAdapter(null);
        RecentlyAddedAcitivityrecyclerViewAdapter = null;
        songsList = null;
    }

    private void setSearchListener() {
        MenuItem searchItem = mOptionsMenu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setActionView(searchItem, searchView);
        SongsSearchListener searchListener = new SongsSearchListener(RecentlyAddedActivity.this, songsList, recentlyAddedRecyclerView, RecentlyAddedAcitivityrecyclerViewAdapter);
        searchView.setOnQueryTextListener(searchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_recently_added_songs_menu, menu);
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
                ActivitySwitcher.initEqualizer(RecentlyAddedActivity.this);
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(RecentlyAddedActivity.this);
                break;
        }
        return true;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityHelper.onActivityResultMethod(this, requestCode, resultCode, data, songsList);
    }
}
