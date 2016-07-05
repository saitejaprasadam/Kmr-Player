package com.prasadam.kmrplayer.socketClasses;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

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

}
