package com.prasadam.kmrplayer.SocketClasses.QuickShare;

import android.content.Context;
import android.os.AsyncTask;

import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.FileSender;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class InitiateQuickShare extends AsyncTask<Void, Void, Void>{

    private Event event;
    private ArrayList<String> songsList;
    private Context context;

    public InitiateQuickShare(Context context, Event event, ArrayList<String> songsList){
        this.event = event;
        this.songsList = new ArrayList<>(songsList);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        for (String song : songsList) {
            FileSender nioClient = new FileSender(context, event);
            nioClient.sendFile(song);
            nioClient.endConnection();
        }

        return null;
    }
}
