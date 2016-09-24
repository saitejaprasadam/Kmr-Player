package com.prasadam.kmrplayer.SocketClasses.FileTransfer.Music;

import android.content.Context;

import com.prasadam.kmrplayer.DatabaseHelper.db4oHelper;
import com.prasadam.kmrplayer.FabricHelpers.CustomEventHelpers;
import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.EventsActivity;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class FileSender {

    private SocketChannel socketChannel;
    private Context context;
    private Event event;

    public FileSender(Context context, Event event){
        try {
            socketChannel = SocketChannel.open();
            this.context = context;
            this.event = event;
            SocketAddress socketAddress = new InetSocketAddress(event.getClientIpAddress(), KeyConstants.FILE_TRANSFER_SOCKET_PORT_ADDRESS);
            socketChannel.connect(socketAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String filePath) {

        try {
            File file = new File(filePath);

            RandomAccessFile aFile = new RandomAccessFile(file, "r");
            FileChannel inChannel = aFile.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(KeyConstants.TRANSFER_BUFFER_SIZE);

            while (inChannel.read(buffer) > 0) {
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
            }

            aFile.close();
            CustomEventHelpers.quickShareEventRegister(file.getName());

            if(event.getCommand().equals(KeyConstants.SOCKET_REQUEST_CURRENT_SONG)){
                event.setEventState(SocketExtensionMethods.EVENT_STATE.Completed);
                db4oHelper.updateEventObject(context, event);
                EventsActivity.eventNotifyDataSetChanged();
            }

            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}