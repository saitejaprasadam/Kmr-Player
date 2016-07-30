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
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.UnifedRecyclerViewAdapter;
import com.prasadam.kmrplayer.AudioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.ListenerClasses.SongsSearchListener;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

import java.util.ArrayList;

import static com.prasadam.kmrplayer.SharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 5/28/2016.
 */

public class FavoritesActivity extends AppCompatActivity{

    private ArrayList<Song> favoriteSongList;
    private UnifedRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private Menu mOptionsMenu;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_favorites_layout);
        recyclerView = (RecyclerView) findViewById(R.id.favorites_recycler_view);

        setStatusBarTranslucent(FavoritesActivity.this);
        ActivityHelper.setDisplayHome(this);

        final MaterialDialog loading = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_populate_list_text)
                .progress(true, 0)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                favoriteSongList = AudioExtensionMethods.getFavoriteSongsList(FavoritesActivity.this);

                if(favoriteSongList.size() == 0)
                    ActivityHelper.showEmptyFragment(FavoritesActivity.this, getResources().getString(R.string.no_fav_songs_text));

                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSearchListener();
                            recyclerViewAdapter = new UnifedRecyclerViewAdapter(FavoritesActivity.this, favoriteSongList);

                            if (!ExtensionMethods.isTablet(FavoritesActivity.this))
                            {
                                if(!ExtensionMethods.isLandScape(FavoritesActivity.this))    //Mobile Portrait
                                    recyclerView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));

                                if(ExtensionMethods.isLandScape(FavoritesActivity.this))    //Mobile Landscape
                                    recyclerView.setLayoutManager(new GridLayoutManager(FavoritesActivity.this, 2, GridLayoutManager.VERTICAL, false));
                            }

                            else{
                                if(!ExtensionMethods.isLandScape(FavoritesActivity.this))    //Tablet Portrait
                                    recyclerView.setLayoutManager(new GridLayoutManager(FavoritesActivity.this, 2, GridLayoutManager.VERTICAL, false));

                                if(ExtensionMethods.isLandScape(FavoritesActivity.this))    //Tablet Landscape
                                    recyclerView.setLayoutManager(new GridLayoutManager(FavoritesActivity.this, 3, GridLayoutManager.VERTICAL, false));
                            }

                            recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(FavoritesActivity.this, LinearLayoutManager.VERTICAL));
                            loading.dismiss();
                        }
                    });
                }
            }
        }).start();
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }

    public void setSearchListener(){
        MenuItem searchItem = mOptionsMenu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setActionView(searchItem, searchView);
        SongsSearchListener searchListener = new SongsSearchListener(this, favoriteSongList, recyclerView, recyclerViewAdapter);
        searchView.setOnQueryTextListener(searchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_favorites_menu, menu);
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
                ActivitySwitcher.initEqualizer(FavoritesActivity.this);
                break;

            case R.id.action_add_to:
                DialogHelper.AddToDialogPlaylist(FavoritesActivity.this, favoriteSongList);
                break;

            case R.id.action_play_next:
                MusicPlayerExtensionMethods.playNext(FavoritesActivity.this, favoriteSongList, "Playlist will be played next");
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(FavoritesActivity.this);
                break;
        }
        return true;
    }
}
