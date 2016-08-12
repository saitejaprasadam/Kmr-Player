package com.prasadam.kmrplayer.SharedClasses;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;

/*
 * Created by Prasadam Saiteja on 2/28/2016.
 */
public class mediaController {

    public static class music{

        public  static MusicService musicService;
        private static Intent mediaServiceIntent;
        private static Context context;

        public static void Initializer(final Context contextParams) {
            context = contextParams;
            if(mediaServiceIntent == null){
                mediaServiceIntent = new Intent(context, MusicService.class);
                context.bindService(mediaServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
                context.startService(mediaServiceIntent);
            }
        }

        public static ServiceConnection musicConnection = new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
                //get service
                musicService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
    }
}
