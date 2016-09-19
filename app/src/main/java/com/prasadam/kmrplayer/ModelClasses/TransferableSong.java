package com.prasadam.kmrplayer.ModelClasses;

import android.content.Context;
import android.graphics.Bitmap;

import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.UtilFunctions;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.SubClasses.SerializableImage;

import java.io.Serializable;

/*
 * Created by Prasadam Saiteja on 9/19/2016.
 */

public class TransferableSong implements Serializable{

    private SocketExtensionMethods.TRANSFER_STATE songTransferState = SocketExtensionMethods.TRANSFER_STATE.WAITING;
    //private SerializableImage albumArt = null;
    private Song song;

    public TransferableSong(Context context, Song song) {
        this.song = song;
        //albumArt = new SerializableImage(UtilFunctions.getAlbumart(context, AudioExtensionMethods.getAlubmID(context, song.getID())));
    }

    public SocketExtensionMethods.TRANSFER_STATE getSongTransferState(){
        return songTransferState;
    }
    public void setSongTransferState(SocketExtensionMethods.TRANSFER_STATE state){
        songTransferState = state;
    }
    public Song getSong(){
        return song;
    }
}