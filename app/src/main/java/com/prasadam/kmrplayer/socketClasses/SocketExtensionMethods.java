package com.prasadam.kmrplayer.SocketClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.widget.Toast;

import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.UtilFunctions;
import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.ModelClasses.TransferableSong;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.Group_Listen_Music.Group_Listen_FileReceiver;
import com.prasadam.kmrplayer.SocketClasses.FileTransfer.Group_Listen_Music.Group_Listen_FileSender;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDServer;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.GroupListenActivity;

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Created by Prasadam Saiteja on 7/3/2016.
 */

public class SocketExtensionMethods {

    public enum EVENT_STATE {
        WAITING, Denied, Approved, Completed
    }
    public enum TRANSFER_STATE{
        WAITING, IN_PROGRESS, Denied, Completed
    }

    public static void stopNSDServies(){

        if(NSDServer.mNsdManager != null)
            NSDServer.mNsdManager.unregisterService(NSDServer.mRegistrationListener);

        if(NSDClient.mNsdManager != null)
            NSDClient.mNsdManager.stopServiceDiscovery(NSDClient.mDiscoveryListener);
        NSDClient.devicesList = null;
    }
    public static void startNSDServices(final Context context){
        NSDServer.startService(context);
        NSDClient.startSearch(context);
        new Thread(new ServerThread(context)).start();
    }

    public static void requestStrictModePermit(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static Event GenerateSocketEventMessage(final Context context, String command, String timeStamp){
        return new Event(getMACAddress(), ExtensionMethods.deviceName(context).replaceAll(KeyConstants.SPACE, KeyConstants.SPECIAL_CHAR), command, timeStamp);
    }
    public static Event GenerateSocketEventMessage(final Context context, String command, String timeStamp, String result){
        Event event = new Event(getMACAddress(), ExtensionMethods.deviceName(context).replaceAll(KeyConstants.SPACE, KeyConstants.SPECIAL_CHAR), command, timeStamp);
        event.setResult(result);
        return event;
    }
    public static Event GenerateSocketEventMessage(final Context context, String command, String timeStamp, String result, ArrayList<Song> transferSongsList){
        Event event = new Event(getMACAddress(), ExtensionMethods.deviceName(context).replaceAll(KeyConstants.SPACE, KeyConstants.SPECIAL_CHAR), command, timeStamp, transferSongsList);
        event.setResult(result);
        return event;
    }

    public static void requestForDeviceType(final Context context, NsdServiceInfo nsdClient) {
        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_REQUEST_DEVICE_TYPE, ExtensionMethods.getTimeStamp());
        Client client = new Client(nsdClient.getHost(), eventMessage);
        client.execute();
    }
    public static String getDeviceType(final Context context){
        if(ExtensionMethods.isTablet(context))
            return KeyConstants.TABLET;
        else
            return KeyConstants.MOBILE;
    }
    public static int getDeviceImage(final String DEVICE_TYPE){

        if(DEVICE_TYPE == null)
            return R.mipmap.android_device;

        switch (DEVICE_TYPE){

            case KeyConstants.MOBILE:
                return R.mipmap.android_device;

            case KeyConstants.TABLET:
                return R.mipmap.tablet;

            case KeyConstants.IPad:
                return R.mipmap.ipad;

            case KeyConstants.Iphone:
                return R.mipmap.iphone_6s;

            case KeyConstants.Mac:
                return R.mipmap.apple_mac;

            case KeyConstants.MacBook:
                return R.mipmap.macbook_pro;

            case KeyConstants.PC:
                return R.mipmap.apple_mac;
        }

        return R.mipmap.android_device;
    }

