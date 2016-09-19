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
import com.prasadam.kmrplayer.DatabaseHelper.db4oHelper;
import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.ModelClasses.Song;
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

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class ServerResponseThread extends Thread {

    private Socket clientSocket;
    private final String clientIPAddress;
    private Context context;

    public ServerResponseThread(final Context context, final Socket socket){
        this.context = context;
        clientSocket = socket;
        clientIPAddress = clientSocket.getInetAddress().toString().replace("/", "");
    }

    public void run() {
        try {
            InputStream is = clientSocket.getInputStream();
            ObjectInputStream inStream = new ObjectInputStream(is);
            Event event = (Event) inStream.readObject();
            Log.d("command", event.getClientName() + " " + event.getCommand());

            event.setClientIpAddress(clientIPAddress);
            event.setServerCurrentSong();

            switch (event.getCommand()){

                case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:
                    InitateQuickShareTransferRequest(event);
                    break;

                case KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT:
                    QuickShareTransferResult(event);
                    break;

                case KeyConstants.SOCKET_INITIATE_GROUP_PLAY_REQUEST:
                    InitateGroupPlayRequest(event);
                    break;

                case KeyConstants.SOCKET_GROUP_PLAY_RESULT:
                    GroupPlayResult(event);
                    break;

                case KeyConstants.SOCKET_REQUEST_DEVICE_TYPE:
                    RequestDeviceType(context);
                    break;

                case KeyConstants.SOCKET_DEVICE_TYPE_RESULT:
                    DeviceTypeResult(event);
                    break;

                case KeyConstants.SOCKET_REQUEST_CURRENT_SONG_NAME:
                    RequestCurrentSongName();
                    break;

                case KeyConstants.SOCKET_CURRENT_SONG_NAME_RESULT:
                    CurrentSongNameResult(event);
                    break;

                case KeyConstants.SOCKET_REQUEST_CURRENT_SONG:
                    RequestCurrentSong(event);
                    break;

                case KeyConstants.SOCKET_CURRENT_SONG_RESULT:
                    CurrentSongResult(event);
                    break;

                case KeyConstants.SOCKET_FEATURE_NOT_AVAILABLE:
                    InvalidCommandResult(event);
                    break;

                case KeyConstants.SOCKET_REQUEST_MAC_ADDRESS:
                    RequestMacAddress(event);
                    break;

                case KeyConstants.SOCKET_MAC_ADDRESS_RESULT:
                    MacAddressResult(event);
                    break;

                default:
                    InvalidCommand(event);
                    break;

            }
        } catch (Exception e) {
            Log.e("ServerResponseThread", String.valueOf(e));
        }
    }

    private void InitateQuickShareTransferRequest(final Event event) {

        if(SharedPreferenceHelper.getClientTransferRequestAlwaysAccept(context, event.getClientMacAddress())){
            event.setEventState(SocketExtensionMethods.EVENT_STATE.Approved);
            db4oHelper.pushEventObject(context, event);
            SocketExtensionMethods.requestStrictModePermit();
            Event eventMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, event.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
            Client quickShareResponse = new Client(event.getClientIpAddress(), eventMessage);
            quickShareResponse.execute();
            FileReceiver nioServer = new FileReceiver(context, event);
            nioServer.execute();
        }

        else
            db4oHelper.pushEventObject(context, event);
    }
    private void QuickShareTransferResult(final Event event) {

        if(event.getResult().equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(NearbyDevicesAdapter.waitingDialog != null){
                        NearbyDevicesAdapter.waitingDialog.dismiss();
                        NearbyDevicesAdapter.waitingDialog = null;
                    }
                    QuickShareHelper.removeQuickShareRequest(event.getTimeStamp());
                    Toast.makeText(context, event.getClientName() + KeyConstants.SPACE + context.getString(R.string.quick_share_rejected), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if(event.getResult().equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(NearbyDevicesAdapter.waitingDialog != null){
                        NearbyDevicesAdapter.waitingDialog.dismiss();
                        NearbyDevicesAdapter.waitingDialog = null;
                    }

                    InitiateQuickShare initiateQuickShare = new InitiateQuickShare(context, event, QuickShareHelper.getSongsList(event.getTimeStamp()));
                    initiateQuickShare.execute();
                    Toast.makeText(context, context.getString(R.string.initating_quick_share) + KeyConstants.SPACE + event.getClientName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InitateGroupPlayRequest(final Event event) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                new MaterialDialog.Builder(context)
                        .title(context.getResources().getString(R.string.group_play_request_text))
                        .content(event.getClientName() + KeyConstants.SPACE + context.getResources().getString(R.string.group_play_request))
                        .positiveText(R.string.agree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    SocketExtensionMethods.requestStrictModePermit();
                                    Event eventMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_PLAY_RESULT, event.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                                    Client client = new Client(clientIPAddress, eventMessage);
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
                                Event eventMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_PLAY_RESULT, event.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
                                Client client = new Client(clientIPAddress, eventMessage);
                                client.execute();
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        });
    }
    private void GroupPlayResult(final Event event) {

        if(event.getResult().equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, event.getClientName() + KeyConstants.SPACE + context.getResources().getString(R.string.group_play_rejected), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if(event.getResult().equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, event.getClientName() + KeyConstants.SPACE + context.getResources().getString(R.string.group_play_accepted), Toast.LENGTH_SHORT).show();
                    GroupPlayHelper.AddNewClientInGroupPlay(clientIPAddress);
                    NearbyDevicesActivity.updateAdapater();
                }
            });
        }
    }

    private void RequestDeviceType(final Context context) {
        Event eventMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_DEVICE_TYPE_RESULT, ExtensionMethods.getTimeStamp(), SocketExtensionMethods.getDeviceType(context));
        Client client = new Client(clientIPAddress, eventMessage);
        client.execute();
    }
    private void DeviceTypeResult(final Event event) {
        for (NSD device : NSDClient.devicesList) {
            if(device.getHostAddress().equals(clientIPAddress))
                device.setDEVICE_TYPE(event.getResult());
        }

        NearbyDevicesActivity.updateAdapater();
        QuickShareActivity.updateAdapater();
    }

    private void RequestCurrentSongName() {

        if(PlayerConstants.getPlaylistSize() != 0 && PlayerConstants.getPlaylistSize() >= PlayerConstants.SONG_NUMBER){
            Event eventMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_CURRENT_SONG_NAME_RESULT, ExtensionMethods.getTimeStamp());
            Client client = new Client(clientIPAddress, eventMessage);
            client.execute();
        }
    }
    private void CurrentSongNameResult(final Event event) {

        String SongName = event.getClientCurrentSong().getTitle();
        if(event.getClientCurrentSong().getArtist().length() > 0)
            SongName = SongName + KeyConstants.SPACE + context.getResources().getString(R.string.by_text) + KeyConstants.SPACE + event.getClientCurrentSong().getArtist();

        for (NSD device : NSDClient.devicesList) {
            if(device.getHostAddress().equals(clientIPAddress))
                device.setCurrentSongPlaying(SongName);
        }
        NearbyDevicesActivity.updateAdapater();
    }

    private void RequestCurrentSong(final Event event) {

        if(PlayerConstants.getPlaylistSize() != 0 && PlayerConstants.getPlaylistSize() >= PlayerConstants.SONG_NUMBER) {

            final String currentSongFilePath = PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getData();
            if(SharedPreferenceHelper.getClientTransferRequestAlwaysAccept(context, event.getClientMacAddress())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketExtensionMethods.requestStrictModePermit();
                        event.setEventState(SocketExtensionMethods.EVENT_STATE.Approved);
                        db4oHelper.pushEventObject(context, event);
                        ArrayList<Song> songArrayList = new ArrayList<>();
                        songArrayList.add(PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER));
                        Event eventMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_CURRENT_SONG_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK, songArrayList);
                        Client quickShareResponse = new Client(event.getClientIpAddress(), eventMessage);
                        quickShareResponse.execute();
                        try {
                            Thread.sleep(500);
                            FileSender fileSender = new FileSender(context, event);
                            fileSender.sendFile(currentSongFilePath);
                            fileSender.endConnection();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            else
                db4oHelper.pushEventObject(context, event);
        }
    }
    private void CurrentSongResult(final Event event){
        if(event.getResult().equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, event.getClientName() + KeyConstants.SPACE + context.getString(R.string.current_song_request_rejected), Toast.LENGTH_LONG).show();
                }
            });
        }

        else if(event.getResult().equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    FileReceiver fileReceiver = new FileReceiver(context, event);
                    fileReceiver.execute();
                    Toast.makeText(context, context.getString(R.string.current_song_request_accepted) + KeyConstants.SPACE + event.getClientName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InvalidCommand(final Event event) {
        Event eventMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_FEATURE_NOT_AVAILABLE, ExtensionMethods.getTimeStamp(), event.getCommand());
        Client invalidCommand = new Client(event.getClientIpAddress(), eventMessage);
        invalidCommand.execute();
    }
    private void InvalidCommandResult(final Event event) {

    }

    private void RequestMacAddress(Event event) {
        Event eventMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_MAC_ADDRESS_RESULT, ExtensionMethods.getTimeStamp(), SocketExtensionMethods.getMACAddress());
        Client macAddressResponse = new Client(event.getClientIpAddress(), eventMessage);
        macAddressResponse.execute();
    }
    private void MacAddressResult(final Event event){
        for (NSD device : NSDClient.devicesList) {
            if(device.getHostAddress().equals(event.getClientIpAddress()))
                device.setMacAddress(event.getResult());
        }
    }
}