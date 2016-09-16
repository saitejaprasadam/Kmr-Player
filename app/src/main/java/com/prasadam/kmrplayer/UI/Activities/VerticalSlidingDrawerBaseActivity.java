package com.prasadam.kmrplayer.UI.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NowPlayingPlaylistAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.NowPlayingAlbumArtAdapter;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.Controls;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.Interfaces.NowPlayingPlaylistInterfaces;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.Map;

/*
 * Created by Prasadam Saiteja on 7/30/2016.
 */

public class VerticalSlidingDrawerBaseActivity extends AppCompatActivity implements NowPlayingPlaylistInterfaces.OnStartDragListener, ViewPager.OnPageChangeListener{

    protected SlidingUpPanelLayout mainLayoutRootLayout;
    private ImageView nowPlayingMinimalAlbumArt, nowPlayingBlurredAlbumArt;
    private RelativeLayout nowPlayingColorPallatteView, nowPlayingColorPallatteViewBackground;
    private ImageView nowPlayingNextButton, nowPlayingPreviousButton, nowPlayingMinimalNextButton;
    private ImageView nowPlayingSongContextMenu;
    private TextView nowPlayingSongArtistTextView, nowPlayingSongMinimalArtistTextView, nowPlayingSongMinimalTitleTextView, nowPlayingSongTitleTextView;
    private ProgressBar nowPlayingMinimalProgressBar;
    private ItemTouchHelper mItemTouchHelper;
    private RelativeLayout nowPlayingsongInfoCardView;
    private CardView nowPlayingAlbumArtContainer;
    private RelativeLayout nowPlayingMinimalRootLayout;
    private SeekBar nowPlayingSeekBar;
    private TextView nowPlayingCurrentDuration, nowPlayingMaxDuration;
    private Handler progressHandler;
    private Animator animator;
    private static ImageView nowPlayingPlayButton, nowPlayingMinimalPlayButton, nowPlayingShuffleButton, nowPlayingRepeatButton;
    private static LikeButton nowPlayingFavButton;
    private static ViewPager viewPager;
    private static NowPlayingAlbumArtAdapter albumArtParallaxAdapter;
    public static NowPlayingPlaylistAdapter NowPlayingPlaylistRecyclerViewAdapter;
    public  RecyclerView nowPlayingPlaylistRecyclerView;

    @Override
    public void setContentView(final int layoutResID) {

        mainLayoutRootLayout = (SlidingUpPanelLayout) getLayoutInflater().inflate(R.layout.base_activity_vertical_sliding_drawer_layout, null);
        FrameLayout actContent = (FrameLayout) mainLayoutRootLayout.findViewById(R.id.main_content);

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(mainLayoutRootLayout);

        albumArtParallaxAdapter = new NowPlayingAlbumArtAdapter(this);
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
            nowPlayingToolbar.setPadding(0, ActivityHelper.getStatusBarHeight(this), 0, 0);

        initalizeNowPlayingUI();
        initalizePlaylistRecyclerView();
        nowPlayingListeners();
        setAlbumAdapter();
        setNowPlayingSongContextMenu();
    }

