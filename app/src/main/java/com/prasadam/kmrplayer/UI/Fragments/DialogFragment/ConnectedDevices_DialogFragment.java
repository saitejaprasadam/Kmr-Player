package com.prasadam.kmrplayer.UI.Fragments.DialogFragment;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NetworkAdapter.ConnectedDevicesAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 9/30/2016.
 */

@SuppressLint("ValidFragment")
public class ConnectedDevices_DialogFragment extends DialogFragment {

    @BindView(R.id.connected_devices_recycler_view) RecyclerView connectedDevicesRecyclerView;
    @BindView(R.id.fragment_container) FrameLayout fragmentConatiner;

    @OnClick(R.id.connected_devices_info)
    public void infoOnClick(View view){
        Toast.makeText(getContext(), getContext().getResources().getString(R.string.connected_devices_info), Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_connected_devices, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
    public void onResume() {
        super.onResume();
        setParamsLayout();
        initRecyclerView();
    }

    private void setParamsLayout() {
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int height = size.y;
        int width = size.x;
        window.setLayout((int) (width * 0.98), (int) (height * 0.65));
        window.setGravity(Gravity.CENTER);
    }
    private void initRecyclerView() {
        ConnectedDevicesAdapter connectedDevicesAdapter = new ConnectedDevicesAdapter(getContext());
        connectedDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        connectedDevicesRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        connectedDevicesRecyclerView.setAdapter(connectedDevicesAdapter);

        if(PlayerConstants.groupListeners.size() == 0)
            ActivityHelper.showEmptyFragmentChildFragment(this, getContext().getString(R.string.no_connected_devices), fragmentConatiner);
    }
}