package com.prasadam.kmrplayer.socketClasses.GroupPlay;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/18/2016.
 */

public class GroupPlayHelper {

    private static ArrayList<String> GroupPlayClientList = new ArrayList<>();
    private static String GroupPlayMaster;

    public static void AddNewClientInGroupPlay(String clientAddress){
        if(!GroupPlayClientList.contains(clientAddress)){
            GroupPlayClientList.add(clientAddress);
        }

    }
    public static void RemoveClientFromGroupPlay(String clientAddress){
        if(GroupPlayClientList.contains(clientAddress))
            GroupPlayClientList.remove(clientAddress);
    }
    public static boolean IsClientConntectedToGroupPlay(String clientAddress){
        return GroupPlayClientList.contains(clientAddress);
    }
    public static void setGroupPlayMaster(String groupPlayMaster){
        GroupPlayClientList.clear();
        GroupPlayMaster = groupPlayMaster;
    }
    public static boolean IsClientGroupPlayMaster(String clientAddress) {
        return GroupPlayMaster != null && GroupPlayMaster.equals(clientAddress);
    }
}
