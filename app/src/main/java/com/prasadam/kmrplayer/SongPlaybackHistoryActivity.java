package com.prasadam.kmrplayer;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ListenerClasses.SongsSearchListener;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.UnifedRecyclerViewAdapter;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.prasadam.kmrplayer.SharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 5/29/2016.
 */

public class SongPlaybackHistoryActivity extends VerticalSlidingDrawerBaseActivity {

    @Bind(R.id.history_recycler_view) RecyclerView recyclerView;
    private Menu mOptionsMenu;
    private ArrayList<Song> songsList;
    private UnifedRecyclerViewAdapter songHistoryActivityrecyclerViewAdapter;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_song_playback_history_layout);
        ButterKnife.bind(this);

        setStatusBarTranslucent(SongPlaybackHistoryActivity.this);
        ActivityHelper.setDisplayHome(this);

        final MaterialDialog loading = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_populate_list_text)
                .progress(true, 0)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                songsList = AudioExtensionMethods.getSongPlayBackHistory(SongPlaybackHistoryActivity.this);

                if(songsList.size() == 0)
                    ActivityHelper.showEmptyFragment(SongPlaybackHistoryActivity.this, getResources().getString(R.string.no_play_history));

                else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSearchListener();
                            songHistoryActivityrecyclerViewAdapter = new UnifedRecyclerViewAdapter(SongPlaybackHistoryActivity.this, songsList);
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
                        }
                    });
                }
            }
        }).start();
    }
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        songHistoryActivityrecyclerViewAdapter = null;
        songsList.clear();
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
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
}
