package com.prasadam.kmrplayer.SocketClasses.FileTransfer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;

;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class FileReceiver extends AsyncTask<Void, Void, Void>{

    private ServerSocketChannel serverSocketChannel;
    private SecureRandom random = new SecureRandom();
    private Context context;
    public int countToBeRecevied;

    public FileReceiver(Context context, int countToBeRecevied){

        try {
            if(serverSocketChannel == null){
                this.countToBeRecevied = countToBeRecevied;
                this.context = context;
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(KeyConstants.FILE_TRANSFER_SOCKET_PORT_ADDRESS));
                System.out.println("Started server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        do{
            try {
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                String fileName = nextSessionId() + ".mp3";

                File PlayerDirectory  = new File(KeyConstants.PLAYER_DIRECTORY_PATH);

                if(!PlayerDirectory.exists())
                    PlayerDirectory.mkdir();

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

                ExtensionMethods.scanMedia(context, PlayerDirectory + File.separator + fileName);
                Thread.sleep(100);
                fileChannel.close();
                clientSocketChannel.close();
                System.out.println("File Received " + countToBeRecevied);
                countToBeRecevied--;
            } catch (IOException | InterruptedException e) {
                countToBeRecevied--;
                Log.d("Exception", e.toString());
            }

        }while(countToBeRecevied > 0);

        return null;
    }
}