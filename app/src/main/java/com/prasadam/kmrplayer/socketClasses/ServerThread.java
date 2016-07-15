package com.prasadam.kmrplayer.socketClasses;

import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

import java.net.ServerSocket;
import java.net.Socket;

/*
 * Created by Prasadam Saiteja on 7/2/2016.
 */

public class ServerThread extends Thread {

    private static ServerSocket serverSocket;

    public ServerThread(){
        try{
            serverSocket = new ServerSocket(SharedVariables.socketSeverPortAddress);
        }
        catch (java.io.IOException ignored){}
    }

    public void run(){
            while (true) {
                try{
                    Socket clientSocket = serverSocket.accept();
                    ServerResponseThread socketServerReplyThread = new ServerResponseThread(clientSocket);
                    socketServerReplyThread.run();
                }

                catch (Exception ignore){}
            }
    }
}