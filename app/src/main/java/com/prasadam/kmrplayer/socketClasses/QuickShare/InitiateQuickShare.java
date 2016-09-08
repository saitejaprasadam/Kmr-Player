package com.prasadam.kmrplayer.SocketClasses.QuickShare;

import android.os.AsyncTask;

import com.prasadam.kmrplayer.SocketClasses.FileTransfer.FileSender;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class InitiateQuickShare extends AsyncTask<Void, Void, Void>{

    private String hostAddress;
    private ArrayList<String> songsList;

    public InitiateQuickShare(String hostAddress, ArrayList<String> songsList){
        this.hostAddress = hostAddress;
        this.songsList = new ArrayList<>(songsList);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        for (String song : songsList) {
            FileSender nioClient = new FileSender(hostAddress);
            nioClient.sendFile(song);
            nioClient.endConnection();
        }

        return null;
    }
}
