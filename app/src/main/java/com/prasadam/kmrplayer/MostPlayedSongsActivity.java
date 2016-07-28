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
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.UnifedRecyclerViewAdapter;
import com.prasadam.kmrplayer.ListenerClasses.SongsSearchListener;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
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

public class MostPlayedSongsActivity extends AppCompatActivity {

    @Bind(R.id.most_played_recycer_view) RecyclerView mostPlayedRecylcerView;
    private ArrayList<Song> songsList;
    private UnifedRecyclerViewAdapter recyclerViewAdapter;
    private Menu mOptionsMenu;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_most_played_layout);
        ButterKnife.bind(this);

        setStatusBarTranslucent(MostPlayedSongsActivity.this);
        ActivityHelper.setDisplayHome(this);

        final MaterialDialog loading = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_populate_list_text)
                .progress(true, 0)
                .show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                songsList = AudioExtensionMethods.getMostPlayedSongsList(MostPlayedSongsActivity.this);
                if(songsList.size() == 0)
                    ActivityHelper.showEmptyFragment(MostPlayedSongsActivity.this, getResources().getString(R.string.no_records_most_played));

                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSearchListener();
                            recyclerViewAdapter = new UnifedRecyclerViewAdapter(MostPlayedSongsActivity.this, songsList);

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

                            mostPlayedRecylcerView.setAdapter(recyclerViewAdapter);
                            mostPlayedRecylcerView.addItemDecoration(new DividerItemDecoration(MostPlayedSongsActivity.this, LinearLayoutManager.VERTICAL));
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
        SongsSearchListener searchListener = new SongsSearchListener(MostPlayedSongsActivity.this, songsList, mostPlayedRecylcerView, recyclerViewAdapter);
        searchView.setOnQueryTextListener(searchListener);
    }

    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
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

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(MostPlayedSongsActivity.this);
                break;

            case R.id.action_pie_chart:
                ActivitySwitcher.launchMostPlayedActivity(this);
                break;
        }
        return true;
    }
}