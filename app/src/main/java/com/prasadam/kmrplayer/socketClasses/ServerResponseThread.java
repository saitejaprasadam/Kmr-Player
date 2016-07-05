package com.prasadam.kmrplayer.socketClasses;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ServerResponseThread extends Thread {

    private Socket clientSocket;

    public ServerResponseThread(Socket socket){
        clientSocket = socket;
    }

    public void run() {
        OutputStream outputStream;

        try {
            outputStream = clientSocket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print("reply");
            printStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}