package com.prasadam.kmrplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.prasadam.kmrplayer.activityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.activityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.NearbyDevicesRecyclerViewAdapter;
import com.prasadam.kmrplayer.adapterClasses.uiAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.util.ArrayList;

import static com.prasadam.kmrplayer.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 7/8/2016.
 */

public class QuickShareActivity extends AppCompatActivity{

    public static TextView NoDevicesTextView;
    public static NearbyDevicesRecyclerViewAdapter QuickShareRecyclerviewAdapter;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_quick_share);

        final ArrayList<String> songsPathList = getIntent().getStringArrayListExtra(KeyConstants.INTENT_SONGS_PATH_LIST);
        NoDevicesTextView = (TextView) findViewById(R.id.no_devices_available_text_view);
        InitActionBarAndToolBar();
        setRecyclerView(songsPathList);
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }

    private void InitActionBarAndToolBar() {
        setStatusBarTranslucent(QuickShareActivity.this);
        ActivityHelper.setDisplayHome(this);
    }
    private void setRecyclerView(ArrayList<String> songsPathList) {
        QuickShareRecyclerviewAdapter = new NearbyDevicesRecyclerViewAdapter(QuickShareActivity.this, this);
        QuickShareRecyclerviewAdapter.setQuickShareSongPathList(songsPathList);
        final RecyclerView quickShareRecyclerView = (RecyclerView) findViewById(R.id.quick_share_recycler_view);
        quickShareRecyclerView.setAdapter(QuickShareRecyclerviewAdapter);
        quickShareRecyclerView.addItemDecoration(new DividerItemDecoration(QuickShareActivity.this, LinearLayoutManager.VERTICAL));
        quickShareRecyclerView.setLayoutManager(new LinearLayoutManager(QuickShareActivity.this));
    }
    public static void updateAdapater(){
        if(QuickShareRecyclerviewAdapter != null && SharedVariables.globalActivityContext != null && SharedVariables.globalActivityContext.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_QUICK_SHARE))
            QuickShareRecyclerviewAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_nearby_info_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.action_nearby_info:
                DialogHelper.showNearbyInfo(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
