package com.prasadam.kmrplayer.UI.Fragments;

import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.SongsAdapter.SongsAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.ListenerClasses.HidingScrollListener;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/*
 * Created by Prasadam Saiteja on 3/7/2016.
 */

public class SongsFragment extends Fragment {

    public  static SongsAdapter recyclerViewAdapter;
    private FastScrollRecyclerView recyclerView;
    private Activity mActivity;
    private FloatingActionButton shuffleButton;
    private FrameLayout fragmentContainer;
    private View rootView;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.songs_recylcer_view_layout);
        fragmentContainer = (FrameLayout) rootView.findViewById(R.id.sg_fragment_container);
        setScrollListener();
        shuffleButton = (FloatingActionButton) rootView.findViewById(R.id.shuffle_fab_button);
        return rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MaterialDialog loading = new MaterialDialog.Builder(mActivity)
                .content(R.string.please_wait_while_we_populate_full_songs_list_text)
                .progress(true, 0)
                .show();

        new Thread(){
            public void run(){

                if(SharedVariables.fullSongsList.size() == 0)
                    AudioExtensionMethods.updateSongList(mActivity);

                if(!SharedVariables.fullSongsList.isEmpty()) {

                    shuffleButton.show();
                    recyclerViewAdapter = new SongsAdapter(mActivity, getContext());

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

                else
                    ActivityHelper.showEmptyFragment(getActivity(), getResources().getString(R.string.no_songs_text), fragmentContainer);

                loading.dismiss();
            }
        }.start();
    }

    private void setScrollListener() {
        recyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                hideShuffleFab();
                if (actionBar != null)
                    actionBar.hide();
            }
            @Override
            public void onShow() {
                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                showShuffleFab();
                if (actionBar != null)
                    actionBar.show();
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
            }
        });
    }
    public static void updateList(){
        try{
            if(recyclerViewAdapter != null)
                recyclerViewAdapter.notifyDataSetChanged();
        }
        catch (Exception ignored){}
    }
    public static void onItemRemoved(int index){
        try{
            if(recyclerViewAdapter != null)
                recyclerViewAdapter.notifyItemRemoved(index);
        }
        catch (Exception ignored){}
    }
    public static void onItemAdded(int index){
        try{
            if(recyclerViewAdapter != null)
                recyclerViewAdapter.notifyItemInserted(index);
        }
        catch (Exception ignored){}
    }
    public static void onItemChanged(int index){
        if(recyclerViewAdapter != null)
            recyclerViewAdapter.notifyItemChanged(index);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_in_songs_fragment, menu);
        ActivityHelper.nearbyDevicesCount(getActivity(), menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    public void onResume(){
        super.onResume();
        fragmentContainer = (FrameLayout) rootView.findViewById(R.id.sg_fragment_container);
    }

    private void hideShuffleFab(){
        shuffleButton.animate().translationY(shuffleButton.getHeight() + 60).setInterpolator(new AccelerateInterpolator(2)).start();
    }
    private void showShuffleFab(){
        shuffleButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }
}