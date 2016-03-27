package com.prasadam.smartcast.audioPackages.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prasadam.smartcast.R;
import com.prasadam.smartcast.adapterClasses.ObservableScrollViewAdapter;
import com.prasadam.smartcast.audioPackages.AdditionalAudioPackageMethods;
import com.prasadam.smartcast.adapterClasses.SongRecyclerViewAdapter;
import com.prasadam.smartcast.audioPackages.Album;
import com.prasadam.smartcast.audioPackages.Song;
import com.prasadam.smartcast.commonClasses.DividerItemDecoration;
import com.prasadam.smartcast.commonClasses.mediaController;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
/**
 * Created by Prasadam Saiteja on 3/7/2016.
 */
public class SongsFragment extends Fragment{

    private ArrayList<Song> songList;
    private SongRecyclerViewAdapter recyclerViewAdapter;
    private ObservableRecyclerView recyclerView;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.songs_recylcer_view_layout);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(){
            public void run(){
                songList = new ArrayList<Song>();
                AdditionalAudioPackageMethods.getSongList(getActivity(), songList);
                Collections.sort(songList, new Comparator<Song>() {

                    public int compare(Song s1, Song s2) {
                        return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
                    }
                });

                mediaController.music.Initializer(getActivity());
                recyclerViewAdapter = new SongRecyclerViewAdapter(getActivity(), songList);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setScrollViewCallbacks(new ObservableScrollViewAdapter(getActivity()));

                        DragScrollBar materialScrollBar = new DragScrollBar(getActivity(), recyclerView, false);
                        materialScrollBar.addIndicator(new AlphabetIndicator(getActivity()), true);
;                    }
                });
            }
        }.start();

    }
}
