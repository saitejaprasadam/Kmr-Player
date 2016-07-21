package com.prasadam.kmrplayer;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.prasadam.kmrplayer.ListenerClasses.SongsSearchListener;
import com.prasadam.kmrplayer.activityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.activityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.RecentlyAddedRecyclerViewAdapter;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.fragments.NoItemsFragment;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.adapterClasses.uiAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.prasadam.kmrplayer.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 5/29/2016.
 */

public class SongPlaybackHistoryActivity extends AppCompatActivity {

    @Bind(R.id.history_recycler_view) RecyclerView recyclerView;

    private ArrayList<Song> songsList;
    private RecentlyAddedRecyclerViewAdapter recyclerViewAdapter;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_song_playback_history_layout);
        ButterKnife.bind(this);

        setStatusBarTranslucent(SongPlaybackHistoryActivity.this);
        ActivityHelper.setDisplayHome(this);

        songsList = AudioExtensionMethods.getSongPlayBackHistory(this);

        if(songsList.size() == 0)
        {
            NoItemsFragment newFragment = new NoItemsFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(android.R.id.content, newFragment).commit();
            newFragment.setDescriptionTextView(getResources().getString(R.string.no_play_history));
        }

        else
        {
            recyclerViewAdapter = new RecentlyAddedRecyclerViewAdapter(this, songsList);
            if (!ExtensionMethods.isTablet(this))
            {
                if(!ExtensionMethods.isLandScape(this))    //Mobile Portrait
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));

                if(ExtensionMethods.isLandScape(this))    //Mobile Landscape
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
            }

            else{
                if(!ExtensionMethods.isLandScape(this))    //Tablet Portrait
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));

                if(ExtensionMethods.isLandScape(this))    //Tablet Landscape
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
            }

            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        }

    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_recently_added_songs_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setActionView(searchItem, searchView);
        SongsSearchListener searchListener = new SongsSearchListener(SongPlaybackHistoryActivity.this, songsList, recyclerView, recyclerViewAdapter);
        searchView.setOnQueryTextListener(searchListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        switch (id){

            case android.R.id.home:
                finish();
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(SongPlaybackHistoryActivity.this);
                break;
        }
        return true;
    }
}
