package com.prasadam.kmrplayer.DatabaseHelper;

import android.content.Context;
import android.content.ContextWrapper;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.prasadam.kmrplayer.ModelClasses.TransferableSong;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.ModelClasses.Event;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities.RequestsActivity;

import java.io.File;
import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 9/16/2016.
 */

public class db4oHelper{

    private static final String eventsDbName = "events.db4o";
    private static final String transfersDbName = "transfers.db4o";

    public static ArrayList<Event> getEventObjects(final Context context) {

        SocketExtensionMethods.requestStrictModePermit();
        ArrayList<Event> list = new ArrayList<>();
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + eventsDbName);
        ObjectSet<Event> result = db.query(Event.class);

        while (result.hasNext()){
            Event event = result.next();

            if(event.getEventState() == SocketExtensionMethods.EVENT_STATE.WAITING)
                event.setEventState(SocketExtensionMethods.EVENT_STATE.Denied);

            list.add(event);
        }

        db.close();
        return list;
    }
    public static void pushEventObject(final Context context, final Event event) {

        SharedVariables.fullEventsList.add(event);
        RequestsActivity.eventNotifyDataSetChanged();
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + eventsDbName);
        db.store(event);
        db.commit();
        db.close();
    }
    public static void removeEventObject(final Context context, final Event event) {
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + eventsDbName);
        ObjectSet result = db.queryByExample(new Event(event.getTimeStamp()));
        Event event1 = (Event) result.next();
        db.delete(event1);
        db.commit();
        db.close();
    }
    public static void updateEventObject(final Context context, final Event updatedEvent){

        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + eventsDbName);
        Event temp = new Event(updatedEvent.getTimeStamp());
        ObjectSet result = db.queryByExample(temp);
        if(result.hasNext()) {
            Event event = (Event) result.next();
            event.copy(updatedEvent);
            db.store(event);
            db.commit();
            db.close();
        }
        db.close();
    }

    public static ArrayList<TransferableSong> getTransferableSongObjects(final Context context){

        SocketExtensionMethods.requestStrictModePermit();
        ArrayList<TransferableSong> list = new ArrayList<>();
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + transfersDbName);
        ObjectSet<TransferableSong> result = db.query(TransferableSong.class);

        while (result.hasNext()){
            TransferableSong transfrableSong = result.next();
            if(transfrableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.WAITING)
                transfrableSong.setSongTransferState(SocketExtensionMethods.TRANSFER_STATE.Denied);

            else if(transfrableSong.getSongTransferState() == SocketExtensionMethods.TRANSFER_STATE.IN_PROGRESS)
                transfrableSong.setSongTransferState(SocketExtensionMethods.TRANSFER_STATE.Completed);

            list.add(transfrableSong);
        }

        db.close();
        return list;
    }
    public static void pushSongTransferObject(final Context context, final ArrayList<TransferableSong> songsToTransferArrayList) {

        SharedVariables.fullTransferList.addAll(songsToTransferArrayList);
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + transfersDbName);
        db.store(songsToTransferArrayList);
        db.commit();
        db.close();
    }
    public static void removeTransferObject(final Context context, final TransferableSong transferableSong) {

        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + transfersDbName);
        db.delete(new TransferableSong(transferableSong.getClient_mac_address(), transferableSong.getSong().getHashID(), transferableSong.getSong().getID()));
        db.commit();
        db.close();
    }
    public static void updateSongTrasferableObject(Context context, TransferableSong transferableSong) {

        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + File.separator + transfersDbName);
        TransferableSong temp = new TransferableSong(transferableSong.getClient_mac_address(), transferableSong.getSong().getHashID(), transferableSong.getSong().getID());
        ObjectSet result = db.queryByExample(temp);
        if(result.hasNext()) {
            TransferableSong finalTransferableSong = (TransferableSong) result.next();
            finalTransferableSong.copy(transferableSong);
            db.store(finalTransferableSong);
            db.commit();
            db.close();
        }
        db.close();
    }
}