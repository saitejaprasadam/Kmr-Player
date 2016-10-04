package com.prasadam.kmrplayer.SocketClasses;

import android.os.AsyncTask;
import android.util.Log;

import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class Client extends AsyncTask<Void, Void, Void> {

    private Socket clientSocket;
    private IRequest requestMessage;

    public Client(final InetAddress serverIPAddress, final IRequest requestMessage) {
        try{
            this.requestMessage = requestMessage;
            clientSocket = new Socket(serverIPAddress.getHostAddress(), KeyConstants.MAIN_SERVER_SOCKET_PORT_ADDRESS);
        }
        catch (ConnectException ignored){}
        catch (Exception e){ e.printStackTrace();}
    }
    public Client(final String serverIPAddress, final IRequest requestMessage) {
        try{
            this.requestMessage = requestMessage;
            clientSocket = new Socket(serverIPAddress, KeyConstants.MAIN_SERVER_SOCKET_PORT_ADDRESS);
        }
        catch (ConnectException ignored){}
        catch (IOException e){ e.printStackTrace();}
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            if(clientSocket == null)
                return null;

            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(requestMessage);

            os.close();
            outputStream.flush();
            outputStream.close();
            clientSocket.close();
            Log.d("Sent", requestMessage.getClientName() + " " + requestMessage.getCommand());
        }

        catch (Exception exception) {
            Log.e("Exception", String.valueOf(exception));
        }
        return null;
    }
}