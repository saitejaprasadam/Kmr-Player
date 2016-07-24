package com.prasadam.kmrplayer.SocketClasses;

import android.os.AsyncTask;
import android.util.Log;

import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class Client extends AsyncTask<Void, Void, Void> {

    private Socket clientSocket;
    private String message;

    public Client(final InetAddress serverIPAddress, final String message) {
        try{
            this.message = message;
            clientSocket = new Socket(serverIPAddress, KeyConstants.MAIN_SERVER_SOCKET_PORT_ADDRESS);
        }
        catch (IOException ignored){}
    }
    public Client(final String serverIPAddress, final String message) {
        try{
            this.message = message;
            clientSocket = new Socket(serverIPAddress, KeyConstants.MAIN_SERVER_SOCKET_PORT_ADDRESS);
        }
        catch (IOException ignored){}
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            if(clientSocket == null)
                return null;

            OutputStream os = clientSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            Log.d("Sent", message);
            bw.write(message);
            bw.flush();
            bw.close();
            osw.close();
            os.close();
            clientSocket.close();
        }

        catch (IOException | RuntimeException exception) {
            Log.e("Exception", String.valueOf(exception));
        }
        return null;
    }
}
