package com.prasadam.kmrplayer;

import android.app.Activity;
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
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
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
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.AlbumInnerLayoutSongRecyclerViewAdapter;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.BlurBuilder;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.MusicService;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.UtilFunctions;
import com.prasadam.kmrplayer.sharedClasses.DividerItemDecoration;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 5/6/2016.
 */

public class AlbumActivity extends Activity{

    private AlbumInnerLayoutSongRecyclerViewAdapter recyclerViewAdapter;
    private String albumTitle, albumartPath = null;
    private ArrayList<Song> songList;
    @Bind (R.id.actual_album_art) ImageView actualAlbumArt;
    @Bind (R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind (R.id.album_info_colored_box) RelativeLayout colorBoxLayout;
    @Bind (R.id.Album_name_albumrecyclerview) TextView albumNameTextView;
    @Bind (R.id.Artist_name_albumrecyclerview) TextView artistNameTextView;
    @Bind (R.id.shuffle_fab_button) FloatingActionButton shuffleFabButton;

    @OnClick (R.id.actual_album_art)
    public void albumartExpand(View view){

        Intent albumActivityIntent = new Intent(this, ExpandedAlbumartActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, actualAlbumArt, "AlbumArtImageTranscition");
        albumActivityIntent.putExtra("albumArtPath", albumartPath);
        startActivity(albumActivityIntent, options.toBundle());
    }

    @OnClick (R.id.vertical_more_button)
    public void moreOnClickButton(View view){
        final PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.album_item_menu);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                                             @Override
                                             public boolean onMenuItemClick(MenuItem item) {
                                                 try {
                                                     int id = item.getItemId();
                                                     switch (id) {

                                                         case R.id.album_context_menu_share_album:
                                                             AudioExtensionMethods.shareAlbum(AlbumActivity.this, songList, albumTitle);
                                                             break;

                                                         case R.id.album_context_menu_delete_album:
                                                             new MaterialDialog.Builder(AlbumActivity.this)
                                                                     .content("Delete this album \'" +  albumTitle + "\' ?")
                                                                     .positiveText(R.string.delete_text)
                                                                     .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                         @Override
                                                                         public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                                             for (Song song : songList) {
                                                                                 File file = new File(song.getData());
                                                                                 if(file.delete())
                                                                                     getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + song.getID() + "'", null);
                                                                             }
                                                                             AudioExtensionMethods.updateLists(AlbumActivity.this);
                                                                             Toast.makeText(AlbumActivity.this, "Album Deleted : \'" + albumTitle + "\'", Toast.LENGTH_SHORT).show();
                                                                             finish();
                                                                         }
                                                                     })
                                                                     .negativeText(R.string.cancel_text)
                                                                     .show();
                                                             break;

                                                         default:
                                                             Toast.makeText(AlbumActivity.this, "pending", Toast.LENGTH_SHORT).show();
                                                             break;
                                                     }
                                                 } catch (Exception ignored) {}

                                                 return true;
                                             }
                                         });

        popup.show();
    }

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_album_layout);
        ButterKnife.bind(this);

        initalize();
        setAlbumArt();
        getSongsList();
    }

    private void initalize() {
        new MaterialFavoriteButton.Builder(this).create();
        albumTitle = getIntent().getExtras().getString("albumTitle");
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

    private void setAlbumArt() {
        Cursor musicCursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.AlbumColumns.ALBUM + "=\"" + albumTitle + "\"", null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){

            String albumArtPath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            albumNameTextView.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            artistNameTextView.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
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
                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

                    if (vibrantSwatch != null) {
                        colorBoxLayout.setBackgroundColor(vibrantSwatch.getRgb());
                        albumNameTextView.setTextColor(vibrantSwatch.getBodyTextColor());
                        artistNameTextView.setTextColor(vibrantSwatch.getTitleTextColor());
                    }

                    else
                    {
                        vibrantSwatch = palette.getMutedSwatch();
                        if (vibrantSwatch != null) {
                            colorBoxLayout.setBackgroundColor(vibrantSwatch.getRgb());
                            albumNameTextView.setTextColor(vibrantSwatch.getBodyTextColor());
                            //toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_black_24dp);
                            //verticalMoreImageView.setImageResource(R.mipmap.ic_more_vert_black_24dp);
                            artistNameTextView.setTextColor(vibrantSwatch.getTitleTextColor());
                        }
                    }
                }
            });
        }
    }

    private void getSongsList() {

        songList = AudioExtensionMethods.getSongList(this, albumTitle);
        recyclerViewAdapter = new AlbumInnerLayoutSongRecyclerViewAdapter(this, songList, albumTitle);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.songs_recylcer_view_layout);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(AlbumActivity.this, LinearLayoutManager.VERTICAL));
                recyclerView.setLayoutManager(new LinearLayoutManager(AlbumActivity.this));
            }
        });

        setShuffleOnClick(songList);
    }

    private void setShuffleOnClick(final ArrayList<Song> songsList) {
        shuffleFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlayerConstants.SONG_PAUSED = false;
                long seed = System.nanoTime();
                ArrayList<Song> shuffledPlaylist = songsList;
                Collections.shuffle(shuffledPlaylist, new Random(seed));
                PlayerConstants.SONGS_LIST = shuffledPlaylist;
                PlayerConstants.SONG_NUMBER = 0;
                boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), AlbumActivity.this);
                if (!isServiceRunning) {
                    Intent i = new Intent(AlbumActivity.this, MusicService.class);
                    AlbumActivity.this.startService(i);
                } else {
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                }
            }
        });
    }
}
