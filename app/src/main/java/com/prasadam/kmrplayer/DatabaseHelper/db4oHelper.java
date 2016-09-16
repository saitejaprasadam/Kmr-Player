package com.prasadam.kmrplayer.DatabaseHelper;

import android.content.Context;
import android.content.ContextWrapper;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.prasadam.kmrplayer.SocketClasses.Event;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.UI.Activities.HelperActivities.EventsActivity;

import java.util.ArrayList;
import java.util.Collections;

/*
 * Created by Prasadam Saiteja on 9/16/2016.
 */

public class db4oHelper{

    private static ObjectContainer db;

    public static void pushEventObject(Context context, Event event){

        if(db == null)
            db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + "/events.db4o");
        db.store(event);
        db.commit();
        EventsActivity.updateEventsAdapter();
    }
    public static ArrayList<Event> getEventObjects(Context context){

        ArrayList<Event> list = new ArrayList<>();
        if(db == null)
            db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + "/events.db4o");
        ObjectSet<Event> result = db.query(Event.class);

        while (result.hasNext()){
            Event event = result.next();
            if(event.getEventState() == null)
                event.setEventState(SocketExtensionMethods.EVENT_STATE.Denied);
            list.add(event);
        }

        Collections.reverse(list);
        return list;
    }
    public static void removeEventObject(Context context, Event e) {
        if(db == null)
            db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + "/events.db4o");
        db.delete(e);
        db.commit();
    }

    public static void updateEventObject(Context context, Event updatedEvent){
        if(db == null)
            db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), new ContextWrapper(context).getFilesDir() + "/events.db4o");

        db.store(updatedEvent);
        db.commit();
    }
}