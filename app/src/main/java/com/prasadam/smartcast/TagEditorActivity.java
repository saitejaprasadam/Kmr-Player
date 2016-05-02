package com.prasadam.smartcast;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.prasadam.smartcast.audioPackages.BlurBuilder;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/*
 * Created by Prasadam Saiteja on 5/2/2016.
 */

public class TagEditorActivity extends Activity{

    @Bind (R.id.input_song_title) EditText songTitle;
    @Bind (R.id.input_song_album) EditText songAlbum;
    @Bind (R.id.input_song_artist) EditText songArtist;
    @Bind (R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind (R.id.actual_album_art) ImageView actualAlbumArt;

    @OnClick ({R.id.actual_album_art, R.id.edit_fab_icon})
    public void changeAlbumArt(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivity(intent);
    }

    public void onCreate(Bundle b){

        super.onCreate(b);
        setContentView(R.layout.activity_tag_editor_layout);
        ButterKnife.bind(this);
        String currentSongID = getIntent().getExtras().getString("songID");

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media._ID + " = '" + currentSongID + "'", null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            songTitle.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            songAlbum.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            songArtist.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

            musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            Cursor cursor = musicResolver
                    .query(musicUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?", new String[]{ albumID }, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String albumArtPath = cursor.getString(0);
                    File imgFile = new File(albumArtPath);
                    if(imgFile.exists())// /storage/emulated/0/Android/data/com.android.providers.media/albumthumbs/1454267773223
                    {
                        actualAlbumArt.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                        blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) actualAlbumArt.getDrawable()).getBitmap()));
                    }
                }
                cursor.close();
            }
        }
    }
}
