package com.prasadam.kmrplayer.socketClasses.QuickShare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class QuickShareHelper {

    private static Map<String, ArrayList<String>> QuickShareHashMap = new HashMap<>();

    public static void addQuickShareRequest(String timeStamp, ArrayList<String> songsList){
        QuickShareHashMap.put(timeStamp, songsList);
    }
    public static void removeQuickShareRequest(String timeStamp){
        QuickShareHashMap.remove(timeStamp);
    }
    public static ArrayList<String> getSongsList(String timeStamp){
       return QuickShareHashMap.get(timeStamp);
    }
}
