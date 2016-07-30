package com.prasadam.kmrplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/*
 * Created by Prasadam Saiteja on 5/2/2016.
 */

public class TagEditorActivity extends AppCompatActivity {

    @Bind (R.id.input_song_title) EditText songTitle;
    @Bind (R.id.input_song_album) EditText songAlbum;
    @Bind (R.id.input_song_artist) EditText songArtist;
    @Bind (R.id.input_song_year) EditText songYear;
    @Bind (R.id.input_song_genre) EditText songGenre;
    @Bind (R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind (R.id.actual_album_art) ImageView actualAlbumArt;
    @Bind (R.id.apply_fab_button) ImageView applyFabButton;
    private static String currentSongID, songLocation;
    private static int songPosition;

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
        try {
            TagOptionSingleton.getInstance().setAndroid(true);
            final File songFile = new File(songLocation);
            AudioFile audioFile = AudioFileIO.read(songFile);
            Tag tag = audioFile.getTagOrCreateAndSetDefault();
            tag.setField(FieldKey.ARTIST, String.valueOf(songArtist.getText()));
            tag.setField(FieldKey.ALBUM, String.valueOf(songAlbum.getText()));
            tag.setField(FieldKey.TITLE, String.valueOf(songTitle.getText()));
            tag.setField(FieldKey.YEAR, String.valueOf(songYear.getText()));
            tag.setField(FieldKey.GENRE, String.valueOf(songGenre.getText()));
            audioFile.commit();
            ExtensionMethods.scanMedia(TagEditorActivity.this, songLocation);
            Toast.makeText(TagEditorActivity.this, "Successfully changed", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("songPosition", songPosition);
            returnIntent.putExtra("songID", currentSongID);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }

        catch (CannotReadException | IOException | InvalidAudioFrameException | TagException | ReadOnlyFileException | CannotWriteException e) {
            Toast.makeText(TagEditorActivity.this, "Error changing tag", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }

    }

    public void onCreate(Bundle b){

        super.onCreate(b);
        setContentView(R.layout.activity_tag_editor_layout);

        ButterKnife.bind(this);
        currentSongID = getIntent().getExtras().getString("songID");
        songPosition = getIntent().getExtras().getInt("position");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_clear_white_24dp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        toolbar.setPadding(0, ExtensionMethods.getStatusBarHeight(this), 0, 0);
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
            songYear.setText(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.YEAR)));
            int songID = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
            songLocation = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
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

                Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", songID);
                Cursor genresCursor = getContentResolver().query(uri, new String[]{MediaStore.Audio.Genres.NAME}, null, null, null);

                if (genresCursor != null && genresCursor.moveToFirst())
                    songGenre.setText(genresCursor.getString(genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)));
            }
            catch(Exception ignored){}
        }

        checkTagModification();
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }

    private void checkTagModification() {

        songTitle.addTextChangedListener(textWatcher);
        songAlbum.addTextChangedListener(textWatcher);
        songTitle.addTextChangedListener(textWatcher);
        songYear.addTextChangedListener(textWatcher);
        songGenre.addTextChangedListener(textWatcher);
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
