package com.prasadam.kmrplayer.UI.Activities.HelperActivities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;

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
import org.jaudiotagger.tag.datatype.Artwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/*
 * Created by Prasadam Saiteja on 5/2/2016.
 */

public class TagEditorActivity extends AppCompatActivity {

    @BindView (R.id.input_song_title) EditText songTitle;
    @BindView (R.id.input_song_album) EditText songAlbum;
    @BindView (R.id.input_song_artist) EditText songArtist;
    @BindView (R.id.input_song_year) EditText songYear;
    @BindView (R.id.input_song_genre) EditText songGenre;
    @BindView (R.id.blurred_album_art) ImageView blurredAlbumArt;
    @BindView (R.id.actual_album_art) ImageView actualAlbumArt;
    @BindView (R.id.apply_fab_button) ImageView applyFabButton;

    private int Choose_Image = 3121;
    private String currentSongID, songLocation;
    private String songHashID;
    private File imageFile = null;

    @OnClick ({R.id.actual_album_art, R.id.edit_fab_button})
    public void changeAlbumArt(View view) {

        new MaterialDialog.Builder(this)
                .items(R.array.change_album_art_materail_dialog_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which){

                            case 0:
                                pickImage();
                                break;

                            case 1:
                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH );
                                intent.putExtra(SearchManager.QUERY, songAlbum.getText() + " by " + songArtist.getText());
                                startActivity(intent);
                                break;
                        }
                    }
                })
                .show();
    }

    @OnClick (R.id.apply_fab_button)
    public void applyChanges(View view){

        final MaterialDialog loading = new MaterialDialog.Builder(TagEditorActivity.this)
                .content(R.string.please_wait_finalising_tag_edit)
                .progress(true, 0)
                .show();

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
            if(imageFile != null){
                Artwork artwork = Artwork.createArtworkFromFile(imageFile);
                tag.deleteArtworkField();
                tag.addField(artwork);
                tag.createField(artwork);
                tag.setField(artwork);
            }

            audioFile.commit();
            ExtensionMethods.scanMedia(this, songLocation);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    loading.dismiss();
                    Toast.makeText(TagEditorActivity.this, "Successfully changed", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("songHashID", songHashID);
                    returnIntent.putExtra("songID", currentSongID);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }, 800);
        }

        catch (CannotReadException | IOException | InvalidAudioFrameException | TagException | ReadOnlyFileException | CannotWriteException | IllegalArgumentException e) {
            Toast.makeText(TagEditorActivity.this, "Error changing tag", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            loading.dismiss();
            finish();
        }
    }

    public void onCreate(Bundle b){

        super.onCreate(b);
        setContentView(R.layout.activity_tag_editor_layout);

        ButterKnife.bind(this);
        currentSongID = getIntent().getExtras().getString("songID");
        songHashID = getIntent().getExtras().getString("songHashID");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_clear_white_24dp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        toolbar.setPadding(0, ActivityHelper.getStatusBarHeight(this), 0, 0);
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

    private void checkTagModification() {

        songTitle.addTextChangedListener(textWatcher);
        songAlbum.addTextChangedListener(textWatcher);
        songArtist.addTextChangedListener(textWatcher);
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
        startActivityForResult(intent, Choose_Image);
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Choose_Image && resultCode == Activity.RESULT_OK){
            try {
                InputStream stream = getContentResolver().openInputStream(data.getData());
                imageFile = new File(getRealPathFromURI(data.getData()));
                actualAlbumArt.setImageBitmap(BitmapFactory.decodeStream(stream));
                blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) actualAlbumArt.getDrawable()).getBitmap()));
                applyFabButton.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
