package com.prasadam.kmrplayer.ActivityHelperClasses;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.Controls;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicService;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.ListenerClasses.HidingScrollListener;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;
import com.prasadam.kmrplayer.UI.Fragments.HelperFragments.NoItemsFragment;
import com.prasadam.kmrplayer.UI.Fragments.HelperFragments.NoItemsFragmentV4;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;

/*
 * Created by Prasadam Saiteja on 7/14/2016.
 */

public class ActivityHelper {

    public static void setDisplayHome(AppCompatActivity appCompatActivity){
        if(appCompatActivity.getSupportActionBar() != null ){
            appCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_chevron_left_white_24dp);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static NoItemsFragmentV4 showEmptyFragment(Activity activtiy, String message){
        NoItemsFragmentV4 newFragment = new NoItemsFragmentV4();
        FragmentTransaction ft = activtiy.getFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, newFragment).commitAllowingStateLoss();
        newFragment.setDescriptionTextView(message);
        return newFragment;
    }
    public static NoItemsFragmentV4 showEmptyFragment(Activity activtiy, String message, FrameLayout fragmentContainer){
        NoItemsFragmentV4 newFragment = new NoItemsFragmentV4();
        FragmentTransaction ft = activtiy.getFragmentManager().beginTransaction();
        ft.replace(fragmentContainer.getId(), newFragment).commitAllowingStateLoss();
        newFragment.setDescriptionTextView(message);
        return newFragment;
    }
    public static NoItemsFragment showEmptyFragmentChildFragment(Fragment fragment, String message, FrameLayout fragmentContainer){
        NoItemsFragment newFragment = new NoItemsFragment();
        fragment.getChildFragmentManager().beginTransaction().add(fragmentContainer.getId(), newFragment).commit();;
        newFragment.setDescriptionTextView(message);
        return newFragment;
    }

