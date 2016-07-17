package com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.NearbyDevicesActivity;
import com.prasadam.kmrplayer.QuickShareActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;
import com.prasadam.kmrplayer.socketClasses.Client;
import com.prasadam.kmrplayer.socketClasses.GroupPlay.GroupPlayHelper;
import com.prasadam.kmrplayer.socketClasses.NSDClient;
import com.prasadam.kmrplayer.socketClasses.QuickShare.QuickShareHelper;
import com.prasadam.kmrplayer.socketClasses.SocketExtensionMethods;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 7/5/2016.
 */

public class NearbyDevicesRecyclerViewAdapter extends RecyclerView.Adapter<NearbyDevicesRecyclerViewAdapter.ViewAdapter>{

    private ArrayList<String> QuickSharePathList;
    private LayoutInflater inflater;
    private Activity mActivity;
    private int count = 0;
    public static MaterialDialog waitingDialog;

    public NearbyDevicesRecyclerViewAdapter(Activity mActivity, Context context){
        this.mActivity = mActivity;
        inflater = LayoutInflater.from(context);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
    public NearbyDevicesRecyclerViewAdapter.ViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_near_by_devices, parent, false);
        return new ViewAdapter(view);
    }
    public void onBindViewHolder(NearbyDevicesRecyclerViewAdapter.ViewAdapter holder, int position) {

        final NsdServiceInfo serverObject = NSDClient.devicesList.get(position);
        holder.nearbyDeviceNameTextView.setText(serverObject.getServiceName());
        if(mActivity.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_QUICK_SHARE))
            setHolderQuickShareActivity(holder, serverObject);
        else
            setHolderNearByActivity(holder, serverObject);

        if(holder.imageID == 0)
            holder.imageID = getDeviceImage();
        holder.nearbyDevicesImageView.setImageResource(holder.imageID);
    }

    public int getItemCount() {
        int count = NSDClient.devicesList.size();
        if(count == 0){
            if(mActivity.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_QUICK_SHARE) && QuickShareActivity.NoDevicesTextView != null)
                QuickShareActivity.NoDevicesTextView.setVisibility(View.VISIBLE);
            else
                if(NearbyDevicesActivity.NoDevicesTextView != null)
                    NearbyDevicesActivity.NoDevicesTextView.setVisibility(View.VISIBLE);
        }

        else{
            if(mActivity.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_QUICK_SHARE) && QuickShareActivity.NoDevicesTextView != null)
                QuickShareActivity.NoDevicesTextView.setVisibility(View.INVISIBLE);
            else
            if(NearbyDevicesActivity.NoDevicesTextView != null)
                NearbyDevicesActivity.NoDevicesTextView.setVisibility(View.INVISIBLE);
        }

        return count;
    }
    public int getDeviceImage(){
        switch (count){

            case 0:
                count++;
                return R.mipmap.android_device;

            case 1:
                count++;
                return R.mipmap.apple_mac;

            case 2:
                count++;
                return R.mipmap.iphone_6s;

            case 3:
                count++;
                return R.mipmap.macbook_pro;

            default:
                count = 0;
                return R.mipmap.apple_mac;
        }
    }
    public void setQuickShareSongPathList(ArrayList<String> songPathList){
        this.QuickSharePathList = songPathList;
    }
    private void setHolderQuickShareActivity(ViewAdapter holder, final NsdServiceInfo serverObject) {
        holder.nearbyDevicesContextMenu.setImageResource(R.mipmap.ic_chevron_right_black_24dp);
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String timeStamp = ExtensionMethods.getTimeStamp();

                waitingDialog = new MaterialDialog.Builder(mActivity)
                        .content(R.string.waiting_for_approval)
                        .progress(true, 0)
                        .cancelable(false)
                        .positiveText(R.string.wait_in_background)
                        .negativeText(R.string.cancel_text)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                QuickShareHelper.removeQuickShareRequest(timeStamp);
                                waitingDialog.dismiss();
                            }
                        })
                        .show();

                QuickShareHelper.addQuickShareRequest(timeStamp, QuickSharePathList);
                String message = SocketExtensionMethods.GenerateSocketMessage(KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST, timeStamp, String.valueOf(QuickSharePathList.size()));
                Client quickShareClient = new Client(serverObject.getHost(), message);
                quickShareClient.execute();
            }
        });
    }
    private void setHolderNearByActivity(ViewAdapter holder, final NsdServiceInfo serverObject) {

        if(GroupPlayHelper.IsClientConntectedToGroupPlay(serverObject.getHost().toString().replace("/", "")))
            holder.nearbyDevicesContextMenu.setImageResource(R.mipmap.ic_surround_sound_black_24dp);

        else if(GroupPlayHelper.IsClientGroupPlayMaster(serverObject.getHost().toString().replace("/", "")))
            holder.nearbyDevicesContextMenu.setImageResource(R.mipmap.ic_hearing_black_24dp);


        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> dialogOptions = PopulateDialogItems(serverObject);
                new MaterialDialog.Builder(mActivity)
                        .items(dialogOptions)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                               if(text.equals(mActivity.getResources().getString(R.string.request_for_group_play_text))){
                                       String message = SocketExtensionMethods.GenerateSocketMessage(KeyConstants.SOCKET_INITIATE_GROUP_PLAY_REQUEST, ExtensionMethods.getTimeStamp());
                                       Client GroupPlayInitiateRequestClient = new Client(serverObject.getHost(), message);
                                       GroupPlayInitiateRequestClient.execute();
                               }

                                else if(text.equals(mActivity.getResources().getString(R.string.disconnect_from_group_play_text))){

                               }


                            }
                        })
                        .show();
            }
        });
    }

    private ArrayList<String> PopulateDialogItems(NsdServiceInfo serverObject) {

        ArrayList<String> dialogOptions = new ArrayList<>();

        if(GroupPlayHelper.IsClientGroupPlayMaster(serverObject.getHost().toString().replace("/", "")) || GroupPlayHelper.IsClientConntectedToGroupPlay(serverObject.getHost().toString().replace("/", "")))
            dialogOptions.add(mActivity.getResources().getString(R.string.disconnect_from_group_play_text));
        else
            dialogOptions.add(mActivity.getResources().getString(R.string.request_for_group_play_text));

        return dialogOptions;
    }

    public class ViewAdapter extends RecyclerView.ViewHolder{

        @Bind(R.id.rootLayout_recycler_view) RelativeLayout rootLayout;
        @Bind(R.id.nearby_devices_album_art) ImageView nearbyDevicesImageView;
        @Bind(R.id.nearby_devices_context_menu) ImageView nearbyDevicesContextMenu;
        @Bind(R.id.device_name_textview) TextView nearbyDeviceNameTextView;
        private int imageID = 0;

        public ViewAdapter(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}