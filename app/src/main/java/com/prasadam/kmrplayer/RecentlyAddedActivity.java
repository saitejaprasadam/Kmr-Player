package com.prasadam.kmrplayer;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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
 * Created by Prasadam Saiteja on 5/27/2016.
 */

public class RecentlyAddedActivity extends AppCompatActivity {

    @Bind (R.id.recently_added_playlist_recycler_view) RecyclerView recentlyAddedRecyclerView;
    private ArrayList<Song> songsList;
    private UnifedRecyclerViewAdapter recyclerViewAdapter;
    private Menu mOptionsMenu;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_recently_added_layout);
        ButterKnife.bind(this);

        setStatusBarTranslucent(RecentlyAddedActivity.this);
        ActivityHelper.setDisplayHome(this);

        final MaterialDialog loading = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_populate_list_text)
                .progress(true, 0)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                songsList = AudioExtensionMethods.getRecentlyAddedSongs(RecentlyAddedActivity.this);

                if(songsList.size() == 0)
                    ActivityHelper.showEmptyFragment(RecentlyAddedActivity.this, getResources().getString(R.string.no_songs_text));

                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSearchListener();
                            recyclerViewAdapter = new UnifedRecyclerViewAdapter(RecentlyAddedActivity.this, songsList);
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

                            recentlyAddedRecyclerView.setAdapter(recyclerViewAdapter);
                            recentlyAddedRecyclerView.addItemDecoration(new DividerItemDecoration(RecentlyAddedActivity.this, LinearLayoutManager.VERTICAL));
                            loading.dismiss();
                        }
                    });
                }
            }
        }).start();
    }
    private void setSearchListener() {
        MenuItem searchItem = mOptionsMenu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setActionView(searchItem, searchView);
        SongsSearchListener searchListener = new SongsSearchListener(RecentlyAddedActivity.this, songsList, recentlyAddedRecyclerView, recyclerViewAdapter);
        searchView.setOnQueryTextListener(searchListener);
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
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
}
