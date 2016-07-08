package com.prasadam.kmrplayer.socketClasses;/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

import android.os.AsyncTask;

import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.io.IOException;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, Void> {

    private String serverIPAddress;
    private Socket clientSocket;

    public Client(String serverIPAddress) {
        try{
            this.serverIPAddress = serverIPAddress;
            clientSocket = new Socket(serverIPAddress, SharedVariables.socketSeverPortAddress);
        }
        catch (IOException ignored){}
    }

    @Override
    protected Void doInBackground(Void... params) {

        return null;
    }
}
