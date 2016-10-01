package com.prasadam.kmrplayer.SocketClasses.FileTransfer.Group_Listen_Music;

import android.content.Context;
import android.widget.Toast;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class Group_Listen_FileSender {

    private SocketChannel socketChannel;

    public Group_Listen_FileSender(Context context, Event event){
        try {
            socketChannel = SocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress(event.getClientIpAddress(), KeyConstants.GROUP_LISTEN_TRANSFER_SOCKET_PORT_ADDRESS);
            socketChannel.connect(socketAddress);
        }
        catch (ConnectException e){
            PlayerConstants.groupListeners.remove(event);
            Toast.makeText(context, event.getClientName() + KeyConstants.SPACE + context.getResources().getString(R.string.client_disconnected_group_listen), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {e.printStackTrace();}
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
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}