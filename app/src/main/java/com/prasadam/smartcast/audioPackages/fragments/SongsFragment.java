package com.prasadam.smartcast.audioPackages.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.prasadam.smartcast.R;
import com.prasadam.smartcast.adapterClasses.ObservableScrollViewAdapter;
import com.prasadam.smartcast.adapterClasses.SongRecyclerViewAdapter;
import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.audioPackages.Song;
import com.prasadam.smartcast.commonClasses.DividerItemDecoration;
import com.prasadam.smartcast.commonClasses.ExtensionMethods;
import com.prasadam.smartcast.commonClasses.mediaController;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/*
 * Created by Prasadam Saiteja on 3/7/2016.
 */
public class SongsFragment extends Fragment{

    private ArrayList<Song> songList;
    private SongRecyclerViewAdapter recyclerViewAdapter;
    private ObservableRecyclerView recyclerView;
    private Activity mActivity;

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
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(){
            public void run(){
                songList = new ArrayList<>();
                AudioExtensionMethods.getSongList(getActivity(), songList);
                Collections.sort(songList, new Comparator<Song>() {

                    public int compare(Song s1, Song s2) {
                        return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
                    }
                });

                mediaController.music.Initializer(getActivity());
                recyclerViewAdapter = new SongRecyclerViewAdapter(getContext(), songList);

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
        }.start();

    }
}
