package com.prasadam.kmrplayer.audioPackages.musicServiceClasses;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Handler.Callback;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.widget.RemoteViews;

import com.prasadam.kmrplayer.MainActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.socketClasses.SocketExtensionMethods;

import java.io.IOException;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener{

    public static final String NOTIFY_PREVIOUS = "com.prasadam.kmrplayer.previous";
    public static final String NOTIFY_DELETE = "com.prasadam.kmrplayer.delete";
    public static final String NOTIFY_PAUSE = "com.prasadam.kmrplayer.pause";
    public static final String NOTIFY_PLAY = "com.prasadam.kmrplayer.play";
    public static final String NOTIFY_NEXT = "com.prasadam.kmrplayer.next";
    public static boolean isFocusSnatched = false;

    Bitmap mDummyAlbumArt;
    private static boolean currentVersionSupportLockScreenControls = false;
    private static boolean currentVersionSupportBigNotification = false;
    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    private static NotificationBroadcast notificationBroadcast;
    AudioManager audioManager;
    public static MediaPlayer player;
    private final IBinder musicBind = new MusicBinder();
    private static final int NOTIFY_ID = 626272;
    public static Song currentSong;

    public void onCreate(){

        super.onCreate();
        notificationBroadcast = new NotificationBroadcast();
        currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
        currentVersionSupportBigNotification = UtilFunctions.currentVersionSupportBigNotification();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        player = new MediaPlayer();
        initMusicPlayer();
    }
    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);  //set player properties
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }
    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        //Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

        /*PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);*/

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            if(currentVersionSupportLockScreenControls)
                RegisterRemoteClient();

            PlayerConstants.SONG_CHANGE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {

                    try{
                        Song song = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
                        String songPath = song.getData();
                        newNotification();
                        try{
                            currentSong = song;
                            playSong(songPath, song);
                            PlayerConstants.SONG_PAUSED = false;
                            MainActivity.changeButton();
                            MainActivity.updateNowPlayingUI(getBaseContext());
                            //AudioPlayerActivity.changeUI();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        return false;
                    }
                    catch (Exception ignored){}
                    return false;
                }
            });

            PlayerConstants.PLAY_PAUSE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String message = (String)msg.obj;
                    if(player == null)
                        return false;
                    if(message.equalsIgnoreCase(getResources().getString(R.string.play))){
                        PlayerConstants.SONG_PAUSED = false;
                        if(currentVersionSupportLockScreenControls){
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        player.start();
                    }else if(message.equalsIgnoreCase(getResources().getString(R.string.pause))){
                        PlayerConstants.SONG_PAUSED = true;
                        if(currentVersionSupportLockScreenControls){
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                        }
                        player.pause();
                    }
                    newNotification();
                    try{
                        MainActivity.changeButton();
                        //AudioPlayerActivity.changeButton();
                    }catch(Exception e){}
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }
    private void RegisterRemoteClient(){
        remoteComponentName = new ComponentName(getApplicationContext(), notificationBroadcast.ComponentName());
        try{
            if(remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
                registerReceiver(notificationBroadcast, filter);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }

            remoteControlClient.setTransportControlFlags(
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        }

        catch (Exception ex){}

    }
    private void UpdateMetadata(Song song){

        MainActivity.updateNowPlayingUI(getBaseContext());
        if (remoteControlClient == null)
            return;

        RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, song.getAlbum());
        metadataEditor.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, song.getDuration());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, song.getArtist());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, song.getTitle());
        mDummyAlbumArt = AudioExtensionMethods.getBitMap(getBaseContext(), song.getAlbumArtLocation());
        if(mDummyAlbumArt == null)
            mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art);
        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
        metadataEditor.apply();
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
    private void playSong(String songPath, Song song) {
        try {
            if(currentVersionSupportLockScreenControls){
                UpdateMetadata(song);
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            }
            player.reset();
            player.setDataSource(songPath);
            player.prepare();
            player.start();
            //timer.scheduleAtFixedRate(new MainTask(), 0, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void newNotification() {

        currentSong = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);

        String songName = currentSong.getTitle();
        String albumName = currentSong.getAlbum();
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.launcher_icon)
                .setContentTitle(songName).build();

        setListeners(simpleContentView);
        setListeners(expandedView);

        notification.contentView = simpleContentView;
        if(currentVersionSupportBigNotification){
            notification.bigContentView = expandedView;
        }

        try{
            Bitmap albumArt = AudioExtensionMethods.getBitMap(getBaseContext(), currentSong.getAlbumArtLocation());

            if(albumArt != null){
                notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                if(currentVersionSupportBigNotification){
                    notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                }
            }else{
                notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.mipmap.unkown_album_art);
                if(currentVersionSupportBigNotification){
                    notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.mipmap.unkown_album_art);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(PlayerConstants.SONG_PAUSED){
            notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

            if(currentVersionSupportBigNotification){
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
            }
        }else{
            notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

            if(currentVersionSupportBigNotification){
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
            }
        }

        notification.contentView.setTextViewText(R.id.textSongName, songName);
        notification.contentView.setTextViewText(R.id.textAlbumName, albumName);
        if(currentVersionSupportBigNotification){
            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
            notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);
        }

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFY_ID, notification);
        Log.d("showed", "notification");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }
    public void onCompletion(MediaPlayer mp) {
        Controls.nextControl(getApplicationContext());
    }
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        AudioExtensionMethods.addSongToHistory(getBaseContext(), currentSong.getHashID());
    }
    public void onDestroy() {
        if(player != null){
            player.stop();
            player = null;
        }
        super.onDestroy();
    }
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {

            case AudioManager.AUDIOFOCUS_LOSS:
                if(PlayerConstants.getIsPlayingState())
                    isFocusSnatched = true;
                Controls.pauseControl(getContext());
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if(PlayerConstants.getIsPlayingState())
                    isFocusSnatched = true;
                Controls.pauseControl(getContext());
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                player.setVolume(0.5f, 0.5f);
                break;

            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                player.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                if(isFocusSnatched)
                    Controls.playControl(getContext());
                isFocusSnatched = false;
                break;
        }

    }
    public Context getContext(){ return getBaseContext();}

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketExtensionMethods.stopNSDServies();
                    Thread.sleep(5000);
                    Log.d("testing", "closed nsd services");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        super.onTaskRemoved(rootIntent);
    }
}
