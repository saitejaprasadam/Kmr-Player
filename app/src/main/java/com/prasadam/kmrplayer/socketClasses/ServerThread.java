package com.prasadam.kmrplayer.socketClasses;

import android.content.Context;
import android.util.Log;

import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Created by Prasadam Saiteja on 7/2/2016.
 */

public class ServerThread extends Thread {

    private static ServerSocket serverSocket;
    private static Context context;

    public ServerThread(Context context){
        try{
            this.context = context;
            serverSocket = new ServerSocket(SharedVariables.socketSeverPortAddress);
        }
        catch (java.io.IOException ignored){}
    }

    public void run(){
            while (true) {
                try{
                    Socket clientSocket = serverSocket.accept();
                    ServerResponseThread socketServerReplyThread = new ServerResponseThread(clientSocket, context);
                    socketServerReplyThread.run();
                }

                catch (Exception ignore){}
            }
    }
}