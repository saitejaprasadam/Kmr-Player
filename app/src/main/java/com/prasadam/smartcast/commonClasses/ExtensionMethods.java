package com.prasadam.smartcast.commonClasses;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.smartcast.R;
import com.prasadam.smartcast.audioPackages.Song;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Prasadam Saiteja on 3/20/2016.
 */
public class ExtensionMethods {

    //Delete Song
    public static void deleteSong(final Context context, final String songName, final String songLocation) {
        new MaterialDialog.Builder(context)
                .content("Delete this song " + songName)
                .positiveText(R.string.delete_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        File file = new File(songLocation);
                        Log.d("FileExists ", String.valueOf(file.exists()));
                        boolean deleted = file.delete();
                        Log.d("Deleted ", String.valueOf(deleted) + songLocation);
                    }
                })
                .negativeText(R.string.cancel_text)
                .show();
    }


    //Send song using Intent
    public static void sendSong(Context context, String songName ,Uri songLocation) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, songLocation);
        context.startActivity(Intent.createChooser(share, "Share using"));
    }


    //Test path
    public static void testPing()
    {
        Log.d("Test", "Works");
    }
    public static void logwritter(String data)
    {
        Log.d("Log  :  ", data);
    }

    //Display song details
    public static void songDetails(Context context, Song currentSongDetails, String albumPath) {

        StringBuilder content = new StringBuilder();
        File songFile = new File(currentSongDetails.getData());
        content.append(context.getString(R.string.song_location_text) + " : " + currentSongDetails.getData() + "\n\n");
        content.append(context.getString(R.string.duration_text) + " : " + formatIntoHHMMSS(Integer.parseInt(currentSongDetails.getDuration())/ 1000) + "\n\n");
        content.append(context.getString(R.string.file_size_text) + " : " + readableFileSize(songFile.length()) + "\n\n");

        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(currentSongDetails.getData());// the adresss location of the sound on sdcard.
            MediaFormat mf = mex.getTrackFormat(0);
            int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
            content.append(context.getString(R.string.bit_rate_text) + " : " + bitRate/1000 + " kb/s" + "\n\n");
            int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            content.append(context.getString(R.string.sampling_rate_text) + " : " + sampleRate + " Hz");
        } catch (IOException e) {}

        if(albumPath != null)
            new MaterialDialog.Builder(context)
                    .icon(Drawable.createFromPath(albumPath))
                    .title(currentSongDetails.getTitle())
                    .positiveText(context.getString(R.string.ok_text))
                    .content(content.toString())
                    .show();

        else
            new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.song_details_text))
                    .positiveText(context.getString(R.string.ok_text))
                    .content(content.toString())
                    .show();
    }


    //convert seconds to human understandable time
    public static String formatIntoHHMMSS(int secsIn) {

        int hours = secsIn / 3600;
        int minutes = (secsIn % 3600) / 60;
        int seconds = (secsIn % 3600) % 60;

        if(hours == 0)
        return (minutes < 10 ? "0" : "") + minutes
                + ":" + (seconds< 10 ? "0" : "") + seconds;

        return ( (hours < 10 ? "0" : "") + hours
                + ":" + (minutes < 10 ? "0" : "") + minutes
                + ":" + (seconds< 10 ? "0" : "") + seconds );

    }


    //convert size to human understandable size
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    //Set Song as ringtone
    public static void setSongAsRingtone(final Context context, final Song currentSongDetails) {

        new MaterialDialog.Builder(context)
                .content("Set song as ringtone : " + currentSongDetails.getTitle())
                .positiveText(R.string.set_text)
                .negativeText(R.string.cancel_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ContentValues values = new ContentValues();

                        File song = new File(currentSongDetails.getData());
                        values.put(MediaStore.MediaColumns.DATA, song.getAbsolutePath());
                        values.put(MediaStore.MediaColumns.TITLE, currentSongDetails.getTitle());
                        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
                        values.put(MediaStore.MediaColumns.SIZE, currentSongDetails.getDuration());
                        values.put(MediaStore.Audio.Media.ARTIST, currentSongDetails.getArtist());
                        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                        values.put(MediaStore.Audio.Media.IS_ALARM, false);
                        values.put(MediaStore.Audio.Media.IS_MUSIC, true);

                        Uri uri = MediaStore.Audio.Media.getContentUriForPath(song.getAbsolutePath());
                        Uri newUri = context.getContentResolver().insert(uri, values);

                        try {
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
                            Toast.makeText(context, "Ringtone set to " + currentSongDetails.getTitle(), Toast.LENGTH_SHORT).show();
                        } catch (Throwable t) {
                            Toast.makeText(context, "Error setting ringtone", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();


    }
}
