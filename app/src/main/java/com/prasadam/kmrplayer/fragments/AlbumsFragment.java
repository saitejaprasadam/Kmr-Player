package com.prasadam.kmrplayer.Fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.AlbumRecyclerViewAdapter;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.HidingScrollListener;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/*
 * Created by Prasadam Saiteja on 3/25/2016.
 */
public class AlbumsFragment extends Fragment {

    private FastScrollRecyclerView recyclerView;
    public static AlbumRecyclerViewAdapter recyclerViewAdapter;
    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album_list, container, false);
        recyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.album_recylcer_view_layout);
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

        new Thread(){
            public void run() {

                if(SharedVariables.fullAlbumList.size() == 0)
                    AudioExtensionMethods.updateAlbumList(mActivity.getBaseContext());

                if(!SharedVariables.fullAlbumList.isEmpty())
                {
                    recyclerViewAdapter = new AlbumRecyclerViewAdapter(mActivity, mActivity.getBaseContext());

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
                {
                    NoItemsFragment newFragment = new NoItemsFragment();
                    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                    ft.add(android.R.id.content, newFragment).commit();
                    newFragment.setDescriptionTextView("You don't have any albums yet...");
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
    public static void updateList(){
        try{
            if(recyclerViewAdapter != null && SharedVariables.globalActivityContext != null && SharedVariables.globalActivityContext.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_MAIN))
                recyclerViewAdapter.notifyDataSetChanged();
        }
        catch (Exception ignore){}
    }
}
