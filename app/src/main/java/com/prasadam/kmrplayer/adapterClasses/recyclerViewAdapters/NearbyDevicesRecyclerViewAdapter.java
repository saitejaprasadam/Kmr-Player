package com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.socketClasses.NSDClient;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 7/5/2016.
 */

public class NearbyDevicesRecyclerViewAdapter extends RecyclerView.Adapter<NearbyDevicesRecyclerViewAdapter.ViewAdapter>{

    private Context context;
    private LayoutInflater inflater;
    private Activity mActivity;
    private int count = 0;

    public NearbyDevicesRecyclerViewAdapter(Activity mActivity, Context context){
        this.mActivity = mActivity;
        this.context = context;
        inflater = LayoutInflater.from(context);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    public NearbyDevicesRecyclerViewAdapter.ViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_near_by_devices, parent, false);
        return new ViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(NearbyDevicesRecyclerViewAdapter.ViewAdapter holder, int position) {

        final NsdServiceInfo serverObject = NSDClient.devicesList.get(position);
        holder.nearbyDeviceNameTextView.setText(serverObject.getServiceName());

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            byte[] temp = serverObject.getAttributes().get(KeyConstants.DEVICE_TYPE);
            if (temp != null && temp.toString().equals(KeyConstants.MOBILE))
                holder.nearbyDevicesImageView.setImageResource(R.mipmap.android_device);
            else if (temp != null && temp.toString().equals(KeyConstants.TABLET))
                holder.nearbyDevicesImageView.setImageResource(R.mipmap.macbook_pro);
            else
                holder.nearbyDevicesImageView.setImageResource(getDeviceImage());
        }

        else*/
        holder.nearbyDevicesImageView.setImageResource(getDeviceImage());
    }

    @Override
    public int getItemCount() {
        return NSDClient.devicesList.size();
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

    public class ViewAdapter extends RecyclerView.ViewHolder{

        @Bind(R.id.nearby_devices_album_art) ImageView nearbyDevicesImageView;
        @Bind(R.id.nearby_devices_context_menu) ImageView nearbyDevicesContextMenu;
        @Bind(R.id.device_name_textview) TextView nearbyDeviceNameTextView;

        public ViewAdapter(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
