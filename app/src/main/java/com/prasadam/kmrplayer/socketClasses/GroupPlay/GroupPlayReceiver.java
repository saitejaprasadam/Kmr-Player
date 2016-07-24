package com.prasadam.kmrplayer.SocketClasses.GroupPlay;

import android.os.AsyncTask;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/*
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class GroupPlayReceiver extends AsyncTask<Void, Void, Void>{

    private static ServerSocketChannel serverSocketChannel;

    public GroupPlayReceiver(){

        try {
            if(serverSocketChannel == null){
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(KeyConstants.GROUP_PLAY_SOCKET_PORT_ADDRESS));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        /*while (true){
            try {
                SocketChannel clientSocketChannel = serverSocketChannel.accept();

                File cachePath = new File(SharedVariables.globalActivityContext.getCacheDir(), "GroupPlay");
                cachePath.mkdirs();
                new File(cachePath + "/GroupPlayCurrentSong.mp3").delete();

                File songFile = new File(cachePath, "/GroupPlayCurrentSong.mp3");;
                songFile.createNewFile();
                RandomAccessFile aFile = new RandomAccessFile(songFile, "rw");
                ByteBuffer buffer = ByteBuffer.allocate(KeyConstants.TRANSFER_BUFFER_SIZE);
                FileChannel fileChannel = aFile.getChannel();
                while (clientSocketChannel.read(buffer) > 0) {
                    buffer.flip();
                    fileChannel.write(buffer);
                    buffer.clear();
                }

                System.out.println("Group play received " + songFile.getAbsolutePath());
                Thread.sleep(100);
                fileChannel.close();
                clientSocketChannel.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        return null;
    }
}