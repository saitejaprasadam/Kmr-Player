package com.prasadam.kmrplayer.SocketClasses.FileTransfer.Bitmap;

import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/*
 * Created by Prasadam Saiteja on 9/20/2016.
 */

public class BitmapSender {

    private SocketChannel socketChannel;

    public BitmapSender(Event event){
        try {
            socketChannel = SocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress(event.getClientIpAddress(), KeyConstants.BITMAP_TRANSFER_SOCKET_PORT_ADDRESS);
            socketChannel.connect(socketAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBitmap(String filePath) {

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
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void endConnection(){
        try {
            if(socketChannel != null)
                socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}