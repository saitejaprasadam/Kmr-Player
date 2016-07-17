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
import com.prasadam.kmrplayer.NearbyDevicesActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.NearbyDevicesRecyclerViewAdapter;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;
import com.prasadam.kmrplayer.socketClasses.FileTransfer.FileReceiver;
import com.prasadam.kmrplayer.socketClasses.GroupPlay.GroupPlayHelper;
import com.prasadam.kmrplayer.socketClasses.QuickShare.InitiateQuickShare;
import com.prasadam.kmrplayer.socketClasses.QuickShare.QuickShareHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerResponseThread extends Thread {

    private Socket clientSocket;
    private final String clientIPAddress;

    public ServerResponseThread(Socket socket){
        clientSocket = socket;
        clientIPAddress = clientSocket.getInetAddress().toString().replace("/", "");
    }

    public void run() {
        try {
            InputStream is = clientSocket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String receivedCommand = br.readLine();
            Log.d("command", receivedCommand);
            final String[] commandArray = receivedCommand.split(" ");
            String clientName = commandArray[0],
                    command = commandArray[1],
                    timeStamp = commandArray[2],
                    result = "";
            if(commandArray.length > 3)
                result = commandArray[3];

            switch (command){

                case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:
                    InitateQuickShareTransferRequest(timeStamp, clientName, result);
                    break;

                case KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT:
                    QuickShareTransferResult(clientName, timeStamp, result);
                    break;

                case KeyConstants.SOCKET_INITIATE_GROUP_PLAY_REQUEST:
                    InitateGroupPlayRequest(timeStamp, clientName);
                    break;

                case KeyConstants.SOCKET_GROUP_PLAY_RESULT:
                    GroupPlayResult(clientName, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InitateQuickShareTransferRequest(final String timeStamp, final String clientName, final String songsCount) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                new MaterialDialog.Builder(SharedVariables.globalActivityContext)
                        .content("Receive " + songsCount + " songs from " + clientName)
                        .positiveText(R.string.agree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SocketExtensionMethods.requestStrictModePermit();
                                String result = SocketExtensionMethods.GenerateSocketMessage(KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST, timeStamp, KeyConstants.SOCKET_RESULT_OK);
                                Client quickShareResponse = new Client(clientIPAddress, result);
                                quickShareResponse.execute();
                                FileReceiver nioServer = new FileReceiver();
                                nioServer.execute();
                            }
                        })
                        .negativeText(R.string.disagree)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SocketExtensionMethods.requestStrictModePermit();
                                String result = SocketExtensionMethods.GenerateSocketMessage(KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, timeStamp, KeyConstants.SOCKET_RESULT_CANCEL);
                                Client quickShareResponse = new Client(clientIPAddress, result);
                                quickShareResponse.execute();
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        });
    }

    private void QuickShareTransferResult(final String timeStamp, final String clientName, final String result) {
        if(result.equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(NearbyDevicesRecyclerViewAdapter.waitingDialog != null){
                        NearbyDevicesRecyclerViewAdapter.waitingDialog.dismiss();
                        NearbyDevicesRecyclerViewAdapter.waitingDialog = null;
                    }
                    QuickShareHelper.removeQuickShareRequest(timeStamp);
                    Toast.makeText(SharedVariables.globalActivityContext, clientName + KeyConstants.SPACE + SharedVariables.globalActivityContext.getString(R.string.quick_share_rejected), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if(result.equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(NearbyDevicesRecyclerViewAdapter.waitingDialog != null){
                        NearbyDevicesRecyclerViewAdapter.waitingDialog.dismiss();
                        NearbyDevicesRecyclerViewAdapter.waitingDialog = null;
                    }
                    InitiateQuickShare initiateQuickShare = new InitiateQuickShare(timeStamp, clientIPAddress, QuickShareHelper.getSongsList(timeStamp));
                    initiateQuickShare.execute();
                    Toast.makeText(SharedVariables.globalActivityContext, SharedVariables.globalActivityContext.getString(R.string.initating_quick_share) + KeyConstants.SPACE + clientName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InitateGroupPlayRequest(final String timeStamp, final String clientName) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                new MaterialDialog.Builder(SharedVariables.globalActivityContext)
                        .title(SharedVariables.globalActivityContext.getResources().getString(R.string.group_play_request_text))
                        .content(clientName + KeyConstants.SPACE +SharedVariables.globalActivityContext.getResources().getString(R.string.group_play_request))
                        .positiveText(R.string.agree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SocketExtensionMethods.requestStrictModePermit();
                                String reply = SocketExtensionMethods.GenerateSocketMessage(KeyConstants.SOCKET_GROUP_PLAY_RESULT, timeStamp, KeyConstants.SOCKET_RESULT_OK);
                                Client client = new Client(clientIPAddress, reply);
                                client.execute();
                                GroupPlayHelper.setGroupPlayMaster(clientIPAddress);
                                NearbyDevicesActivity.updateAdapater();
                            }
                        })
                        .negativeText(R.string.disagree)
                        .cancelable(false)
                        .show();
            }
        });
    }

    private void GroupPlayResult(final String clientName,final String result) {

        if(result.equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SharedVariables.globalActivityContext, clientName + KeyConstants.SPACE + SharedVariables.globalActivityContext.getResources().getString(R.string.group_play_rejected), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if(result.equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SharedVariables.globalActivityContext, clientName + KeyConstants.SPACE + SharedVariables.globalActivityContext.getResources().getString(R.string.group_play_accepted), Toast.LENGTH_SHORT).show();
                    GroupPlayHelper.AddNewClientInGroupPlay(clientIPAddress);
                    NearbyDevicesActivity.updateAdapater();
                }
            });
        }
    }
}