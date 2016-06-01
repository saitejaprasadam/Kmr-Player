package com.prasadam.kmrplayer.ListenerClasses;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.FavoritesRecyclerViewAdapter;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.RecentlyAddedRecyclerViewAdapter;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 5/30/2016.
 */

public class SongsSearchListener implements SearchView.OnQueryTextListener {


    private Context context;
    private ArrayList<Song> songsList;
    private RecyclerView recyclerView;
    private RecentlyAddedRecyclerViewAdapter recRecyclerViewAdapter;
    private FavoritesRecyclerViewAdapter favRecyclerViewAdapter;
    private int adapterValue;

    public SongsSearchListener(Context context, ArrayList<Song> songsList, RecyclerView recyclerView, RecentlyAddedRecyclerViewAdapter recyclerViewAdapter) {

        this.context = context;
        this.songsList = songsList;
        this.recyclerView = recyclerView;
        this.recRecyclerViewAdapter = recyclerViewAdapter;
        adapterValue = 1;
    }

    public SongsSearchListener(Context context, ArrayList<Song> songsList, RecyclerView recyclerView, FavoritesRecyclerViewAdapter recyclerViewAdapter) {

        this.context = context;
        this.songsList = songsList;
        this.recyclerView = recyclerView;
        this.favRecyclerViewAdapter = recyclerViewAdapter;
        adapterValue = 2;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String searchString) {
        searchString = searchString.toLowerCase();
        final ArrayList<Song> filteredList = new ArrayList<>();

        for (Song song : songsList) {
            if(song.getTitle().toLowerCase().contains(searchString))
                filteredList.add(song);
        }

        Activity applicationContext = (Activity) context;

        switch (adapterValue)
        {
            case 1:
                recRecyclerViewAdapter = new RecentlyAddedRecyclerViewAdapter(applicationContext, filteredList);
                recyclerView.setAdapter(recRecyclerViewAdapter);
                break;

            case 2:
                favRecyclerViewAdapter = new FavoritesRecyclerViewAdapter(applicationContext, filteredList);
                recyclerView.setAdapter(favRecyclerViewAdapter);
                break;
        }

        //recentlyAddedRecyclerView.addItemDecoration(new DividerItemDecoration(RecentlyAddedActivity.this, LinearLayoutManager.VERTICAL));

        if (!ExtensionMethods.isTablet(applicationContext))
        {
            if(!ExtensionMethods.isLandScape(applicationContext))    //Mobile Portrait
                recyclerView.setLayoutManager(new LinearLayoutManager(applicationContext));

            if(ExtensionMethods.isLandScape(applicationContext))    //Mobile Landscape
                recyclerView.setLayoutManager(new GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false));
        }

        else{
            if(!ExtensionMethods.isLandScape(applicationContext))    //Tablet Portrait
                recyclerView.setLayoutManager(new GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false));

            if(ExtensionMethods.isLandScape(applicationContext))    //Tablet Landscape
                recyclerView.setLayoutManager(new GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false));
        }
        return true;
    }
}
