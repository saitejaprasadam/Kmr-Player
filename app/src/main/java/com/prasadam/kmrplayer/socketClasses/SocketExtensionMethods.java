package com.prasadam.kmrplayer.SocketClasses;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;

import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDServer;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class SocketExtensionMethods {

    public static void stopNSDServies(){
        NSDServer.mNsdManager.unregisterService(NSDServer.mRegistrationListener);
        NSDClient.mNsdManager.stopServiceDiscovery(NSDClient.mDiscoveryListener);
        NSDClient.devicesList = null;
    }

    public static void startNSDServices(Context context){
        NSDServer.startService(context);
        NSDClient.startSearch(context);
        Thread socketServerThread = new Thread(new ServerThread());
        socketServerThread.start();
    }

    public static void requestStrictModePermit(){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
    }

    public static String GenerateSocketMessage(String command, String timeStamp, String result){
        return getMACAddress(SharedVariables.globalActivityContext) + KeyConstants.DIVIDER + ExtensionMethods.deviceName() + KeyConstants.DIVIDER + command + KeyConstants.DIVIDER + timeStamp + KeyConstants.DIVIDER + result;
    }

    public static String GenerateSocketMessage(String command, String timeStamp){
        return getMACAddress(SharedVariables.globalActivityContext) + KeyConstants.DIVIDER + ExtensionMethods.deviceName() + KeyConstants.DIVIDER + command + KeyConstants.DIVIDER + timeStamp;
    }

    public static void requestForDeviceType(NsdServiceInfo nsdClient) {
        String message = GenerateSocketMessage(KeyConstants.SOCKET_REQUEST_DEVICE_TYPE, ExtensionMethods.getTimeStamp());
        Client client = new Client(nsdClient.getHost(), message);
        client.execute();
    }

    public static String getDeviceType(){
        if(ExtensionMethods.isTablet(SharedVariables.globalActivityContext))
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

    public static void requestForCurrentSongPlaying(NsdServiceInfo nsdClient) {
        String message = GenerateSocketMessage(KeyConstants.SOCKET_REQUEST_CURRENT_SONG_NAME, ExtensionMethods.getTimeStamp());
        Client client = new Client(nsdClient.getHost(), message);
        client.execute();
    }

    public static String getMACAddress(Context context){
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }
}
