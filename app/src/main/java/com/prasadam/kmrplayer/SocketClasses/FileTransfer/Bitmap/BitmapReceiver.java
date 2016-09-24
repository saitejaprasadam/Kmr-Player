package com.prasadam.kmrplayer.SocketClasses.FileTransfer.Bitmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.UI.Fragments.DialogFragment.NearbyDevicesDetails_DialogFragment;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/*
 * Created by Prasadam Saiteja on 9/20/2016.
 */

public class BitmapReceiver extends AsyncTask<Void, Void, Void> {

    private ServerSocketChannel serverSocketChannel;
    private Context context;
    private Event event;

    public BitmapReceiver(Context context, Event event){

        try {
            if(serverSocketChannel == null){
                this.event = event;
                this.context = context;
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(KeyConstants.BITMAP_TRANSFER_SOCKET_PORT_ADDRESS));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            SocketChannel clientSocketChannel = serverSocketChannel.accept();

            File cachePath = new File(context.getCacheDir(), "albumArt");
            cachePath.mkdirs();
            new File(cachePath + "/" + event.getResult()).delete();

            File newFile = new File(cachePath, "/" + event.getTimeStamp());

            RandomAccessFile aFile = new RandomAccessFile(newFile, "rw");
            ByteBuffer buffer = ByteBuffer.allocate(KeyConstants.TRANSFER_BUFFER_SIZE);
            FileChannel fileChannel = aFile.getChannel();
            while (clientSocketChannel.read(buffer) > 0) {
                buffer.flip();
                fileChannel.write(buffer);
                buffer.clear();
            }

            NearbyDevicesDetails_DialogFragment.refreshDialogFragment(event.getClientMacAddress());
            fileChannel.close();
            clientSocketChannel.close();
            serverSocketChannel.close();
        }

        catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return null;
    }
}