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
import android.view.View;
import android.widget.ImageView;

import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.CustomPlaylistSongsRecylcerViewAdapter;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.BlurBuilder;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.sharedClasses.DividerItemDecoration;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods.getAlbumArtsForPlaylistCover;

/*
 * Created by Prasadam Saiteja on 5/30/2016.
 */

public class CustomPlaylistInnerActivity extends Activity{

    private String playlistName;
    private CustomPlaylistSongsRecylcerViewAdapter customPlaylistSongsRecylcerViewAdapter;
    @Bind (R.id.custom_playlist_inner_recyler_view) RecyclerView customPlaylistInnerRecyclerView;
    @Bind(R.id.background_image_view) ImageView blurredBackgroundImageView;
    @Bind (R.id.album_art_image_view1) ImageView albumartImageView1;
    @Bind (R.id.album_art_image_view2) ImageView albumartImageView2;
    @Bind (R.id.album_art_image_view3) ImageView albumartImageView3;
    @Bind (R.id.album_art_image_view4) ImageView albumartImageView4;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_custom_playlist_inner_layout);
        ButterKnife.bind(this);

        playlistName = getIntent().getExtras().getString("playlistName");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle(playlistName);

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

        int songCount = AudioExtensionMethods.getPlaylistSongCount(this, playlistName);

        if(songCount == 0){

        }

        else{
            ArrayList<String> albumArtPathList = getAlbumArtsForPlaylistCover(this, playlistName);
            setAlbumArt(albumArtPathList);

            ArrayList<Song> songsList = AudioExtensionMethods.getSongsListFromCustomPlaylist(this, playlistName);
            customPlaylistSongsRecylcerViewAdapter = new CustomPlaylistSongsRecylcerViewAdapter(this, songsList);

            customPlaylistInnerRecyclerView.setAdapter(customPlaylistSongsRecylcerViewAdapter);
            customPlaylistInnerRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

            if (!ExtensionMethods.isTablet(this))
            {
                if(!ExtensionMethods.isLandScape(this))    //Mobile Portrait
                    customPlaylistInnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

                if(ExtensionMethods.isLandScape(this))    //Mobile Landscape
                    customPlaylistInnerRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
            }

            else{
                if(!ExtensionMethods.isLandScape(this))    //Tablet Portrait
                    customPlaylistInnerRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));

                if(ExtensionMethods.isLandScape(this))    //Tablet Landscape
                    customPlaylistInnerRecyclerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
            }
        }


    }

    private void setAlbumArt(ArrayList<String> albumArtPathList) {

        if(albumArtPathList.size() > 0){
            String albumArtPath = albumArtPathList.get(0);
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists()){
                    albumartImageView1.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                    blurredBackgroundImageView.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) albumartImageView1.getDrawable()).getBitmap()));
                }
            }
        }

        if(albumArtPathList.size() > 1){
            String albumArtPath = albumArtPathList.get(1);
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists())
                    albumartImageView2.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
            }
        }

        if(albumArtPathList.size() > 2){
            String albumArtPath = albumArtPathList.get(2);
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists())
                    albumartImageView3.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
            }
        }

        if(albumArtPathList.size() > 3){
            String albumArtPath = albumArtPathList.get(3);
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists())
                    albumartImageView4.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
            }
        }
    }
}
