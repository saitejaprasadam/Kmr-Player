package com.prasadam.smartcast;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prasadam.smartcast.adapterClasses.AlbumInnerLayoutSongRecyclerViewAdapter;
import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.audioPackages.BlurBuilder;
import com.prasadam.smartcast.audioPackages.Song;
import com.prasadam.smartcast.commonClasses.DividerItemDecoration;
import com.prasadam.smartcast.commonClasses.ExtensionMethods;
import com.prasadam.smartcast.commonClasses.mediaController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 5/6/2016.
 */

public class AlbumActivity extends Activity{

    private AlbumInnerLayoutSongRecyclerViewAdapter recyclerViewAdapter;
    private String albumTitle;
    @Bind (R.id.actual_album_art) ImageView actualAlbumArt;
    @Bind (R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind (R.id.album_info_colored_box) RelativeLayout colorBoxLayout;
    @Bind (R.id.Album_name_albumrecyclerview) TextView albumNameTextView;
    @Bind (R.id.Artist_name_albumrecyclerview) TextView artistNameTextView;
    @Bind (R.id.vertical_more_button) ImageView verticalMoreImageView;
    @Bind (R.id.shuffle_fab_button) FloatingActionButton shuffleFabButton;

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
        //ExtensionMethods.setStatusBarTranslucent(true, AlbumActivity.this);
        albumTitle = getIntent().getExtras().getString("albumTitle");
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            toolbar.setPadding(0, ExtensionMethods.getStatusBarHeight(this), 0, 0);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();

                else
                    finish();
            }
        });

        Cursor musicCursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.AlbumColumns.ALBUM + "='" + albumTitle + "'", null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){

            String albumArtPath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            albumNameTextView.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            artistNameTextView.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
            final File imgFile = new File(albumArtPath);
            if(imgFile.exists())// /storage/emulated/0/Android/data/com.android.providers.media/albumthumbs/1454267773223
            {
                actualAlbumArt.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) actualAlbumArt.getDrawable()).getBitmap()));
                final Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
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
                                toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_black_24dp);
                                verticalMoreImageView.setImageResource(R.mipmap.ic_more_vert_black_24dp);
                                artistNameTextView.setTextColor(vibrantSwatch.getTitleTextColor());
                            }
                        }
                    }
                });
            }
        }
        getSongsList();
    }

    private void getSongsList() {
        ArrayList<Song> songList = AudioExtensionMethods.getSongList(this, new ArrayList<Song>(), albumTitle);
        recyclerViewAdapter = new AlbumInnerLayoutSongRecyclerViewAdapter(this, songList);
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

                Collections.shuffle(songsList);
                mediaController.music.musicService.setList(songsList);
                mediaController.music.musicService.setShuffle(true);
                try
                {
                    mediaController.music.musicService.playSong();
                }
                catch (Exception ignored){}
            }
        });
    }
}
