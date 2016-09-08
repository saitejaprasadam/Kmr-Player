package com.prasadam.kmrplayer.UI.Fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.ArtistAdapter;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.ListenerClasses.HidingScrollListener;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class ArtistFragment extends Fragment {

    private FastScrollRecyclerView recyclerView;
    public static ArtistAdapter recyclerViewAdapter;
    private FrameLayout fragmentContainer;
    private Activity mActivity;
    private View rootView;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist_layout, container, false);
        recyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.artist_recylcer_view_layout);
        fragmentContainer = (FrameLayout) rootView.findViewById(R.id.ar_fragment_container);
        setScrollListener();
        return rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread() {
            public void run() {

                if(SharedVariables.fullArtistList.size() == 0)
                    AudioExtensionMethods.updateArtistList(mActivity.getBaseContext());

                if(!SharedVariables.fullArtistList.isEmpty())
                {
                    recyclerViewAdapter = new ArtistAdapter(mActivity, mActivity.getBaseContext());

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!ExtensionMethods.isTablet(mActivity.getBaseContext())){
                                if(!ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Mobile Portrait
                                    recyclerView.setLayoutManager(new GridLayoutManager(mActivity.getBaseContext(), 3, GridLayoutManager.VERTICAL, false));

                                if(ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Mobile Landscape
                                    recyclerView.setLayoutManager(new GridLayoutManager(mActivity.getBaseContext(), 5, GridLayoutManager.VERTICAL, false));
                            }

                            else{
                                if(!ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Tablet Portrait
                                    recyclerView.setLayoutManager(new GridLayoutManager(mActivity.getBaseContext(), 6, GridLayoutManager.VERTICAL, false));

                                if(ExtensionMethods.isLandScape(mActivity.getBaseContext()))    //Tablet Landscape
                                    recyclerView.setLayoutManager(new GridLayoutManager(mActivity.getBaseContext(), 7, GridLayoutManager.VERTICAL, false));
                            }

                            recyclerView.setAdapter(recyclerViewAdapter);
                        }
                    });
                }

                else
                    ActivityHelper.showEmptyFragment(getActivity(), getResources().getString(R.string.no_artist_text), fragmentContainer);
            }
        }.start();
    }
    public void onResume(){
        super.onResume();
        fragmentContainer = (FrameLayout) rootView.findViewById(R.id.ar_fragment_container);
    }
    private void setScrollListener() {
        recyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                if (actionBar != null)
                    actionBar.hide();

            }
            @Override
            public void onShow() {
                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                if (actionBar != null)
                    actionBar.show();
            }
        });
    }

    public static void updateList() {
        try{
            if(recyclerViewAdapter != null)
                recyclerViewAdapter.notifyDataSetChanged();
        }
        catch (Exception ignore){}
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
}