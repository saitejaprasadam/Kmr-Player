package com.prasadam.kmrplayer.SharedClasses;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.ITransferableSong;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.AlbumArrayList;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.ArtistArrayList;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;
import com.prasadam.kmrplayer.UI.Fragments.AlbumsFragment;
import com.prasadam.kmrplayer.UI.Fragments.ArtistFragment;
import com.prasadam.kmrplayer.UI.Fragments.SongsFragment;

import java.util.ArrayList;

/*
 * Created by Prasadam saiteja on 3/14/2016.
 */

public class SharedVariables {

    public static void Initializers(Context context) {
        Fresco.initialize(context);
    }

    public static volatile SongsArrayList fullSongsList = new SongsArrayList() {
        @Override
        public void notifyDataSetChanged() { SongsFragment.updateList(); }
        public void notifyItemRemoved(int index) {SongsFragment.onItemRemoved(index);}
        public void notifyItemInserted(int index) {SongsFragment.onItemAdded(index);}
        public void notifyItemChanged(int index) { SongsFragment.onItemChanged(index); }
    };
    public static volatile AlbumArrayList fullAlbumList = new AlbumArrayList() {
        @Override
        public void onArrayListChanged() {AlbumsFragment.updateList();}
        public void onItemRemovedListener(int index) {AlbumsFragment.onItemRemoved(index);}
        public void onItemAddedListener(int index) {AlbumsFragment.onItemAdded(index);}
    };
    public static volatile ArtistArrayList fullArtistList = new ArtistArrayList() {
        @Override
        public void onArrayListChanged() {ArtistFragment.updateList();}
        public void onItemRemovedListener(int index) { ArtistFragment.onItemRemoved(index); }
        public void onItemAddedListener(int index) { ArtistFragment.onItemAdded(index); }
    };

    public static volatile ArrayList<IRequest> fullEventsList = new ArrayList<>();
    public static volatile ArrayList<ITransferableSong> fullTransferList = new ArrayList<>();

    public static ArrayList<ITransferableSong> getFullTransferList(){

        ArrayList<ITransferableSong> transferableSongArrayList = new ArrayList<>();

        for(ITransferableSong transferableSong : fullTransferList)
            if(AudioExtensionMethods.checkIfSongExists(transferableSong.getSong().getHashID())
                    && !transferableSongArrayList.contains(transferableSong)
                    && transferableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.Completed)
                transferableSongArrayList.add(transferableSong);

        return transferableSongArrayList;
    }
    public static ArrayList<ITransferableSong> TransferList(NSD serverObject){

        ArrayList<ITransferableSong> transferableSongArrayList = new ArrayList<>();

        for (ITransferableSong transferableSong : fullTransferList)
            if(transferableSong.getClient_mac_address().equals(serverObject.getMacAddress())
                    && !transferableSongArrayList.contains(transferableSong)
                    && AudioExtensionMethods.checkIfSongExists(transferableSong.getSong().getHashID())
                    && transferableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.Completed)
                transferableSongArrayList.add(transferableSong);

        return transferableSongArrayList;
    }
}