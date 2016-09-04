package com.prasadam.kmrplayer.SocketClasses;

import android.content.Context;

import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Created by Prasadam Saiteja on 7/2/2016.
 */

public class ServerThread extends Thread {

    private ServerSocket serverSocket;
    private Context context;

    public ServerThread(Context context){
        try{
            serverSocket = new ServerSocket(KeyConstants.MAIN_SERVER_SOCKET_PORT_ADDRESS);
            this.context = context;
        }
        catch (java.io.IOException e){
            e.printStackTrace();
        }
    }
    public void run(){
            while (true) {
                try{
                    Socket clientSocket = serverSocket.accept();
                    ServerResponseThread socketServerReplyThread = new ServerResponseThread(context, clientSocket);
                    socketServerReplyThread.run();
                }

                catch (IOException| NullPointerException e){
                    e.printStackTrace();
                }
            }
    }
}