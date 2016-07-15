package com.prasadam.kmrplayer.socketClasses.QuickShare;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class InitiateQuickShare {

    private String timeStamp, hostAddress;
    private ArrayList<String> songsList;

    public InitiateQuickShare(String timeStamp, String hostAddress, ArrayList<String> songsList){
        this.timeStamp = timeStamp;
        this.hostAddress = hostAddress;
        this.songsList = songsList;

    }
}
