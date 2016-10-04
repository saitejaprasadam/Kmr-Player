package com.prasadam.kmrplayer.SocketClasses;

import android.content.Context;

import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/20/2016.
 */

public class ClientHelper {

    public static void requestForCurrentSong(Context context, NSD serverObject){

        IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_REQUEST_CURRENT_SONG, ExtensionMethods.getTimeStamp());
        Client GroupPlayInitiateRequestClient = new Client(serverObject.getHostAddress(), iRequestMessage);
        GroupPlayInitiateRequestClient.execute();
    }
    public static void requestForGroupPlay(Context context, NSD serverObject){

        IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_INITIATE_GROUP_PLAY_REQUEST, ExtensionMethods.getTimeStamp());
        Client GroupPlayInitiateRequestClient = new Client(serverObject.getHostAddress(), iRequestMessage);
        GroupPlayInitiateRequestClient.execute();
    }
    public static void requstForQuickShare(Context context, NSD serverObject, String timeStamp, ArrayList<Song> QuickShareSongsList, ArrayList<String> QuickSharePathList){

        IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST, timeStamp, String.valueOf(QuickSharePathList.size()), QuickShareSongsList);
        Client quickShareClient = new Client(serverObject.GetClientNSD().getHost(), iRequestMessage);
        quickShareClient.execute();
    }
}
