package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/28/2016.
 */

public class ShareIntentHelper {

    public static void shareAlbum(Context context, ArrayList<Song> songsList, String albumTitle){

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND_MULTIPLE);
        share.setType("audio/*");

        ArrayList<Uri> files = new ArrayList<>();

        for(Song song : songsList) {
            files.add(Uri.parse(song.getData()));
        }

        albumTitle = albumTitle.trim();
        if(albumTitle.length() > 20)
            albumTitle =  albumTitle.substring(0, 18) + "...";

        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        context.startActivity(Intent.createChooser(share, "Share " + "\'" + albumTitle  + "\'" +  " album using"));

    }

    public static void shareAllSongsFromCurrentArtist(Context context, ArrayList<Song> songsList, String artistTitle) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND_MULTIPLE);
        share.setType("audio/*");

        ArrayList<Uri> files = new ArrayList<>();

        for(Song song : songsList) {
            files.add(Uri.parse(song.getData()));
        }

        artistTitle = artistTitle.trim();
        if(artistTitle.length() > 20)
            artistTitle =  artistTitle.substring(0, 18) + "...";

        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        context.startActivity(Intent.createChooser(share, "Share all songs of " + "\'" + artistTitle  + "\'" +  " artist using"));
    }

    public static void sharePlaylist(Activity customPlaylistInnerActivity, ArrayList<Song> songsList, String playlistName) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND_MULTIPLE);
        share.setType("audio/*");

        ArrayList<Uri> files = new ArrayList<>();

        for(Song song : songsList) {
            files.add(Uri.parse(song.getData()));
        }

        playlistName = playlistName.trim();
        if(playlistName.length() > 20)
            playlistName =  playlistName.substring(0, 18) + "...";

        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        customPlaylistInnerActivity.startActivity(Intent.createChooser(share, "Share " + "\'" + playlistName  + "\'" +  " playlist using"));
    }

    public static void sendSong(Context context ,String songName,Uri songLocation) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_STREAM, songLocation);

        songName = songName.trim();
        if(songName.length() > 20)
            songName = songName.substring(0, 18) + "...";

        context.startActivity(Intent.createChooser(share, "Share " + "\'" + songName + "\'"  + " using"));
    }
}
