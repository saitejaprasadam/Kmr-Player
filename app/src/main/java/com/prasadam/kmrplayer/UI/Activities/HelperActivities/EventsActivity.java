package com.prasadam.kmrplayer.UI.Activities.HelperActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.EventsAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.DatabaseHelper.db4oHelper;
import com.prasadam.kmrplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/14/2016.
 */

public class EventsActivity extends AppCompatActivity{

    @BindView(R.id.fragment_container) FrameLayout fragmentContainer;
    @BindView(R.id.events_recycler_view) RecyclerView eventRecyclerView;

    private static EventsAdapter eventsAdapter;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_events_layout);
        ButterKnife.bind(this);

        ActivityHelper.setBackButtonToCustomToolbarBar(this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome_CloseIcon(this);
        if(db4oHelper.getEventObjects(this).size() == 0)
            ActivityHelper.showEmptyFragment(this, "No events", fragmentContainer);
        else{
            eventsAdapter = new EventsAdapter(this);
            eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            eventRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            eventRecyclerView.setAdapter(eventsAdapter);
        }
    }
    public void onBackPressed(){
        finish();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_events_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            case R.id.action_devices_button:
                ActivitySwitcher.jumpToAvaiableDevies(this);
                break;

            case R.id.action_events_info:
                DialogHelper.showEventsInfo(this);
                break;
        }
        return true;
    }

    public static void updateEventsAdapter(){
        try{
            if(eventsAdapter != null)
                eventsAdapter.notifyDataSetChanged();
        }

        catch (Exception ignored){}

    }
}