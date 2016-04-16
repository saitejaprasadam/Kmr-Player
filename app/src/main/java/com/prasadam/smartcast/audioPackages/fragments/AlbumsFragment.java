package com.prasadam.smartcast.audioPackages.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.prasadam.smartcast.R;
import com.prasadam.smartcast.adapterClasses.AlbumRecyclerViewAdapter;
import com.prasadam.smartcast.adapterClasses.ObservableScrollViewAdapter;
import com.prasadam.smartcast.audioPackages.Album;
import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.commonClasses.ExtensionMethods;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * Created by Prasadam Saiteja on 3/25/2016.
 */
public class AlbumsFragment extends Fragment {

    private ObservableRecyclerView recyclerView;
    private AlbumRecyclerViewAdapter recyclerViewAdapter;
    private Activity mActivity;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album_list, container, false);
        recyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.album_recylcer_view_layout);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(){
            public void run() {
                Log.d("context", String.valueOf(getContext()));
                ArrayList<Album> albumArrayList = AudioExtensionMethods.getAlbumList(mActivity.getBaseContext());
                Collections.sort(albumArrayList, new Comparator<Album>() {
                    public int compare(Album s1, Album s2) {

                        if(ExtensionMethods.stringIsEmptyorNull(s1.getTitle()) || ExtensionMethods.stringIsEmptyorNull(s2.getTitle()))
                            return 1;

                        return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
                    }
                });

                recyclerViewAdapter = new AlbumRecyclerViewAdapter(mActivity, mActivity.getBaseContext(), albumArrayList);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(recyclerViewAdapter);

                        if (!ExtensionMethods.isTablet(mActivity.getBaseContext())){
                            if(!ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Mobile Portrait
                                recyclerView.setLayoutManager(new GridLayoutManager(mActivity.getBaseContext(), 2, GridLayoutManager.VERTICAL, false));

                            if(ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Mobile Landscape
                                recyclerView.setLayoutManager(new GridLayoutManager(mActivity.getBaseContext(), 4, GridLayoutManager.VERTICAL, false));
                        }

                        else{
                            if(!ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Tablet Portrait
                                recyclerView.setLayoutManager(new GridLayoutManager(mActivity.getBaseContext(), 4, GridLayoutManager.VERTICAL, false));

                            if(ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Tablet Landscape
                                recyclerView.setLayoutManager(new GridLayoutManager(mActivity.getBaseContext(), 6, GridLayoutManager.VERTICAL, false));
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
