package com.prasadam.kmrplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.SmallAlbumRecyclerViewAdapter;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.SongRecyclerViewAdapterForArtistActivity;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Album;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Artist;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.AudioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class ArtistActivity extends VerticalSlidingDrawerBaseActivity {

    public static String ARTIST_EXTRA = "artist";
    private Artist artist;
    private ArrayList<Song> songsList;
    private FrameLayout colorPaletteView;
    private String artistAlbumArt = null;

    @Bind(R.id.artist_image) ImageView artistAlbumArtImageView;
    @Bind(R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind(R.id.artist_title) TextView artistTitle;
    @Bind(R.id.album_count_text_view) TextView albumCountTextview;
    @Bind(R.id.song_count_text_view) TextView songCountTextview;
    @Bind(R.id.songs_recycler_view_artist_activity) RecyclerView songRecyclerview;
    @Bind(R.id.albums_recycler_view_artist_activity) RecyclerView albumRecyclerview;

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

        artistTitle.setSelected(true);
        colorPaletteView = (FrameLayout) findViewById(R.id.color_pallete_view);

        initalizer();
        setData();
        setAlbumRecyclerView();
        setSongRecyclerView();
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }

    private void setSongRecyclerView() {

        songsList = AudioExtensionMethods.getSongListFromArtist(ArtistActivity.this, artist.getArtistTitle());
        SongRecyclerViewAdapterForArtistActivity songRecyclerViewAdapter = new SongRecyclerViewAdapterForArtistActivity(ArtistActivity.this, songsList);
        songRecyclerview.setLayoutManager(new LinearLayoutManager(ArtistActivity.this));
        songRecyclerview.setAdapter(songRecyclerViewAdapter);
        songRecyclerview.addItemDecoration(new DividerItemDecoration(ArtistActivity.this, LinearLayoutManager.VERTICAL));
    }
    private void setAlbumRecyclerView() {

        ArrayList<Album> albumList = AudioExtensionMethods.getAlbumListFromArtist(ArtistActivity.this, artist.getArtistTitle());
        SmallAlbumRecyclerViewAdapter albumRecyclerViewAdapter = new SmallAlbumRecyclerViewAdapter(ArtistActivity.this, this, albumList);
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
        toolbar.inflateMenu(R.menu.activity_artist_menu);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_more_vert_white_24dp));
        setToolBarMenuListener(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

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

    private void setToolBarMenuListener(Toolbar toolbar) {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    int id = item.getItemId();
                    switch (id) {

                        case R.id.action_share_all_songs_from_artist:
                            ShareIntentHelper.shareAllSongsFromCurrentArtist(ArtistActivity.this, songsList, artist.getArtistTitle());
                            break;

                        case R.id.action_search:
                            ActivitySwitcher.launchSearchActivity(ArtistActivity.this);
                            break;

                        case R.id.action_play_next:
                            MusicPlayerExtensionMethods.playNext(ArtistActivity.this, songsList, "Artist songs will be played next");
                            break;

                        case R.id.action_add_to:
                            DialogHelper.AddToDialogArtist(ArtistActivity.this, songsList);
                            break;

                        case R.id.action_equilzer:
                            ActivitySwitcher.initEqualizer(ArtistActivity.this);
                            break;

                        case R.id.action_devices_button:
                            ActivitySwitcher.jumpToAvaiableDevies(ArtistActivity.this);
                            break;

                        case R.id.action_quick_share:
                            ActivitySwitcher.jumpToQuickShareActivity(ArtistActivity.this, songsList);
                            break;

                        default:
                            Toast.makeText(ArtistActivity.this, "pending", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (Exception ignored) {}
                return true;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
