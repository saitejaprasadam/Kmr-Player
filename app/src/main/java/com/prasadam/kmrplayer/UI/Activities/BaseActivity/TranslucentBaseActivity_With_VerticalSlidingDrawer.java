package com.prasadam.kmrplayer.UI.Activities.BaseActivity;

import android.widget.FrameLayout;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/19/2016.
 */

public class TranslucentBaseActivity_With_VerticalSlidingDrawer extends VerticalSlidingDrawerBaseActivity {

    protected FrameLayout rootLayout;
    protected @BindView(R.id.fragment_container) FrameLayout fragmentContainer;

    @Override
    public void setContentView(final int layoutResID) {
        rootLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.base_activity_translucent_layout, null);
        FrameLayout actContent = (FrameLayout) rootLayout.findViewById(R.id.main_content);

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(actContent);
        ButterKnife.bind(this);

        ActivityHelper.setBackButtonToCustomToolbarBar(TranslucentBaseActivity_With_VerticalSlidingDrawer.this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        ActivityHelper.setDisplayHome(this);
    }
}
