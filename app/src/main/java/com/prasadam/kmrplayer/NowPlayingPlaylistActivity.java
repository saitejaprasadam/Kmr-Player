package com.prasadam.kmrplayer;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.NowPlayingPlaylistAdapter;
import com.prasadam.kmrplayer.adapterClasses.uiAdapters.NowPlayingPlaylistInterfaces;
import com.prasadam.kmrplayer.adapterClasses.uiAdapters.SimpleItemTouchHelperCallback;

/*
 * Created by Prasadam Saiteja on 6/30/2016.
 */

public class NowPlayingPlaylistActivity extends AppCompatActivity implements NowPlayingPlaylistInterfaces.OnStartDragListener {

    public static RecyclerView nowPlayingPlaylistRecyclerView;
    private ItemTouchHelper mItemTouchHelper;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_now_playing_playlist);
        nowPlayingPlaylistRecyclerView = (RecyclerView) findViewById(R.id.now_playing_playlist_recycler_view);
        setRecyclerViewAdapter();
    }

    private void setRecyclerViewAdapter() {
        final NowPlayingPlaylistAdapter recyclerViewAdapter = new NowPlayingPlaylistAdapter(this, this);
        nowPlayingPlaylistRecyclerView.setAdapter(recyclerViewAdapter);
        nowPlayingPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(NowPlayingPlaylistActivity.this));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(recyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(nowPlayingPlaylistRecyclerView);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
