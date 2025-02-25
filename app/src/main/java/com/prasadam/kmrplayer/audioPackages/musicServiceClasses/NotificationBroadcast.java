package com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

/*
 * Created by Prasadam Saiteja on 5/31/2016.
 */

public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
                KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
                if (keyEvent != null && keyEvent.getAction() != KeyEvent.ACTION_DOWN) return;

                if (keyEvent != null) {
                    switch (keyEvent.getKeyCode()) {
                        case KeyEvent.KEYCODE_HEADSETHOOK:
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            if(!PlayerConstants.SONG_PAUSED)
                                Controls.pauseControl(context);
                            else
                                Controls.playControl(context);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            Controls.playControl(context);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            Controls.pauseControl(context);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_STOP:
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            Controls.nextControl(context);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            Controls.previousControl(context);
                            break;
                    }
                }
            }

            else{

                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()))
                    Controls.pauseControl(context);
                else if (intent.getAction().equals(MusicService.NOTIFY_PLAY))
                    Controls.playControl(context);
                else if (intent.getAction().equals(MusicService.NOTIFY_PAUSE))
                    Controls.pauseControl(context);
                else if (intent.getAction().equals(MusicService.NOTIFY_NEXT))
                    Controls.nextControl(context);
                else if (intent.getAction().equals(MusicService.NOTIFY_FAV))
                    Controls.favControl(context);
                else if (intent.getAction().equals(MusicService.NOTIFY_PREVIOUS))
                    Controls.previousControl(context);

            }
        }

        catch (Exception ignored){}
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}
