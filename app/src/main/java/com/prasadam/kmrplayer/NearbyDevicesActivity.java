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

import static com.prasadam.kmrplayer.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 7/5/2016.
 */

public class NearbyDevicesActivity extends AppCompatActivity{

    public static NearbyDevicesRecyclerViewAdapter nearbyDevicesRecyclerviewAdapter;
    public static TextView NoDevicesTextView;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_nearby_devices);

        NoDevicesTextView = (TextView) findViewById(R.id.no_devices_available_text_view);
        ActivityHelper.setDisplayHome(this);
        setStatusBarTranslucent(NearbyDevicesActivity.this);
        setRecyclerView();
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }

    private void setRecyclerView() {

        nearbyDevicesRecyclerviewAdapter = new NearbyDevicesRecyclerViewAdapter(NearbyDevicesActivity.this, this);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.available_devices_recycler_view);
        recyclerView.setAdapter(nearbyDevicesRecyclerviewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(NearbyDevicesActivity.this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(NearbyDevicesActivity.this));
    }
    public static void updateAdapater(){
        try{
            if(nearbyDevicesRecyclerviewAdapter != null && SharedVariables.globalActivityContext != null && SharedVariables.globalActivityContext.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_NEARBY_DEVICES))
                nearbyDevicesRecyclerviewAdapter.notifyDataSetChanged();
        }
        catch (Exception ignore){}
    }

    @Override
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