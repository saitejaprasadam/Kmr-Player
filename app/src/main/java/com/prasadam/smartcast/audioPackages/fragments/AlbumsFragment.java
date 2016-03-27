package com.prasadam.smartcast.audioPackages.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.prasadam.smartcast.R;
import com.prasadam.smartcast.adapterClasses.AlbumRecyclerViewAdapter;
import com.prasadam.smartcast.adapterClasses.ObservableScrollViewAdapter;
import com.prasadam.smartcast.audioPackages.AdditionalAudioPackageMethods;
import com.prasadam.smartcast.audioPackages.Album;
import com.prasadam.smartcast.audioPackages.Song;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Prasadam Saiteja on 3/25/2016.
 */
public class AlbumsFragment extends Fragment {

    private ObservableRecyclerView recyclerView;
    private ArrayList<Album> albumArrayList;
    private View rootView;
    private AlbumRecyclerViewAdapter recyclerViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_album_list, container, false);
        recyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.album_recylcer_view_layout);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(){
            public void run() {
                ArrayList<Album> albumArrayList = AdditionalAudioPackageMethods.getAlbumList(getContext());
                Collections.sort(albumArrayList, new Comparator<Album>() {
                    public int compare(Album s1, Album s2) {
                        return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
                    }
                });
                recyclerViewAdapter = new AlbumRecyclerViewAdapter(getActivity(), albumArrayList);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
                        recyclerView.setScrollViewCallbacks(new ObservableScrollViewAdapter(getActivity()));

                        DragScrollBar materialScrollBar = new DragScrollBar(getActivity(), recyclerView, false);
                        materialScrollBar.addIndicator(new AlphabetIndicator(getActivity()), true);
                    }
                });
            }
        }.start();
    }
}
