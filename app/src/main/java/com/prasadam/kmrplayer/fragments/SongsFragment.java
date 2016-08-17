package com.prasadam.kmrplayer.Fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.SongRecyclerViewAdapter;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.HidingScrollListener;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
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
        //setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.songs_recylcer_view_layout);
        setScrollListener();
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

                if(SharedVariables.fullSongsList.size() == 0)
                    AudioExtensionMethods.updateSongList(mActivity);

                if(!SharedVariables.fullSongsList.isEmpty()) {

                    //shuffleButton.show();
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

    private void setScrollListener() {
        recyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                try{
                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    actionBar.hide();
                    actionBar.getCustomView().animate().translationY(actionBar.getHeight()).setDuration(500);
                }
                catch (NullPointerException ignored){}

            }
            @Override
            public void onShow() {
                try{
                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    actionBar.show();
                    actionBar.getCustomView().animate().translationY(0).setDuration(500);
                }
                catch (NullPointerException ignored){}
            }
        });
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
    public static void updateList(){
        try{
            if(recyclerViewAdapter != null && SharedVariables.globalActivityContext != null && SharedVariables.globalActivityContext.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_MAIN))
                recyclerViewAdapter.notifyDataSetChanged();
        }
        catch (Exception ignore){}
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_in_songs_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_sort:
                DialogHelper.songsSortDialog(getContext());
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}