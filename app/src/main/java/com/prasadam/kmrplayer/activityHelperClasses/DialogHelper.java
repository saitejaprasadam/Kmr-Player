package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.AudioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.R;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/14/2016.
 */

public class DialogHelper {

    public static void showNearbyInfo(Context context){
        new MaterialDialog.Builder(context)
                .content(R.string.nearby_info_text)
                .show();
    }

    public static void AddToDialog(final Context context, final Song song){

        new MaterialDialog.Builder(context)
                .items(R.array.add_to_dialog_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which){

                            case 0:
                                AudioExtensionMethods.addToPlaylist(context, song.getHashID());
                                break;

                            case 1:
                                MusicPlayerExtensionMethods.addToNowPlayingPlaylist(context, song);
                                break;
                        }
                    }
                })
                .show();
    }

    public static void AddToDialogAlbum(final Context context, final ArrayList<Song> songsList){

        new MaterialDialog.Builder(context)
                .items(R.array.add_to_dialog_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which){

                            case 0:
                                //AudioExtensionMethods.addToPlaylist(context, song.getHashID());
                                break;

                            case 1:
                                MusicPlayerExtensionMethods.addToNowPlayingPlaylistAlbum(context, songsList);
                                break;
                        }
                    }
                })
                .show();
    }

    public static void AddToDialogArtist(final Context context, final ArrayList<Song> songsList){

        new MaterialDialog.Builder(context)
                .items(R.array.add_to_dialog_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which){

                            case 0:
                                //AudioExtensionMethods.addToPlaylist(context, song.getHashID());
                                break;

                            case 1:
                                MusicPlayerExtensionMethods.addToNowPlayingPlaylistArtist(context, songsList);
                                break;
                        }
                    }
                })
                .show();
    }

    public static void songsSortDialog(Context context) {

        new MaterialDialog.Builder(context)
                .title(R.string.sort_by_text)
                .items(R.array.songs_sort_by)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        return true;
                    }
                })
                .positiveText(R.string.ok_text)
                .show();
    }
}
