package com.prasadam.smartcast.audioPackages.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.prasadam.smartcast.R;
import com.prasadam.smartcast.adapterClasses.uiAdapters.ObservableScrollViewAdapter;
import com.prasadam.smartcast.adapterClasses.recyclerViewAdapters.SongRecyclerViewAdapter;
import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.audioPackages.modelClasses.Song;
import com.prasadam.smartcast.audioPackages.musicServiceClasses.MusicService;
import com.prasadam.smartcast.audioPackages.musicServiceClasses.PlayerConstants;
import com.prasadam.smartcast.audioPackages.musicServiceClasses.UtilFunctions;
import com.prasadam.smartcast.sharedClasses.SharedVariables;
import com.prasadam.smartcast.sharedClasses.DividerItemDecoration;
import com.prasadam.smartcast.sharedClasses.ExtensionMethods;
import com.prasadam.smartcast.sharedClasses.mediaController;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * Created by Prasadam Saiteja on 3/7/2016.
 */

public class SongsFragment extends Fragment{

    public static SongRecyclerViewAdapter recyclerViewAdapter;
    private ObservableRecyclerView recyclerView;
    private Activity mActivity;
    private LinearLayout noSongsLayout;
    private FloatingActionButton shuffleButton;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.songs_recylcer_view_layout);
        noSongsLayout = (LinearLayout) rootView.findViewById(R.id.no_songs_view);
        shuffleButton =(FloatingActionButton) rootView.findViewById(R.id.shuffle_fab_button);
        new MaterialFavoriteButton.Builder(getContext()).create();
        return rootView;
    }

    @Override
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(){
            public void run(){

                AudioExtensionMethods.updateSongList(getActivity());

                if(!SharedVariables.fullSongsList.isEmpty()) {

                    recyclerView.setVisibility(View.VISIBLE);
                    noSongsLayout.setVisibility(View.INVISIBLE);
                    shuffleButton.show();

                    shuffleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PlayerConstants.SONG_PAUSED = false;
                            ArrayList<Song> shuffledPlaylist = SharedVariables.fullSongsList;
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

                    //mediaController.music.Initializer(getActivity());
                    recyclerViewAdapter = new SongRecyclerViewAdapter(getContext());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

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

                            recyclerView.setScrollViewCallbacks(new ObservableScrollViewAdapter(getActivity()));

                            DragScrollBar materialScrollBar = new DragScrollBar(getActivity(), recyclerView, false);
                            materialScrollBar.addIndicator(new AlphabetIndicator(getActivity()), true);
                        }
                    });
                }

                else{
                    recyclerView.setVisibility(View.INVISIBLE);
                    noSongsLayout.setVisibility(View.VISIBLE);
                    shuffleButton.setVisibility(View.INVISIBLE);
                }

            }
        }.start();

    }
}