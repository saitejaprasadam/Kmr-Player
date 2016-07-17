package com.prasadam.kmrplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class SongSharingActivity extends AppCompatActivity{

    @Bind(R.id.song_sharing_recycler_view) RecyclerView songSharingRecyclerView;

    public SongSharingActivity(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_song_sharing_layout);
        ButterKnife.bind(this);
    }
}
