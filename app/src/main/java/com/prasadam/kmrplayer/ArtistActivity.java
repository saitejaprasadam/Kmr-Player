package com.prasadam.kmrplayer;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.ArtistRecyclerViewAdapter;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.RecentlyAddedRecyclerViewAdapter;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.SmallAlbumRecyclerViewAdapter;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.BlurBuilder;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Album;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Artist;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.sharedClasses.DividerItemDecoration;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class ArtistActivity extends Activity {

    @Bind(R.id.artist_image) ImageView artistAlbumArtImageView;
    @Bind(R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind(R.id.artist_title) TextView artistTitle;
    @Bind(R.id.album_count_text_view) TextView albumCountTextview;
    @Bind(R.id.song_count_text_view) TextView songCountTextview;
    @Bind(R.id.songs_recycler_view_artist_activity) RecyclerView songRecyclerview;
    @Bind(R.id.albums_recycler_view_artist_activity) RecyclerView albumRecyclerview;

    private Artist artist;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_artist_layout);
        ButterKnife.bind(this);

        initalizer();
        setData();
        setAlbumRecyclerView();
        setSongRecyclerView();
    }

    private void setSongRecyclerView() {

        ArrayList<Song> songsList = AudioExtensionMethods.getSongListFromArtist(ArtistActivity.this, artist.getArtistTitle());
        final RecentlyAddedRecyclerViewAdapter recyclerViewAdapter = new RecentlyAddedRecyclerViewAdapter(ArtistActivity.this, songsList);

        if (!ExtensionMethods.isTablet(this)) {
            if (!ExtensionMethods.isLandScape(this))    //Mobile Portrait
                songRecyclerview.setLayoutManager(new LinearLayoutManager(ArtistActivity.this));

            if (ExtensionMethods.isLandScape(this))    //Mobile Landscape
                songRecyclerview.setLayoutManager(new GridLayoutManager(ArtistActivity.this, 2, GridLayoutManager.VERTICAL, false));
        } else {
            if (!ExtensionMethods.isLandScape(this))    //Tablet Portrait
                songRecyclerview.setLayoutManager(new GridLayoutManager(ArtistActivity.this, 2, GridLayoutManager.VERTICAL, false));

            if (ExtensionMethods.isLandScape(this))    //Tablet Landscape
                songRecyclerview.setLayoutManager(new GridLayoutManager(ArtistActivity.this, 3, GridLayoutManager.VERTICAL, false));
        }

        songRecyclerview.setAdapter(recyclerViewAdapter);
        songRecyclerview.addItemDecoration(new DividerItemDecoration(ArtistActivity.this, LinearLayoutManager.VERTICAL));
    }

    private void setAlbumRecyclerView() {

        ArrayList<Album> albumList = AudioExtensionMethods.getAlbumListFromArtist(ArtistActivity.this, artist.getArtistTitle());
        final SmallAlbumRecyclerViewAdapter recyclerViewAdapter = new SmallAlbumRecyclerViewAdapter(ArtistActivity.this, this, albumList);
        albumRecyclerview.setLayoutManager(new LinearLayoutManager(ArtistActivity.this, LinearLayoutManager.HORIZONTAL, false));
        albumRecyclerview.setAdapter(recyclerViewAdapter);
    }

    private void setData() {

        String artistName = getIntent().getExtras().getString("artist");
        artist = AudioExtensionMethods.getArtist(ArtistActivity.this, artistName);

        artistTitle.setText(artistName);
        int songCount = Integer.parseInt(artist.getSongCount());
        int albumCount = Integer.parseInt(artist.getAlbumCount());

        if(albumCount > 0){
            if(albumCount == 1)
                albumCountTextview.setText(albumCount + " album");

            else
                albumCountTextview.setText(albumCount + " albums");
        }

        if(songCount > 0){
            if(songCount == 1)
                songCountTextview.setText(songCount + " song");

            else
                songCountTextview.setText(songCount + " songs");
        }


        String albumArtPath = artist.artistAlbumArt;
        if(albumArtPath != null)
        {
            File imgFile = new File(albumArtPath);
            if(imgFile.exists()){
                artistAlbumArtImageView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                /*BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, bitmap));*/
                blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) artistAlbumArtImageView.getDrawable()).getBitmap()));
            }

            else{
                artistAlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
                blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) artistAlbumArtImageView.getDrawable()).getBitmap()));
            }

        }
        else {
            artistAlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
            blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) artistAlbumArtImageView.getDrawable()).getBitmap()));
        }
    }

    private void initalizer() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);

        if (Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        toolbar.setPadding(0, ExtensionMethods.getStatusBarHeight(this), 0, 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();

                else
                    finish();
            }
        });
    }
}
