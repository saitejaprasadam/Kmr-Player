package com.prasadam.kmrplayer.audioPackages.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.prasadam.kmrplayer.R;

/*
 * Created by Prasadam saiteja on 3/14/2016.
 */

public class TabFragment extends Fragment{
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x =  inflater.inflate(R.layout.fragment_songs_tab_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return x;
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
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return songsFragment;
                case 1 : return albumsFragment;
                case 2 : return artistFragment;
                case 3 : return playlistFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        @Override
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
