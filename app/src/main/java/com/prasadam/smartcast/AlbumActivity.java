package com.prasadam.smartcast;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prasadam.smartcast.adapterClasses.AlbumInnerLayoutSongRecyclerViewAdapter;
import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.audioPackages.BlurBuilder;
import com.prasadam.smartcast.audioPackages.Song;
import com.prasadam.smartcast.audioPackages.fragments.AlbumInnerFragment;
import com.prasadam.smartcast.commonClasses.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 5/6/2016.
 */

public class AlbumActivity extends Activity{

    private AlbumInnerLayoutSongRecyclerViewAdapter recyclerViewAdapter;
    @Bind(R.id.actual_album_art) ImageView actualAlbumArt;
    @Bind(R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind(R.id.album_info_colored_box) RelativeLayout colorBoxLayout;
    @Bind(R.id.Album_name_albumrecyclerview) TextView albumNameTextView;
    @Bind(R.id.Artist_name_albumrecyclerview) TextView artistNameTextView;
    @Bind(R.id.vertical_more_button) ImageView verticalMoreImageView;

    @OnClick (R.id.shuffle_fab_button)
    public void shuffleButtonOnClick(View view)
    {
        Toast.makeText(this, "Pending", Toast.LENGTH_SHORT).show();
    }

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_album_layout);
        ButterKnife.bind(this);
        String albumTitle = getIntent().getExtras().getString("albumTitle");
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);

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
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.recycler_view_fragment_layout, new AlbumInnerFragment());
        fragmentTransaction.commit();
        getSongsList();
    }

    private void getSongsList() {
        ArrayList<Song> songList = AudioExtensionMethods.getSongList(this, new ArrayList<Song>());
        recyclerViewAdapter = new AlbumInnerLayoutSongRecyclerViewAdapter(this, songList);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.songs_recylcer_view_layout);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(AlbumActivity.this, LinearLayoutManager.VERTICAL));
                //recyclerView.setLayoutManager(new LinearLayoutManager(AlbumActivity.this));
                //recyclerView.setLayoutManager(new CustomLinearLayoutManager(AlbumActivity.this, 1, false));
            }
        });
    }
}
