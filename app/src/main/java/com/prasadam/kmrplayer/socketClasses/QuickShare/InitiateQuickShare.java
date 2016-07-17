package com.prasadam.kmrplayer.socketClasses.QuickShare;

import android.os.AsyncTask;
import android.util.Log;

import com.prasadam.kmrplayer.socketClasses.FileTransfer.FileSender;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class InitiateQuickShare extends AsyncTask<Void, Void, Void>{

    private String timeStamp, hostAddress;
    private ArrayList<String> songsList;

    public InitiateQuickShare(String timeStamp, String hostAddress, ArrayList<String> songsList){
        this.timeStamp = timeStamp;
        this.hostAddress = hostAddress;
        this.songsList = songsList;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        FileSender nioClient = new FileSender(hostAddress);
        nioClient.sendFile(songsList.get(0));
        nioClient.endConnection();

        return null;
    }
}
