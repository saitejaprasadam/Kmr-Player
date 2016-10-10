package com.prasadam.kmrplayer.SocketClasses.FileTransfer.Music;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NetworkAdapter.ReceivedSongsAdapter;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.DatabaseHelperClasses.db4oHelper;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.ITransferableSong;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.RequestsActivity;
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
 * Created by Prasadam Saiteja on 7/16/2016.
 */

public class FileReceiver extends AsyncTask<Void, Void, Void>{

    private ServerSocketChannel serverSocketChannel;
    private Context context;
    private IRequest request;

    public FileReceiver(Context context, IRequest request){

        try {
            if(serverSocketChannel == null){
                this.request = request;
                this.context = context;
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(KeyConstants.FILE_TRANSFER_SOCKET_PORT_ADDRESS));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        for (final ITransferableSong transferableSong : request.getSongsToTransferArrayList()) {
            try {
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                final String fileName = ExtensionMethods.extractFileNameFromPath(transferableSong.getSong().getData());
                File PlayerDirectory  = new File(KeyConstants.PLAYER_DIRECTORY_PATH);

                if(!PlayerDirectory.exists())
                    PlayerDirectory.mkdir();

                final File songFile = new File(PlayerDirectory.getAbsolutePath() + File.separator + fileName);
                songFile.createNewFile();
                RandomAccessFile aFile = new RandomAccessFile(songFile, "rw");
                ByteBuffer buffer = ByteBuffer.allocate(KeyConstants.TRANSFER_BUFFER_SIZE);
                FileChannel fileChannel = aFile.getChannel();
                while (clientSocketChannel.read(buffer) > 0) {
                    buffer.flip();
                    fileChannel.write(buffer);
                    buffer.clear();
                }

                transferableSong.setSongTransferState(SocketExtensionMethods.TRANSFER_STATE.Completed);
                ExtensionMethods.scanMedia(context, PlayerDirectory + File.separator + fileName);
                fileChannel.close();
                clientSocketChannel.close();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        Song song = AudioExtensionMethods.getSongFromPath(context, songFile.getAbsolutePath());
                        if(song != null){
                            SharedVariables.fullSongsList.add(song);
                            db4oHelper.updateSongTrasferableObject(context, transferableSong);
                            ReceivedSongsAdapter.updateAdapter();
                            NearbyDevicesDetails_DialogFragment.refreshDialogFragment(request.getClientMacAddress());
                        }
                    }
                }, 800);
            }

            catch (Exception e) {
                //Log.d("Exception", e.toString());
                e.printStackTrace();
            }
        }

        request.setEventState(SocketExtensionMethods.EVENT_STATE.Completed);
        db4oHelper.updateRequestObject(context, request);
        RequestsActivity.eventNotifyDataSetChanged();
        try {
            serverSocketChannel.close();
        } catch (Exception ignored) {}
        return null;
    }
}