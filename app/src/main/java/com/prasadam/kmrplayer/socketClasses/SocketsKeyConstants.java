package com.prasadam.kmrplayer.SocketClasses;

import android.os.Environment;

import java.io.File;

/*
 * Created by Prasadam Saiteja on 7/19/2016.
 */

public class SocketsKeyConstants {
    public static final String TABLET = "TABLET";
    public static final String MOBILE = "MOBILE";
    public static final String Iphone = "IPHONE";
    public static final String IPad = "IPAD";
    public static final String PC = "PC";
    public static final String MacBook = "MACBOOK";
    public static final String Mac = "MAC";
    public static final String DEVICE_TYPE = "DEV_TYPE";

    public static final int MAIN_SERVER_SOCKET_PORT_ADDRESS = 6262;
    public static final int FILE_TRANSFER_SOCKET_PORT_ADDRESS = 7248;
    public static final int GROUP_PLAY_SOCKET_PORT_ADDRESS = 1104;

    public static final int TRANSFER_BUFFER_SIZE = 1024 * 50;
    public static final String PLAYER_DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + "kmr player";

    public static final String SOCKET_RESULT_OK = "SOCKET_RESULT_OK";
    public static final String SOCKET_RESULT_CANCEL = "SOCKET_RESULT_CANCEL";

    public static final String SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST = "SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST";
    public static final String SOCKET_QUICK_SHARE_TRANSFER_RESULT = "SOCKET_QUICK_SHARE_TRANSFER_RESULT";

    public static final String SOCKET_INITIATE_GROUP_PLAY_REQUEST = "SOCKET_INITIATE_GROUP_PLAY_REQUEST";
    public static final String SOCKET_GROUP_PLAY_RESULT = "SOCKET_GROUP_PLAY_RESULT";

    public static final String SOCKET_REQUEST_DEVICE_TYPE = "SOCKET_REQUEST_DEVICE_TYPE";
    public static final String SOCKET_DEVICE_TYPE_RESULT = "SOCKET_DEVICE_TYPE_RESULT";

    public static final String SOCKET_REQUEST_CURRENT_SONG_NAME = "SOCKET_REQUEST_CURRENT_SONG_NAME";
    public static final String SOCKET_CURRENT_SONG_NAME_RESULT = "SOCKET_CURRENT_SONG_NAME_RESULT";

    public static final String SOCKET_REQUEST_CURRENT_SONG = "SOCKET_REQUEST_CURRENT_SONG";
    public static final String SOCKET_CURRENT_SONG_RESULT = "SOCKET_CURRENT_SONG_RESULT";

    public static final String SOCKET_FEATURE_NOT_AVAILABLE = "SOCKET_FEATURE_NOT_AVAILABLE";

    public static final String SOCKET_REQUEST_MAC_ADDRESS = "SOCKET_REQUEST_MAC_ADDRESS";
    public static final String SOCKET_MAC_ADDRESS_RESULT = "SOCKET_MAC_ADDRESS_RESULT";
}
