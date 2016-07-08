package com.prasadam.kmrplayer.socketClasses;

import android.os.AsyncTask;
import android.util.Log;

import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class QuickShareClient extends AsyncTask<Void, Void, Void> {

    private InetAddress serverIPAddress;
    private ArrayList<String> songsPathList;
    private Socket QuickShareClientSocket;

    public QuickShareClient(InetAddress serverIPAddress, ArrayList<String> songsPathList) {
        try{
            this.serverIPAddress = serverIPAddress;
            this.songsPathList = songsPathList;
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

            String sendMessage = KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER + " " + songsPathList.size();
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
