package com.prasadam.smartcast;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.prasadam.smartcast.adapterClasses.recyclerViewAdapters.FavoritesRecyclerViewAdapter;
import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.audioPackages.fragments.NoItemsFragment;
import com.prasadam.smartcast.audioPackages.modelClasses.Song;
import com.prasadam.smartcast.sharedClasses.DividerItemDecoration;
import com.prasadam.smartcast.sharedClasses.ExtensionMethods;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 5/29/2016.
 */

public class MostPlayedSongsActivity extends AppCompatActivity {

    @Bind(R.id.most_played_recycer_view) RecyclerView mostPlayedRecylcerView;
    private ArrayList<Song> songsList;
    private FavoritesRecyclerViewAdapter recyclerViewAdapter;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_most_played_layout);
        ButterKnife.bind(this);

        new MaterialFavoriteButton.Builder(this).create();
        if(getSupportActionBar() != null )
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        songsList = AudioExtensionMethods.getMostPlayedSongsList(this);

        if(songsList.size() == 0)
        {
            NoItemsFragment newFragment = new NoItemsFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(android.R.id.content, newFragment).commit();
            newFragment.setDescriptionTextView(getResources().getString(R.string.no_records_most_played));
        }

        else
        {
            recyclerViewAdapter = new FavoritesRecyclerViewAdapter(this, songsList);

            mostPlayedRecylcerView.setAdapter(recyclerViewAdapter);
            mostPlayedRecylcerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

            if (!ExtensionMethods.isTablet(this))
            {
                if(!ExtensionMethods.isLandScape(this))    //Mobile Portrait
                    mostPlayedRecylcerView.setLayoutManager(new LinearLayoutManager(this));

                if(ExtensionMethods.isLandScape(this))    //Mobile Landscape
                    mostPlayedRecylcerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
            }

            else{
                if(!ExtensionMethods.isLandScape(this))    //Tablet Portrait
                    mostPlayedRecylcerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));

                if(ExtensionMethods.isLandScape(this))    //Tablet Landscape
                    mostPlayedRecylcerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return false;
    }
}
