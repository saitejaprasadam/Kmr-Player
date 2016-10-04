package com.prasadam.kmrplayer.SocketClasses.QuickShare;

import android.content.Context;
import android.os.AsyncTask;

import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.Music.FileSender;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class InitiateQuickShare extends AsyncTask<Void, Void, Void>{

    private IRequest request;
    private ArrayList<String> songsList;
    private Context context;

    public InitiateQuickShare(Context context, IRequest request, ArrayList<String> songsList){
        this.request = request;
        this.context = context;
        this.songsList = new ArrayList<>(songsList);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        for (String song : songsList) {
            FileSender nioClient = new FileSender(context, request);
            nioClient.sendFile(song);
        }

        return null;
    }
}
