package com.prasadam.kmrplayer.SocketClasses;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NearbyDevicesAdapter;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.FileReceiver;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.FileSender;
import com.prasadam.kmrplayer.SocketClasses.GroupPlay.GroupPlayHelper;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SocketClasses.QuickShare.InitiateQuickShare;
import com.prasadam.kmrplayer.SocketClasses.QuickShare.QuickShareHelper;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.NearbyDevicesActivity;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.QuickShareActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class ServerResponseThread extends Thread {

    private Socket clientSocket;
    private final String clientIPAddress;
    private Context context;

    public ServerResponseThread(Context context, Socket socket){
        this.context = context;
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

            String clientMacAddress = commandArray[0],
                    clientName = commandArray[1].replaceAll(KeyConstants.SPECIAL_CHAR, KeyConstants.SPACE),
                    command = commandArray[2],
                    timeStamp = commandArray[3],
                    result = "";

            if(commandArray.length > 4)
                result = commandArray[4];

            switch (command){

                case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:
                    InitateQuickShareTransferRequest(timeStamp, clientName, result, clientMacAddress);
                    break;

                case KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT:
                    QuickShareTransferResult(timeStamp, clientName, result);
                    break;

                case KeyConstants.SOCKET_INITIATE_GROUP_PLAY_REQUEST:
                    InitateGroupPlayRequest(timeStamp, clientName);
                    break;

                case KeyConstants.SOCKET_GROUP_PLAY_RESULT:
                    GroupPlayResult(clientName, result);
                    break;

                case KeyConstants.SOCKET_REQUEST_DEVICE_TYPE:
                    RequestDeviceType(context);
                    break;

                case KeyConstants.SOCKET_DEVICE_TYPE_RESULT:
                    DeviceTypeResult(result);
                    break;

                case KeyConstants.SOCKET_REQUEST_CURRENT_SONG_NAME:
                    RequestCuurentSongName();
                    break;

                case KeyConstants.SOCKET_CURRENT_SONG_NAME_RESULT:
                    CurrentSongNameResult(result);
                    break;

                case KeyConstants.SOCKET_REQUEST_CURRENT_SONG:
                    RequestCurrentSong(clientName, clientMacAddress);
                    break;

                case KeyConstants.SOCKET_CURRENT_SONG_RESULT:
                    CurrentSongResult(clientName, result);
                    break;

                case KeyConstants.SOCKET_FEATURE_NOT_AVAILABLE:
                    InvalidCommandResult(result);
                    break;

                case KeyConstants.SOCKET_REQUEST_MAC_ADDRESS:
                    RequestMacAddress();
                    break;

                case KeyConstants.SOCKET_MAC_ADDRESS_RESULT:
                    MacAddressResult(result);
                    break;

                default:
                    InvalidCommand(command);
                    break;

            }
        } catch (IOException e) {
            Log.e("ServerResponseThread", String.valueOf(e));
        }
    }

    private void InitateQuickShareTransferRequest(final String timeStamp, final String clientName, final String songsCount, final String macAddress) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                if(SharedPreferenceHelper.getClientTransferRequestAlwaysAccept(context, macAddress)){
                    SocketExtensionMethods.requestStrictModePermit();
                    String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, timeStamp, KeyConstants.SOCKET_RESULT_OK);
                    Client quickShareResponse = new Client(clientIPAddress, result);
                    quickShareResponse.execute();
                    FileReceiver nioServer = new FileReceiver(context, Integer.valueOf(songsCount));
                    nioServer.execute();
                }

                else
                new MaterialDialog.Builder(context)
                        .title(context.getString(R.string.quick_share_request_text))
                        .content("Receive " + songsCount + " songs from " + clientName)
                        .positiveText(R.string.agree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SocketExtensionMethods.requestStrictModePermit();
                                String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, timeStamp, KeyConstants.SOCKET_RESULT_OK);
                                Client quickShareResponse = new Client(clientIPAddress, result);
                                quickShareResponse.execute();
                                FileReceiver nioServer = new FileReceiver(context, Integer.valueOf(songsCount));
                                nioServer.execute();
                            }
                        })
                        .negativeText(R.string.disagree)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SocketExtensionMethods.requestStrictModePermit();
                                String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, timeStamp, KeyConstants.SOCKET_RESULT_CANCEL);
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
                    if(NearbyDevicesAdapter.waitingDialog != null){
                        NearbyDevicesAdapter.waitingDialog.dismiss();
                        NearbyDevicesAdapter.waitingDialog = null;
                    }
                    QuickShareHelper.removeQuickShareRequest(timeStamp);
                    Toast.makeText(context, clientName + KeyConstants.SPACE + context.getString(R.string.quick_share_rejected), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if(result.equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(NearbyDevicesAdapter.waitingDialog != null){
                        NearbyDevicesAdapter.waitingDialog.dismiss();
                        NearbyDevicesAdapter.waitingDialog = null;
                    }

                    InitiateQuickShare initiateQuickShare = new InitiateQuickShare(clientIPAddress, QuickShareHelper.getSongsList(timeStamp));
                    initiateQuickShare.execute();
                    Toast.makeText(context, context.getString(R.string.initating_quick_share) + KeyConstants.SPACE + clientName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InitateGroupPlayRequest(final String timeStamp, final String clientName) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                new MaterialDialog.Builder(context)
                        .title(context.getResources().getString(R.string.group_play_request_text))
                        .content(clientName + KeyConstants.SPACE + context.getResources().getString(R.string.group_play_request))
                        .positiveText(R.string.agree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    SocketExtensionMethods.requestStrictModePermit();
                                    String reply = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_GROUP_PLAY_RESULT, timeStamp, KeyConstants.SOCKET_RESULT_OK);
                                    Client client = new Client(clientIPAddress, reply);
                                    client.execute();
                                    GroupPlayHelper.setGroupPlayMaster(clientIPAddress);
                                    NearbyDevicesActivity.updateAdapater();
                            }
                        })
                        .negativeText(R.string.disagree)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SocketExtensionMethods.requestStrictModePermit();
                                String reply = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_GROUP_PLAY_RESULT, timeStamp, KeyConstants.SOCKET_RESULT_CANCEL);
                                Client client = new Client(clientIPAddress, reply);
                                client.execute();
                            }
                        })
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
                    Toast.makeText(context, clientName + KeyConstants.SPACE + context.getResources().getString(R.string.group_play_rejected), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if(result.equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, clientName + KeyConstants.SPACE + context.getResources().getString(R.string.group_play_accepted), Toast.LENGTH_SHORT).show();
                    GroupPlayHelper.AddNewClientInGroupPlay(clientIPAddress);
                    NearbyDevicesActivity.updateAdapater();
                }
            });
        }
    }

    private void RequestDeviceType(Context context) {
        String message = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_DEVICE_TYPE_RESULT, ExtensionMethods.getTimeStamp(), SocketExtensionMethods.getDeviceType(context));
        Client client = new Client(clientIPAddress, message);
        client.execute();
    }
    private void DeviceTypeResult(String result) {
        for (NSD device : NSDClient.devicesList) {
            if(device.getHostAddress().equals(clientIPAddress))
                device.setDEVICE_TYPE(result);
        }

        NearbyDevicesActivity.updateAdapater();
        QuickShareActivity.updateAdapater();
    }

    private void RequestCuurentSongName() {

        if(PlayerConstants.getPlaylistSize() != 0 && PlayerConstants.getPlaylistSize() >= PlayerConstants.SONG_NUMBER){
            String SongName = PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getTitle();
            if(PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getArtist().length() > 0)
                SongName = SongName + KeyConstants.SPACE + context.getResources().getString(R.string.by_text) + KeyConstants.SPACE + PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getArtist();
            SongName = SongName.replaceAll(KeyConstants.SPACE, KeyConstants.SPECIAL_CHAR);
            String message = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_CURRENT_SONG_NAME_RESULT, ExtensionMethods.getTimeStamp(), SongName);
            Client client = new Client(clientIPAddress, message);
            client.execute();
        }
    }
    private void CurrentSongNameResult(String result) {
        for (NSD device : NSDClient.devicesList) {
            if(device.getHostAddress().equals(clientIPAddress))
                device.setCurrentSongPlaying(result.replaceAll(KeyConstants.SPECIAL_CHAR, KeyConstants.SPACE));
        }
        NearbyDevicesActivity.updateAdapater();
    }

    private void RequestCurrentSong(final String clientName, final String MacAddress) {

        if(PlayerConstants.getPlaylistSize() != 0 && PlayerConstants.getPlaylistSize() >= PlayerConstants.SONG_NUMBER) {

            final String currentSongFilePath = PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getData();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    if(SharedPreferenceHelper.getClientTransferRequestAlwaysAccept(context, MacAddress)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SocketExtensionMethods.requestStrictModePermit();
                                String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_CURRENT_SONG_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                                Client quickShareResponse = new Client(clientIPAddress, result);
                                quickShareResponse.execute();
                                try {
                                    Thread.sleep(1000);
                                    FileSender fileSender = new FileSender(clientIPAddress);
                                    fileSender.sendFile(currentSongFilePath);
                                    fileSender.endConnection();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    else
                    new MaterialDialog.Builder(context)
                            .title(context.getString(R.string.request_for_current_playing_song_text))
                            .content(clientName + KeyConstants.SPACE + context.getResources().getString(R.string.request_for_current_playing_song))
                            .positiveText(R.string.agree)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SocketExtensionMethods.requestStrictModePermit();
                                            String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_CURRENT_SONG_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                                            Client quickShareResponse = new Client(clientIPAddress, result);
                                            quickShareResponse.execute();
                                            try {
                                                Thread.sleep(1000);
                                                FileSender fileSender = new FileSender(clientIPAddress);
                                                fileSender.sendFile(currentSongFilePath);
                                                fileSender.endConnection();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            })
                            .negativeText(R.string.disagree)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    SocketExtensionMethods.requestStrictModePermit();
                                    String result = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_CURRENT_SONG_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
                                    Client quickShareResponse = new Client(clientIPAddress, result);
                                    quickShareResponse.execute();
                                }
                            })
                            .cancelable(false)
                            .show();
                }
            });
        }
    }
    private void CurrentSongResult(final String clientName,final String result){
        if(result.equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, clientName + KeyConstants.SPACE + context.getString(R.string.current_song_request_rejected), Toast.LENGTH_LONG).show();
                }
            });
        }

        else if(result.equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    FileReceiver fileReceiver = new FileReceiver(context, 1);
                    fileReceiver.execute();
                    Toast.makeText(context, context.getString(R.string.current_song_request_accepted) + KeyConstants.SPACE + clientName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InvalidCommand(final String receivedCommand) {
        String message = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_FEATURE_NOT_AVAILABLE, ExtensionMethods.getTimeStamp(), receivedCommand);
        Client invalidCommand = new Client(clientIPAddress, message);
        invalidCommand.execute();
    }
    private void InvalidCommandResult(final String result) {

    }

    private void RequestMacAddress() {
        String message = SocketExtensionMethods.GenerateSocketMessage(context, KeyConstants.SOCKET_MAC_ADDRESS_RESULT, ExtensionMethods.getTimeStamp(), SocketExtensionMethods.getMACAddress());
        Client macAddressResponse = new Client(clientIPAddress, message);
        macAddressResponse.execute();
    }
    private void MacAddressResult(final String result){
        for (NSD device : NSDClient.devicesList) {
            if(device.getHostAddress().equals(clientIPAddress))
                device.setMacAddress(result);
        }
    }
}