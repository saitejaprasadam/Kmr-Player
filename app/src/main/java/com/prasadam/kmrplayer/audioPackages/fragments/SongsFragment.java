package com.prasadam.kmrplayer.audioPackages.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.SongRecyclerViewAdapter;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.MusicService;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.UtilFunctions;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;
import com.prasadam.kmrplayer.sharedClasses.DividerItemDecoration;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * Created by Prasadam Saiteja on 3/7/2016.
 */

public class SongsFragment extends Fragment {

    public  static SongRecyclerViewAdapter recyclerViewAdapter;
    private FastScrollRecyclerView recyclerView;
    private Activity mActivity;
    private FloatingActionButton shuffleButton;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.songs_recylcer_view_layout);
        shuffleButton = (FloatingActionButton) rootView.findViewById(R.id.shuffle_fab_button);
        new MaterialFavoriteButton.Builder(getContext()).create();
        return rootView;
    }

    public void onResume(){
        super.onResume();
        new Thread() {
            public void run() {
                if (recyclerViewAdapter != null) {
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }.run();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(){
            public void run(){

                AudioExtensionMethods.updateSongList(mActivity);

                if(!SharedVariables.fullSongsList.isEmpty()) {

                    shuffleButton.show();
                    recyclerViewAdapter = new SongRecyclerViewAdapter(mActivity, getContext());

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!ExtensionMethods.isTablet(mActivity.getBaseContext()))
                            {
                                if(!ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Mobile Portrait
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                if(ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Mobile Landscape
                                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
                            }

                            else{
                                if(!ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Tablet Portrait
                                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));

                                if(ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Tablet Landscape
                                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
                            }

                            recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));

                            try{
                                for(int index = 0; index < SharedVariables.fullSongsList.size(); index++){
                                    for (int jndex = index + 1; jndex < SharedVariables.fullSongsList.size(); jndex++)
                                        if(SharedVariables.fullSongsList.get(index).getHashID().equals(SharedVariables.fullSongsList.get(jndex).getHashID())){
                                            Toast.makeText(mActivity, "Duplicates found at " + SharedVariables.fullSongsList.get(index).getTitle(), Toast.LENGTH_SHORT).show();
                                        }
                                }
                            }

                            catch (Exception ignored){}
                        }
                    });

                    shuffleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            PlayerConstants.SONG_PAUSED = false;

                            ArrayList<Song> shuffledPlaylist = new ArrayList<>();
                            for (Song song : SharedVariables.fullSongsList) {
                                shuffledPlaylist.add(song);
                            }

                            long seed = System.nanoTime();
                            Collections.shuffle(shuffledPlaylist, new Random(seed));
                            PlayerConstants.SONGS_LIST = shuffledPlaylist;
                            PlayerConstants.SONG_NUMBER = 0;

                            boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), getContext());
                            if (!isServiceRunning) {
                                Intent i = new Intent(getContext(), MusicService.class);
                                getContext().startService(i);
                            } else {
                                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                            }
                            shuffleButton.hide();
                        }
                    });
                }

                else{
                    NoItemsFragment newFragment = new NoItemsFragment();
                    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                    ft.add(android.R.id.content, newFragment).commit();
                    newFragment.setDescriptionTextView("You don't have any songs yet...");
                    shuffleButton.setVisibility(View.INVISIBLE);
                }

            }
        }.start();
    }
}