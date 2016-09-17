package com.prasadam.kmrplayer.ListenerClasses;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.SongsAdapter.UnifedSongAdapter;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 5/30/2016.
 */

public class SongsSearchListener implements SearchView.OnQueryTextListener {

    private Context context;
    private ArrayList<Song> songsList;
    private RecyclerView recyclerView;
    private UnifedSongAdapter recRecyclerViewAdapter;

    public SongsSearchListener(Context context, ArrayList<Song> songsList, RecyclerView recyclerView, UnifedSongAdapter recyclerViewAdapter) {
        this.context = context;
        this.songsList = songsList;
        this.recyclerView = recyclerView;
        this.recRecyclerViewAdapter = recyclerViewAdapter;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }
    public boolean onQueryTextChange(String searchString) {
        searchString = searchString.toLowerCase();
        final SongsArrayList filteredList = new SongsArrayList() {
            @Override
            public void notifyDataSetChanged() {

            }

            @Override
            public void notifyItemRemoved(int index) {

            }

            @Override
            public void notifyItemInserted(int index) {

            }

            @Override
            public void notifyItemChanged(int index) {

            }
        };

        for (Song song : songsList) {
            if(song.getTitle().toLowerCase().contains(searchString))
                filteredList.add(song);
        }

        Activity applicationContext = (Activity) context;
        recRecyclerViewAdapter = new UnifedSongAdapter(applicationContext, filteredList);
        recyclerView.setAdapter(recRecyclerViewAdapter);

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
