package com.prasadam.smartcast;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.prasadam.smartcast.ListenerClasses.SongsSearchListener;
import com.prasadam.smartcast.adapterClasses.recyclerViewAdapters.FavoritesRecyclerViewAdapter;
import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.audioPackages.modelClasses.Song;
import com.prasadam.smartcast.audioPackages.fragments.NoItemsFragment;
import com.prasadam.smartcast.sharedClasses.DividerItemDecoration;
import com.prasadam.smartcast.sharedClasses.ExtensionMethods;

import java.util.ArrayList;

import static com.prasadam.smartcast.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 5/28/2016.
 */

public class FavoritesActivity extends AppCompatActivity{

    private ArrayList<Song> favoriteSongList;
    private FavoritesRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_favorites_layout);
        recyclerView = (RecyclerView) findViewById(R.id.favorites_recycler_view);

        setStatusBarTranslucent(FavoritesActivity.this);
        if(getSupportActionBar() != null )
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        favoriteSongList = AudioExtensionMethods.getFavoriteSongsList(this);

        if(favoriteSongList.size() == 0)
        {
            NoItemsFragment newFragment = new NoItemsFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(android.R.id.content, newFragment).commit();
            newFragment.setDescriptionTextView("You don't have any favorite songs yet...");
        }

        else{
            recyclerViewAdapter = new FavoritesRecyclerViewAdapter(this, favoriteSongList);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_recently_added_songs_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setActionView(searchItem, searchView);
        SongsSearchListener searchListener = new SongsSearchListener(this, favoriteSongList, recyclerView, recyclerViewAdapter);
        searchView.setOnQueryTextListener(searchListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
