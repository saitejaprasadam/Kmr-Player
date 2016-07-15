package com.prasadam.kmrplayer.socketClasses;

import android.os.AsyncTask;

import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.io.IOException;
import java.net.Socket;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class Client extends AsyncTask<Void, Void, Void> {

    public Socket clientSocket;

    public Client(String serverIPAddress) {
        try{
            clientSocket = new Socket(serverIPAddress, SharedVariables.socketSeverPortAddress);
        }
        catch (IOException ignored){}
    }

    @Override
    protected Void doInBackground(Void... params) {

        return null;
    }
}
