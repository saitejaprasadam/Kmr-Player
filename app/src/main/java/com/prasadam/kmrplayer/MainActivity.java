package com.prasadam.kmrplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.prasadam.kmrplayer.activityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.NowPlayingPlaylistAdapter;
import com.prasadam.kmrplayer.adapterClasses.uiAdapters.NowPlayingPlaylistInterfaces;
import com.prasadam.kmrplayer.adapterClasses.uiAdapters.SimpleItemTouchHelperCallback;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.BlurBuilder;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.Controls;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.MusicService;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.fragments.AlbumsFragment;
import com.prasadam.kmrplayer.fragments.SongsFragment;
import com.prasadam.kmrplayer.fragments.TabFragment;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.Calendar;

import static com.prasadam.kmrplayer.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , NowPlayingPlaylistInterfaces.OnStartDragListener{

    private static ImageView nowPlayingMinimalAlbumArt, nowPlayingActualAlbumArt, nowPlayingBlurredAlbumArt;
    private static RelativeLayout nowPlayingColorPallatteView;
    private static ImageView nowPlayingNextButton, nowPlayingPreviousButton, nowPlayingPlayButton, nowPlayingSongContextMenu;
    private static TextView nowPlayingSongArtistTextView, nowPlayingSongMinimalArtistTextView, nowPlayingSongMinimalTitleTextView, nowPlayingSongTitleTextView;
    private static LikeButton nowPlayingFavButton;
    private static RelativeLayout nowPlayingMinimalRootLayout;
    private static SlidingUpPanelLayout mainLayoutRootLayout;
    private static ImageView nowPlayingMinimalNextButton, nowPlayingMinimalPlayButton, nowPlayingMinimizeButton ,nowPlayingShuffleButton, nowPlayingRepeatButton;
    private static CardView nowPlayingAlbumArtContainer;
    private static ProgressBar nowPlayingMinimalProgressBar;
    private static RelativeLayout nowPlayingsongInfoCardView;
    public static RecyclerView nowPlayingPlaylistRecyclerView;
    private static NowPlayingPlaylistAdapter recyclerViewAdapter;
    private ItemTouchHelper mItemTouchHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initalizer();
        SharedVariables.Initializers(this);
    }
    private void initalizer() {

        createTabFragment();
        setNavigationDrawer();
        setStatusBarTranslucent(MainActivity.this);
        MusicPlayerExtensionMethods.startMusicService(MainActivity.this);
        initalizeNowPlayingUI();
        updateNowPlayingUI(this);
        initalizePlaylistRecyclerView();
    }
    private void initalizeNowPlayingUI() {
        nowPlayingMinimalRootLayout = (RelativeLayout) findViewById(R.id.now_playing_minimal_root_layout);
        mainLayoutRootLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        nowPlayingMinimalAlbumArt = (ImageView) findViewById(R.id.now_playing_minimal_album_art);
        nowPlayingActualAlbumArt = (ImageView) findViewById(R.id.vertical_slide_drawer_actual_image_view);
        nowPlayingBlurredAlbumArt = (ImageView) findViewById(R.id.vertical_slide_drawer_blurred_image_view);
        nowPlayingColorPallatteView = (RelativeLayout) findViewById(R.id.now_playing_color_pallete_view);
        nowPlayingPlayButton = (ImageView) findViewById(R.id.now_playing_play_pause_button);
        nowPlayingNextButton = (ImageView) findViewById(R.id.now_playing_next_button);
        nowPlayingPreviousButton = (ImageView) findViewById(R.id.now_playing_previous_button);
        nowPlayingSongArtistTextView = (TextView) findViewById(R.id.now_playing_song_artist_text_view);
        nowPlayingSongMinimalArtistTextView = (TextView) findViewById(R.id.now_playing_minimal_song_artist_text_view);
        nowPlayingSongMinimalTitleTextView = (TextView) findViewById(R.id.now_playing_minimal_song_title_text_view);
        nowPlayingSongTitleTextView = (TextView) findViewById(R.id.now_playing_song_title_text_view);
        nowPlayingFavButton = (LikeButton) findViewById(R.id.now_playing_fav_button);
        nowPlayingMinimalNextButton = (ImageView) findViewById(R.id.now_playing_minimal_next_button);
        nowPlayingMinimalPlayButton = (ImageView) findViewById(R.id.now_playing_minimal_play_button);
        nowPlayingSongContextMenu = (ImageView) findViewById(R.id.now_playing_song_context_menu);
        Toolbar nowPlayingToolbar = (Toolbar) findViewById(R.id.now_playing_toolbar);
        nowPlayingMinimizeButton = (ImageView) findViewById(R.id.now_playing_minimize_button);
        nowPlayingShuffleButton = (ImageView) findViewById(R.id.now_playing_shuffle_button);
        nowPlayingRepeatButton = (ImageView) findViewById(R.id.now_playing_repeat_button);
        nowPlayingMinimalProgressBar = (ProgressBar) findViewById(R.id.now_playing_minimal_progress_bar);
        nowPlayingsongInfoCardView = (RelativeLayout) findViewById(R.id.now_playing_song_info_layout);
        nowPlayingAlbumArtContainer = (CardView) findViewById(R.id.now_playing_album_art_container);
        nowPlayingPlaylistRecyclerView = (RecyclerView) findViewById(R.id.now_playing_playlist_recycler_view);

        if (nowPlayingToolbar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            nowPlayingToolbar.setPadding(0, ExtensionMethods.getStatusBarHeight(this), 0, 0);


        if(mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            nowPlayingMinimalRootLayout.setVisibility(View.INVISIBLE);

        else
            nowPlayingMinimalRootLayout.setVisibility(View.VISIBLE);

        nowPlayingListeners();
        setNowPlayingSongContextMenu();
    }
    private void nowPlayingListeners() {
        mainLayoutRootLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                nowPlayingMinimalRootLayout.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                    nowPlayingMinimalRootLayout.setVisibility(View.INVISIBLE);

                else
                    nowPlayingMinimalRootLayout.setVisibility(View.VISIBLE);

                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    hidePlaylist();
                }
            }
        });
        nowPlayingMinimalRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);}
        });
        nowPlayingMinimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);}
        });
        nowPlayingsongInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAdapter.notifyDataSetChanged();
                if(!PlayerConstants.SHOWING_PLAYLIST)
                    showPlaylist();
                else
                    hidePlaylist();
            }
        });

        nowPlayingShuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAdapter.notifyDataSetChanged();
                if(PlayerConstants.SHUFFLE){
                    PlayerConstants.SHUFFLE = false;
                    Controls.shuffleMashUpMethod();
                    nowPlayingShuffleButton.setAlpha(0.5f);
                }

                else{
                    PlayerConstants.SHUFFLE = true;
                    Controls.shuffleMashUpMethod();
                    nowPlayingShuffleButton.setAlpha(1f);
                }
            }
        });

        nowPlayingRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlayerConstants.PLAY_BACK_STATE == PlayerConstants.PLAYBACK_STATE_ENUM.OFF){
                    PlayerConstants.PLAY_BACK_STATE = PlayerConstants.PLAYBACK_STATE_ENUM.LOOP;
                    nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_white_24dp);
                    Controls.setLoop(false);
                    nowPlayingRepeatButton.setAlpha(1f);
                }

                else
                    if(PlayerConstants.PLAY_BACK_STATE == PlayerConstants.PLAYBACK_STATE_ENUM.LOOP){
                        PlayerConstants.PLAY_BACK_STATE = PlayerConstants.PLAYBACK_STATE_ENUM.SINGLE_LOOP;
                        nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_one_white_24dp);
                        Controls.setLoop(true);
                        nowPlayingRepeatButton.setAlpha(1f);
                    }

                    else
                        if(PlayerConstants.PLAY_BACK_STATE == PlayerConstants.PLAYBACK_STATE_ENUM.SINGLE_LOOP){
                            PlayerConstants.PLAY_BACK_STATE = PlayerConstants.PLAYBACK_STATE_ENUM.OFF;
                            nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_white_24dp);
                            Controls.setLoop(false);
                            nowPlayingRepeatButton.setAlpha(0.5f);
                        }
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            nowPlayingMinimalProgressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccentGeneric), android.graphics.PorterDuff.Mode.SRC_IN);
        final Handler mHandler = new Handler();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(MusicService.player != null){
                    int mCurrentPosition = MusicService.player.getCurrentPosition();
                    nowPlayingMinimalProgressBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    private void setNowPlayingSongContextMenu() {

        nowPlayingSongContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.song_item_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        try
                        {
                            int id = item.getItemId();

                            switch(id)
                            {
                                case R.id.song_context_menu_delete:
                                    new MaterialDialog.Builder(MainActivity.this)
                                            .content("Delete this song \'" +  MusicService.currentSong.getTitle() + "\' ?")
                                            .positiveText(R.string.delete_text)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    File file = new File(MusicService.currentSong.getData());
                                                    if(file.delete())
                                                    {
                                                        Controls.nextControl(MainActivity.this);
                                                        Toast.makeText(MainActivity.this, "Song Deleted : \'" + MusicService.currentSong.getTitle() + "\'", Toast.LENGTH_SHORT).show();
                                                        MainActivity.this.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + MusicService.currentSong.getID() + "'", null);
                                                        AudioExtensionMethods.updateLists(MainActivity.this);
                                                    }
                                                }
                                            })
                                            .negativeText(R.string.cancel_text)
                                            .show();
                                    break;

                                case R.id.song_context_menu_add_to_playlist:
                                    AudioExtensionMethods.addToPlaylist(MainActivity.this, MusicService.currentSong.getHashID());
                                    break;

                                case R.id.song_context_menu_share:
                                    AudioExtensionMethods.sendSong(MainActivity.this, MusicService.currentSong.getTitle(), Uri.parse(MusicService.currentSong.getData()));
                                    break;

                                case R.id.song_context_menu_details:
                                    AudioExtensionMethods.songDetails(MainActivity.this, MusicService.currentSong, MusicService.currentSong.getAlbumArtLocation());
                                    break;

                                case R.id.song_context_menu_ringtone:
                                    AudioExtensionMethods.setSongAsRingtone(MainActivity.this, MusicService.currentSong);
                                    break;

                                case R.id.song_context_menu_tagEditor:
                                    AudioExtensionMethods.launchTagEditor(MainActivity.this, MusicService.currentSong.getID());
                                    break;

                                case R.id.song_context_menu_jump_to_album:
                                    ActivitySwitcher.jumpToAlbum(MainActivity.this, MusicService.currentSong.getAlbum());
                                    break;

                                case R.id.song_context_menu_jump_to_artist:
                                    ActivitySwitcher.jumpToArtist(MainActivity.this, MusicService.currentSong.getArtist());
                                    break;
                            }
                        }
                        catch (Exception e){
                            Log.e("exception", e.toString());
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });
    }
    private void createTabFragment() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
    }
    private void setNavigationDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().getItem(0).setChecked(true);
            navigationView.getMenu().removeItem(R.id.nav_google_signup);

            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
            View header = navigationView.getHeaderView(0);
            ImageView headerImageView = (ImageView) header.findViewById(R.id.nav_header_imageView);
            TextView albumTextView = (TextView) header.findViewById(R.id.nav_album_name_text_view);
            TextView artistTextView = (TextView) header.findViewById(R.id.nav_artist_name_text_view);

            if(timeOfDay < 6 || timeOfDay >= 19) {
                headerImageView.setImageResource(R.mipmap.material_wallpaper_dark);
                albumTextView.setTextColor(getResources().getColor(R.color.white));
                artistTextView.setTextColor(getResources().getColor(R.color.white));
            }

        }
    }
    private void refreshList() {
        new Thread(){
            public void run(){
                int prevCount = SharedVariables.fullSongsList.size();
                AudioExtensionMethods.updateLists(MainActivity.this);
                SongsFragment.recyclerViewAdapter.notifyDataSetChanged();
                AlbumsFragment.recyclerViewAdapter.notifyDataSetChanged();
                if(prevCount < SharedVariables.fullSongsList.size())
                    Toast.makeText(MainActivity.this, "Songs lists updated, " + String.valueOf(SharedVariables.fullSongsList.size() - prevCount) + " songs added", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Songs lists updated, no new songs found", Toast.LENGTH_SHORT).show();
            }
        }.run();
    }
    private void initalizePlaylistRecyclerView(){
        recyclerViewAdapter = new NowPlayingPlaylistAdapter(this, this);
        nowPlayingPlaylistRecyclerView.setAdapter(recyclerViewAdapter);
        nowPlayingPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(recyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(nowPlayingPlaylistRecyclerView);
    }

    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else
                if (mainLayoutRootLayout != null && (mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED))
                    mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                else
                    super.onBackPressed();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh)
        {
            refreshList();
            return true;
        }

        else
            if(id == R.id.action_settings){
                Intent intent = new Intent(this, NowPlayingPlaylistActivity.class);
                startActivity(intent);
            }
                //Toast.makeText(MainActivity.this, "Pending", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }
    public boolean onNavigationItemSelected(MenuItem item) {

        item.getItemId();
        return true;
    }

    public static void updateNowPlayingUI(final Context context){

        final Song currentPlayingSong = MusicService.currentSong;
        if(currentPlayingSong != null){
            setNowPlayingAlbumArt(context, currentPlayingSong);
            nowPlayingSongArtistTextView.setText(currentPlayingSong.getArtist());
            nowPlayingSongMinimalArtistTextView.setText(currentPlayingSong.getArtist());

            nowPlayingSongTitleTextView.setText(currentPlayingSong.getTitle());
            nowPlayingSongMinimalTitleTextView.setText(currentPlayingSong.getTitle());

            updateSongLikeStatus(context);
            nowPlayingMinimalProgressBar.setMax((int) currentPlayingSong.getDuration());
            nowPlayingFavButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    currentPlayingSong.setIsLiked(context, true);
                    SongsFragment.recyclerViewAdapter.notifyDataSetChanged();
                    recyclerViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    currentPlayingSong.setIsLiked(context, false);
                    SongsFragment.recyclerViewAdapter.notifyDataSetChanged();
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            });
            if(recyclerViewAdapter != null)
                recyclerViewAdapter.notifyDataSetChanged();

            nowPlayingPlaylistRecyclerView.scrollToPosition(PlayerConstants.SONG_NUMBER);
        }

        else
            setDefaultNowPlayingScreen(context);

        changeButton();
        setNowPlayingControlButtons(context);
    }
    public static void updateSongLikeStatus(Context context) {
        nowPlayingFavButton.setLiked(MusicService.currentSong.getIsLiked(context));
    }
    private static void setDefaultNowPlayingScreen(Context context) {
        MusicService.currentSong = AudioExtensionMethods.getLastPlayedSong(context);
        PlayerConstants.SONGS_LIST.add(MusicService.currentSong);
        updateNowPlayingUI(context);
    }
    private static void setNowPlayingAlbumArt(Context context, Song currentPlayingSong) {
        String albumArtPath = currentPlayingSong.getAlbumArtLocation();
        final Bitmap bitmap;

        if(albumArtPath != null) {
            final File imgFile = new File(albumArtPath);
            if (imgFile.exists())
            {
                nowPlayingMinimalAlbumArt.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                nowPlayingActualAlbumArt.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                nowPlayingBlurredAlbumArt.setImageBitmap(BlurBuilder.blur(context, ((BitmapDrawable) nowPlayingActualAlbumArt.getDrawable()).getBitmap()));
                bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            else{
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art);
                nowPlayingActualAlbumArt.setImageResource(R.mipmap.unkown_album_art);
                nowPlayingBlurredAlbumArt.setImageBitmap(BlurBuilder.blur(context, ((BitmapDrawable) nowPlayingActualAlbumArt.getDrawable()).getBitmap()));
            }
        }

        else{
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art);
            nowPlayingActualAlbumArt.setImageResource(R.mipmap.unkown_album_art);
            nowPlayingBlurredAlbumArt.setImageBitmap(BlurBuilder.blur(context, ((BitmapDrawable) nowPlayingActualAlbumArt.getDrawable()).getBitmap()));
        }

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                if (vibrantSwatch != null) {
                    nowPlayingColorPallatteView.setBackgroundColor(vibrantSwatch.getRgb());

                }

                else
                {
                    vibrantSwatch = palette.getMutedSwatch();
                    if (vibrantSwatch != null) {
                        nowPlayingColorPallatteView.setBackgroundColor(vibrantSwatch.getRgb());
                    }

                }
            }
        });
    }
    private static void setNowPlayingControlButtons(final Context context) {
        nowPlayingNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Controls.nextControl(context);}});
        nowPlayingMinimalNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Controls.nextControl(context);
            }
        });
        nowPlayingPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {Controls.previousControl(context);}
        });

        nowPlayingPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {playPausePressed(context);
            }
        });
        nowPlayingMinimalPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {playPausePressed(context);
            }
        });
    }
    private static void playPausePressed(Context context) {
        if (PlayerConstants.SONG_PAUSED)
            Controls.playControl(context);
        else
            Controls.pauseControl(context);
    }
    public static void changeButton(){
        if(PlayerConstants.SONG_PAUSED){
            nowPlayingMinimalPlayButton.setImageResource(R.mipmap.ic_play_arrow_black_24dp);
            nowPlayingPlayButton.setImageResource(R.mipmap.ic_play_arrow_black_36dp);
        }

        else{
            nowPlayingMinimalPlayButton.setImageResource(R.mipmap.ic_pause_black_24dp);
            nowPlayingPlayButton.setImageResource(R.mipmap.ic_pause_black_36dp);
        }

        Log.d("Shuffle", String.valueOf(PlayerConstants.SHUFFLE));
        if(PlayerConstants.SHUFFLE)
            nowPlayingShuffleButton.setAlpha(1f);
        else
            nowPlayingShuffleButton.setAlpha(0.5f);

        if(PlayerConstants.PLAY_BACK_STATE == PlayerConstants.PLAYBACK_STATE_ENUM.OFF){
            nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_white_24dp);
            nowPlayingRepeatButton.setAlpha(0.5f);
        }

        else
        if(PlayerConstants.PLAY_BACK_STATE == PlayerConstants.PLAYBACK_STATE_ENUM.LOOP){
            nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_white_24dp);
            nowPlayingRepeatButton.setAlpha(1f);
        }

        else
        if(PlayerConstants.PLAY_BACK_STATE == PlayerConstants.PLAYBACK_STATE_ENUM.LOOP){
            nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_one_white_24dp);
            nowPlayingRepeatButton.setAlpha(1f);
        }

    }
    private void showPlaylist() {
        nowPlayingAlbumArtContainer.setVisibility(View.INVISIBLE);
        nowPlayingPlaylistRecyclerView.setVisibility(View.VISIBLE);
        PlayerConstants.SHOWING_PLAYLIST = true;
    }
    private void hidePlaylist(){
        nowPlayingAlbumArtContainer.setVisibility(View.VISIBLE);
        nowPlayingPlaylistRecyclerView.setVisibility(View.INVISIBLE);
        PlayerConstants.SHOWING_PLAYLIST = false;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
