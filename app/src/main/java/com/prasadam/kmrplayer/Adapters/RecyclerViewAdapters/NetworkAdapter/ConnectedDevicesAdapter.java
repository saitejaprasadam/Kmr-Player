package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NetworkAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 10/1/2016.
 */

public class ConnectedDevicesAdapter extends RecyclerView.Adapter<ConnectedDevicesAdapter.ViewAdapter>{

    private final Context context;
    private final LayoutInflater inflater;

    public ConnectedDevicesAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ConnectedDevicesAdapter.ViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewAdapter(inflater.inflate(R.layout.recycler_view_connected_devices, parent, false));
    }
    public void onBindViewHolder(ConnectedDevicesAdapter.ViewAdapter holder, final int position) {
        final IRequest connectedDevice = PlayerConstants.groupListeners.get(position);

        for (NSD nsd : NSDClient.devicesList)
            if(nsd.getMacAddress().equals(connectedDevice.getClientMacAddress())){
                holder.deviceObject = nsd;
                break;
            }

        holder.deviceName.setText(connectedDevice.getClientName());
        holder.connectionType.setText(context.getResources().getString(R.string.conntection_type_text) + KeyConstants.SPACE + context.getResources().getString(R.string.group_listen_text));

        if(holder.deviceObject != null)
            holder.device_art.setImageResource(SocketExtensionMethods.getDeviceImage(holder.deviceObject.GetDeviceType()));

        holder.disconnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerConstants.groupListeners.remove(connectedDevice);
                SocketExtensionMethods.disconnectDeviceFromGroupListen(context, connectedDevice);
                notifyItemRemoved(position);
            }
        });
    }

    public int getItemCount() {
        return PlayerConstants.groupListeners.size();
    }

    public class ViewAdapter extends RecyclerView.ViewHolder {

        @BindView(R.id.connected_device_art) ImageView device_art;
        @BindView(R.id.disconnect_connected_device) ImageView disconnectDevice;
        @BindView(R.id.device_name_textview) TextView deviceName;
        @BindView(R.id.connection_type) TextView connectionType;
        private NSD deviceObject = null;

        public ViewAdapter(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