    public static void requestForCurrentSongPlaying(final Context context, final NsdServiceInfo nsdClient) {
        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_REQUEST_CURRENT_SONG_NAME, ExtensionMethods.getTimeStamp());
        Client client = new Client(nsdClient.getHost(), eventMessage);
        client.execute();
    }

    public static Bitmap getAlbumArt(final Context context, NSD serverObject) {

        if(serverObject.getCurrentSongPlaying() == null)
            return null;

        File cachePath = new File(context.getCacheDir(), "albumArt");
        File file = new File(cachePath, "/" + serverObject.getCurrentSongPlaying().getHashID());
        if(file.exists())
            return BitmapFactory.decodeFile(file.getAbsolutePath(), new BitmapFactory.Options());

        else{
            for (Song song : SharedVariables.fullSongsList)
                if(song.getHashID().equals(serverObject.getCurrentSongPlaying().getHashID()))
                    return UtilFunctions.getAlbumart(context, AudioExtensionMethods.getAlubmID(context, song.getID()));
        }

        SocketExtensionMethods.requestAlbumArt(context, serverObject);
        return null;
    }
    public static Bitmap getAlbumArt(final Context context, TransferableSong transferableSong){

        if(transferableSong == null)
            return null;

        File cachePath = new File(context.getCacheDir(), "albumArt");
        File file = new File(cachePath, "/" + transferableSong.getSong().getHashID());
        if(file.exists())
            return BitmapFactory.decodeFile(file.getAbsolutePath(), new BitmapFactory.Options());

        else{
            for (Song song : SharedVariables.fullSongsList)
                if(song.getHashID().equals(transferableSong.getSong().getHashID()))
                    return UtilFunctions.getAlbumart(context, AudioExtensionMethods.getAlubmID(context, song.getID()));
        }

        SocketExtensionMethods.requestAlbumArt(context, transferableSong);
        return null;
    }
    public static Bitmap getAlbumArt(final Context context, Song song, String client_mac_address){

        if(song == null)
            return null;

        File cachePath = new File(context.getCacheDir(), "albumArt");
        File file = new File(cachePath, "/" + song.getHashID());
        if(file.exists())
            return BitmapFactory.decodeFile(file.getAbsolutePath(), new BitmapFactory.Options());

        else{
            for (Song s : SharedVariables.fullSongsList)
                if(s.getHashID().equals(song.getHashID()))
                    return UtilFunctions.getAlbumart(context, AudioExtensionMethods.getAlubmID(context, song.getID()));
        }

        SocketExtensionMethods.requestAlbumArt(context, client_mac_address);
        return null;
    }

    public static String getAlbumArtLocation(final Context context, NSD serverObject){

        if(serverObject.getCurrentSongPlaying() == null)
            return null;

        File cachePath = new File(context.getCacheDir(), "albumArt");
        File file = new File(cachePath, "/" + serverObject.getCurrentSongPlaying().getHashID());
        if(file.exists())
            return file.getAbsolutePath();

        else{
            for (Song song : SharedVariables.fullSongsList)
                if(song.getHashID().equals(serverObject.getCurrentSongPlaying().getHashID()))
                   return song.getAlbumArtLocation();
        }

        return null;
    }

    public static void requestAlbumArt(final Context context, NSD serverObject) {
        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_REQUEST_ALBUM_ART, serverObject.getCurrentSongPlaying().getHashID());
        Client client = new Client(serverObject.GetClientNSD().getHost(), eventMessage);
        client.execute();
    }
    public static void requestAlbumArt(final Context context, TransferableSong transferableSong){
        for (NSD client : NSDClient.devicesList)
            if(client.getMacAddress().equals(transferableSong.getClient_mac_address())){
                requestAlbumArt(context, client);
                break;
            }
    }
    private static void requestAlbumArt(Context context, String client_mac_address) {
        for (NSD client : NSDClient.devicesList)
            if(client.getMacAddress().equals(client_mac_address)){
                requestAlbumArt(context, client);
                break;
            }
    }

    public static void requestGroupListen(Context context, NSD serverObject) {

        if(PlayerConstants.parentGroupListener == null && PlayerConstants.groupListeners.size() == 0){
            Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_INITIATE_GROUP_LISTEN_REQUEST, ExtensionMethods.getTimeStamp());
            Client client = new Client(serverObject.GetClientNSD().getHost(), eventMessage);
            client.execute();
            Toast.makeText(context, "Group play request sent to " + serverObject.GetClientNSD().getServiceName(), Toast.LENGTH_SHORT).show();
        }

        else if(PlayerConstants.parentGroupListener != null)
            Toast.makeText(context, "You are in a group listen currently", Toast.LENGTH_SHORT).show();

        else if(PlayerConstants.groupListeners.size() != 0)
            Toast.makeText(context, "You have group listen server running, please disconnect " + PlayerConstants.groupListeners.size() +  " clients", Toast.LENGTH_LONG).show();
    }
    public static void sendGroupListenSongBroadCast(final Context context){

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(final Event event : PlayerConstants.groupListeners){
                    try{
                        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_LISTEN_OPEN_FILE_RECEIVER, ExtensionMethods.getTimeStamp());
                        Client client = new Client(event.getClientIpAddress(), eventMessage);
                        client.execute();

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            public void run() {
                                Group_Listen_FileSender group_listen_fileSender = new Group_Listen_FileSender(context, event);
                                group_listen_fileSender.sendFile(MusicService.currentSong.getData());
                            }
                        }, 300);
                    }
                    catch (Exception ignored){}
                }

            }
        }).start();
    }
    public static void receiveGroupListenSongBroadCast(final String fileName){
        if(PlayerConstants.parentGroupListener != null)
            GroupListenActivity.updateSong(fileName);
    }
    public static void GroupListenStartFileReceiver(final Context context, final Event event) {
        if(PlayerConstants.parentGroupListener != null && event.getClientMacAddress().equals(PlayerConstants.parentGroupListener.getClientMacAddress())){
            Group_Listen_FileReceiver group_listen_fileReceiver = new Group_Listen_FileReceiver(context);
            group_listen_fileReceiver.execute();
        }
    }
    public static void SendDisconnectMessageFromGroupListen(final Context context, final NsdServiceInfo nsdClient){
        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_LISTEN_DISCONNECT, ExtensionMethods.getTimeStamp());
        Client client = new Client(nsdClient.getHost(), eventMessage);
        client.execute();
    }
    public static void GroupListenDisconnect(final Context context, final Event event) {

        for(Event groupListener : PlayerConstants.groupListeners)
            if(groupListener.getClientMacAddress().equals(event.getClientMacAddress())){
                PlayerConstants.groupListeners.remove(groupListener);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, event.getClientName() + KeyConstants.SPACE + context.getResources().getString(R.string.client_disconnected_group_listen), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            }
    }
    public static void disconnectDeviceFromGroupListen(final Context context, final Event connectedDevice) {
        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_GROUP_LISTEN_KICK_OUT_DEVICE, ExtensionMethods.getTimeStamp());
        Client client = new Client(connectedDevice.getClientIpAddress(), eventMessage);
        Toast.makeText(context, connectedDevice.getClientName() + KeyConstants.SPACE +  context.getResources().getString(R.string.group_listen_device_disconnected), Toast.LENGTH_SHORT).show();
        client.execute();
    }
    public static void GroupListenEndConnection(final Context context, final Event event) {

            if(PlayerConstants.parentGroupListener.getClientMacAddress().equals(event.getClientMacAddress())) {
                PlayerConstants.parentGroupListener = null;
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        GroupListenActivity.updateSong(event.getCommand());
                        Toast.makeText(context, event.getClientName() + KeyConstants.SPACE + context.getResources().getString(R.string.kicked_out_group_listen), Toast.LENGTH_SHORT).show();
                    }
                });
            }
    }

    public static void requestForMacAddress(final Context context, NsdServiceInfo nsdClient) {
        Event eventMessage = GenerateSocketEventMessage(context, KeyConstants.SOCKET_REQUEST_MAC_ADDRESS, ExtensionMethods.getTimeStamp());
        Client client = new Client(nsdClient.getHost(), eventMessage);
        client.execute();
    }
    public static String getMACAddress(){

        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)){
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac==null){
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length()>0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ignored) { }
        return "02:00:00:00:00:00";
    }
}