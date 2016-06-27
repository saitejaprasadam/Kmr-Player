package com.prasadam.kmrplayer.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prasadam.kmrplayer.MainActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.SongRecyclerViewAdapter;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;
import com.prasadam.kmrplayer.sharedClasses.DividerItemDecoration;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

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
                        }
                    });

                    checkForDuplicates();
                    setShuffleButtonListener();
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

    private void checkForDuplicates() {
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

    private void setShuffleButtonListener() {
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MusicPlayerExtensionMethods.shufflePlay(mActivity, SharedVariables.fullSongsList);
                shuffleButton.hide();
            }
        });
    }
}