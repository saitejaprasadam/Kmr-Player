package com.prasadam.kmrplayer.socketClasses.FileTransfer;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import com.prasadam.kmrplayer.sharedClasses.KeyConstants;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;

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

    private static ServerSocketChannel serverSocketChannel;

    public FileReceiver(){

        try {
            if(serverSocketChannel == null){
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(KeyConstants.FILE_TRANSFER_SOCKET_PORT_ADDRESS));
                System.out.println("Started server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        while (true){
            try {
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                System.out.println("Receving from client");

                ByteBuffer namebuff = ByteBuffer.allocate(150);
                clientSocketChannel.read(namebuff);

                byte[] namebyte = new byte[150];
                String filename = "";
                int position=namebuff.position();

                while(namebuff.hasRemaining()){
                    namebyte[position] = namebuff.get();
                    position = namebuff.position();
                }

                filename = new String(namebyte, 0, position, "UTF-8");
                System.out.println(filename);

                File PlayerDirectory  = new File(KeyConstants.PLAYER_DIRECTORY_PATH);

                if(!PlayerDirectory.exists())
                    PlayerDirectory.mkdir();

                File songFile = new File(PlayerDirectory.getAbsolutePath() + File.separator + "temp.mp3");
                songFile.createNewFile();
                RandomAccessFile aFile = new RandomAccessFile(songFile, "rw");
                ByteBuffer buffer = ByteBuffer.allocate(KeyConstants.TRANSFER_BUFFER_SIZE);
                FileChannel fileChannel = aFile.getChannel();
                while (clientSocketChannel.read(buffer) > 0) {
                    buffer.flip();
                    fileChannel.write(buffer);
                    buffer.clear();
                }

                SharedVariables.globalActivityContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + PlayerDirectory + File.separator + "temp.mp3")));
                System.out.println("Completed");
                Thread.sleep(100);
                fileChannel.close();
                clientSocketChannel.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}