package com.prasadam.kmrplayer.AudioPackages;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;

/*
 * Created by Prasadam Saiteja on 9/8/2016.
 */

public class HearingHelper {

    public static void StartHearingHelper(){
        new Thread(new HearingThread()).start();
    }
    public static void StopHearingHelper(){

    }

    private static class HearingThread extends Thread {

        public void run(){

            int i = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, i);
            audioRecorder.startRecording();
            byte audiobuffer[] = new byte[20];

            AudioTrack aud= new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT),
                    AudioTrack.MODE_STREAM);
            aud.play();
            aud.setVolume(2.5f);

            while(true) {
                audioRecorder.read(audiobuffer,0,audiobuffer.length);
                aud.write(audiobuffer,0,audiobuffer.length);
            }
        }
    }
}
