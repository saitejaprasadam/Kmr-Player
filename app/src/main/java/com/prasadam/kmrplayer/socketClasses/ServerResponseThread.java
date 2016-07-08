package com.prasadam.kmrplayer.socketClasses;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerResponseThread extends Thread {

    private Socket clientSocket;
    private Context context;

    public ServerResponseThread(Socket socket, Context context){
        this.context = context;
        clientSocket = socket;
    }

    public void run() {
        try {
            InputStream is = clientSocket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            final String receivedCommand = br.readLine();
            final String[] commandArray = receivedCommand.split(" ");

            switch (commandArray[0]){
                case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER:
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new MaterialDialog.Builder(SharedVariables.globalActivityContext)
                                    .content("Receive " + commandArray[1] + " songs from " + clientSocket.getInetAddress())
                                    .positiveText(R.string.agree)
                                    .negativeText(R.string.disagree)
                                    .cancelable(false)
                                    .show();
                        }
                    });
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}