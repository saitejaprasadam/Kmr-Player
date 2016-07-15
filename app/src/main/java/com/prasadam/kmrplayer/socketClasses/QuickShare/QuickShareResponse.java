package com.prasadam.kmrplayer.socketClasses.QuickShare;

import android.os.AsyncTask;

import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class QuickShareResponse extends AsyncTask<Void, Void, Void> {

    private Socket QuickShareResponseSocket;
    private String result;

    public QuickShareResponse(String serverIPAddress, String result) {
        try{
            this.result = result;
            QuickShareResponseSocket = new Socket(serverIPAddress, SharedVariables.socketSeverPortAddress);
        }
        catch (IOException | android.os.NetworkOnMainThreadException e){ e.printStackTrace(); }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            OutputStream os = QuickShareResponseSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            String sendMessage = result;
            bw.write(sendMessage);
            bw.flush();
            bw.close();
            osw.close();
            os.close();
            QuickShareResponseSocket.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}