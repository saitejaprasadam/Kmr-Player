package com.prasadam.kmrplayer.socketClasses;

import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.Nullable;

import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class SocketExtensionMethods {

    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress())
                        return inetAddress.getHostAddress();

                }
            }

        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

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
        return ExtensionMethods.deviceName() + KeyConstants.DIVIDER + command + KeyConstants.DIVIDER + timeStamp + KeyConstants.DIVIDER + result;
    }

    public static String GenerateSocketMessage(String command, String timeStamp){
        return ExtensionMethods.deviceName() + KeyConstants.DIVIDER + command + KeyConstants.DIVIDER + timeStamp;
    }
}