    public static void setBackButtonToCustomToolbarBar(AppCompatActivity mAcitivity) {
        Toolbar toolbar = (Toolbar) mAcitivity.findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(mAcitivity.getResources().getDrawable(R.mipmap.ic_more_vert_white_24dp));
        mAcitivity.setSupportActionBar(toolbar);
    }
    public static int[] getAvailableColor(Context context, Palette palette) {
        int[] temp = new int[3];
        if (palette.getVibrantSwatch() != null) {
            temp[0] = palette.getVibrantSwatch().getRgb();
            temp[1] = palette.getVibrantSwatch().getBodyTextColor();
            temp[2] = palette.getVibrantSwatch().getTitleTextColor();
        } else if (palette.getLightVibrantSwatch() != null) {
            temp[0] = palette.getLightVibrantSwatch().getRgb();
            temp[1] = palette.getLightVibrantSwatch().getBodyTextColor();
            temp[2] = palette.getLightVibrantSwatch().getTitleTextColor();
        } else if (palette.getMutedSwatch() != null) {
            temp[0] = palette.getMutedSwatch().getRgb();
            temp[1] = palette.getMutedSwatch().getBodyTextColor();
            temp[2] = palette.getMutedSwatch().getTitleTextColor();
        } else if (palette.getLightMutedSwatch() != null) {
            temp[0] = palette.getLightMutedSwatch().getRgb();
            temp[1] = palette.getLightMutedSwatch().getBodyTextColor();
            temp[2] = palette.getLightMutedSwatch().getTitleTextColor();
        } else if (palette.getDarkVibrantSwatch() != null) {
            temp[0] = palette.getDarkVibrantSwatch().getRgb();
            temp[1] = palette.getDarkVibrantSwatch().getBodyTextColor();
            temp[2] = palette.getDarkVibrantSwatch().getTitleTextColor();
        } else if (palette.getDarkMutedSwatch() != null) {
            temp[0] = palette.getDarkMutedSwatch().getRgb();
            temp[1] = palette.getDarkMutedSwatch().getBodyTextColor();
            temp[2] = palette.getDarkMutedSwatch().getTitleTextColor();
        } else {
            temp[0] = ContextCompat.getColor(context, R.color.colorPrimary);
            temp[1] = ContextCompat.getColor(context, android.R.color.white);
            temp[2] = 0xffe5e5e5;
        }
        return temp;
    }
    public static void setShuffleFAB(final Activity mActivity, final FrameLayout rootLayout, final RecyclerView recyclerView, final ArrayList<Song> songsList) {
        final FloatingActionButton fab = new FloatingActionButton(mActivity);
        fab.setColorFilter(getColor(mActivity, R.color.colorAccentGeneric));
        fab.getBackground().setColorFilter(getColor(mActivity, R.color.white), PorterDuff.Mode.SRC_ATOP);
        fab.setImageResource(R.mipmap.ic_shuffle_white_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayerExtensionMethods.shufflePlay(mActivity, songsList);
            }
        });
        FrameLayout.LayoutParams lp =  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        lp.setMargins(0, 0 , 32, 32);

        recyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                fab.animate().translationY(fab.getHeight() + 60).setInterpolator(new AccelerateInterpolator(2)).start();
            }
            @Override
            public void onShow() {
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });
        rootLayout.addView(fab, lp);
    }

    public static void onActivityResultMethod(Context context, int requestCode, int resultCode, Intent data, SongsArrayList songsList) {
        updateViewOnTagEditorSuccess(context, requestCode, resultCode, data, songsList);
        removeAlbumIfDeleted(requestCode, resultCode, data);
    }
    private static void removeAlbumIfDeleted(int requestCode, int resultCode, Intent data) {
        if (requestCode == KeyConstants.REQUEST_CODE_DELETE_ALBUM && resultCode == Activity.RESULT_OK){
            long albumID = data.getExtras().getLong("albumID", 0);
            if(albumID != 0){
                for (int index = 0; index < SharedVariables.fullAlbumList.size(); index++)
                    if(SharedVariables.fullAlbumList.get(index).getID().equals(albumID))
                        SharedVariables.fullAlbumList.remove(index);
            }
        }
    }
    private static void updateViewOnTagEditorSuccess(Context context, int requestCode, int resultCode, Intent data, SongsArrayList songsList) {

        if (requestCode == KeyConstants.REQUEST_CODE_TAG_EDITOR && resultCode == Activity.RESULT_OK) {
                String songID = data.getExtras().getString("songID", null);
                String songHashID = data.getExtras().getString("songHashID", null);
                if(songID != null || songHashID != null){

                    Song updatedSong = AudioExtensionMethods.getSongFromID(context, Long.parseLong(songID));
                    if(updatedSong == null)
                        return;

                    if(songsList != null)
                    for(int index = 0; index < songsList.size(); index++)
                        if(songsList.get(index).getHashID().equals(songHashID))
                            songsList.set(index, updatedSong);

                    for (int index = 0; index < SharedVariables.fullSongsList.size(); index++)
                        if(SharedVariables.fullSongsList.get(index).getHashID().equals(songHashID))
                            SharedVariables.fullSongsList.set(index, updatedSong);

                    for(int index = 0; index < PlayerConstants.getPlaylistSize(); index++)
                        if(PlayerConstants.getPlayList().get(index).getHashID().equals(songHashID))
                            PlayerConstants.setSongToPlaylist(context, index, updatedSong);

                    if(MusicService.currentSong.getHashID().equals(songHashID)){
                        MusicService.currentSong = updatedSong;
                        Controls.updateNowPlayingUI();
                        Controls.updateNotification();
                    }
                }
        }
    }

    public static boolean hasSoftNavBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }
    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= 23)
            return ContextCompat.getColor(context, id);

        return context.getResources().getColor(id);
    }

    public static void setStatusBarTranslucent_PreLollipop(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintColor(getColor(activity, R.color.colorPrimaryDark));
            tintManager.setStatusBarTintEnabled(true);
        }
    }
    public static void setStatusBarTranslucent(Activity context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        view.getLayoutParams().height = getStatusBarHeight(context);
    }
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void nearbyDevicesCount(Activity context, Menu menu) {

        Drawable myIcon = context.getResources().getDrawable(R.mipmap.ic_devices_white_24dp);
        if (NSDClient.devicesList.size() > 0)
            ActionItemBadge.update(context,
                    menu.findItem(R.id.action_devices_button), myIcon,
                    new BadgeStyle(BadgeStyle.Style.LARGE, R.layout.menu_action_item_badge_layout, Color.parseColor("#606060"), Color.parseColor("#3e3e3e"), Color.WHITE),
                    NSDClient.devicesList.size());
    }
}