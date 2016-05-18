package com.prasadam.smartcast.audioPackages;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.prasadam.smartcast.MainActivity;
import com.prasadam.smartcast.R;

import java.io.File;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer player; //media player
    private List<Song> songs;//song list
    private int songPosn;//current position
    private final IBinder musicBind = new MusicBinder();//binder
    private String songTitle="";//title of current song
    private static final int NOTIFY_ID=1;//notification id
    private boolean shuffle=false;//shuffle flag and random
    private Random rand;

    public void onCreate(){
        super.onCreate();//create the service
        songPosn=0;//initialize position
        rand=new Random();//random
        player = new MediaPlayer();//create player
        initMusicPlayer();//initialize
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);  //set player properties
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //pass song list
    public void setList(List<Song> theSongs){
        songs = theSongs;
    }

    //binder
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    //play a song
    public void playSong(){
        player.reset();//play
        Song playSong = songs.get(songPosn);//get song
        songTitle = playSong.getTitle();//get title
        long currSong = playSong.getID();//get id
        Log.d("test", playSong.getTitle());
        //set uri
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        //set the data source
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    //set the song
    public void setSong(int songIndex){
        songPosn = songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        Log.d("completed", String.valueOf(player.getCurrentPosition()));
        if(player.getCurrentPosition() > 0){
            mp.reset();
            Log.d("Playing Next", "Wait");
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        //notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        Song currentSong = songs.get(songPosn);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_favorite_black_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle(songTitle)
                .setContentText(songs.get(getPosn()).getArtist());
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);
    }

    //playback methods
    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    //skip to previous track
    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong == songPosn){
                newSong = rand.nextInt(songs.size());
            }
            songPosn = newSong;
        }
        else{
            songPosn++;
            if(songPosn>=songs.size()) songPosn = 0;
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    //toggle shuffle
    public void Shuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    public void setShuffle(boolean value){
        shuffle = value;
    }
}