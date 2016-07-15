package com.prasadam.kmrplayer.socketClasses;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.NearbyDevicesRecyclerViewAdapter;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;
import com.prasadam.kmrplayer.socketClasses.QuickShare.QuickShareHelper;
import com.prasadam.kmrplayer.socketClasses.QuickShare.QuickShareResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerResponseThread extends Thread {

    private Socket clientSocket;

    public ServerResponseThread(Socket socket){
        clientSocket = socket;
    }

    public void run() {
        try {
            InputStream is = clientSocket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            final String[] commandArray = br.readLine().split(" ");
            Handler handler = new Handler(Looper.getMainLooper());
            Log.d("Command", commandArray[0]);

            switch (commandArray[0]){

                case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER:
                    SocketInitateQuickShareTransfer(commandArray, handler);
                    break;

                case KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT:
                    SocketInitiateQuickShareTransferResult(commandArray, handler);
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SocketInitiateQuickShareTransferResult(final String[] commandArray, Handler handler) {
        if(commandArray[2].equals(KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT_CANCEL)){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(NearbyDevicesRecyclerViewAdapter.waitingDialog != null){
                        NearbyDevicesRecyclerViewAdapter.waitingDialog.dismiss();
                        NearbyDevicesRecyclerViewAdapter.waitingDialog = null;
                    }
                    QuickShareHelper.removeQuickShareRequest(commandArray[1]);
                    Toast.makeText(SharedVariables.globalActivityContext, "User rejected quick share request", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else
            if(commandArray[2].equals(KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT_OK)){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(NearbyDevicesRecyclerViewAdapter.waitingDialog != null){
                            NearbyDevicesRecyclerViewAdapter.waitingDialog.dismiss();
                            NearbyDevicesRecyclerViewAdapter.waitingDialog = null;
                        }
                        Toast.makeText(SharedVariables.globalActivityContext, "Initiating transfer", Toast.LENGTH_SHORT).show();
                    }
                });
            }
    }

    private void SocketInitateQuickShareTransfer(final String[] commandArray, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                new MaterialDialog.Builder(SharedVariables.globalActivityContext)
                        .content("Receive " + commandArray[2] + " songs from " + clientSocket.getInetAddress())
                        .positiveText(R.string.agree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SocketExtensionMethods.requestStrictModePermit();
                                String result = KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT + " " + commandArray[1] + " " + KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT_OK;
                                QuickShareResponse quickShareResponse = new QuickShareResponse(clientSocket.getInetAddress().toString().replace("/", ""), result);
                                quickShareResponse.execute();
                            }
                        })
                        .negativeText(R.string.disagree)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SocketExtensionMethods.requestStrictModePermit();
                                String result = KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT + " " + commandArray[1] + " " + KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT_CANCEL;
                                QuickShareResponse quickShareResponse = new QuickShareResponse(clientSocket.getInetAddress().toString().replace("/", ""), result);
                                quickShareResponse.execute();
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        });
    }
}