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
import com.prasadam.kmrplayer.UI.Activities.VerticalSlidingDrawerBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 5/29/2016.
 */

public class MostPlayedSongsActivity extends VerticalSlidingDrawerBaseActivity {

    @BindView(R.id.most_played_recycer_view) RecyclerView mostPlayedRecylcerView;
    @BindView (R.id.root_layout) FrameLayout rootLayout;
    @BindView(R.id.fragment_container) FrameLayout fragmentContainer;

    private SongsArrayList songsList;
    private UnifedSongAdapter MostPlayedActivityrecyclerViewAdapter;
    private Menu mOptionsMenu;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_most_played_layout);
        ButterKnife.bind(this);

        ActivityHelper.setBackButtonToCustomToolbarBar(MostPlayedSongsActivity.this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome(this);

        final MaterialDialog loading = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_populate_list_text)
                .progress(true, 0)
                .show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                songsList = new SongsArrayList(AudioExtensionMethods.getMostPlayedSongsList(MostPlayedSongsActivity.this)) {
                    @Override
                    public void notifyDataSetChanged() {
                        if(MostPlayedActivityrecyclerViewAdapter != null)
                            MostPlayedActivityrecyclerViewAdapter.notifyDataSetChanged();
                    }
                    public void notifyItemRemoved(int index) {
                        if(MostPlayedActivityrecyclerViewAdapter != null)
                            MostPlayedActivityrecyclerViewAdapter.notifyItemRemoved(index);
                    }
                    public void notifyItemInserted(int index) {
                        if(MostPlayedActivityrecyclerViewAdapter != null)
                            MostPlayedActivityrecyclerViewAdapter.notifyItemInserted(index);
                    }
                    public void notifyItemChanged(int index) {
                        if(MostPlayedActivityrecyclerViewAdapter != null)
                            MostPlayedActivityrecyclerViewAdapter.notifyItemChanged(index);
                    }
                };
                if(songsList.size() == 0){
                    ActivityHelper.showEmptyFragment(MostPlayedSongsActivity.this, getResources().getString(R.string.no_records_most_played), fragmentContainer);
                    loading.dismiss();
                }


                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MostPlayedActivityrecyclerViewAdapter = new UnifedSongAdapter(MostPlayedSongsActivity.this, songsList);

                            if (!ExtensionMethods.isTablet(MostPlayedSongsActivity.this))
                            {
                                if(!ExtensionMethods.isLandScape(MostPlayedSongsActivity.this))    //Mobile Portrait
                                    mostPlayedRecylcerView.setLayoutManager(new LinearLayoutManager(MostPlayedSongsActivity.this));

                                if(ExtensionMethods.isLandScape(MostPlayedSongsActivity.this))    //Mobile Landscape
                                    mostPlayedRecylcerView.setLayoutManager(new GridLayoutManager(MostPlayedSongsActivity.this, 2, GridLayoutManager.VERTICAL, false));
                            }

                            else{
                                if(!ExtensionMethods.isLandScape(MostPlayedSongsActivity.this))    //Tablet Portrait
                                    mostPlayedRecylcerView.setLayoutManager(new GridLayoutManager(MostPlayedSongsActivity.this, 2, GridLayoutManager.VERTICAL, false));

                                if(ExtensionMethods.isLandScape(MostPlayedSongsActivity.this))    //Tablet Landscape
                                    mostPlayedRecylcerView.setLayoutManager(new GridLayoutManager(MostPlayedSongsActivity.this, 3, GridLayoutManager.VERTICAL, false));
                            }

                            mostPlayedRecylcerView.setAdapter(MostPlayedActivityrecyclerViewAdapter);
                            mostPlayedRecylcerView.addItemDecoration(new DividerItemDecoration(MostPlayedSongsActivity.this, LinearLayoutManager.VERTICAL));
                            loading.dismiss();
                            ActivityHelper.setShuffleFAB(MostPlayedSongsActivity.this, rootLayout, mostPlayedRecylcerView, songsList);
                            setSearchListener();
                        }
                    });
                }
            }
        }).start();

    }
    public void onDestroy() {
        super.onDestroy();
        mostPlayedRecylcerView.setAdapter(null);
        MostPlayedActivityrecyclerViewAdapter = null;
        songsList = null;
    }

    private void setSearchListener() {
        MenuItem searchItem = mOptionsMenu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setActionView(searchItem, searchView);
        SongsSearchListener searchListener = new SongsSearchListener(MostPlayedSongsActivity.this, songsList, mostPlayedRecylcerView, MostPlayedActivityrecyclerViewAdapter);
        searchView.setOnQueryTextListener(searchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_most_played_menu, menu);
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
                ActivitySwitcher.initEqualizer(MostPlayedSongsActivity.this);
                break;

            case R.id.action_quick_share:
                ActivitySwitcher.jumpToQuickShareActivity(MostPlayedSongsActivity.this, songsList);
                break;

            case R.id.action_share_playlist:
                ShareIntentHelper.sharePlaylist(MostPlayedSongsActivity.this, songsList, "Most Played");
                break;

            case R.id.action_add_to:
                DialogHelper.AddToDialogPlaylist(MostPlayedSongsActivity.this, songsList);
                break;

            case R.id.action_play_next:
                MusicPlayerExtensionMethods.playNext(MostPlayedSongsActivity.this, songsList, "Playlist will be played next");
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(MostPlayedSongsActivity.this);
                break;
        }
        return true;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityHelper.onActivityResultMethod(this, requestCode, resultCode, data, songsList);
    }
}