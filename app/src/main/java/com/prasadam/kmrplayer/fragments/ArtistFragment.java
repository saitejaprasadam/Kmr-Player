package com.prasadam.kmrplayer.Fragments;

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

import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.ArtistRecyclerViewAdapter;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.HidingScrollListener;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class ArtistFragment extends Fragment {

    private FastScrollRecyclerView recyclerView;
    public static ArtistRecyclerViewAdapter recyclerViewAdapter;
    private Activity mActivity;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_layout, container, false);
        recyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.artist_recylcer_view_layout);
        setScrollListener();
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

        new Thread() {
            public void run() {

                AudioExtensionMethods.updateArtistList(mActivity.getBaseContext());

                if(!SharedVariables.fullArtistList.isEmpty())
                {
                    recyclerViewAdapter = new ArtistRecyclerViewAdapter(mActivity, mActivity.getBaseContext());

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
                {
                    NoItemsFragment newFragment = new NoItemsFragment();
                    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                    ft.add(android.R.id.content, newFragment).commit();
                    newFragment.setDescriptionTextView("You don't have any artist yet...");
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

    public static void updateList() {
        try{
            if(recyclerViewAdapter != null && SharedVariables.globalActivityContext != null && SharedVariables.globalActivityContext.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_MAIN))
                recyclerViewAdapter.notifyDataSetChanged();
        }
        catch (Exception ignore){}
    }
}