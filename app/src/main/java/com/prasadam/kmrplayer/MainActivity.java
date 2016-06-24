package com.prasadam.kmrplayer;

import android.content.Intent;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.fragments.AlbumsFragment;
import com.prasadam.kmrplayer.fragments.SongsFragment;
import com.prasadam.kmrplayer.fragments.TabFragment;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.util.Calendar;

import static com.prasadam.kmrplayer.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


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

            //if (headerImageView != null)
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
            } else {
                super.onBackPressed();
            }
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
}
