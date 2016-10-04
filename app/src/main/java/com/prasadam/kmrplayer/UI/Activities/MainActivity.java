package com.prasadam.kmrplayer.UI.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.Controls;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.FabricHelpers.CustomEventHelpers;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.UI.Activities.BaseActivity.VerticalSlidingDrawerBaseActivity;
import com.prasadam.kmrplayer.UI.Activities.HelperActivities.AppIntroActivity;
import com.prasadam.kmrplayer.UI.Fragments.HelperFragments.TabFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends VerticalSlidingDrawerBaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected void onCreate(Bundle savedInstanceState) {
        checkInitalLaunch();
        setTheme(R.style.MainActivityNoActionBar);
        super.onCreate(savedInstanceState);
        SharedVariables.Initializers(this);
        setContentView(R.layout.activity_main);

        SocketExtensionMethods.startNSDServices(this);
        Intent intent = getIntent();
        if (intent != null && MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }

        initalizer();
    }
    public void onResume(){
        super.onResume();
        invalidateOptionsMenu();
    }
    private void checkInitalLaunch() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("FirstStart312", true);

                if (isFirstStart) {
                    Intent i = new Intent(MainActivity.this, AppIntroActivity.class);
                    startActivity(i);
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("FirstStart312", false);
                    e.apply();
                }
            }
        }).start();
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("notificationIntent", false))
            mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        else {
            Uri uri = intent.getData();
            if (uri != null && uri.toString().length() > 0) {

                String scheme = uri.getScheme();
                String fileName;
                if ("file".equals(scheme))
                    fileName = uri.getPath();
                else
                    fileName = uri.toString();

                Song song = AudioExtensionMethods.getSongFromPath(MainActivity.this, fileName);
                if (song != null) {
                    MusicPlayerExtensionMethods.playNext(MainActivity.this, song);
                    Controls.nextControl(MainActivity.this);
                }
                mainLayoutRootLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }
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
        ActivityHelper.nearbyDevicesCount(this, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshList();
                break;

            case R.id.action_search:
                ActivitySwitcher.launchSearchActivity(this);
                break;

            case R.id.action_settings:
                ActivitySwitcher.launchSettings(this);
                break;

            case R.id.action_equilzer:
                ActivitySwitcher.initEqualizer(this);
                break;

            case R.id.action_requests:
                ActivitySwitcher.launchRequestsActivity(this);
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(this);
                break;

            default:
                Toast.makeText(MainActivity.this, "Pending", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.main_drawer_about:
                ActivitySwitcher.launchAboutActivity(this);
                break;
        }
        return true;
    }

    private void initalizer() {
        if(SharedPreferenceHelper.getUserName(this) == null)
            new MaterialDialog.Builder(this)
                    .title(R.string.Display_Name)
                    .content(R.string.Enter_name_desc)
                    .cancelable(false)
                    .inputRange(4, 25)
                    .input(R.string.name_text, R.string.empty, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            SharedPreferenceHelper.setUsername(getBaseContext(), String.valueOf(input));
                            Toast.makeText(getBaseContext(), "Reboot application to apply changes", Toast.LENGTH_LONG).show();
                            CustomEventHelpers.registerUser(String.valueOf(input));
                        }
                    }).show();

        createTabFragment();
        setNavigationDrawer();
        ActivityHelper.setStatusBarTranslucent_PreLollipop(MainActivity.this);
        MusicPlayerExtensionMethods.startMusicService(MainActivity.this);
    }
    private void createTabFragment() {
        if (!isFinishing()) {
            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        }
    }
    private void setNavigationDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_more_vert_white_24dp));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setShowHideAnimationEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().getItem(0).setChecked(true);
        }*/
    }
    private void refreshList() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final int prevCount = SharedVariables.fullSongsList.size();
                AudioExtensionMethods.updateLists(MainActivity.this);
                if (prevCount < SharedVariables.fullSongsList.size())
                    Toast.makeText(MainActivity.this, "Songs lists updated, " + String.valueOf(SharedVariables.fullSongsList.size() - prevCount) + " songs added", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Songs lists updated, no new songs found", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityHelper.onActivityResultMethod(this, requestCode, resultCode, data, null);
    }
}
