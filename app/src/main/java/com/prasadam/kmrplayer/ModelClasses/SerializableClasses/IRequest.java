package com.prasadam.kmrplayer.ModelClasses.SerializableClasses;

import com.prasadam.kmrplayer.ModelClasses.Request;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 10/3/2016.
 */

public class IRequest extends Request{

    private static final long serialVersionUID = 123456789L;

    public IRequest(String clientMacAddress, String clientName, String command, String timeStamp) {
        super(clientMacAddress, clientName, command, timeStamp);
    }
    public IRequest(String clientMacAddress, String clientName, String command, String timeStamp, String result) {
        super(clientMacAddress, clientName, command, timeStamp, result);
    }
    public IRequest(String clientMacAddress, String clientName, String command, String timeStamp, ArrayList<ISong> songsList) {
        super(clientMacAddress, clientName, command, timeStamp, songsList);
    }
    public IRequest(String clientMacAddress, String clientName, String command, String timeStamp, ArrayList<ISong> songsList, String result) {
        super(clientMacAddress, clientName, command, timeStamp, songsList, result);
    }
    public IRequest(String timeStamp) {
        super(timeStamp);
    }
}