package com.prasadam.kmrplayer.SocketClasses;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NetworkAdapter.NearbyDevicesAdapter;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.DatabaseHelperClasses.db4oHelper;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SharedPreferences.SharedPreferenceHelper;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.Bitmap.BitmapReceiver;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.Bitmap.BitmapSender;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.Music.FileReceiver;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.Music.FileSender;
import com.prasadam.kmrplayer.SocketClasses.GroupPlay.GroupPlayHelper;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SocketClasses.QuickShare.InitiateQuickShare;
import com.prasadam.kmrplayer.SocketClasses.QuickShare.QuickShareHelper;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.GroupListenActivity;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.NearbyDevicesActivity;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.QuickShareActivity;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.RequestsActivity;

import java.io.File;
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
            IRequest request = (IRequest) inStream.readObject();
            Log.d("command", request.getClientName() + " " + request.getCommand());

            request.setClientIpAddress(clientIPAddress);
            request.setServerCurrentSong();

            switch (request.getCommand()){

                case KeyConstants.SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST:
                    InitateQuickShareTransferRequest(request);
                    break;

                case KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT:
                    QuickShareTransferResult(request);
                    break;

                case KeyConstants.SOCKET_INITIATE_GROUP_PLAY_REQUEST:
                    InitateGroupPlayRequest(request);
                    break;

                case KeyConstants.SOCKET_GROUP_PLAY_RESULT:
                    GroupPlayResult(request);
                    break;

                case KeyConstants.SOCKET_REQUEST_DEVICE_TYPE:
                    RequestDeviceType(context);
                    break;

                case KeyConstants.SOCKET_DEVICE_TYPE_RESULT:
                    DeviceTypeResult(request);
                    break;

                case KeyConstants.SOCKET_REQUEST_CURRENT_SONG_NAME:
                    RequestCurrentSongName();
                    break;

                case KeyConstants.SOCKET_CURRENT_SONG_NAME_RESULT:
                    CurrentSongNameResult(request);
                    break;

                case KeyConstants.SOCKET_REQUEST_CURRENT_SONG:
                    RequestCurrentSong(request);
                    break;

                case KeyConstants.SOCKET_CURRENT_SONG_RESULT:
                    CurrentSongResult(request);
                    break;

                case KeyConstants.SOCKET_FEATURE_NOT_AVAILABLE:
                    InvalidCommandResult(request);
                    break;

                case KeyConstants.SOCKET_REQUEST_MAC_ADDRESS:
                    RequestMacAddress(request);
                    break;

                case KeyConstants.SOCKET_MAC_ADDRESS_RESULT:
                    MacAddressResult(request);
                    break;

                case KeyConstants.SOCKET_REQUEST_ALBUM_ART:
                    RequestAlbumArt(request);
                    break;

                case KeyConstants.SOCKET_ALBUM_ART_RESULT:
                    AlbumArtResult(request);
                    break;

                case KeyConstants.SOCKET_INITIATE_GROUP_LISTEN_REQUEST:
                    RequestGroupListen(request);
                    break;

                case KeyConstants.SOCKET_GROUP_LISTEN_RESULT:
                    GroupListenResult(request);
                    break;

                case KeyConstants.SOCKET_GROUP_LISTEN_OPEN_FILE_RECEIVER:
                    SocketExtensionMethods.GroupListenStartFileReceiver(context, request);
                    break;

                case KeyConstants.SOCKET_GROUP_LISTEN_DISCONNECT:
                    SocketExtensionMethods.GroupListenDisconnect(context, request);
                    break;

                case KeyConstants.SOCKET_GROUP_LISTEN_KICK_OUT_DEVICE:
                    SocketExtensionMethods.GroupListenEndConnection(context, request);
                    break;

                default:
                    InvalidCommand(request);
                    break;

            }
        } catch (Exception e) {
            Log.e("ServerResponseThread", String.valueOf(e));
        }
    }

    private void InitateQuickShareTransferRequest(final IRequest request) {

        if(request.getSongsToTransferArrayList().size() > 0){
            if(SharedPreferenceHelper.getClientTransferRequestAlwaysAccept(context, request.getClientMacAddress())){
                request.setEventState(SocketExtensionMethods.EVENT_STATE.Approved);
                db4oHelper.pushRequestObject(context, request);
                db4oHelper.pushSongTransferObject(context, request.getSongsToTransferArrayList());

                SocketExtensionMethods.requestStrictModePermit();
                IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_QUICK_SHARE_TRANSFER_RESULT, request.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                Client quickShareResponse = new Client(request.getClientIpAddress(), iRequestMessage);
                quickShareResponse.execute();
                FileReceiver nioServer = new FileReceiver(context, request);
                nioServer.execute();
            }

            else
                RequestsActivity.pushRequestObjectAndNotify(context, request);
        }
    }
    private void QuickShareTransferResult(final IRequest request) {

        if(request.getResult().equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    NearbyDevicesAdapter.dismissMaterialDialog();
                    QuickShareHelper.removeQuickShareRequest(request.getTimeStamp());
                    Toast.makeText(context, request.getClientName() + KeyConstants.SPACE + context.getString(R.string.quick_share_rejected), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if(request.getResult().equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    NearbyDevicesAdapter.dismissMaterialDialog();
                    InitiateQuickShare initiateQuickShare = new InitiateQuickShare(context, request, QuickShareHelper.getSongsList(request.getTimeStamp()));
                    initiateQuickShare.execute();
                    Toast.makeText(context, context.getString(R.string.initating_quick_share) + KeyConstants.SPACE + request.getClientName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InitateGroupPlayRequest(final IRequest request) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                new MaterialDialog.Builder(context)
                        .title(context.getResources().getString(R.string.group_play_request_text))
                        .content(request.getClientName() + KeyConstants.SPACE + context.getResources().getString(R.string.group_play_request))
                        .positiveText(R.string.agree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    SocketExtensionMethods.requestStrictModePermit();
                                    IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_PLAY_RESULT, request.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                                    Client client = new Client(clientIPAddress, iRequestMessage);
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
                                IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_PLAY_RESULT, request.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
                                Client client = new Client(clientIPAddress, iRequestMessage);
                                client.execute();
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        });
    }
    private void GroupPlayResult(final IRequest request) {

        if(request.getResult().equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, request.getClientName() + KeyConstants.SPACE + context.getResources().getString(R.string.group_play_rejected), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if(request.getResult().equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, request.getClientName() + KeyConstants.SPACE + context.getResources().getString(R.string.group_play_accepted), Toast.LENGTH_SHORT).show();
                    GroupPlayHelper.AddNewClientInGroupPlay(clientIPAddress);
                    NearbyDevicesActivity.updateAdapater();
                }
            });
        }
    }

    private void RequestDeviceType(final Context context) {
        IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_DEVICE_TYPE_RESULT, ExtensionMethods.getTimeStamp(), SocketExtensionMethods.getDeviceType(context));
        Client client = new Client(clientIPAddress, iRequestMessage);
        client.execute();
    }
    private void DeviceTypeResult(final IRequest request) {
        for (NSD device : NSDClient.devicesList) {
            if(device.getHostAddress().equals(clientIPAddress))
                device.setDEVICE_TYPE(request.getResult());
        }

        NearbyDevicesActivity.updateAdapater();
        QuickShareActivity.updateAdapater();
    }

    private void RequestCurrentSongName() {

        if(PlayerConstants.getPlaylistSize() != 0 && PlayerConstants.getPlaylistSize() >= PlayerConstants.SONG_NUMBER){
            IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_CURRENT_SONG_NAME_RESULT, ExtensionMethods.getTimeStamp());
            Client client = new Client(clientIPAddress, iRequestMessage);
            client.execute();
        }
    }
    private void CurrentSongNameResult(final IRequest request) {

        for (NSD device : NSDClient.devicesList) {
            if(device.getHostAddress().equals(clientIPAddress))
                device.setCurrentSongPlaying(request.getClientCurrentSong());
        }
        GroupListenActivity.updateSong(KeyConstants.SOCKET_CURRENT_SONG_NAME_RESULT);
        NearbyDevicesActivity.updateAdapater();
    }

    private void RequestCurrentSong(final IRequest request) {

        if(PlayerConstants.getPlaylistSize() != 0 && PlayerConstants.getPlaylistSize() >= PlayerConstants.SONG_NUMBER) {

            final String currentSongFilePath = PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getData();
            if(SharedPreferenceHelper.getClientTransferRequestAlwaysAccept(context, request.getClientMacAddress())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketExtensionMethods.requestStrictModePermit();
                        request.setEventState(SocketExtensionMethods.EVENT_STATE.Approved);
                        db4oHelper.pushRequestObject(context, request);
                        ArrayList<Song> songArrayList = new ArrayList<>();
                        songArrayList.add(PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER));
                        IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_CURRENT_SONG_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK, songArrayList);
                        Client quickShareResponse = new Client(request.getClientIpAddress(), iRequestMessage);
                        quickShareResponse.execute();
                        try {
                            Thread.sleep(500);
                            FileSender fileSender = new FileSender(context, request);
                            fileSender.sendFile(currentSongFilePath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            else
                RequestsActivity.pushRequestObjectAndNotify(context, request);
        }
    }
    private void CurrentSongResult(final IRequest request){
        if(request.getResult().equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, request.getClientName() + KeyConstants.SPACE + context.getString(R.string.current_song_request_rejected), Toast.LENGTH_LONG).show();
                }
            });
        }

        else if(request.getResult().equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    FileReceiver fileReceiver = new FileReceiver(context, request);
                    fileReceiver.execute();
                    db4oHelper.pushSongTransferObject(context, request.getSongsToTransferArrayList());
                    Toast.makeText(context, context.getString(R.string.current_song_request_accepted) + KeyConstants.SPACE + request.getClientName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InvalidCommand(final IRequest request) {
        IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_FEATURE_NOT_AVAILABLE, ExtensionMethods.getTimeStamp(), request.getCommand());
        Client invalidCommand = new Client(request.getClientIpAddress(), iRequestMessage);
        invalidCommand.execute();
    }
    private void InvalidCommandResult(final IRequest request) {

    }

    private void RequestMacAddress(final IRequest request) {
        IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_MAC_ADDRESS_RESULT, ExtensionMethods.getTimeStamp(), SocketExtensionMethods.getMACAddress());
        Client macAddressResponse = new Client(request.getClientIpAddress(), iRequestMessage);
        macAddressResponse.execute();
    }
    private void MacAddressResult(final IRequest request){
        for (NSD device : NSDClient.devicesList) {
            if(device.getHostAddress().equals(request.getClientIpAddress()))
                device.setMacAddress(request.getResult());
        }
    }

    private void RequestAlbumArt(final IRequest request) {

        for (Song song : SharedVariables.fullSongsList)
            if(song.getHashID().equals(request.getTimeStamp()))
            {
                File file = new File(song.getAlbumArtLocation());
                if(file.exists()){
                    IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_ALBUM_ART_RESULT, request.getTimeStamp(), KeyConstants.SOCKET_RESULT_OK);
                    Client quickShareResponse = new Client(request.getClientIpAddress(), iRequestMessage);
                    quickShareResponse.execute();
                    try {
                        Thread.sleep(500);
                        BitmapSender bitmapSender = new BitmapSender(request);
                        bitmapSender.sendBitmap(file.getAbsolutePath());
                        bitmapSender.endConnection();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                else{
                    IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_ALBUM_ART_RESULT, request.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
                    Client quickShareResponse = new Client(request.getClientIpAddress(), iRequestMessage);
                    quickShareResponse.execute();
                }

                break;
            }
    }
    private void AlbumArtResult(final IRequest request) {

        if(request.getResult().equals(KeyConstants.SOCKET_RESULT_OK)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BitmapReceiver fileReceiver = new BitmapReceiver(context, request);
                    fileReceiver.execute();
                }
            });
        }
    }

    private void RequestGroupListen(final IRequest request) {
        if(PlayerConstants.parentGroupListener == null)
            RequestsActivity.pushRequestObjectAndNotify(context, request);
        else{
            IRequest iRequestMessage = SocketExtensionMethods.GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_LISTEN_RESULT, ExtensionMethods.getTimeStamp(), KeyConstants.SOCKET_RESULT_CANCEL);
            Client client = new Client(clientIPAddress, iRequestMessage);
            client.execute();
        }
    }
    private void GroupListenResult(final IRequest request) {

        if(request.getResult().equals(KeyConstants.SOCKET_RESULT_OK) && PlayerConstants.parentGroupListener == null && PlayerConstants.groupListeners.size() == 0){
            PlayerConstants.parentGroupListener = request;
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, request.getClientName() + KeyConstants.SPACE + context.getString(R.string.group_listen_accepted), Toast.LENGTH_LONG).show();
                }
            });
            ActivitySwitcher.startGroupListen(context);
        }

        else if(request.getResult().equals(KeyConstants.SOCKET_RESULT_CANCEL)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, request.getClientName() + KeyConstants.SPACE + context.getString(R.string.group_listen_rejected), Toast.LENGTH_LONG).show();
                }
            });
        }

        else{
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, context.getString(R.string.group_listen_unkown_issue), Toast.LENGTH_LONG).show();
                }
            });
        }



    }
}