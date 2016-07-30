package com.prasadam.kmrplayer;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.CustomPlaylistRecyclerViewAdapter;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.Fragments.NoItemsFragment;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods.createNewCustomPlaylist;
import static com.prasadam.kmrplayer.SharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 5/29/2016.
 */

public class CustomPlaylistActivity extends AppCompatActivity {

    @Bind(R.id.custom_playlist_recycler_view) RecyclerView recyclerView;
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

        setStatusBarTranslucent(CustomPlaylistActivity.this);
        ActivityHelper.setDisplayHome(this);

        ArrayList<String> playlistNames = AudioExtensionMethods.getCustomPlaylistNames(this);

        if(playlistNames.size() == 0)
        {
            NoItemsFragment newFragment = new NoItemsFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(android.R.id.content, newFragment).commit();
            newFragment.setDescriptionTextView("You don't have any playlists...");
        }

        else{
            CustomPlaylistRecyclerViewAdapter recyclerViewAdapter = new CustomPlaylistRecyclerViewAdapter(this, playlistNames);
            recyclerView.setAdapter(recyclerViewAdapter);

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
        }
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
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
