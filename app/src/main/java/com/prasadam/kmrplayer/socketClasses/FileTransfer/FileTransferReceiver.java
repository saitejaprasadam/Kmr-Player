package com.prasadam.kmrplayer.socketClasses.FileTransfer;

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

public class FileTransferReceiver {

    /*public static void main(String[] args) {
        FileReceiver nioServer = new FileReceiver();
        SocketChannel socketChannel = nioServer.createServerSocketChannel();
        nioServer.readFileFromSocket(socketChannel);
    }*/

    public SocketChannel createServerSocketChannel() {

        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9999));
            socketChannel = serverSocketChannel.accept();
            System.out.println("Connection established...." + socketChannel.getRemoteAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return socketChannel;
    }

    public void readFileFromSocket(SocketChannel socketChannel) {
        RandomAccessFile aFile = null;
        try {
            aFile = new RandomAccessFile("E:\\Test\\Video.avi", "rw");
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            FileChannel fileChannel = aFile.getChannel();
            while (socketChannel.read(buffer) > 0) {
                buffer.flip();
                fileChannel.write(buffer);
                buffer.clear();
            }
            Thread.sleep(1000);
            fileChannel.close();
            socketChannel.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
