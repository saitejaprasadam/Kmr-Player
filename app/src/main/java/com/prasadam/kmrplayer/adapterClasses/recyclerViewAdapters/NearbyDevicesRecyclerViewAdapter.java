package com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
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
import com.prasadam.kmrplayer.ActivityHelperClasses.SharedPreferenceHelper;
import com.prasadam.kmrplayer.NearbyDevicesActivity;
import com.prasadam.kmrplayer.QuickShareActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.ClientHelper;
import com.prasadam.kmrplayer.SocketClasses.GroupPlay.GroupPlayHelper;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SocketClasses.QuickShare.QuickShareHelper;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

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
        View view;
        if(mActivity.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_NEARBY_DEVICES))
            view = inflater.inflate(R.layout.recycler_view_near_by_devices, parent, false);
        else
            view = inflater.inflate(R.layout.recycler_view_quick_share, parent, false);
        return new ViewAdapter(view);
    }
    public void onBindViewHolder(NearbyDevicesRecyclerViewAdapter.ViewAdapter holder, int position) {

        final NSD serverObject = NSDClient.devicesList.get(position);

        if(serverObject.GetDeviceType() == null)
            SocketExtensionMethods.requestForDeviceType(serverObject.GetClientNSD());

        SocketExtensionMethods.requestForCurrentSongPlaying(serverObject.GetClientNSD());

        holder.nearbyDeviceNameTextView.setText(serverObject.GetClientNSD().getServiceName());
        if(mActivity.getClass().getSimpleName().equals(KeyConstants.ACTIVITY_QUICK_SHARE))
            setHolderQuickShareActivity(holder, serverObject);
        else
            setHolderNearByActivity(holder, serverObject);

        if(holder.imageID == 0)
            holder.imageID = SocketExtensionMethods.getDeviceImage(serverObject.GetDeviceType());

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

    public void setQuickShareSongPathList(ArrayList<String> songPathList){
        this.QuickSharePathList = songPathList;
    }

    private void setHolderQuickShareActivity(ViewAdapter holder, final NSD serverObject) {
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
                ClientHelper.requstForQuickShare(serverObject, timeStamp, QuickSharePathList);
            }
        });
    }

    private void setHolderNearByActivity(ViewAdapter holder, final NSD serverObject) {

        if(GroupPlayHelper.IsClientConntectedToGroupPlay(serverObject.getHostAddress()))
            holder.nearbyDevicesContextMenu.setImageResource(R.mipmap.ic_surround_sound_black_24dp);

        else if(GroupPlayHelper.IsClientGroupPlayMaster(serverObject.getHostAddress()))
            holder.nearbyDevicesContextMenu.setImageResource(R.mipmap.ic_hearing_black_24dp);

        if(serverObject.getCurrentSongPlaying() != null)
            holder.currentSongTextView.setText(mActivity.getResources().getString(R.string.now_playing_text) + KeyConstants.SPACE + serverObject.getCurrentSongPlaying());
        else
            holder.currentSongTextView.setText(mActivity.getResources().getString(R.string.problem_fetching_current_playing_song));

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> dialogOptions = PopulateDialogItems(serverObject);
                new MaterialDialog.Builder(mActivity)
                        .items(dialogOptions)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                               if(text.equals(mActivity.getResources().getString(R.string.request_for_group_play_text)))
                                   ClientHelper.requestForGroupPlay(serverObject);

                                else if(text.equals(mActivity.getResources().getString(R.string.get_current_playing_song)))
                                   ClientHelper.requestForCurrentSong(serverObject);

                                   else if(text.equals(mActivity.getResources().getString(R.string.accept_all_transfers_without_confirmation)))
                                       SharedPreferenceHelper.setClientTransferRequestAlwaysAccept(mActivity, serverObject.getMacAddress(), true);

                                        else if(text.equals(mActivity.getResources().getString(R.string.prompt_confirmation_before_initating_transfers)))
                                            SharedPreferenceHelper.setClientTransferRequestAlwaysAccept(mActivity, serverObject.getMacAddress(), false);
                            }
                        })
                        .show();
            }
        });
    }

    private ArrayList<String> PopulateDialogItems(NSD serverObject) {

        ArrayList<String> dialogOptions = new ArrayList<>();

        if(GroupPlayHelper.IsClientGroupPlayMaster(serverObject.getHostAddress()) || GroupPlayHelper.IsClientConntectedToGroupPlay(serverObject.getHostAddress()))
            dialogOptions.add(mActivity.getResources().getString(R.string.disconnect_from_group_play_text));
        else
            dialogOptions.add(mActivity.getResources().getString(R.string.request_for_group_play_text));

        if(serverObject.getCurrentSongPlaying() != null)
            dialogOptions.add(mActivity.getResources().getString(R.string.get_current_playing_song));

        if(serverObject.getMacAddress() != null){
            if(!SharedPreferenceHelper.getClientTransferRequestAlwaysAccept(mActivity, serverObject.getMacAddress()))
                dialogOptions.add(mActivity.getResources().getString(R.string.accept_all_transfers_without_confirmation));
            else
                dialogOptions.add(mActivity.getResources().getString(R.string.prompt_confirmation_before_initating_transfers));
        }


        return dialogOptions;
    }

    public class ViewAdapter extends RecyclerView.ViewHolder{

        @Bind(R.id.rootLayout_recycler_view) RelativeLayout rootLayout;
        @Bind(R.id.nearby_devices_album_art) ImageView nearbyDevicesImageView;
        @Bind(R.id.nearby_devices_context_menu) ImageView nearbyDevicesContextMenu;
        @Bind(R.id.device_name_textview) TextView nearbyDeviceNameTextView;
        @Bind(R.id.current_song_playing) TextView currentSongTextView;
        private int imageID = 0;

        public ViewAdapter(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            currentSongTextView.setSelected(true);
        }
    }
}