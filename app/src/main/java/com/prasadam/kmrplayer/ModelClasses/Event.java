package com.prasadam.kmrplayer.ModelClasses;

import android.content.Context;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/*
 * Created by Prasadam Saiteja on 9/16/2016.
 */

public class Event implements Serializable {

    private String clientMacAddress, clientIpAddress, clientName, command, timeStamp, result;
    private Song clientCurrentSong, serverCurrentSong;
    private ArrayList<TransferableSong> songsToTransfer = new ArrayList<>();
    private SocketExtensionMethods.EVENT_STATE eventState = SocketExtensionMethods.EVENT_STATE.WAITING;
    private Date time;

    public Event(String clientMacAddress, String clientName, String command, String timeStamp){
        this.clientMacAddress = clientMacAddress;
        this.clientName = clientName;
        this.command = command;
        this.timeStamp = timeStamp;
        this.time = new Date();
        if(MusicService.currentSong != null)
            this.clientCurrentSong = MusicService.currentSong;
    }
    public Event(Context context, String clientMacAddress, String clientName, String command, String timeStamp, ArrayList<Song> songsList){
        this.clientMacAddress = clientMacAddress;
        this.clientName = clientName;
        this.command = command;
        this.timeStamp = timeStamp;
        this.time = new Date();
        for (Song song : songsList) {
            songsToTransfer.add(new TransferableSong(context, song));
        }
        if(MusicService.currentSong != null)
            this.clientCurrentSong = MusicService.currentSong;
    }

    public void setResult(String result){
        this.result = result;
    }
    public void setEventState(SocketExtensionMethods.EVENT_STATE eventState) {
        this.eventState = eventState;
    }
    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }
    public void setServerCurrentSong() {
        if(MusicService.currentSong != null)
            this.serverCurrentSong = MusicService.currentSong;
    }

    public String getCommand(){
        return command;
    }
    public String getClientIpAddress(){ return clientIpAddress; }
    public String getClientMacAddress() {
        return clientMacAddress;
    }
    public String getResult(){
        return result;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
    public String getClientName() {
        return clientName;
    }
    public Date getTime(){
        return time;
    }
    public SocketExtensionMethods.EVENT_STATE getEventState() {
        return eventState;
    }
    public Song getServerCurrentSong() {
        return serverCurrentSong;
    }
    public Song getClientCurrentSong(){ return clientCurrentSong; }
    public ArrayList<TransferableSong> getSongsToTransferArrayList(){ return songsToTransfer; }
}