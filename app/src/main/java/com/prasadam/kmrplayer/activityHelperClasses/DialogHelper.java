package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
                                AudioExtensionMethods.addToPlaylist(context, new ArrayList<>(songsList));
                                break;

                            case 1:
                                MusicPlayerExtensionMethods.addToNowPlayingPlaylist(context, new ArrayList<>(songsList), "Album added to now playing playlist");
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
                                AudioExtensionMethods.addToPlaylist(context, new ArrayList<>(songsList));
                                break;

                            case 1:
                                MusicPlayerExtensionMethods.addToNowPlayingPlaylist(context, new ArrayList<>(songsList), "Artist songs added to now playing playlist");
                                break;
                        }
                    }
                })
                .show();
    }

    public static void AddToDialogPlaylist(final Context context, final ArrayList<Song> songsList){

        new MaterialDialog.Builder(context)
                .items(R.array.add_to_dialog_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which){

                            case 0:
                                AudioExtensionMethods.addToPlaylist(context, new ArrayList<>(songsList));
                                break;

                            case 1:
                                MusicPlayerExtensionMethods.addToNowPlayingPlaylist(context, new ArrayList<>(songsList), "Playlist songs added to now playing playlist");
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

    public static void checkForNetworkState(final Context context, FloatingActionButton fab) {

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Method[] wmMethods = wifi.getClass().getDeclaredMethods();
        boolean hotspot = false;

        for (Method method: wmMethods) {
            if (method.getName().equals("isWifiApEnabled")) {

                try {
                    hotspot = (boolean) method.invoke(wifi);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!mWifi.isConnected() && !hotspot) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new MaterialDialog.Builder(context)
                            .content(R.string.Connect_to_a_network_text)
                            .positiveText(R.string.WIFI_text)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            })
                            .negativeText(R.string.hotspot_text)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                                    final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
                                    intent.setComponent(cn);
                                    context.startActivity(intent);
                                }
                            })
                            .neutralText(R.string.cancel_text)
                            .show();
                }
            });
        }

        else
            fab.setVisibility(View.INVISIBLE);
    }
}
