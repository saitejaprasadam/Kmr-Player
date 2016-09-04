package com.prasadam.kmrplayer.UI.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.SongsAdapter.AlbumInnerLayoutSongRecyclerViewAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 5/6/2016.
 */

public class AlbumActivity extends VerticalSlidingDrawerBaseActivity {

    private AlbumInnerLayoutSongRecyclerViewAdapter recyclerViewAdapter;
    private String albumTitle, albumArtist, albumartPath = null;
    private Long albumID;
    private SongsArrayList songsList;

    @Bind (R.id.actual_album_art) ImageView actualAlbumArt;
    @Bind (R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind (R.id.album_info_colored_box) RelativeLayout colorBoxLayout;
    @Bind (R.id.Album_name_albumrecyclerview) TextView albumNameTextView;
    @Bind (R.id.Artist_name_albumrecyclerview) TextView artistNameTextView;
    @Bind (R.id.shuffle_fab_button) FloatingActionButton shuffleFabButton;
    @Bind (R.id.songs_recylcer_view_layout) RecyclerView recyclerView;

    @OnClick (R.id.album_info)
    public void artistOnClickListener(View view){
        ActivitySwitcher.jumpToArtist(AlbumActivity.this, albumArtist);
    }

    @OnClick (R.id.actual_album_art)
    public void albumartExpand(View view){
        ActivitySwitcher.ExpandedAlbumArtWithTranscition(AlbumActivity.this, actualAlbumArt, albumartPath);
    }

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_album_layout);
        ButterKnife.bind(this);

        initalize();
        setAlbumArt();
        getSongsList();
    }
    public void onDestroy(){
        super.onDestroy();
        recyclerView.setAdapter(null);
        recyclerViewAdapter = null;
        songsList = null;
    }

    private void initalize() {
        albumID = getIntent().getExtras().getLong("albumID");
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);
        toolbar.inflateMenu(R.menu.activity_album_menu);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_more_vert_white_24dp));
        setToolBarMenuListener(toolbar);

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

    private void setAlbumArt() {
        Cursor musicCursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Albums._ID + "=\"" + albumID + "\"", null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){

            String albumArtPath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            albumTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
            albumNameTextView.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            artistNameTextView.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
            musicCursor.close();
            final Bitmap bitmap;

            if(albumArtPath != null)
            {
                final File imgFile = new File(albumArtPath);
                if(imgFile.exists())// /storage/emulated/0/Android/data/com.android.providers.media/albumthumbs/1454267773223
                {
                    albumartPath = "file://" + imgFile.getAbsolutePath();
                    actualAlbumArt.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                    blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) actualAlbumArt.getDrawable()).getBitmap()));
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                }

                else{
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art);
                    actualAlbumArt.setImageResource(R.mipmap.unkown_album_art);
                    blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) actualAlbumArt.getDrawable()).getBitmap()));
                }
            }

            else{
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art);
                actualAlbumArt.setImageResource(R.mipmap.unkown_album_art);
                blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) actualAlbumArt.getDrawable()).getBitmap()));
            }

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    final int[] colors = ActivityHelper.getAvailableColor(AlbumActivity.this, palette);
                    colorBoxLayout.setBackgroundColor(colors[0]);
                    albumNameTextView.setTextColor(colors[1]);
                    artistNameTextView.setTextColor(colors[2]);
                }
            });
        }
    }
    private void getSongsList() {

        songsList = new SongsArrayList(AudioExtensionMethods.getSongList(this, albumID)) {
            @Override
            public void notifyDataSetChanged() {
                if(recyclerViewAdapter != null)
                    recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void notifyItemRemoved(int index) {
                if(recyclerViewAdapter != null)
                    recyclerViewAdapter.notifyItemRemoved(index);
            }

            @Override
            public void notifyItemInserted(int index) {
                if(recyclerViewAdapter != null)
                    recyclerViewAdapter.notifyItemInserted(index);
            }

            @Override
            public void notifyItemChanged(int index) {
                if(recyclerViewAdapter != null)
                    recyclerViewAdapter.notifyItemChanged(index);
            }
        };
        albumArtist = AudioExtensionMethods.getAlbumArtistTitle(this, albumID);
        recyclerViewAdapter = new AlbumInnerLayoutSongRecyclerViewAdapter(this, albumID, songsList);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(AlbumActivity.this, LinearLayoutManager.VERTICAL));
                recyclerView.setLayoutManager(new LinearLayoutManager(AlbumActivity.this));
            }
        });

        setShuffleOnClick(songsList);
    }
    private void setShuffleOnClick(final ArrayList<Song> songsList) {
        shuffleFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayerExtensionMethods.shufflePlay(AlbumActivity.this, songsList);
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

                        case R.id.album_context_menu_share_album:
                            ShareIntentHelper.shareAlbum(AlbumActivity.this, songsList, albumTitle);
                            break;

                        case R.id.album_context_menu_delete_album:
                            deleteAlbum();
                            break;

                        case R.id.action_devices_button:
                            ActivitySwitcher.jumpToAvaiableDevies(AlbumActivity.this);
                            break;

                        case R.id.action_add_to:
                            DialogHelper.AddToDialogAlbum(AlbumActivity.this, songsList);
                            break;

                        case R.id.action_play_next:
                            MusicPlayerExtensionMethods.playNext(AlbumActivity.this, songsList, "Album will be played next");
                            break;

                        case R.id.album_context_menu_jump_to_artist:
                            ActivitySwitcher.jumpToArtist(AlbumActivity.this, albumArtist);
                            break;

                        case R.id.action_equilzer:
                            ActivitySwitcher.initEqualizer(AlbumActivity.this);
                            break;

                        case R.id.action_quick_share:
                            ActivitySwitcher.jumpToQuickShareActivity(AlbumActivity.this, recyclerViewAdapter.getSongsList());
                            break;

                        default:
                            Toast.makeText(AlbumActivity.this, "pending", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (Exception ignored) {}
                return true;
            }

            private void deleteAlbum() {
                new MaterialDialog.Builder(AlbumActivity.this)
                        .content("Delete this album \'" +  albumTitle + "\' ?")
                        .positiveText(R.string.delete_text)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                for (final Song song : songsList) {
                                    File file = new File(song.getData());
                                    if (file.delete()){
                                        songsList.remove(song);
                                        SharedVariables.fullSongsList.remove(song);
                                        getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + song.getID() + "'", null);
                                    }

                                }

                                Toast.makeText(AlbumActivity.this, "Album Deleted : \'" + albumTitle + "\'", Toast.LENGTH_SHORT).show();
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("albumID", albumID);
                                setResult(RESULT_OK, returnIntent);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                    finishAfterTransition();
                                else
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