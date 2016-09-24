package com.prasadam.kmrplayer.SocketClasses.GroupPlay;

import android.util.Log;

import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

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

public class GroupPlaySender{

    private SocketChannel socketChannel;

    public GroupPlaySender(String clientAddress){
        try {
            socketChannel = SocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress(clientAddress, KeyConstants.GROUP_PLAY_SOCKET_PORT_ADDRESS);
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

            Thread.sleep(1000);
            aFile.close();

        } catch (IOException | InterruptedException e) {
            Log.e("exception", String.valueOf(e));
        }
    }

    public void endConnection(){
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}