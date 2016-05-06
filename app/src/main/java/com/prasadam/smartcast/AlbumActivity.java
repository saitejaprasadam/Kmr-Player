package com.prasadam.smartcast;/*
 * Created by Prasadam Saiteja on 5/6/2016.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.prasadam.smartcast.audioPackages.BlurBuilder;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlbumActivity extends Activity{

    private String albumTitle;
    @Bind(R.id.actual_album_art) ImageView actualAlbumArt;
    @Bind(R.id.blurred_album_art) ImageView blurredAlbumArt;

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_album_layout);
        ButterKnife.bind(this);
        albumTitle = getIntent().getExtras().getString("albumTitle");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
            File imgFile = new File(albumArtPath);
            if(imgFile.exists())// /storage/emulated/0/Android/data/com.android.providers.media/albumthumbs/1454267773223
            {
                actualAlbumArt.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) actualAlbumArt.getDrawable()).getBitmap()));
            }
        }
    }
}
