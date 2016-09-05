package com.prasadam.kmrplayer.UI.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.PermissionHelper;

/*
 * Created by Prasadam saiteja on 3/14/2016.
 */

public class TabFragment extends Fragment{
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View x =  inflater.inflate(R.layout.fragment_songs_tab_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 3){
                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    if(actionBar != null){
                        actionBar.show();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        if (savedInstanceState != null){
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(savedInstanceState.getInt("viewPager_current_item"));
                }
            });
        }
        return x;
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("viewPager_current_item", viewPager.getCurrentItem());
        super.onSaveInstanceState(savedInstanceState);
    }
    class MyAdapter extends FragmentPagerAdapter {

        private SongsFragment songsFragment;
        private AlbumsFragment albumsFragment;
        private PlaylistFragment playlistFragment;
        private ArtistFragment artistFragment;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            songsFragment = new SongsFragment();
            albumsFragment = new AlbumsFragment();
            playlistFragment = new PlaylistFragment();
            artistFragment = new ArtistFragment();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 : return songsFragment;
                case 1 : return albumsFragment;
                case 2 : return artistFragment;
                case 3 : return playlistFragment;
            }
            return null;
        }
        public int getCount() {
            return int_items;
        }
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return getResources().getString(R.string.songs_text);
                case 1 :
                    return getResources().getString(R.string.albums_text);

                case 2 :
                    return getResources().getString(R.string.artist_text);

                case 3:
                    return getResources().getString(R.string.playlist_text);
            }
            return null;
        }
    }
}
