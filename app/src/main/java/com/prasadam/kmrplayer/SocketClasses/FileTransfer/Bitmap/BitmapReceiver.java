package com.prasadam.kmrplayer.SocketClasses.FileTransfer.Bitmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.GroupListenActivity;
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
    private IRequest request;

    public BitmapReceiver(Context context, IRequest request){

        try {
            if(serverSocketChannel == null){
                this.request = request;
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
            new File(cachePath + "/" + request.getResult()).delete();

            File newFile = new File(cachePath, "/" + request.getTimeStamp());

            RandomAccessFile aFile = new RandomAccessFile(newFile, "rw");
            ByteBuffer buffer = ByteBuffer.allocate(KeyConstants.TRANSFER_BUFFER_SIZE);
            FileChannel fileChannel = aFile.getChannel();
            while (clientSocketChannel.read(buffer) > 0) {
                buffer.flip();
                fileChannel.write(buffer);
                buffer.clear();
            }

            Log.d("received bitmap", String.valueOf(newFile.length()));
            NearbyDevicesDetails_DialogFragment.refreshDialogFragment(request.getClientMacAddress());
            GroupListenActivity.updateSong(KeyConstants.SOCKET_CURRENT_SONG_NAME_RESULT);
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