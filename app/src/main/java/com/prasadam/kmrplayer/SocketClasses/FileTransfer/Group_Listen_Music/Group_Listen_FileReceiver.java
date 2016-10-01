package com.prasadam.kmrplayer.SocketClasses.FileTransfer.Group_Listen_Music;

import android.content.Context;
import android.os.AsyncTask;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

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

public class Group_Listen_FileReceiver extends AsyncTask<Void, Void, Void>{

    private ServerSocketChannel serverSocketChannel;
    private Context context;

    public Group_Listen_FileReceiver(Context context){

        try {
            if(serverSocketChannel == null){
                this.context = context;
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(KeyConstants.GROUP_LISTEN_TRANSFER_SOCKET_PORT_ADDRESS));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            final SocketChannel clientSocketChannel = serverSocketChannel.accept();
            final String fileName = "Group_listen";

            File cachePath = new File(context.getCacheDir(), "albumArt");
            cachePath.mkdirs();
            new File(cachePath + "/" + fileName).delete();
            final File songFile = new File(cachePath, "/" + fileName);

            RandomAccessFile aFile = new RandomAccessFile(songFile, "rw");
            ByteBuffer buffer = ByteBuffer.allocate(KeyConstants.TRANSFER_BUFFER_SIZE);
            FileChannel fileChannel = aFile.getChannel();
            while (clientSocketChannel.read(buffer) > 0) {
                buffer.flip();
                fileChannel.write(buffer);
                buffer.clear();
            }

            if(PlayerConstants.parentGroupListener != null)
                SocketExtensionMethods.receiveGroupListenSongBroadCast(songFile.getAbsolutePath());

            fileChannel.close();
            clientSocketChannel.close();
            try {serverSocketChannel.close();}
            catch (Exception ignored) {}
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            serverSocketChannel.close();
        } catch (Exception ignored) {}
        return null;
    }
}