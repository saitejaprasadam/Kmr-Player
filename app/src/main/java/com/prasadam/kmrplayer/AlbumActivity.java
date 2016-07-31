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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.AlbumInnerLayoutSongRecyclerViewAdapter;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.AudioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

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
    private ArrayList<Song> songsList;
    @Bind (R.id.actual_album_art) ImageView actualAlbumArt;
    @Bind (R.id.blurred_album_art) ImageView blurredAlbumArt;
    @Bind (R.id.album_info_colored_box) RelativeLayout colorBoxLayout;
    @Bind (R.id.Album_name_albumrecyclerview) TextView albumNameTextView;
    @Bind (R.id.Artist_name_albumrecyclerview) TextView artistNameTextView;
    @Bind (R.id.shuffle_fab_button) FloatingActionButton shuffleFabButton;

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
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }

    private void initalize() {
        albumTitle = getIntent().getExtras().getString("albumTitle");
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);
        toolbar.inflateMenu(R.menu.activity_album_menu);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_more_vert_white_24dp));
        setToolBarMenuListener(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

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
                            artistNameTextView.setTextColor(vibrantSwatch.getTitleTextColor());
                        }
                    }
                }
            });
        }
    }
    private void getSongsList() {

        songsList = AudioExtensionMethods.getSongList(this, albumTitle);
        albumArtist = AudioExtensionMethods.getAlbumArtistTitle(this, albumTitle);
        recyclerViewAdapter = new AlbumInnerLayoutSongRecyclerViewAdapter(this, songsList, albumTitle);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.songs_recylcer_view_layout);

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
                            new MaterialDialog.Builder(AlbumActivity.this)
                                    .content("Delete this album \'" +  albumTitle + "\' ?")
                                    .positiveText(R.string.delete_text)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            for (Song song : songsList) {
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
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KeyConstants.TAG_EDITOR_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK)
                recyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