    public void onResume() {
        super.onResume();
        initalizeNowPlayingUI();
        setHandlers();
        Controls.updateNowPlayingUI();
        if(mainLayoutRootLayout != null && mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            nowPlayingMinimalRootLayout.setVisibility(View.INVISIBLE);

        if((ExtensionMethods.isTablet(VerticalSlidingDrawerBaseActivity.this) && ExtensionMethods.isLandScape(VerticalSlidingDrawerBaseActivity.this))){
            nowPlayingPlaylistRecyclerView.setVisibility(View.VISIBLE);
            nowPlayingPlaylistRecyclerView.scrollToPosition(PlayerConstants.SONG_NUMBER);
        }
    }
    public void onDestroy(){
        super.onDestroy();
        if(animator != null){
            animator.removeAllListeners();
            animator = null;
        }
    }
    public void onPause(){
        super.onPause();
        if(animator != null){
            animator.removeAllListeners();
            animator = null;
        }
    }
    public void onBackPressed() {
        if (mainLayoutRootLayout != null && (mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED))
            mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else
            super.onBackPressed();
    }

    private void initalizeNowPlayingUI(){
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
        viewPager = (ViewPager) findViewById(R.id.parallaxSlider);

        if(ExtensionMethods.isTablet(this) && ExtensionMethods.isLandScape(this))
            nowPlayingPlaylistRecyclerView.setPadding(0, ActivityHelper.getStatusBarHeight(this), 0, 0);

        nowPlayingSeekBar.getProgressDrawable().setColorFilter(ActivityHelper.getColor(this, R.color.colorAccentGeneric), PorterDuff.Mode.MULTIPLY);
        if(!(ExtensionMethods.isLandScape(this) && !ExtensionMethods.isTablet(this)))
            nowPlayingSeekBar.setPadding(0, 0, 0, 0);

        if(mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            nowPlayingMinimalRootLayout.setVisibility(View.INVISIBLE);
        else
            nowPlayingMinimalRootLayout.setVisibility(View.VISIBLE);
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
    private void nowPlayingListeners() {
        mainLayoutRootLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                nowPlayingMinimalRootLayout.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                        getWindow().setNavigationBarColor(ActivityHelper.getColor(getBaseContext(), R.color.black));
                    else if(SharedPreferenceHelper.getColoredNavBarState(VerticalSlidingDrawerBaseActivity.this))
                            getWindow().setNavigationBarColor(((ColorDrawable) nowPlayingColorPallatteView.getBackground()).getColor());
                }

                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                    nowPlayingMinimalRootLayout.setVisibility(View.GONE);
                else
                    nowPlayingMinimalRootLayout.setVisibility(View.VISIBLE);

                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED && !(ExtensionMethods.isTablet(VerticalSlidingDrawerBaseActivity.this) && ExtensionMethods.isLandScape(VerticalSlidingDrawerBaseActivity.this)))
                    hidePlaylist();

            }
        });
        nowPlayingMinimalRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);}
        });
        setNowPlayingControlButtons();

        nowPlayingsongInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PlayerConstants.SHOWING_PLAYLIST && !(ExtensionMethods.isTablet(VerticalSlidingDrawerBaseActivity.this) && ExtensionMethods.isLandScape(VerticalSlidingDrawerBaseActivity.this)))
                    showPlaylist();

                else if(PlayerConstants.SHOWING_PLAYLIST && !(ExtensionMethods.isTablet(VerticalSlidingDrawerBaseActivity.this) && ExtensionMethods.isLandScape(VerticalSlidingDrawerBaseActivity.this)))
                    hidePlaylist();
            }
        });

        nowPlayingShuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlayerConstants.getShuffleState()){
                    PlayerConstants.setShuffleState(VerticalSlidingDrawerBaseActivity.this, false);
                    Controls.shuffleMashUpMethod(VerticalSlidingDrawerBaseActivity.this);
                    nowPlayingShuffleButton.setAlpha(0.5f);
                    if(nowPlayingPlaylistRecyclerView != null)
                        nowPlayingPlaylistRecyclerView.scrollToPosition(PlayerConstants.SONG_NUMBER);
                }

                else{
                    PlayerConstants.setShuffleState(VerticalSlidingDrawerBaseActivity.this, true);
                    Controls.shuffleMashUpMethod(VerticalSlidingDrawerBaseActivity.this);
                    nowPlayingShuffleButton.setAlpha(1f);
                    if(nowPlayingPlaylistRecyclerView != null)
                        nowPlayingPlaylistRecyclerView.scrollToPosition(PlayerConstants.SONG_NUMBER);
                }
            }
        });

        nowPlayingRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.OFF){
                    PlayerConstants.setPlayBackState(VerticalSlidingDrawerBaseActivity.this, PlayerConstants.PLAYBACK_STATE_ENUM.LOOP);
                    nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_white_24dp);
                    Controls.setLoop(false);
                    nowPlayingRepeatButton.setAlpha(1f);
                }

                else
                if(PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.LOOP){
                    PlayerConstants.setPlayBackState(VerticalSlidingDrawerBaseActivity.this, PlayerConstants.PLAYBACK_STATE_ENUM.SINGLE_LOOP);
                    nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_one_white_24dp);
                    Controls.setLoop(true);
                    nowPlayingRepeatButton.setAlpha(1f);
                }

                else
                if(PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.SINGLE_LOOP){
                    PlayerConstants.setPlayBackState(VerticalSlidingDrawerBaseActivity.this, PlayerConstants.PLAYBACK_STATE_ENUM.OFF);
                    nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_white_24dp);
                    Controls.setLoop(false);
                    nowPlayingRepeatButton.setAlpha(0.5f);
                }
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            nowPlayingMinimalProgressBar.getProgressDrawable().setColorFilter(ActivityHelper.getColor(getBaseContext(), R.color.colorAccentGeneric), android.graphics.PorterDuff.Mode.SRC_IN);

        nowPlayingFavButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Controls.favControl(VerticalSlidingDrawerBaseActivity.this);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Controls.favControl(VerticalSlidingDrawerBaseActivity.this);
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
                popup.inflate(R.menu.song_item_menu_currently_playing);
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
                                                    File file = new File(MusicService.currentSong.getData());
                                                    if (file.delete()) {
                                                        SharedVariables.fullSongsList.remove(MusicService.currentSong);
                                                        PlayerConstants.removeSongFromPlaylist(VerticalSlidingDrawerBaseActivity.this, PlayerConstants.SONG_NUMBER - 1);
                                                        Controls.nextControl(VerticalSlidingDrawerBaseActivity.this);
                                                        Toast.makeText(VerticalSlidingDrawerBaseActivity.this, "Song Deleted : \'" + MusicService.currentSong.getTitle() + "\'", Toast.LENGTH_SHORT).show();
                                                        VerticalSlidingDrawerBaseActivity.this.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + MusicService.currentSong.getID() + "'", null);
                                                    }
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

                                case R.id.song_context_menu_quick_share:
                                    ActivitySwitcher.jumpToQuickShareActivity(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong);
                                    break;

                                case R.id.song_context_menu_details:
                                    DialogHelper.songDetails(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong, MusicService.currentSong.getAlbumArtLocation());
                                    break;

                                case R.id.song_context_menu_ringtone:
                                    AudioExtensionMethods.setSongAsRingtone(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong);
                                    break;

                                case R.id.song_context_menu_tagEditor:
                                    ActivitySwitcher.launchTagEditor(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong.getID(), MusicService.currentSong.getHashID());
                                    break;

                                case R.id.song_context_menu_jump_to_album:
                                    ActivitySwitcher.jumpToAlbum(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong.getID());
                                    break;

                                case R.id.song_context_menu_jump_to_artist:
                                    ActivitySwitcher.jumpToArtist(VerticalSlidingDrawerBaseActivity.this, MusicService.currentSong.getArtistID());
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
        NowPlayingPlaylistRecyclerViewAdapter = new NowPlayingPlaylistAdapter(this, this);
        nowPlayingPlaylistRecyclerView.setAdapter(NowPlayingPlaylistRecyclerViewAdapter);
        nowPlayingPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(VerticalSlidingDrawerBaseActivity.this));
        ItemTouchHelper.Callback callback = new NowPlayingPlaylistInterfaces.SimpleItemTouchHelperCallback(NowPlayingPlaylistRecyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(nowPlayingPlaylistRecyclerView);
    }
    private void setAlbumAdapter() {
        viewPager.setAdapter(albumArtParallaxAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(0);
    }

    private void setHandlers(){

        PlayerConstants.UPDATE_NOW_PLAYING_UI = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg){
                try {
                    final Song currentPlayingSong = MusicService.currentSong;
                    if(currentPlayingSong != null){
                        if(mainLayoutRootLayout != null && mainLayoutRootLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN)
                            mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        setNowPlayingAlbumArt(currentPlayingSong);
                        nowPlayingSongArtistTextView.setText(currentPlayingSong.getArtist());
                        nowPlayingSongMinimalArtistTextView.setText(currentPlayingSong.getArtist());

                        nowPlayingSongTitleTextView.setText(currentPlayingSong.getTitle());
                        nowPlayingSongMinimalTitleTextView.setText(currentPlayingSong.getTitle());

                        updateSongLikeStatus(VerticalSlidingDrawerBaseActivity.this);
                        nowPlayingMinimalProgressBar.setMax((int) currentPlayingSong.getDuration());
                        nowPlayingSeekBar.setMax((int) currentPlayingSong.getDuration());
                        nowPlayingMaxDuration.setText(ExtensionMethods.formatIntoHHMMSS((int) currentPlayingSong.getDuration()));

                        NowPlayingPlaylistRecyclerViewAdapter.notifyDataSetChanged();
                        nowPlayingPlaylistRecyclerView.scrollToPosition(PlayerConstants.SONG_NUMBER);
                        changeButton();
                    }

                    else
                        mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }

                catch (Exception ignored) {}
                return true;
            }
        });

        progressHandler = new Handler();
        progressHandler.post(new Runnable() {
            @Override
            public void run() {
                if(MusicService.player != null){
                    int mCurrentPosition = MusicService.player.getCurrentPosition();
                    if(MusicService.currentSong != null && mCurrentPosition <= MusicService.currentSong.getDuration()){
                        SharedPreferenceHelper.setDuration(VerticalSlidingDrawerBaseActivity.this);
                        nowPlayingMinimalProgressBar.setProgress(mCurrentPosition);
                        nowPlayingCurrentDuration.setText(ExtensionMethods.formatIntoHHMMSS(mCurrentPosition));
                        if(!nowPlayingSeekBar.isInEditMode())
                            nowPlayingSeekBar.setProgress(mCurrentPosition);
                    }
                }
                if(progressHandler != null)
                    progressHandler.postDelayed(this, 1000);
            }
        });
    }
    public static void updateSongLikeStatus(Context context) {
        nowPlayingFavButton.setLiked(MusicService.currentSong.getIsLiked(context));
    }
    private void setNowPlayingAlbumArt(Song currentPlayingSong) {

        String albumArtPath = currentPlayingSong.getAlbumArtLocation();
        final Bitmap bitmap;

        albumArtParallaxAdapter.updateAlbumArt(viewPager);
        if(albumArtPath != null) {
            final File imgFile = new File(albumArtPath);
            if (imgFile.exists())
            {
                nowPlayingMinimalAlbumArt.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                nowPlayingBlurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) nowPlayingMinimalAlbumArt.getDrawable()).getBitmap()));
                bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            else{
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art);
                nowPlayingMinimalAlbumArt.setImageResource(R.mipmap.unkown_album_art);
                nowPlayingBlurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art)));
            }
        }

        else{
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art);
            nowPlayingMinimalAlbumArt.setImageResource(R.mipmap.unkown_album_art);
            nowPlayingBlurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art)));
        }

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                final int[] colors = ActivityHelper.getAvailableColor(VerticalSlidingDrawerBaseActivity.this, palette);
                if(colors[0] != ((ColorDrawable) nowPlayingColorPallatteView.getBackground()).getColor())
                    setColorPallete(colors[0], ((ColorDrawable) nowPlayingColorPallatteView.getBackground()).getColor());
            }
        });
    }

    private void setNowPlayingControlButtons() {
        nowPlayingNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Controls.nextControl(VerticalSlidingDrawerBaseActivity.this);}});
        nowPlayingMinimalNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Controls.nextControl(VerticalSlidingDrawerBaseActivity.this);}
        });
        nowPlayingPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {Controls.previousControl(VerticalSlidingDrawerBaseActivity.this);}
        });

        nowPlayingPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {playPausePressed();}
        });
        nowPlayingMinimalPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {playPausePressed();}
        });
    }
    private void playPausePressed() {
        try{
            if (PlayerConstants.SONG_PAUSED)
                Controls.playControl(this);
            else
                Controls.pauseControl(this);
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

        if(PlayerConstants.getShuffleState())
            nowPlayingShuffleButton.setAlpha(1f);
        else
            nowPlayingShuffleButton.setAlpha(0.5f);

        if(PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.OFF){
            nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_white_24dp);
            nowPlayingRepeatButton.setAlpha(0.5f);
        }

        else
        if(PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.LOOP){
            nowPlayingRepeatButton.setImageResource(R.mipmap.ic_repeat_white_24dp);
            nowPlayingRepeatButton.setAlpha(1f);
        }

        else
        if(PlayerConstants.getPlayBackState() == PlayerConstants.PLAYBACK_STATE_ENUM.SINGLE_LOOP){
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

    private void setColorPallete(final int toRGB, final int fromRGB) {

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                nowPlayingColorPallatteViewBackground.setBackgroundColor(fromRGB);
                animator = ViewAnimationUtils.createCircularReveal(nowPlayingColorPallatteView,
                        nowPlayingColorPallatteView.getWidth() / 2,
                        nowPlayingColorPallatteView.getHeight() / 2, 0,
                        nowPlayingColorPallatteView.getWidth() / 2);

                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        nowPlayingColorPallatteView.setBackgroundColor(toRGB);
                    }
                });

                if (SharedPreferenceHelper.getColoredNavBarState(this) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mainLayoutRootLayout.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
                    getWindow().setNavigationBarColor(toRGB);

                animator.setStartDelay(0);
                animator.setDuration(400);
                animator.start();
            }

            else
                nowPlayingColorPallatteView.setBackgroundColor(toRGB);
        }
        catch(Exception ignored){}

    }
    private void parallaxImages(int position, int positionOffsetPixels) {
        Map<Integer, View> imageViews = albumArtParallaxAdapter.getImageViews();

        for (Map.Entry<Integer, View> entry: imageViews.entrySet()){
            int imagePosition = entry.getKey();
            int correctedPosition = imagePosition - position;
            int displace = -(correctedPosition * viewPager.getMeasuredWidth()/2)+ (positionOffsetPixels / 2);

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
}
