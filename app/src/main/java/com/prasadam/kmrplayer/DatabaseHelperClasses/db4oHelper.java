package com.prasadam.kmrplayer.DatabaseHelperClasses;

import android.content.Context;
import android.content.ContextWrapper;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.IRequest;
import com.prasadam.kmrplayer.ModelClasses.SerializableClasses.ITransferableSong;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.RequestsActivity;

import java.io.File;
import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 9/16/2016.
 */

public class db4oHelper{

    private static final String requestsDbName = "requests.db4o";
    private static final String transfersDbName = "transfers.db4o";

    public static ArrayList<IRequest> getRequestObjects(final Context context) {

        SocketExtensionMethods.requestStrictModePermit();
        ArrayList<IRequest> list = new ArrayList<>();
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + requestsDbName);
        ObjectSet<IRequest> result = db.query(IRequest.class);

        while (result.hasNext()){
            IRequest request = result.next();

            if(request.getEventState() == SocketExtensionMethods.EVENT_STATE.WAITING)
                request.setEventState(SocketExtensionMethods.EVENT_STATE.Denied);

            list.add(request);
        }

        db.close();
        return list;
    }
    public static void pushRequestObject(final Context context, final IRequest request) {

        SharedVariables.fullEventsList.add(request);
        RequestsActivity.eventNotifyDataSetChanged();
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + requestsDbName);
        db.store(request);
        db.commit();
        db.close();
    }
    public static void removeRequestObject(final Context context, final IRequest request) {
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + requestsDbName);
        ObjectSet result = db.queryByExample(new IRequest(request.getTimeStamp()));
        IRequest request1 = (IRequest) result.next();
        db.delete(request1);
        db.commit();
        db.close();
    }
    public static void updateRequestObject(final Context context, final IRequest updatedRequest){

        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + requestsDbName);
        IRequest temp = new IRequest(updatedRequest.getTimeStamp());
        ObjectSet result = db.queryByExample(temp);
        if(result.hasNext()) {
            IRequest request = (IRequest) result.next();
            request.copy(updatedRequest);
            db.store(request);
            db.commit();
            db.close();
        }
        db.close();
    }

    public static ArrayList<ITransferableSong> getTransferableSongObjects(final Context context){

        SocketExtensionMethods.requestStrictModePermit();
        ArrayList<ITransferableSong> list = new ArrayList<>();
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + transfersDbName);
        ObjectSet<ITransferableSong> result = db.query(ITransferableSong.class);

        while (result.hasNext()){
            ITransferableSong transfrableSong = result.next();
            if(transfrableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.WAITING)
                transfrableSong.setSongTransferState(SocketExtensionMethods.TRANSFER_STATE.Denied);

            else if(transfrableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.IN_PROGRESS)
                transfrableSong.setSongTransferState(SocketExtensionMethods.TRANSFER_STATE.Completed);

            list.add(transfrableSong);
        }

        db.close();
        return list;
    }
    public static void pushSongTransferObject(final Context context, final ArrayList<ITransferableSong> songsToTransferArrayList) {

        SharedVariables.fullTransferList.addAll(songsToTransferArrayList);
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + transfersDbName);
        db.store(songsToTransferArrayList);
        db.commit();
        db.close();
    }
    public static void removeTransferObject(final Context context, final ITransferableSong transferableSong) {

        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + transfersDbName);
        db.delete(new ITransferableSong(transferableSong.getClient_mac_address(), transferableSong.getSong().getHashID(), transferableSong.getSong().getID()));
        db.commit();
        db.close();
    }
    public static void updateSongTrasferableObject(final Context context, final ITransferableSong transferableSong) {

        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + transfersDbName);
        ITransferableSong temp = new ITransferableSong(transferableSong.getClient_mac_address(), transferableSong.getSong().getHashID(), transferableSong.getSong().getID());
        ObjectSet result = db.queryByExample(temp);
        if(result.hasNext()) {
            ITransferableSong finalTransferableSong = (ITransferableSong) result.next();
            finalTransferableSong.copy(transferableSong);
            db.store(finalTransferableSong);
            db.commit();
            db.close();
        }
        db.close();
    }
}