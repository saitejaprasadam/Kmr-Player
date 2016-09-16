package com.prasadam.kmrplayer.UI.Activities.Playlist.PlaylistHelpers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.CustomPlaylistAdapter;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.ListenerClasses.HidingScrollListener;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.UI.Activities.VerticalSlidingDrawerBaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods.createNewCustomPlaylist;

/*
 * Created by Prasadam Saiteja on 5/29/2016.
 */

public class CustomPlaylistActivity extends VerticalSlidingDrawerBaseActivity {

    private CustomPlaylistAdapter CustomPlaylistActivityrecyclerViewAdapter;
    @BindView(R.id.custom_playlist_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_container) FrameLayout fragmentContainer;
    @BindView(R.id.add_new_custom_playlist) FloatingActionButton addNewPlaylistFab;

    @OnClick (R.id.add_new_custom_playlist)
    public void addNewPlaylist(View view){
        new MaterialDialog.Builder(this)
                .title(R.string.enter_playlist_name_text)
                .inputRangeRes(3, 20, R.color.colorAccentGeneric)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(createNewCustomPlaylist(CustomPlaylistActivity.this, String.valueOf(input)))
                            Toast.makeText(CustomPlaylistActivity.this, "playlist created", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(CustomPlaylistActivity.this, "playlist already found", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_custom_playlist_layout);
        ButterKnife.bind(this);

        ActivityHelper.setBackButtonToCustomToolbarBar(CustomPlaylistActivity.this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome(this);

        ArrayList<String> playlistNames = AudioExtensionMethods.getCustomPlaylistNames(this);

        if(playlistNames.size() == 0)
            ActivityHelper.showEmptyFragment(CustomPlaylistActivity.this, "You don't have any playlists...", fragmentContainer);


        else{
            CustomPlaylistActivityrecyclerViewAdapter = new CustomPlaylistAdapter(this, playlistNames);
            recyclerView.setAdapter(CustomPlaylistActivityrecyclerViewAdapter);

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
            recyclerView.setOnScrollListener(new HidingScrollListener() {
                @Override
                public void onHide() {
                    addNewPlaylistFab.animate().translationY(addNewPlaylistFab.getHeight() + 60).setInterpolator(new AccelerateInterpolator(2)).start();
                }

                @Override
                public void onShow() {
                    addNewPlaylistFab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                }
            });
        }
    }
    public void onDestroy(){
        super.onDestroy();
        recyclerView.setAdapter(null);
        CustomPlaylistActivityrecyclerViewAdapter = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_custom_playlist_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        switch (id){

            case android.R.id.home:
                finish();
                break;

            case R.id.action_search:
                ActivitySwitcher.launchSearchActivity(this);
                break;

            case R.id.action_equilzer:
                ActivitySwitcher.initEqualizer(this);
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(this);
                break;
        }
        return true;
    }
}
