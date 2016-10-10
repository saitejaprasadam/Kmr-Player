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
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.SongsAdapter.UnifedSongAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.ListenerClasses.SongsSearchListener;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;
import com.prasadam.kmrplayer.UI.Activities.BaseActivity.VerticalSlidingDrawerBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 5/28/2016.
 */

public class FavoritesActivity extends VerticalSlidingDrawerBaseActivity{

    @BindView(R.id.root_layout) FrameLayout rootLayout;
    @BindView(R.id.favorites_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_container) FrameLayout fragmentContainer;

    private SongsArrayList favoriteSongList;
    private UnifedSongAdapter FavoritesActivityrecyclerViewAdapter;
    private Menu mOptionsMenu;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_favorites_layout);
        ButterKnife.bind(this);
        recyclerView = (RecyclerView) findViewById(R.id.favorites_recycler_view);

        ActivityHelper.setBackButtonToCustomToolbarBar(FavoritesActivity.this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome(this);

        final MaterialDialog loading = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_populate_list_text)
                .progress(true, 0)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                favoriteSongList = new SongsArrayList(AudioExtensionMethods.getFavoriteSongsList(FavoritesActivity.this)) {
                    @Override
                    public void notifyDataSetChanged() {
                        if(FavoritesActivityrecyclerViewAdapter != null)
                            FavoritesActivityrecyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void notifyItemRemoved(int index) {
                        if(FavoritesActivityrecyclerViewAdapter != null)
                            FavoritesActivityrecyclerViewAdapter.notifyItemRemoved(index);
                    }

                    @Override
                    public void notifyItemInserted(int index) {
                        if(FavoritesActivityrecyclerViewAdapter != null)
                            FavoritesActivityrecyclerViewAdapter.notifyItemInserted(index);
                    }

                    @Override
                    public void notifyItemChanged(int index) {
                        if(FavoritesActivityrecyclerViewAdapter != null)
                            FavoritesActivityrecyclerViewAdapter.notifyItemChanged(index);
                    }
                } ;

                if(favoriteSongList.size() == 0){
                    ActivityHelper.showEmptyFragment(FavoritesActivity.this, getResources().getString(R.string.no_fav_songs_text), fragmentContainer);
                    loading.dismiss();
                }

                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FavoritesActivityrecyclerViewAdapter = new UnifedSongAdapter(FavoritesActivity.this, favoriteSongList);

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

                            recyclerView.setAdapter(FavoritesActivityrecyclerViewAdapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(FavoritesActivity.this, LinearLayoutManager.VERTICAL));
                            loading.dismiss();
                            ActivityHelper.setShuffleFAB(FavoritesActivity.this, rootLayout, recyclerView, favoriteSongList);
                            setSearchListener();
                        }
                    });
                }
            }
        }).start();
    }
    public void onDestroy() {
        recyclerView.setAdapter(null);
        FavoritesActivityrecyclerViewAdapter = null;
        favoriteSongList = null;
        super.onDestroy();
    }

    public void setSearchListener(){
        try {
            if (mOptionsMenu == null)
                Thread.sleep(1000);

            MenuItem searchItem = mOptionsMenu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            MenuItemCompat.setActionView(searchItem, searchView);
            SongsSearchListener searchListener = new SongsSearchListener(this, favoriteSongList, recyclerView, FavoritesActivityrecyclerViewAdapter);
            searchView.setOnQueryTextListener(searchListener);
        }

        catch (Exception ignored){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_favorites_menu, menu);
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

            case R.id.action_quick_share:
                ActivitySwitcher.jumpToQuickShareActivity(FavoritesActivity.this, favoriteSongList);
                break;

            case R.id.action_share_playlist:
                ShareIntentHelper.sharePlaylist(FavoritesActivity.this, favoriteSongList, "Favorites");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityHelper.onActivityResultMethod(this, requestCode, resultCode, data, favoriteSongList);
    }
}
