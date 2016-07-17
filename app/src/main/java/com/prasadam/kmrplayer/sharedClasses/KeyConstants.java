package com.prasadam.kmrplayer.sharedClasses;

/*
 * Created by Prasadam Saiteja on 7/5/2016.
 */

import android.os.Environment;

import com.prasadam.kmrplayer.R;

import java.io.File;

public class KeyConstants {

    public static final String TABLET = "TABLET";
    public static final String MOBILE = "MOBILE";
    public static final String DEVICE_TYPE = "DEV_TYPE";
    public static final String INTENT_SONGS_PATH_LIST = "INTENT_SONGS_PATH_LIST";
    public static final String DIVIDER = " ";
    public static final Character SPACE = ' ';

    public static final String ACTIVITY_QUICK_SHARE = "QuickShareActivity";
    public static final String ACTIVITY_NEARBY_DEVICES = "NearbyDevicesActivity";

    public static final int MAIN_SERVER_SOCKET_PORT_ADDRESS = 6262;
    public static final int FILE_TRANSFER_SOCKET_PORT_ADDRESS = 7248;
    public static final int TAG_EDITOR_REQUEST_CODE = 1104;
    public static final int TRANSFER_BUFFER_SIZE = 1024 * 50;
    public static final String PLAYER_DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + SharedVariables.globalActivityContext.getResources().getString(R.string.app_name);

    public static final String SOCKET_RESULT_OK = "SOCKET_RESULT_OK";
    public static final String SOCKET_RESULT_CANCEL = "SOCKET_RESULT_CANCEL";
    public static final String SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST = "SOCKET_INITIATE_QUICK_SHARE_TRANSFER_REQUEST";
    public static final String SOCKET_QUICK_SHARE_TRANSFER_RESULT = "SOCKET_QUICK_SHARE_TRANSFER_RESULT";
    public static final String SOCKET_INITIATE_GROUP_PLAY_REQUEST = "SOCKET_INITIATE_GROUP_PLAY_REQUEST";
    public static final String SOCKET_GROUP_PLAY_RESULT = "SOCKET_GROUP_PLAY_RESULT";

}