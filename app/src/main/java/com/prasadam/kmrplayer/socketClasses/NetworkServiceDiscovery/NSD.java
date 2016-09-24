package com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;

import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

/*
 * Created by Prasadam Saiteja on 7/19/2016.
 */

public class NSD {

    private NsdServiceInfo NSDClient;
    private String DEVICE_TYPE = null;
    private String DEVICE_MAC_ADDRESS = null;
    private Song currentSongPlaying = null;

    public NSD(Context context, NsdServiceInfo NSDClient){
        this.NSDClient = NSDClient;
        SocketExtensionMethods.requestForDeviceType(context, NSDClient);
        SocketExtensionMethods.requestForCurrentSongPlaying(context, NSDClient);
        SocketExtensionMethods.requestForMacAddress(context, NSDClient);
    }
    public String GetDeviceType(){ return DEVICE_TYPE; }

    public NsdServiceInfo GetClientNSD(){ return NSDClient; }

    public void setDEVICE_TYPE(String DEVICE_TYPE){this.DEVICE_TYPE = DEVICE_TYPE; }

    public String getHostAddress(){
        return NSDClient.getHost().toString().replace("/", "");
    }
    public void setMacAddress(String MacAddress){
        DEVICE_MAC_ADDRESS = MacAddress;
    }
    public String getMacAddress(){
        return DEVICE_MAC_ADDRESS;
    }

    public void setCurrentSongPlaying(Song currentSongPlaying){
        this.currentSongPlaying = currentSongPlaying;
    }
    public Song getCurrentSongPlaying(){
        return currentSongPlaying;
    }
    public String getCurrentSongTitle(Context context){

        if(currentSongPlaying != null){
            String SongName = currentSongPlaying.getTitle();
            if(currentSongPlaying.getArtist().length() > 0)
                SongName = SongName + KeyConstants.SPACE + context.getResources().getString(R.string.by_text) + KeyConstants.SPACE + currentSongPlaying.getArtist();
            return SongName;
        }

        else
            return context.getResources().getString(R.string.problem_fetching_current_playing_song);
    }
}
