package com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.DatabaseHelper.db4oHelper;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.UI.Activities.BaseActivity.TranslucentBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/22/2016.
 */

public class TransfersActivity extends TranslucentBaseActivity{

    @BindView (R.id.transfer_recycler_view) RecyclerView transfersRecyclerView;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_transfers_layout);
        ButterKnife.bind(this);

        if(SharedVariables.fullTransferList.size() == 0)
            SharedVariables.fullTransferList = db4oHelper.getTransferableSongObjects(this);

        InitRecyclerView();
    }

    private void InitRecyclerView() {

        if(SharedVariables.fullTransferList.size() == 0)
            ActivityHelper.showEmptyFragment(this, "No transfer history", fragmentContainer);

        else{
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            transfersRecyclerView.setLayoutManager(mLayoutManager);
            transfersRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            //transfersRecyclerView.setAdapter();
        }
    }
}
