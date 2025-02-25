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
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.AlbumAdapter.AlbumAdapter;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.ListenerClasses.HidingScrollListener;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/*
 * Created by Prasadam Saiteja on 3/25/2016.
 */
public class AlbumsFragment extends Fragment {

    private FastScrollRecyclerView recyclerView;
    public static AlbumAdapter recyclerViewAdapter;
    private Activity mActivity;
    private FrameLayout fragmentContainer;
    private View rootView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_album_list, container, false);
        recyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.album_recylcer_view_layout);
        fragmentContainer = (FrameLayout) rootView.findViewById(R.id.ab_fragment_container);
        setScrollListener();
        return rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(){
            public void run() {

                if(SharedVariables.fullAlbumList.size() == 0)
                    AudioExtensionMethods.updateAlbumList(mActivity.getBaseContext());

                if(!SharedVariables.fullAlbumList.isEmpty())
                {
                    recyclerViewAdapter = new AlbumAdapter(mActivity, mActivity.getBaseContext());

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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

                            recyclerView.setAdapter(recyclerViewAdapter);
                        }
                    });
                }

                else
                    ActivityHelper.showEmptyFragment(getActivity(), getResources().getString(R.string.no_albums_text), fragmentContainer);
            }
        }.start();
    }
    public void onResume(){
        super.onResume();
        fragmentContainer = (FrameLayout) rootView.findViewById(R.id.sg_fragment_container);
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
    public static void updateList(){
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
