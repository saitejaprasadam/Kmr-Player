package com.prasadam.kmrplayer.SocketClasses;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.StrictMode;

import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDServer;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class SocketExtensionMethods {

    public enum EVENT_STATE {
        WAITING, Denied, Approved, Completed
    }
    public enum TRANSFER_STATE{
        WAITING, Denied, Completed
    }

    public static void stopNSDServies(){

        if(NSDServer.mNsdManager != null)
            NSDServer.mNsdManager.unregisterService(NSDServer.mRegistrationListener);

        if(NSDClient.mNsdManager != null)
            NSDClient.mNsdManager.stopServiceDiscovery(NSDClient.mDiscoveryListener);
        NSDClient.devicesList = null;
    }
    public static void startNSDServices(Context context){
        NSDServer.startService(context);
        NSDClient.startSearch(context);
        new Thread(new ServerThread(context)).start();
    }

    public static void requestStrictModePermit(){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
    }

    public static Event GenerateSocketEventMessage(Context context, String command, String timeStamp){
        return new Event(getMACAddress(), ExtensionMethods.deviceName(context).replaceAll(KeyConstants.SPACE, KeyConstants.SPECIAL_CHAR), command, timeStamp);
    }
    public static Event GenerateSocketEventMessage(Context context, String command, String timeStamp, String result){
        Event event = new Event(getMACAddress(), ExtensionMethods.deviceName(context).replaceAll(KeyConstants.SPACE, KeyConstants.SPECIAL_CHAR), command, timeStamp);
        event.setResult(result);
        return event;
    }
    public static Event GenerateSocketEventMessage(Context context, String command, String timeStamp, String result, ArrayList<Song> transferSongsList){
        Event event = new Event(context, getMACAddress(), ExtensionMethods.deviceName(context).replaceAll(KeyConstants.SPACE, KeyConstants.SPECIAL_CHAR), command, timeStamp, transferSongsList);
        event.setResult(result);
        return event;
    }


    public static void requestForDeviceType(Context context, NsdServiceInfo nsdClient) {
        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_REQUEST_DEVICE_TYPE, ExtensionMethods.getTimeStamp());
        Client client = new Client(nsdClient.getHost(), eventMessage);
        client.execute();
    }
    public static String getDeviceType(Context context){
        if(ExtensionMethods.isTablet(context))
            return KeyConstants.TABLET;
        else
            return KeyConstants.MOBILE;
    }
    public static int getDeviceImage(String DEVICE_TYPE){

        if(DEVICE_TYPE == null)
            return R.mipmap.android_device;

        switch (DEVICE_TYPE){

            case KeyConstants.MOBILE:
                return R.mipmap.android_device;

            case KeyConstants.TABLET:
                return R.mipmap.tablet;

            case KeyConstants.IPad:
                return R.mipmap.ipad;

            case KeyConstants.Iphone:
                return R.mipmap.iphone_6s;

            case KeyConstants.Mac:
                return R.mipmap.apple_mac;

            case KeyConstants.MacBook:
                return R.mipmap.macbook_pro;

            case KeyConstants.PC:
                return R.mipmap.apple_mac;
        }

        return R.mipmap.android_device;
    }

    public static void requestForCurrentSongPlaying(Context context, NsdServiceInfo nsdClient) {
        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_REQUEST_CURRENT_SONG_NAME, ExtensionMethods.getTimeStamp());
        Client client = new Client(nsdClient.getHost(), eventMessage);
        client.execute();
    }

    public static void requestForMacAddress(Context context, NsdServiceInfo nsdClient) {
        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_REQUEST_MAC_ADDRESS, ExtensionMethods.getTimeStamp());
        Client client = new Client(nsdClient.getHost(), eventMessage);
        client.execute();
    }
    public static String getMACAddress(){

        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)){
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac==null){
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length()>0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ignored) { }
        return "02:00:00:00:00:00";
    }
}