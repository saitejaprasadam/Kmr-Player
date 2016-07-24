package com.prasadam.kmrplayer.SocketClasses.GroupPlay;

import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/18/2016.
 */

public class GroupPlayHelper {

    private static ArrayList<String> GroupPlayClientsList = new ArrayList<>();
    private static String GroupPlayMasterIPAddress;
    private static GroupPlayReceiver groupPlayReceiver;
    private static boolean isMaster = false;

    public static void AddNewClientInGroupPlay(String clientAddress){

        if(!GroupPlayClientsList.contains(clientAddress)){
            GroupPlayClientsList.add(clientAddress);
            isMaster = true;
        }

    }
    public static void DisconnectClientFromGroupPlay(String clientAddress){
        if(GroupPlayClientsList.contains(clientAddress))
            GroupPlayClientsList.remove(clientAddress);

        if(GroupPlayClientsList.size() == 0)
            isMaster = false;
    }

    public static boolean IsClientConntectedToGroupPlay(String clientAddress){
        return GroupPlayClientsList.contains(clientAddress);
    }
    public static boolean IsClientGroupPlayMaster(String clientAddress) {
        return GroupPlayMasterIPAddress != null && GroupPlayMasterIPAddress.equals(clientAddress);
    }

    public static void setGroupPlayMaster(String groupPlayMasterIPAddress){
        GroupPlayClientsList.clear();
        GroupPlayMasterIPAddress = groupPlayMasterIPAddress;
        groupPlayReceiver = new GroupPlayReceiver();
        groupPlayReceiver.execute();
    }
    public static void DisconnectFromGroupPlayMaster(){
        GroupPlayClientsList.clear();
        GroupPlayMasterIPAddress = null;
        groupPlayReceiver = null;
    }
    public static boolean IsMaster(){
        return isMaster;
    }

    public static void notifyGroupPlayClientsIfExists() {

        if(IsMaster() && GroupPlayClientsList.size() > 0){

            SocketExtensionMethods.requestStrictModePermit();
            GroupPlaySenderHelper senderHelper = new GroupPlaySenderHelper(GroupPlayClientsList);
            senderHelper.execute();
        }
    }
}
