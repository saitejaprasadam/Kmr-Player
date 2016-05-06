package com.prasadam.smartcast.audioPackages.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prasadam.smartcast.R;

/*
 * Created by Prasadam saiteja on 3/14/2016.
 */

public class TabFragment extends Fragment{
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x =  inflater.inflate(R.layout.fragment_songs_tab_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

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

        public MyAdapter(FragmentManager fm) {
            super(fm);
            songsFragment = new SongsFragment();
            albumsFragment = new AlbumsFragment();
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return songsFragment;
                case 1 : return albumsFragment;
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
            }
            return null;
        }
    }
}
