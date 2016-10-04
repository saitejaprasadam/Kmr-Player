package com.prasadam.kmrplayer.ModelClasses;

import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.ISong;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import java.io.Serializable;

/*
 * Created by Prasadam Saiteja on 9/19/2016.
 */

public class TransferableSong implements Serializable{

    private SocketExtensionMethods.TRANSFER_STATE songTransferState = SocketExtensionMethods.TRANSFER_STATE.WAITING;
    private String client_mac_address;
    private ISong song;

    public TransferableSong(ISong song, String client_mac_address) {
        this.song = song;
        this.client_mac_address = client_mac_address;
    }
    public TransferableSong(String client_mac_address, String hashID, long id) {
        this.songTransferState = null;
        this.client_mac_address = client_mac_address;
        this.song = new ISong(hashID, id);
    }

    public SocketExtensionMethods.TRANSFER_STATE getSongTransferState(){
        return songTransferState;
    }
    public void setSongTransferState(SocketExtensionMethods.TRANSFER_STATE state){
        songTransferState = state;
    }
    public ISong getSong(){
        return song;
    }
    public String getClient_mac_address() {
        return client_mac_address;
    }

    public void copy(TransferableSong updatedTransferableSong) {
        this.songTransferState = updatedTransferableSong.getSongTransferState();
        this.client_mac_address = updatedTransferableSong.getClient_mac_address();
        this.song = updatedTransferableSong.getSong();
    }
}