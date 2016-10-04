package com.prasadam.kmrplayer.UI.Activities.BaseActivity;

import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/19/2016.
 */

public class TranslucentBaseActivity extends AppCompatActivity{

    protected FrameLayout fragmentContainer;

    @Override
    public void setContentView(final int layoutResID) {
        FrameLayout rootLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.base_activity_translucent_layout, null);
        fragmentContainer = (FrameLayout) rootLayout.findViewById(R.id.translucent_main_content);

        getLayoutInflater().inflate(layoutResID, fragmentContainer, true);
        super.setContentView(rootLayout);
        ButterKnife.bind(this);

        ActivityHelper.setBackButtonToCustomToolbarBar(TranslucentBaseActivity.this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome(this);
    }
}
