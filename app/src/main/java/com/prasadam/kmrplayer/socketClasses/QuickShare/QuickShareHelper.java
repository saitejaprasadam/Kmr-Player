package com.prasadam.kmrplayer.socketClasses.QuickShare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class QuickShareHelper {

    private static Map<String, Map<String, ArrayList<String>>> QuickShareHashMap = new HashMap<>();

    public static void addQuickShareRequest(String hostAddress, String timeStamp, ArrayList<String> songsList){
        HashMap<String, ArrayList<String>> temp = new HashMap<>();
        temp.put(hostAddress, songsList);
        QuickShareHashMap.put(timeStamp, temp);
    }

    public static void removeQuickShareRequest(String timeStamp){
        QuickShareHashMap.remove(timeStamp);
    }
}
