package com.prasadam.kmrplayer.socketClasses.NetworkServiceDiscovery;

import android.net.nsd.NsdServiceInfo;

import com.prasadam.kmrplayer.socketClasses.SocketExtensionMethods;

/*
 * Created by Prasadam Saiteja on 7/19/2016.
 */

public class NSD {

    private NsdServiceInfo NSDClient;
    private String DEVICE_TYPE = null;
    private String currentSongPlaying = null;

    public NSD(NsdServiceInfo NSDClient){
        this.NSDClient = NSDClient;
        SocketExtensionMethods.requestForDeviceType(NSDClient);
        SocketExtensionMethods.requestForCurrentSongPlaying(NSDClient);
    }
    public String GetDeviceType(){ return DEVICE_TYPE; }
    public NsdServiceInfo GetClientNSD(){ return NSDClient; }
    public void setDEVICE_TYPE(String DEVICE_TYPE){
        this.DEVICE_TYPE = DEVICE_TYPE; }
    public String getHostAddress(){
        return NSDClient.getHost().toString().replace("/", "");
    }
    public void setCurrentSongPlaying(String currentSongPlaying){
        this.currentSongPlaying = currentSongPlaying;
    }
    public String getCurrentSongPlaying(){
        return currentSongPlaying;
    }

}
