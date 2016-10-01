package com.prasadam.kmrplayer.UI.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.AlbumAdapter.SmallAlbumAdapter;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.SongsAdapter.ArtistInnerLayoutSongRecyclerViewAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.ModelClasses.Album;
import com.prasadam.kmrplayer.ModelClasses.Artist;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;
import com.prasadam.kmrplayer.UI.Activities.BaseActivity.VerticalSlidingDrawerBaseActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class ArtistActivity extends VerticalSlidingDrawerBaseActivity {

    private String ARTIST_EXTRA = "artistID";
    private Artist artist;
    private SongsArrayList songsList;
    private ArrayList<Album> albumList;
    private FrameLayout colorPaletteView;
    private String artistAlbumArt = null;
    private ArtistInnerLayoutSongRecyclerViewAdapter songRecyclerViewAdapter;
    private SmallAlbumAdapter albumRecyclerViewAdapter;

    @BindView(R.id.artist_image) ImageView artistAlbumArtImageView;
    @BindView(R.id.blurred_album_art) ImageView blurredAlbumArt;
    @BindView(R.id.artist_title) TextView artistTitle;
    @BindView(R.id.album_count_text_view) TextView albumCountTextview;
    @BindView(R.id.song_count_text_view) TextView songCountTextview;
    @BindView(R.id.songs_recycler_view_artist_activity) RecyclerView songRecyclerview;
    @BindView(R.id.albums_recycler_view_artist_activity) RecyclerView albumRecyclerview;

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

        long artistID = getIntent().getExtras().getLong(ARTIST_EXTRA);
        artist = AudioExtensionMethods.getArtist(ArtistActivity.this, artistID);

        if(artist == null){
            Toast.makeText(this, "Problem fetching artist", Toast.LENGTH_SHORT).show();
            finish();
        }

        initalizer();
        setData();

        final MaterialDialog loading = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_populate_list_text)
                .progress(true, 0)
                .show();

        setAlbumRecyclerView();
        setSongRecyclerView(loading);
    }
    public void onDestroy(){
        songRecyclerview.setAdapter(null);
        albumRecyclerview.setAdapter(null);
        songRecyclerViewAdapter = null;
        albumRecyclerViewAdapter = null;
        songsList.clear();
        albumList.clear();
        super.onDestroy();
    }

    private void setSongRecyclerView(final MaterialDialog loading) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                songsList = new SongsArrayList(AudioExtensionMethods.getSongListFromArtist(ArtistActivity.this, artist.getArtistID())) {
                    @Override
                    public void notifyDataSetChanged() {
                        if(songRecyclerViewAdapter != null)
                            songRecyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void notifyItemRemoved(int index) {
                        if(songRecyclerViewAdapter != null)
                            songRecyclerViewAdapter.notifyItemRemoved(index);
                    }

                    @Override
                    public void notifyItemInserted(int index) {
                        if(songRecyclerViewAdapter != null)
                            songRecyclerViewAdapter.notifyItemInserted(index);
                    }

                    @Override
                    public void notifyItemChanged(int index) {
                        if(songRecyclerViewAdapter != null)
                            songRecyclerViewAdapter.notifyItemChanged(index);
                    }
                };
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        songRecyclerViewAdapter = new ArtistInnerLayoutSongRecyclerViewAdapter(ArtistActivity.this, songsList);
                        songRecyclerview.setLayoutManager(new LinearLayoutManager(ArtistActivity.this));
                        songRecyclerview.setAdapter(songRecyclerViewAdapter);
                        songRecyclerview.addItemDecoration(new DividerItemDecoration(ArtistActivity.this, LinearLayoutManager.VERTICAL));
                        loading.dismiss();
                    }
                });
            }
        }).start();
    }
    private void setAlbumRecyclerView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                albumList = AudioExtensionMethods.getAlbumListFromArtist(ArtistActivity.this, artist.getArtistID());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        albumRecyclerViewAdapter = new SmallAlbumAdapter(ArtistActivity.this, ArtistActivity.this, albumList);
                        albumRecyclerview.setLayoutManager(new LinearLayoutManager(ArtistActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        albumRecyclerview.setAdapter(albumRecyclerViewAdapter);
                    }
                });
            }
        }).start();
    }
    private void setData() {

        try{
            artistTitle.setText(artist.getArtistTitle());
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
        ActivityHelper.nearbyDevicesCount(this, toolbar.getMenu());
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_more_vert_white_24dp));
        setToolBarMenuListener(toolbar);

        toolbar.setPadding(0, ActivityHelper.getStatusBarHeight(this), 0, 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        case R.id.action_quick_share:
                            ActivitySwitcher.jumpToQuickShareActivity(ArtistActivity.this, songsList);
                            break;

                        case R.id.action_share_all_songs_from_artist:
                            ShareIntentHelper.shareAllSongsFromCurrentArtist(ArtistActivity.this, songsList, artist.getArtistTitle());
                            break;

                        case R.id.action_play_next:
                            MusicPlayerExtensionMethods.playNext(ArtistActivity.this, songsList, "Artist songs will be played next");
                            break;

                        case R.id.action_add_to:
                            DialogHelper.AddToDialogArtist(ArtistActivity.this, songsList);
                            break;

                        case R.id.action_delete_artist:
                            deleteArtist();
                            break;

                        case R.id.action_equilzer:
                            ActivitySwitcher.initEqualizer(ArtistActivity.this);
                            break;

                        case R.id.action_devices_button:
                            ActivitySwitcher.jumpToAvaiableDevies(ArtistActivity.this);
                            break;

                        case R.id.action_search:
                            ActivitySwitcher.launchSearchActivity(ArtistActivity.this);
                            break;

                        default:
                            Toast.makeText(ArtistActivity.this, "pending", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (Exception ignored) {}
                return true;
            }

            private void deleteArtist() {
                new MaterialDialog.Builder(ArtistActivity.this)
                        .content("Delete all song from this artist : \'" +  artistTitle.getText() + "\' ?")
                        .positiveText(R.string.delete_text)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                for (final Song song : songsList) {
                                    File file = new File(song.getData());
                                    if (file.delete()){
                                        songsList.remove(song);
                                        SharedVariables.fullSongsList.remove(song);
                                        AudioExtensionMethods.RemoveSongFromContentResolver(ArtistActivity.this, song.getID());
                                    }

                                }

                                Toast.makeText(ArtistActivity.this, "Artist Deleted : \'" + artistTitle.getText() + "\'", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .negativeText(R.string.cancel_text)
                        .show();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityHelper.onActivityResultMethod(this, requestCode, resultCode, data, songsList);
    }
}
