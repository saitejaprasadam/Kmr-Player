package com.prasadam.kmrplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prasadam.kmrplayer.activityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.SmallAlbumRecyclerViewAdapter;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.SongRecyclerViewAdapterForArtistActivity;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.BlurBuilder;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Album;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Artist;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.DividerItemDecoration;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class ArtistActivity extends Activity {

    public static String ARTIST_EXTRA = "artist";
    private Artist artist;
    private ArrayList<Song> songsList;
    private ArrayList<Album> albumList;
    private FrameLayout colorPaletteView;
    private static String artistAlbumArt = null;
    private SongRecyclerViewAdapterForArtistActivity songRecyclerViewAdapter;
    private SmallAlbumRecyclerViewAdapter albumRecyclerViewAdapter;

    @Bind(R.id.artist_image) ImageView artistAlbumArtImageView;
    @Bind(R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind(R.id.artist_title) TextView artistTitle;
    @Bind(R.id.album_count_text_view) TextView albumCountTextview;
    @Bind(R.id.song_count_text_view) TextView songCountTextview;
    @Bind(R.id.songs_recycler_view_artist_activity) RecyclerView songRecyclerview;
    @Bind(R.id.albums_recycler_view_artist_activity) RecyclerView albumRecyclerview;

    @OnClick(R.id.activity_options_menu)
    public void ActivityOptionsMenuOnClick(View view){
        final PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.activity_artist_options_menu);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    int id = item.getItemId();
                    switch (id) {

                        case R.id.share_all_songs_from_artist:
                            AudioExtensionMethods.shareAllSongsFromCurrentArtist(ArtistActivity.this, songsList, artist.getArtistTitle());
                            break;

                        case R.id.action_equilzer:
                            ActivitySwitcher.initEqualizer(ArtistActivity.this);
                            break;

                        default:
                            Toast.makeText(ArtistActivity.this, "pending", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (Exception ignored) {}

                return true;
            }
        });

        popup.show();
    }

    @OnClick(R.id.shuffle_fab_button)
    public void ShuffleOnClickListener(View view){
        MusicPlayerExtensionMethods.shufflePlay(ArtistActivity.this, songsList);
    }

    @OnClick (R.id.artist_image)
    public void albumartExpand(View view){
        ActivitySwitcher.ExpandedAlbumArtWithTranscition(ArtistActivity.this, artistAlbumArtImageView, artistAlbumArt);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_artist_layout);
        ButterKnife.bind(this);

        colorPaletteView = (FrameLayout) findViewById(R.id.color_pallete_view);
        initalizer();
        setData();
        setAlbumRecyclerView();
        setSongRecyclerView();
    }

    private void setSongRecyclerView() {

        songsList = AudioExtensionMethods.getSongListFromArtist(ArtistActivity.this, artist.getArtistTitle());
        songRecyclerViewAdapter = new SongRecyclerViewAdapterForArtistActivity(ArtistActivity.this, songsList);
        songRecyclerview.setLayoutManager(new LinearLayoutManager(ArtistActivity.this));
        songRecyclerview.setAdapter(songRecyclerViewAdapter);
        songRecyclerview.addItemDecoration(new DividerItemDecoration(ArtistActivity.this, LinearLayoutManager.VERTICAL));
    }
    private void setAlbumRecyclerView() {

        albumList = AudioExtensionMethods.getAlbumListFromArtist(ArtistActivity.this, artist.getArtistTitle());
        albumRecyclerViewAdapter = new SmallAlbumRecyclerViewAdapter(ArtistActivity.this, this, albumList);
        albumRecyclerview.setLayoutManager(new LinearLayoutManager(ArtistActivity.this, LinearLayoutManager.HORIZONTAL, false));
        albumRecyclerview.setAdapter(albumRecyclerViewAdapter);
    }
    private void setData() {

        try{
            String artistName = getIntent().getExtras().getString(ARTIST_EXTRA);
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
                    artistAlbumArt = "file://" + imgFile.getAbsolutePath();
                    blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) artistAlbumArtImageView.getDrawable()).getBitmap()));
                    if(colorPaletteView != null)
                        setColorPalette(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));


                }

                else{
                    artistAlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
                    blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) artistAlbumArtImageView.getDrawable()).getBitmap()));
                    if(colorPaletteView != null)
                        setColorPalette(BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art));
                }

            }
            else {
                artistAlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
                blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) artistAlbumArtImageView.getDrawable()).getBitmap()));
                if(colorPaletteView != null)
                    setColorPalette(BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art));
            }
        }

        catch (Exception ignored){}
    }
    private void setColorPalette(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                if (vibrantSwatch != null){
                    colorPaletteView.setBackgroundColor(vibrantSwatch.getRgb());
                    artistTitle.setTextColor(vibrantSwatch.getBodyTextColor());
                    songCountTextview.setTextColor(vibrantSwatch.getTitleTextColor());
                    albumCountTextview.setTextColor(vibrantSwatch.getTitleTextColor());
                }

                else
                {
                    vibrantSwatch = palette.getMutedSwatch();
                    if (vibrantSwatch != null){
                        colorPaletteView.setBackgroundColor(vibrantSwatch.getRgb());
                        artistTitle.setTextColor(vibrantSwatch.getBodyTextColor());
                        songCountTextview.setTextColor(vibrantSwatch.getTitleTextColor());
                        albumCountTextview.setTextColor(vibrantSwatch.getTitleTextColor());
                    }

                }
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SharedVariables.TAG_EDITOR_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                int position = data.getExtras().getInt("songPosition");
                songsList = AudioExtensionMethods.getSongListFromArtist(ArtistActivity.this, artist.getArtistTitle());
                songRecyclerViewAdapter.setSongsList(songsList);
                albumList = AudioExtensionMethods.getAlbumListFromArtist(ArtistActivity.this, artist.getArtistTitle());
                albumRecyclerViewAdapter.setAlbumList(albumList);
                songRecyclerViewAdapter.notifyDataSetChanged();
                albumRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
}
