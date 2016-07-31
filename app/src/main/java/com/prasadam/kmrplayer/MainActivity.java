package com.prasadam.kmrplayer;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prasadam.kmrplayer.ListenerClasses.GoogleLoginListeners;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.Fragments.AlbumsFragment;
import com.prasadam.kmrplayer.Fragments.ArtistFragment;
import com.prasadam.kmrplayer.Fragments.SongsFragment;
import com.prasadam.kmrplayer.Fragments.TabFragment;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import java.util.Calendar;

import static com.prasadam.kmrplayer.SharedClasses.ExtensionMethods.setStatusBarTranslucent;

public class MainActivity extends VerticalSlidingDrawerBaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static GoogleLoginListeners googleLoginListeners;
    public static Toolbar toolbar;
    public static TextView navHeaderProfileName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedVariables.Initializers(this);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }

        googleLoginListeners = new GoogleLoginListeners(MainActivity.this);
        initalizer();
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else
                super.onBackPressed();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_refresh:
                refreshList();
                break;

            case R.id.action_search:
                ActivitySwitcher.launchSearchActivity(this);
                break;

            case R.id.action_equilzer:
                ActivitySwitcher.initEqualizer(MainActivity.this);
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(MainActivity.this);
                break;

            case R.id.action_sort:
                super.onOptionsItemSelected(item);
                break;

            default:
                Toast.makeText(MainActivity.this, "Pending", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onNavigationItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.google_login){
            googleLoginListeners.signInMethod();
        }
        return true;
    }

    private void initalizer() {

        createTabFragment();
        setNavigationDrawer();
        setStatusBarTranslucent(MainActivity.this);
        MusicPlayerExtensionMethods.startMusicService(MainActivity.this);
        SocketExtensionMethods.startNSDServices(this);
    }

    private void createTabFragment() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
    }
    private void setNavigationDrawer() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_more_vert_white_24dp));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setShowHideAnimationEnabled(true);

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
            //TextView albumTextView = (TextView) header.findViewById(R.id.nav_album_name_text_view);
            navHeaderProfileName = (TextView) header.findViewById(R.id.nav_artist_name_text_view);
            //navHeaderProfilePic = (ImageView) header.findViewById(R.id.nav_header_profile_pic);

            if(timeOfDay < 6 || timeOfDay >= 19) {
                headerImageView.setImageResource(R.mipmap.material_wallpaper_dark);
                //albumTextView.setTextColor(getResources().getColor(R.color.white));
                navHeaderProfileName.setTextColor(getResources().getColor(R.color.white));
            }

        }
    }
    private void refreshList() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final int prevCount = SharedVariables.fullSongsList.size();
                AudioExtensionMethods.updateLists(MainActivity.this);
                SongsFragment.updateList();
                AlbumsFragment.updateList();
                ArtistFragment.updateList();
                if(prevCount < SharedVariables.fullSongsList.size())
                    Toast.makeText(MainActivity.this, "Songs lists updated, " + String.valueOf(SharedVariables.fullSongsList.size() - prevCount) + " songs added", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Songs lists updated, no new songs found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        googleLoginListeners.onActivityResult(requestCode, resultCode, data);
    }
}
