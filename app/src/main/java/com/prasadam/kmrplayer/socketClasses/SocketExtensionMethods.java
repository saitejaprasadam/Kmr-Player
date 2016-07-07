package com.prasadam.kmrplayer.socketClasses;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

import android.content.Context;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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
    }
}
