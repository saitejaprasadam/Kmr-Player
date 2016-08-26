package com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Handler.Callback;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.media.MediaMetadataRetriever;
import android.widget.RemoteViews;

import com.prasadam.kmrplayer.ActivityHelperClasses.SharedPreferenceHelper;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.AchievementUnlocked;
import com.prasadam.kmrplayer.Activities.MainActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.Activities.VerticalSlidingDrawerBaseActivity;
import com.prasadam.kmrplayer.Widgets.NowPlayingWidget;

import java.io.IOException;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener{

    public static final String NOTIFY_PREVIOUS = "com.prasadam.kmrplayer.previous";
    public static final String NOTIFY_FAV = "com.prasadam.kmrplayer.favorite";
    public static final String NOTIFY_PAUSE = "com.prasadam.kmrplayer.pause";
    public static final String NOTIFY_PLAY = "com.prasadam.kmrplayer.play";
    public static final String NOTIFY_NEXT = "com.prasadam.kmrplayer.next";
    public boolean isFocusSnatched = false;

    private boolean currentVersionSupportLockScreenControls = false;
    private boolean currentVersionSupportBigNotification = false;
    private final IBinder musicBind = new MusicBinder();
    private RemoteControlClient remoteControlClient;
    private NotificationBroadcast notificationBroadcast;
    private AudioManager audioManager;
    private Handler historyHandler;
    public static MediaPlayer player;
    public static Song currentSong;

    public void onCreate(){

        super.onCreate();
        notificationBroadcast = new NotificationBroadcast();
        currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
        currentVersionSupportBigNotification = UtilFunctions.currentVersionSupportBigNotification();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, new SongsContentObserver(new Handler()));
        player = new MediaPlayer();
        initMusicPlayer();
    }
    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

        if(PlayerConstants.SONG_NUMBER < PlayerConstants.getPlaylistSize()){
            try {
                currentSong = PlayerConstants.getPlaylist().get(PlayerConstants.SONG_NUMBER);
                player.reset();
                player.setDataSource(currentSong.getData());
                player.prepare();
                player.start();
                player.pause();
                SharedPreferenceHelper.getDuration(getContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent fav = new Intent(NOTIFY_FAV);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_prev_button, pPrevious);

        if(player.isPlaying()){
            PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.notification_play_pause_button, pPause);
        }
        else{
            PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.notification_play_pause_button, pPlay);
        }

        PendingIntent pFav = PendingIntent.getBroadcast(getApplicationContext(), 0, fav, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_favorite_button, pFav);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_next_button, pNext);
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
                        Song song = PlayerConstants.getPlaylist().get(PlayerConstants.SONG_NUMBER);
                        String songPath = song.getData();
                        newNotification();
                        try{
                            currentSong = song;
                            playSong(songPath, song);
                            PlayerConstants.SONG_PAUSED = false;
                            if(SharedVariables.globalActivityContext != null){
                                VerticalSlidingDrawerBaseActivity.changeButton();
                                VerticalSlidingDrawerBaseActivity.updateNowPlayingUI(getBaseContext());
                            }
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
                            UpdateMetadata(currentSong);
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        player.start();
                    }else if(message.equalsIgnoreCase(getResources().getString(R.string.pause))){
                        PlayerConstants.SONG_PAUSED = true;
                        if(currentVersionSupportLockScreenControls){
                            UpdateMetadata(currentSong);
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                        }
                        player.pause();
                    }
                    try{
                        newNotification();
                        VerticalSlidingDrawerBaseActivity.changeButton();
                    }
                    catch(Exception ignored){}
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        PlayerConstants.NOTIFICATION_HANDLER = new Handler(new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                newNotification();
                return true;
            }
        });

        return START_STICKY;
    }
    private void RegisterRemoteClient(){
        ComponentName remoteComponentName = new ComponentName(getApplicationContext(), notificationBroadcast.ComponentName());
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

        catch (Exception ignored){}

    }
    private void UpdateMetadata(Song song){

        currentSong = song;
        if(currentSong != null){
            if(SharedVariables.globalActivityContext != null)
                VerticalSlidingDrawerBaseActivity.updateNowPlayingUI(getBaseContext());
            if (remoteControlClient == null)
                return;

            RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
            metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, song.getAlbum());
            metadataEditor.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, song.getDuration());
            metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, song.getArtist());
            metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, song.getTitle());
            Bitmap mDummyAlbumArt = AudioExtensionMethods.getBitMap(getBaseContext(), song.getAlbumArtLocation());
            if(mDummyAlbumArt == null)
                mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.unkown_album_art);
            metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
            metadataEditor.apply();
            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
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
            SharedPreferenceHelper.setLastPlayingSongPosition(getContext());
            if(historyHandler != null)
                historyHandler.removeCallbacks(historyRunnable);
            historyHandler = new Handler();
            historyHandler.postDelayed(historyRunnable, 10000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void newNotification() {
        currentSong = PlayerConstants.getPlaylist().get(PlayerConstants.SONG_NUMBER);
        updateWidget();
        RemoteViews smallView = new RemoteViews(getPackageName(), R.layout.notification);
        smallView.setTextViewText(R.id.notification_song_name, currentSong.getTitle());
        smallView.setTextViewText(R.id.notification_artist_name, currentSong.getArtist());

        RemoteViews expanedView = new RemoteViews(getPackageName(), R.layout.notification_expanded);
        expanedView.setTextViewText(R.id.notification_song_name, currentSong.getTitle());
        expanedView.setTextViewText(R.id.notification_album_name, currentSong.getAlbum());
        expanedView.setTextViewText(R.id.notification_artist_name, currentSong.getArtist());

        if(PlayerConstants.SONG_PAUSED){
            smallView.setImageViewBitmap(R.id.notification_play_pause_button, ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_play_arrow_black_24dp)).getBitmap());
            expanedView.setImageViewBitmap(R.id.notification_play_pause_button, ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_play_arrow_black_24dp)).getBitmap());
        }
        else{
            smallView.setImageViewBitmap(R.id.notification_play_pause_button, ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_pause_black_24dp)).getBitmap());
            expanedView.setImageViewBitmap(R.id.notification_play_pause_button, ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_pause_black_24dp)).getBitmap());
        }

        if(currentSong.getIsLiked(this))
            expanedView.setImageViewBitmap(R.id.notification_favorite_button, ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_favorite_red_24dp)).getBitmap());

        else
            expanedView.setImageViewBitmap(R.id.notification_favorite_button, ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_favorite_border_black_24dp)).getBitmap());


        setListeners(smallView);
        setListeners(expanedView);

        Bitmap albumArt = AudioExtensionMethods.getBitMap(getBaseContext(), currentSong.getAlbumArtLocation());
        if(albumArt != null){
            smallView.setImageViewBitmap(R.id.notification_album_art, albumArt);
            expanedView.setImageViewBitmap(R.id.notification_album_art, albumArt);
        }

        else{
            smallView.setImageViewBitmap(R.id.notification_album_art, ((BitmapDrawable) getResources().getDrawable(R.mipmap.unkown_album_art)).getBitmap());
            expanedView.setImageViewBitmap(R.id.notification_album_art, ((BitmapDrawable) getResources().getDrawable(R.mipmap.unkown_album_art)).getBitmap());
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContent(smallView)
                .setSmallIcon(R.mipmap.launcher_icon)
                .setOngoing(true);

        if(currentVersionSupportBigNotification)
            builder.setCustomBigContentView(expanedView);

        Intent nIntent = new Intent(this, MainActivity.class);
        nIntent.putExtra("notificationIntent", true);
        nIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(626272, notification);

        Log.d("showed", "notifications");
    }
    private void updateWidget() {
        Intent intent = new Intent(this, NowPlayingWidget.class);
        int[] ids = {R.id.widget_now_playing_album_art, R.id.widget_song_name, R.id.widget_artist_name, R.id.widget_play_pause_button, R.id.widget_fav_button};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intent);
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
        showAchivementUnlocked();
        Controls.nextControl(getApplicationContext());
    }

    private void showAchivementUnlocked() {
        if(PlayerConstants.SONG_NUMBER + 1 < PlayerConstants.getPlaylistSize()){
            Song nextSong = PlayerConstants.getPlaylist().get(PlayerConstants.SONG_NUMBER + 1);
            if(nextSong != null){
                Bitmap albumArt = AudioExtensionMethods.getBitMap(nextSong.getAlbumArtLocation());
                if(albumArt != null)
                    new AchievementUnlocked(getApplicationContext())
                            .setTitle(nextSong.getTitle())
                            .setTitleColor(SharedVariables.globalActivityContext.getResources().getColor(R.color.white))
                            .setBackgroundColor(SharedVariables.globalActivityContext.getResources().getColor(R.color.launcher_background_color))
                            .setIcon(new BitmapDrawable(albumArt))
                            .setSubTitle(nextSong.getArtist())
                            .setSubtitleColor(SharedVariables.globalActivityContext.getResources().getColor(R.color.layout_Background)).isLarge(false).build().show();
                else
                    new AchievementUnlocked(getApplicationContext())
                            .setTitle(nextSong.getTitle())
                            .setTitleColor(SharedVariables.globalActivityContext.getResources().getColor(R.color.white))
                            .setBackgroundColor(SharedVariables.globalActivityContext.getResources().getColor(R.color.launcher_background_color))
                            .setIcon(getDrawable(R.mipmap.launcher_icon))
                            .setSubTitle(nextSong.getArtist())
                            .setSubtitleColor(SharedVariables.globalActivityContext.getColor(R.color.layout_Background)).isLarge(false).build().show();
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER " + what, "Playback Error " + extra);
        //mp.reset();
        return true;
    }
    public void onPrepared(MediaPlayer mp) {
        Log.d("PReapred", currentSong.getTitle());
    }
    public void onDestroy() {
        if(player != null){
            player.stop();
            player = null;
        }
        super.onDestroy();
    }
    public void onAudioFocusChange(int focusChange) {

        try{
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

        catch (Exception e){ Log.d("Exception", e.toString());}

    }
    public Context getContext(){ return getBaseContext();}

    Runnable historyRunnable = new Runnable() {
        @Override
        public void run() {
            AudioExtensionMethods.addSongToHistory(getBaseContext(), currentSong.getHashID());
            historyHandler.removeCallbacks(this);
        }
    };

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
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
