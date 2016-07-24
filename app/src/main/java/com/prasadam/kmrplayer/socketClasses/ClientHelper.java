package com.prasadam.kmrplayer.SocketClasses;

import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/20/2016.
 */

public class ClientHelper {

    public static void requestForCurrentSong(NSD serverObject){
        String message = SocketExtensionMethods.GenerateSocketMessage(KeyConstants.SOCKET_REQUEST_CURRENT_SONG, ExtensionMethods.getTimeStamp());
        Client GroupPlayInitiateRequestClient = new Client(serverObject.getHostAddress(), message);
        GroupPlayInitiateRequestClient.execute();
    }

    public static void requestForGroupPlay(NSD serverObject){
        String message = SocketExtensionMethods.GenerateSocketMessage(KeyConstants.SOCKET_INITIATE_GROUP_PLAY_REQUEST, ExtensionMethods.getTimeStamp());
        Client GroupPlayInitiateRequestClient = new Client(serverObject.getHostAddress(), message);
        GroupPlayInitiateRequestClient.execute();
    }

    public static void requstForQuickShare(NSD serverObject, String timeStamp, ArrayList<String> QuickSharePathList){
        String message = SocketExtensionMethods.GenerateSocketMessage(KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST, timeStamp, String.valueOf(QuickSharePathList.size()));
        Client quickShareClient = new Client(serverObject.GetClientNSD().getHost(), message);
        quickShareClient.execute();
    }
}
