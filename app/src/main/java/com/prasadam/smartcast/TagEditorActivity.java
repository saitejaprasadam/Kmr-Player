package com.prasadam.smartcast;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
    @Bind (R.id.apply_fab_button) ImageView applyFabButton;

    @OnClick ({R.id.actual_album_art, R.id.edit_fab_button})
    public void changeAlbumArt(View view) {

        new MaterialDialog.Builder(this)
                .items(R.array.change_album_art_materail_dialog_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                    }
                })
                .show();
    }

    @OnClick (R.id.apply_fab_button)
    public void applyChanges(View view){
        Toast.makeText(this, "Pending", Toast.LENGTH_SHORT).show();
    }

    public void onCreate(Bundle b){

        super.onCreate(b);
        setContentView(R.layout.activity_tag_editor_layout);

        ButterKnife.bind(this);
        String currentSongID = getIntent().getExtras().getString("songID");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_clear_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " and " + MediaStore.Audio.Media._ID + " = '" + currentSongID + "'", null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            songTitle.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            songAlbum.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            songArtist.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            String albumID = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

            try{
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
            catch(Exception ignored){}
        }

        checkTagModification();
    }

    private void checkTagModification() {

        songTitle.addTextChangedListener(textWatcher);
        songAlbum.addTextChangedListener(textWatcher);
        songTitle.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            applyFabButton.setVisibility(View.VISIBLE);
        }
    };

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivity(intent);
    }
}
