package com.prasadam.kmrplayer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters.NowPlayingPlaylistAdapter;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.NowPlayingAlbumArtAdapter;
import com.prasadam.kmrplayer.AdapterClasses.Interfaces.NowPlayingPlaylistInterfaces;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.Controls;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.Fragments.AlbumsFragment;
import com.prasadam.kmrplayer.Fragments.ArtistFragment;
import com.prasadam.kmrplayer.Fragments.SongsFragment;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.Map;

/*
 * Created by Prasadam Saiteja on 7/30/2016.
 */

public class VerticalSlidingDrawerBaseActivity extends AppCompatActivity implements NowPlayingPlaylistInterfaces.OnStartDragListener, ViewPager.OnPageChangeListener{

    protected static SlidingUpPanelLayout mainLayoutRootLayout;
    protected static FrameLayout actContent;
    private static ImageView nowPlayingMinimalAlbumArt, nowPlayingBlurredAlbumArt;
    private static RelativeLayout nowPlayingColorPallatteView, nowPlayingColorPallatteViewBackground;
    private static ImageView nowPlayingNextButton, nowPlayingPreviousButton, nowPlayingPlayButton, nowPlayingSongContextMenu;
    private static TextView nowPlayingSongArtistTextView, nowPlayingSongMinimalArtistTextView, nowPlayingSongMinimalTitleTextView, nowPlayingSongTitleTextView;
    private static LikeButton nowPlayingFavButton;
    private static ImageView nowPlayingMinimalNextButton, nowPlayingMinimalPlayButton, nowPlayingShuffleButton, nowPlayingRepeatButton;
    private static ProgressBar nowPlayingMinimalProgressBar;
    public static RecyclerView nowPlayingPlaylistRecyclerView;
    public static NowPlayingPlaylistAdapter recyclerViewAdapter;
    private static ViewPager viewPager;
    private static NowPlayingAlbumArtAdapter albumArtParallaxAdapter;
    private static ItemTouchHelper mItemTouchHelper;
    private static RelativeLayout nowPlayingsongInfoCardView;
    private static CardView nowPlayingAlbumArtContainer;
    protected static RelativeLayout nowPlayingMinimalRootLayout;
    private static SeekBar nowPlayingSeekBar;
    private TextView nowPlayingCurrentDuration;
    private static TextView nowPlayingMaxDuration;
    private static int width;

