package com.prasadam.kmrplayer.ModelClasses;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.ISong;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.ITransferableSong;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/*
 * Created by Prasadam Saiteja on 9/16/2016.
 */

public class Request implements Serializable {

    private String clientMacAddress, clientIpAddress, clientName, command, timeStamp, result;
    private ISong clientCurrentSong, serverCurrentSong;
    private ArrayList<ITransferableSong> songsToTransfer = new ArrayList<>();
    private SocketExtensionMethods.EVENT_STATE eventState = SocketExtensionMethods.EVENT_STATE.WAITING;
    private Date time;

    public Request(String clientMacAddress, String clientName, String command, String timeStamp){
        this.clientMacAddress = clientMacAddress;
        this.clientName = clientName;
        this.command = command;
        this.timeStamp = timeStamp;
        this.time = new Date();
        if(MusicService.currentSong != null)
            this.clientCurrentSong = new ISong(MusicService.currentSong);
    }
    public Request(String clientMacAddress, String clientName, String command, String timeStamp, String result){
        this.clientMacAddress = clientMacAddress;
        this.clientName = clientName;
        this.command = command;
        this.timeStamp = timeStamp;
        this.time = new Date();
        this.result = result;
        if(MusicService.currentSong != null)
            this.clientCurrentSong = new ISong(MusicService.currentSong);
    }
    public Request(String clientMacAddress, String clientName, String command, String timeStamp, ArrayList<ISong> songsList){
        this.clientMacAddress = clientMacAddress;
        this.clientName = clientName;
        this.command = command;
        this.timeStamp = timeStamp;
        this.time = new Date();
        for (ISong isong : songsList) {
            songsToTransfer.add(new ITransferableSong(isong, clientMacAddress));
        }
        if(MusicService.currentSong != null)
            this.clientCurrentSong = new ISong(MusicService.currentSong);
    }
    public Request(String clientMacAddress, String clientName, String command, String timeStamp, ArrayList<ISong> songsList, String result){
        this.clientMacAddress = clientMacAddress;
        this.clientName = clientName;
        this.command = command;
        this.timeStamp = timeStamp;
        this.time = new Date();
        this.result = result;
        for (ISong isong : songsList) {
            songsToTransfer.add(new ITransferableSong(isong, clientMacAddress));
        }
        if(MusicService.currentSong != null)
            this.clientCurrentSong = new ISong(MusicService.currentSong);
    }
    public Request(String timeStamp) {
        this.timeStamp = timeStamp;
        eventState = null;
        songsToTransfer = null;
    }

    public void setEventState(SocketExtensionMethods.EVENT_STATE eventState) {
        this.eventState = eventState;
    }
    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }
    public void setServerCurrentSong() {
        if(MusicService.currentSong != null)
            this.serverCurrentSong = new ISong(MusicService.currentSong);
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
    public ISong getServerCurrentSong() {
        return serverCurrentSong;
    }
    public ISong getClientCurrentSong(){ return clientCurrentSong; }
    public ArrayList<ITransferableSong> getSongsToTransferArrayList(){ return songsToTransfer; }

    public void copy(Request request) {
        this.clientMacAddress = request.getClientMacAddress();
        this.clientIpAddress = request.getClientIpAddress();
        this.clientName = request.getClientName();
        this.command = request.getCommand();
        this.timeStamp = request.getTimeStamp();
        this.clientCurrentSong = request.getClientCurrentSong();
        this.serverCurrentSong = request.getServerCurrentSong();
        this.songsToTransfer = request.getSongsToTransferArrayList();
        this.eventState = request.getEventState();
        this.time = request.getTime();
    }
}