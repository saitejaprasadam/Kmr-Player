package com.prasadam.smartcast;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;

import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.audioPackages.fragments.AlbumsFragment;
import com.prasadam.smartcast.audioPackages.fragments.SongsFragment;
import com.prasadam.smartcast.audioPackages.fragments.TabFragment;
import com.prasadam.smartcast.audioPackages.musicServiceClasses.MusicService;
import com.prasadam.smartcast.audioPackages.musicServiceClasses.PlayerConstants;
import com.prasadam.smartcast.audioPackages.musicServiceClasses.UtilFunctions;
import com.prasadam.smartcast.sharedClasses.SharedVariables;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import static com.prasadam.smartcast.audioPackages.AudioExtensionMethods.getCustomPlaylistNames;
import static com.prasadam.smartcast.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //@Bind (R.id.search_view) MaterialSearchView searchView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedVariables.Initializers(this);
        startMusicService();

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setStatusBarTranslucent(MainActivity.this);
    }

    private void startMusicService() {
        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), getApplicationContext());
        if (!isServiceRunning) {
            Intent i = new Intent(getApplicationContext(), MusicService.class);
            startService(i);
        }
        else {
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        //MenuItem item = menu.findItem(R.id.action_search);
        //searchView.showVoice(true);
        //searchView.setVoiceSearch(true); //or false
        //searchView.setMenuItem(item);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh)
        {
            refreshList();
            return true;
        }

        else
            if(id == R.id.action_settings)
                Toast.makeText(MainActivity.this, "Pending", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
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


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
