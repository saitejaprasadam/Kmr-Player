package com.prasadam.kmrplayer.SocketClasses.FileTransfer.Music;

import android.content.Context;

import com.prasadam.kmrplayer.DatabaseHelperClasses.db4oHelper;
import com.prasadam.kmrplayer.FabricHelperClasses.CustomEventHelpers;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.RequestsActivity;

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
    private IRequest request;

    public FileSender(Context context, IRequest request){
        try {
            socketChannel = SocketChannel.open();
            this.context = context;
            this.request = request;
            SocketAddress socketAddress = new InetSocketAddress(request.getClientIpAddress(), KeyConstants.FILE_TRANSFER_SOCKET_PORT_ADDRESS);
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

            if(request.getCommand().equals(KeyConstants.SOCKET_REQUEST_CURRENT_SONG)){
                request.setEventState(SocketExtensionMethods.EVENT_STATE.Completed);
                db4oHelper.updateRequestObject(context, request);
                RequestsActivity.eventNotifyDataSetChanged();
            }

            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}