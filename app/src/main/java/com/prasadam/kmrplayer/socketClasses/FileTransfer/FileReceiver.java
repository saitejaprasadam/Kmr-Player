package com.prasadam.kmrplayer.SocketClasses.FileTransfer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.prasadam.kmrplayer.DatabaseHelper.db4oHelper;
import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.ModelClasses.TransferableSong;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.EventsActivity;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class FileReceiver extends AsyncTask<Void, Void, Void>{

    private ServerSocketChannel serverSocketChannel;
    private Context context;
    private Event event;

    public FileReceiver(Context context, Event event){

        try {
            if(serverSocketChannel == null){
                this.event = event;
                this.context = context;
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(KeyConstants.FILE_TRANSFER_SOCKET_PORT_ADDRESS));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Log.d("count", String.valueOf(event.getSongsToTransferArrayList().size()));

        for (TransferableSong transferableSong : event.getSongsToTransferArrayList()) {
            try {
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                String fileName = ExtensionMethods.extractFileNameFromPath(transferableSong.getSong().getData());
                File PlayerDirectory  = new File(KeyConstants.PLAYER_DIRECTORY_PATH);

                if(!PlayerDirectory.exists())
                    PlayerDirectory.mkdir();

                Log.d("song", transferableSong.getSong().getTitle());
                File songFile = new File(PlayerDirectory.getAbsolutePath() + File.separator + fileName);
                songFile.createNewFile();
                RandomAccessFile aFile = new RandomAccessFile(songFile, "rw");
                ByteBuffer buffer = ByteBuffer.allocate(KeyConstants.TRANSFER_BUFFER_SIZE);
                FileChannel fileChannel = aFile.getChannel();
                while (clientSocketChannel.read(buffer) > 0) {
                    buffer.flip();
                    fileChannel.write(buffer);
                    buffer.clear();
                }

                transferableSong.setSongTransferState(SocketExtensionMethods.TRANSFER_STATE.Completed);
                ExtensionMethods.scanMedia(context, PlayerDirectory + File.separator + fileName);
                fileChannel.close();
                clientSocketChannel.close();
            }

            catch (IOException e) {
                Log.d("Exception", e.toString());
            }
        }

        event.setEventState(SocketExtensionMethods.EVENT_STATE.Completed);
        db4oHelper.updateEventObject(context, event);
        EventsActivity.eventNotifyDataSetChanged();
        return null;
    }
}