    @Override
    public void setContentView(final int layoutResID) {

        mainLayoutRootLayout = (SlidingUpPanelLayout) getLayoutInflater().inflate(R.layout.base_activity_vertical_sliding_drawer_layout, null);
        actContent = (FrameLayout) mainLayoutRootLayout.findViewById(R.id.main_content);

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(mainLayoutRootLayout);

        Toolbar nowPlayingToolbar = (Toolbar) findViewById(R.id.now_playing_toolbar);
        nowPlayingToolbar.setNavigationIcon(R.mipmap.ic_keyboard_arrow_down_white_24dp);
        nowPlayingToolbar.inflateMenu(R.menu.fragment_now_playing_menu);
        nowPlayingToolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_more_vert_white_24dp));
        setNowPlayingToolBarMenuListener(nowPlayingToolbar);
        nowPlayingToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            nowPlayingToolbar.setPadding(0, ExtensionMethods.getStatusBarHeight(this), 0, 0);

        initalize();
    }
    public void onResume() {
        super.onResume();
        if(mainLayoutRootLayout != null && mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            nowPlayingMinimalRootLayout.setVisibility(View.INVISIBLE);

        if((ExtensionMethods.isTablet(VerticalSlidingDrawerBaseActivity.this) && ExtensionMethods.isLandScape(VerticalSlidingDrawerBaseActivity.this))){
            nowPlayingPlaylistRecyclerView.setVisibility(View.VISIBLE);
            nowPlayingPlaylistRecyclerView.scrollToPosition(PlayerConstants.SONG_NUMBER);
        }


        initalize();
        updateNowPlayingUI(this);
    }
    public void onBackPressed() {
        super.onBackPressed();
        if (mainLayoutRootLayout != null && (mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED))
            mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void initalize() {
        initalizeNowPlayingUI();
        calculateWidth();
        initalizePlaylistRecyclerView();
        updateNowPlayingUI(this);
    }
    private void initalizeNowPlayingUI() {
        nowPlayingMinimalRootLayout = (RelativeLayout) findViewById(R.id.now_playing_minimal_root_layout);
        mainLayoutRootLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        nowPlayingMinimalAlbumArt = (ImageView) findViewById(R.id.now_playing_minimal_album_art);
        nowPlayingBlurredAlbumArt = (ImageView) findViewById(R.id.vertical_slide_drawer_blurred_image_view);
        nowPlayingColorPallatteView = (RelativeLayout) findViewById(R.id.now_playing_color_pallete_view);
        nowPlayingColorPallatteViewBackground = (RelativeLayout) findViewById(R.id.now_playing_color_pallete_view_background);
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
        nowPlayingShuffleButton = (ImageView) findViewById(R.id.now_playing_shuffle_button);
        nowPlayingRepeatButton = (ImageView) findViewById(R.id.now_playing_repeat_button);
        nowPlayingMinimalProgressBar = (ProgressBar) findViewById(R.id.now_playing_minimal_progress_bar);
        nowPlayingsongInfoCardView = (RelativeLayout) findViewById(R.id.now_playing_song_info_layout);
        nowPlayingAlbumArtContainer = (CardView) findViewById(R.id.now_playing_album_art_container);
        nowPlayingPlaylistRecyclerView = (RecyclerView) findViewById(R.id.now_playing_playlist_recycler_view);
        nowPlayingSeekBar = (SeekBar) findViewById(R.id.window_song_seekbar);
        nowPlayingMaxDuration = (TextView) findViewById(R.id.now_playing_max_duration);
        nowPlayingCurrentDuration = (TextView) findViewById(R.id.now_playing_current_duration);

        nowPlayingSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccentGeneric), PorterDuff.Mode.MULTIPLY);
        if(!(ExtensionMethods.isLandScape(this) && !ExtensionMethods.isTablet(this)))
            nowPlayingSeekBar.setPadding(0, 0, 0, 0);

        if(mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            nowPlayingMinimalRootLayout.setVisibility(View.INVISIBLE);
        else
            nowPlayingMinimalRootLayout.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            nowPlayingPlaylistRecyclerView.setPadding(0, ExtensionMethods.getStatusBarHeight(this), 0, 0);

        nowPlayingListeners();
        setNowPlayingSongContextMenu();
        viewPager = (ViewPager) findViewById(R.id.parallaxSlider);
        albumArtParallaxAdapter = new NowPlayingAlbumArtAdapter(this);
        viewPager.setAdapter(albumArtParallaxAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(0);
    }

    private void setNowPlayingToolBarMenuListener(Toolbar nowPlayingToolbar) {
        nowPlayingToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    int id = item.getItemId();
                    switch (id) {

                        case R.id.action_search:
                            ActivitySwitcher.launchSearchActivity(VerticalSlidingDrawerBaseActivity.this);
                            break;

                        case R.id.action_devices_button:
                            ActivitySwitcher.jumpToAvaiableDevies(VerticalSlidingDrawerBaseActivity.this);
                            break;

                        case R.id.action_equilzer:
                            ActivitySwitcher.initEqualizer(VerticalSlidingDrawerBaseActivity.this);
                            break;

                        default:
                            Toast.makeText(VerticalSlidingDrawerBaseActivity.this, "pending", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (Exception ignored) {}
                return true;
            }
        });
    }
    private void calculateWidth() {
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                width = viewPager.getMeasuredWidth();
            }
        });
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

                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED && !(ExtensionMethods.isTablet(VerticalSlidingDrawerBaseActivity.this) && ExtensionMethods.isLandScape(VerticalSlidingDrawerBaseActivity.this))){
                    hidePlaylist();
                }
            }
        });
        nowPlayingMinimalRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);}
        });

        nowPlayingsongInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAdapter.notifyDataSetChanged();

                if(!PlayerConstants.SHOWING_PLAYLIST && !(ExtensionMethods.isTablet(VerticalSlidingDrawerBaseActivity.this) && ExtensionMethods.isLandScape(VerticalSlidingDrawerBaseActivity.this)))
                    showPlaylist();

                else if(PlayerConstants.SHOWING_PLAYLIST && !(ExtensionMethods.isTablet(VerticalSlidingDrawerBaseActivity.this) && ExtensionMethods.isLandScape(VerticalSlidingDrawerBaseActivity.this)))
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
                    if(MusicService.currentSong != null && mCurrentPosition <= MusicService.currentSong.getDuration()){
                        nowPlayingMinimalProgressBar.setProgress(mCurrentPosition);
                        nowPlayingCurrentDuration.setText(ExtensionMethods.formatIntoHHMMSS(mCurrentPosition));
                        if(!nowPlayingSeekBar.isInEditMode())
                            nowPlayingSeekBar.setProgress(mCurrentPosition);
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        nowPlayingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.player.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

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
                                    new MaterialDialog.Builder(VerticalSlidingDrawerBaseActivity.this)
                                            .content("Delete this song \'" +  MusicService.currentSong.getTitle() + "\' ?")
                                            .positiveText(R.string.delete_text)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            File file = new File(MusicService.currentSong.getData());
                                                            if (file.delete()) {
                                                                Controls.nextControl(VerticalSlidingDrawerBaseActivity.this);
                                                                PlayerConstants.SONGS_LIST.remove(PlayerConstants.SONG_NUMBER - 1);
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(VerticalSlidingDrawerBaseActivity.this, "Song Deleted : \'" + MusicService.currentSong.getTitle() + "\'", Toast.LENGTH_SHORT).show();
                                                                        VerticalSlidingDrawerBaseActivity.this.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + MusicService.currentSong.getID() + "'", null);
                                                                    }
                                                                });
                                                                AudioExtensionMethods.updateLists(VerticalSlidingDrawerBaseActivity.this);
                                                            }
                                                        }
                                                    });
                                                }
                                            })
                                            .negativeText(R.string.cancel_text)
                                            .show();
                                    break;

                                case R.id.song_context_menu_add_to_dialog:
                                    DialogHelper.AddToDialog(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong);
                                    break;

                                case R.id.song_context_menu_share:
                                    ShareIntentHelper.sendSong(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong.getTitle(), Uri.parse(MusicService.currentSong.getData()));
                                    break;

                                case R.id.song_context_menu_details:
                                    AudioExtensionMethods.songDetails(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong, MusicService.currentSong.getAlbumArtLocation());
                                    break;

                                case R.id.song_context_menu_ringtone:
                                    AudioExtensionMethods.setSongAsRingtone(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong);
                                    break;

                                case R.id.song_context_menu_tagEditor:
                                    ActivitySwitcher.launchTagEditor(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong.getID(), PlayerConstants.SONG_NUMBER);
                                    break;

                                case R.id.song_context_menu_jump_to_album:
                                    ActivitySwitcher.jumpToAlbum(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong.getAlbum());
                                    break;

                                case R.id.song_context_menu_jump_to_artist:
                                    ActivitySwitcher.jumpToArtist(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong.getArtist());
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
    private void initalizePlaylistRecyclerView(){
        recyclerViewAdapter = new NowPlayingPlaylistAdapter(this, this);
        nowPlayingPlaylistRecyclerView.setAdapter(recyclerViewAdapter);
        nowPlayingPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(VerticalSlidingDrawerBaseActivity.this));
        ItemTouchHelper.Callback callback = new NowPlayingPlaylistInterfaces.SimpleItemTouchHelperCallback(recyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(nowPlayingPlaylistRecyclerView);
    }

    public static void updateNowPlayingUI(final Context context){

        final Song currentPlayingSong = MusicService.currentSong;
        if(currentPlayingSong != null){
            setImagesInAlbumArtParallaxAdapater();
            setNowPlayingAlbumArt(context, currentPlayingSong);
            nowPlayingSongArtistTextView.setText(currentPlayingSong.getArtist());
            nowPlayingSongMinimalArtistTextView.setText(currentPlayingSong.getArtist());

            nowPlayingSongTitleTextView.setText(currentPlayingSong.getTitle());
            nowPlayingSongMinimalTitleTextView.setText(currentPlayingSong.getTitle());

            updateSongLikeStatus(context);
            nowPlayingMinimalProgressBar.setMax((int) currentPlayingSong.getDuration());
            nowPlayingSeekBar.setMax((int) currentPlayingSong.getDuration());
            nowPlayingMaxDuration.setText(ExtensionMethods.formatIntoHHMMSS((int) currentPlayingSong.getDuration()));

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
        albumArtParallaxAdapter.dataSetChange();
        viewPager.setAdapter(albumArtParallaxAdapter);
        viewPager.setCurrentItem(PlayerConstants.SONG_NUMBER);
        updateNowPlayingUI(context);
    }
    private static void setNowPlayingAlbumArt(Context context, Song currentPlayingSong) {
        String albumArtPath = currentPlayingSong.getAlbumArtLocation();
        final Bitmap bitmap;

        albumArtParallaxAdapter.updateAlbumArt(viewPager);
        if(albumArtPath != null) {
            final File imgFile = new File(albumArtPath);
            if (imgFile.exists())
            {
                nowPlayingMinimalAlbumArt.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                nowPlayingBlurredAlbumArt.setImageBitmap(BlurBuilder.blur(context, ((BitmapDrawable) nowPlayingMinimalAlbumArt.getDrawable()).getBitmap()));
                bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            else{
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art);
                nowPlayingMinimalAlbumArt.setImageResource(R.mipmap.unkown_album_art);
                nowPlayingBlurredAlbumArt.setImageBitmap(BlurBuilder.blur(context, BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art)));
            }
        }

        else{
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art);
            nowPlayingMinimalAlbumArt.setImageResource(R.mipmap.unkown_album_art);
            nowPlayingBlurredAlbumArt.setImageBitmap(BlurBuilder.blur(context, BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art)));
        }

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                if (vibrantSwatch != null)
                    setColorPallete(vibrantSwatch.getRgb(), ((ColorDrawable)nowPlayingColorPallatteView.getBackground()).getColor());

                else
                {
                    vibrantSwatch = palette.getMutedSwatch();
                    if (vibrantSwatch != null)
                        setColorPallete(vibrantSwatch.getRgb(), ((ColorDrawable)nowPlayingColorPallatteView.getBackground()).getColor());
                }
            }
        });
    }

    private static void setColorPallete(final int toRGB, final int fromRGB) {

        nowPlayingColorPallatteView.post(new Runnable(){
            @Override
            public void run()
            {
                if (Build.VERSION.SDK_INT >= 21){
                    nowPlayingColorPallatteView.post(new Runnable() {
                        @Override
                        public void run() {
                            Animator animator = ViewAnimationUtils.createCircularReveal(nowPlayingColorPallatteView,
                                    nowPlayingColorPallatteView.getWidth() / 2,
                                    nowPlayingColorPallatteView.getHeight() / 2, 0,
                                    nowPlayingColorPallatteView.getWidth() / 2);

                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    nowPlayingColorPallatteView.setBackgroundColor(toRGB);
                                }
                            });

                            if(nowPlayingColorPallatteViewBackground != null)
                                nowPlayingColorPallatteViewBackground.setBackgroundColor(fromRGB);
                            animator.setStartDelay(0);
                            animator.setDuration(400);
                            animator.start();
                        }
                    });
                }

                else
                    nowPlayingColorPallatteView.setBackgroundColor(toRGB);


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
        try{
            if (PlayerConstants.SONG_PAUSED)
                Controls.playControl(context);
            else
                Controls.pauseControl(context);
        }

        catch (Exception e){ Log.d("Exception", e.toString());}
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
        nowPlayingPlaylistRecyclerView.scrollToPosition(PlayerConstants.SONG_NUMBER);
        mainLayoutRootLayout.setScrollableView(nowPlayingPlaylistRecyclerView);
    }
    private void hidePlaylist(){
        nowPlayingAlbumArtContainer.setVisibility(View.VISIBLE);
        nowPlayingPlaylistRecyclerView.setVisibility(View.GONE);
        PlayerConstants.SHOWING_PLAYLIST = false;
        mainLayoutRootLayout.setScrollableView(null);
    }
    private static void setImagesInAlbumArtParallaxAdapater(){

    }
    private void parallaxImages(int position, int positionOffsetPixels) {
        Map<Integer, View> imageViews = albumArtParallaxAdapter.getImageViews();

        for (Map.Entry<Integer, View> entry: imageViews.entrySet()){
            int imagePosition = entry.getKey();
            int correctedPosition = imagePosition - position;
            int displace = -(correctedPosition * width/2)+ (positionOffsetPixels / 2);

            View view = entry.getValue();
            view.setX(displace);
        }
    }
    public static void updateAlbumAdapter(){
        if(albumArtParallaxAdapter != null){
            albumArtParallaxAdapter.dataSetChange();
            viewPager.setAdapter(albumArtParallaxAdapter);
            viewPager.setCurrentItem(PlayerConstants.SONG_NUMBER);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        parallaxImages(position, positionOffsetPixels);
    }
    public void onPageSelected(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    if(position != PlayerConstants.SONG_NUMBER)
                        MusicPlayerExtensionMethods.changeSong(VerticalSlidingDrawerBaseActivity.this, position);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    public void onPageScrollStateChanged(int state) {}
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK && requestCode == KeyConstants.REQUEST_CODE_DELETE_ALBUM){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AudioExtensionMethods.updateLists(VerticalSlidingDrawerBaseActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SongsFragment.updateList();
                            AlbumsFragment.updateList();
                            ArtistFragment.updateList();
                        }
                    });
                }
            });
        }
    }
}
