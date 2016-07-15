package com.prasadam.kmrplayer.socketClasses.QuickShare;

import android.os.AsyncTask;

import com.prasadam.kmrplayer.sharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class QuickShareClient extends AsyncTask<Void, Void, Void> {

    private ArrayList<String> songsPathList;
    private Socket QuickShareClientSocket;
    private String timeStamp;

    public QuickShareClient(InetAddress serverIPAddress, ArrayList<String> songsPathList, String timeStamp) {
        try{
            this.songsPathList = songsPathList;
            this.timeStamp = timeStamp;
            QuickShareClientSocket = new Socket(serverIPAddress, SharedVariables.socketSeverPortAddress);
        }
        catch (IOException ignored){}
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            OutputStream os = QuickShareClientSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            String sendMessage = KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER + " " + timeStamp +" "+ songsPathList.size();
            bw.write(sendMessage);
            bw.flush();
            bw.close();
            osw.close();
            os.close();
            QuickShareClientSocket.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}