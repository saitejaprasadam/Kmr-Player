package com.prasadam.kmrplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
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
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.io.File;
import java.util.Calendar;

import butterknife.Bind;

import static com.prasadam.kmrplayer.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static ImageView nowPlayingBlurredAlbumArt;
    private static ImageView nowPlayingActualAlbumArt;
    private static LinearLayout nowPlayingRootLayout;
    private static RelativeLayout nowPlayingColorPallatteView;
    private static LikeButton nowPlayingFavButton;
    private static ImageView nowPlayingPlayButton, nowPlayingNextButton, nowPlayingPreviousButton;
    private static TextView nowPlayingSongTitleTextView, nowPlayingSongArtistTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initalizer();
        SharedVariables.Initializers(this);
    }
    private void initalizer() {

        nowPlayingActualAlbumArt = (ImageView) findViewById(R.id.vertical_slide_drawer_actual_image_view);
        nowPlayingBlurredAlbumArt = (ImageView) findViewById(R.id.vertical_slide_drawer_blurred_image_view);
        nowPlayingRootLayout = (LinearLayout) findViewById(R.id.vertical_sliding_drawer_root_layout);
        nowPlayingColorPallatteView = (RelativeLayout) findViewById(R.id.now_playing_color_pallete_view);
        nowPlayingPlayButton = (ImageView) findViewById(R.id.now_playing_play_pause_button);
        nowPlayingNextButton = (ImageView) findViewById(R.id.now_playing_next_button);
        nowPlayingPreviousButton = (ImageView) findViewById(R.id.now_playing_previous_button);
        nowPlayingSongArtistTextView = (TextView) findViewById(R.id.now_playing_song_artist_text_view);
        nowPlayingSongTitleTextView = (TextView) findViewById(R.id.now_playing_song_title_text_view);
        nowPlayingFavButton = (LikeButton) findViewById(R.id.now_playing_fav_button);

        updateNowPlayingUI(this);
        createTabFragment();
        setNavigationDrawer();
        setStatusBarTranslucent(MainActivity.this);
        MusicPlayerExtensionMethods.startMusicService(MainActivity.this);
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

    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else
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
                Intent intent = new Intent(this, testActivity.class);
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
            nowPlayingSongTitleTextView.setText(currentPlayingSong.getTitle());
            nowPlayingFavButton.setLiked(currentPlayingSong.getIsLiked(context));
            nowPlayingFavButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    currentPlayingSong.setIsLiked(context, true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    currentPlayingSong.setIsLiked(context, false);
                }
            });
        }

        else
            if(SharedVariables.fullSongsList.size() > 0)
                setDefaultNowPlayingScreen(context);

        setPlayPauseIcon();
        setNowPlayingControlButtons(context);
    }
    private static void setDefaultNowPlayingScreen(Context context) {
        MusicService.currentSong = SharedVariables.fullSongsList.get(0);
        updateNowPlayingUI(context);
    }
    private static void setPlayPauseIcon() {
        if(PlayerConstants.SONG_PAUSED)
            nowPlayingPlayButton.setImageResource(R.mipmap.ic_play_arrow_white_36dp);
        else
            nowPlayingPlayButton.setImageResource(R.mipmap.ic_pause_white_36dp);
    }
    private static void setNowPlayingAlbumArt(Context context, Song currentPlayingSong) {
        String albumArtPath = currentPlayingSong.getAlbumArtLocation();
        final Bitmap bitmap;

        if(albumArtPath != null) {
            final File imgFile = new File(albumArtPath);
            if (imgFile.exists())
            {
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
        nowPlayingPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {Controls.previousControl(context);}
        });
        nowPlayingPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayerConstants.SONG_PAUSED)
                    Controls.playControl(context);

                else
                    Controls.pauseControl(context);
            }
        });
    }
}
