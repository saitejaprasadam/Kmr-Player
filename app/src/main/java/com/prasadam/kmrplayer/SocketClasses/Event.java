package com.prasadam.kmrplayer.SocketClasses;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;

import java.util.Date;

/*
 * Created by Prasadam Saiteja on 9/16/2016.
 */

public class Event {

    private String clientMacAddress, clientIpAddress, clientName, command, timeStamp, result, currentSongName;
    private SocketExtensionMethods.EVENT_STATE eventState = null;
    private Date time;

    public Event(){}
    public Event(String clientMacAddress, String clientIpAddress, String clientName, String command, String timeStamp){
        this.clientMacAddress = clientMacAddress;
        this.clientIpAddress = clientIpAddress;
        this.clientName = clientName;
        this.command = command;
        this.timeStamp = timeStamp;
        this.time = new Date();
        this.eventState = SocketExtensionMethods.EVENT_STATE.WAITING;
        if(MusicService.currentSong != null)
            this.currentSongName = MusicService.currentSong.getTitle();
    }

    public void setResult(String result){
        this.result = result;
    }
    public void setEventState(SocketExtensionMethods.EVENT_STATE eventState) {
        this.eventState = eventState;
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
    public String getCurrentSongName() {
        return currentSongName;
    }
}