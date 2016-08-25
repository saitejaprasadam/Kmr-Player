package com.prasadam.kmrplayer.SocketClasses.GroupPlay;

import android.os.AsyncTask;
import android.util.Log;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/19/2016.
 */

public class GroupPlaySenderHelper extends AsyncTask<Void, Void, Void>{

    private ArrayList<String> clients;

    public GroupPlaySenderHelper(ArrayList<String> clients){
        this.clients = clients;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        for (String clientAddress : clients) {
            GroupPlaySender sender = new GroupPlaySender(clientAddress);
            sender.sendFile(PlayerConstants.getPlaylist().get(PlayerConstants.SONG_NUMBER).getData());
            sender.endConnection();
        }

        Log.d("Group play sent to all", String.valueOf(clients.size()));
        return null;
    }
}
