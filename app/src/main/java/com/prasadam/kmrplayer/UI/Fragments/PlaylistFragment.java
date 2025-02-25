package com.prasadam.kmrplayer.UI.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.UI.Activities.Playlist.ReceivedSongsActivity;
import com.prasadam.kmrplayer.UI.Activities.Playlist.PlaylistHelpers.CustomPlaylistActivity;
import com.prasadam.kmrplayer.UI.Activities.Playlist.FavoritesActivity;
import com.prasadam.kmrplayer.UI.Activities.Playlist.MostPlayedSongsActivity;
import com.prasadam.kmrplayer.UI.Activities.Playlist.RecentlyAddedActivity;
import com.prasadam.kmrplayer.UI.Activities.Playlist.SongPlaybackHistoryActivity;

/*
 * Created by Prasadam Saiteja on 5/18/2016.
 */

public class PlaylistFragment extends Fragment{

    private LinearLayout recentlyAddedPlaylistLinearLayout, favoritesPlaylistLinearLayout,
            songsPlaybackHistoryLinearLayout, mostPlayedLinearLayout, customPlaylistLinearLayout, receivedSongsLinearLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        initFragment(rootView);
        return rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addListeners();
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_in_playlist_fragment, menu);
        ActivityHelper.nearbyDevicesCount(getActivity(), menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initFragment(View rootView) {
        recentlyAddedPlaylistLinearLayout = (LinearLayout) rootView.findViewById(R.id.recently_added_playlist);
        favoritesPlaylistLinearLayout = (LinearLayout) rootView.findViewById(R.id.favorites_playlist);
        songsPlaybackHistoryLinearLayout = (LinearLayout) rootView.findViewById(R.id.songs_playback_history);
        mostPlayedLinearLayout = (LinearLayout) rootView.findViewById(R.id.most_played_linear_layout);
        customPlaylistLinearLayout = (LinearLayout) rootView.findViewById(R.id.custom_playlist_linear_layout);
        receivedSongsLinearLayout = (LinearLayout) rootView.findViewById(R.id.received_songs);
    }
    private void addListeners() {

        recentlyAddedPlaylistLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rectenlyAddedIntent = new Intent(getContext(), RecentlyAddedActivity.class);
                startActivity(rectenlyAddedIntent);
            }
        });

        favoritesPlaylistLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent favoritesIntent = new Intent(getContext(), FavoritesActivity.class);
                startActivity(favoritesIntent);
            }
        });

        songsPlaybackHistoryLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(getContext(), SongPlaybackHistoryActivity.class);
                startActivity(historyIntent);
            }
        });

        mostPlayedLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mostPlayedIntent = new Intent(getContext(), MostPlayedSongsActivity.class);
                startActivity(mostPlayedIntent);
            }
        });

        customPlaylistLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customPlaylistIntent = new Intent(getContext(), CustomPlaylistActivity.class);
                startActivity(customPlaylistIntent);
            }
        });

        receivedSongsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent receivedSongsIntent = new Intent(getContext(), ReceivedSongsActivity.class);
                startActivity(receivedSongsIntent);
            }
        });
    }
}
