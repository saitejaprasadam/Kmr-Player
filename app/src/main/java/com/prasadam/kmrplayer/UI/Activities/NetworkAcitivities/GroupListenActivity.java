package com.prasadam.kmrplayer.UI.Activities.NetworkAcitivities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.KeyConstants;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSDClient;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/27/2016.
 */

public class GroupListenActivity extends Activity{

    @BindView(R.id.blurred_album_art) ImageView blurredAlbumArt;
    @BindView(R.id.album_art) ImageView albumArt;
    @BindView(R.id.song_title) TextView songTitle;
    @BindView(R.id.song_artist_album) TextView songAlbumArtist;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private MediaPlayer groupListenPlayer = new MediaPlayer();
    private static Handler refreshSongHandler;
    private NSD parent;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_listen_layout);
        ButterKnife.bind(this);
        setToolBar();

        albumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(groupListenPlayer.isPlaying())
                        groupListenPlayer.pause();
                    else
                        groupListenPlayer.start();
                }

                catch (Exception ignored){}
            }
        });

        songAlbumArtist.setSelected(true);
        initHandler();
        groupListenPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        for(NSD nsd : NSDClient.devicesList)
            if(nsd.getMacAddress().equals(PlayerConstants.parentGroupListener.getClientMacAddress())){
                parent = nsd;
                break;
            }
    }

    private void setToolBar() {
        toolbar.setPadding(0, ActivityHelper.getStatusBarHeight(this), 0, 0);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGroupListenSessionDialog();
            }
        });
    }

    public void onBackPressed(){
        endGroupListenSessionDialog();
    }
    private void refreshSong(String fileName) {
        try{
            SocketExtensionMethods.requestStrictModePermit();
            SocketExtensionMethods.requestForCurrentSongPlaying(GroupListenActivity.this, parent.GetClientNSD());
            groupListenPlayer.reset();
            groupListenPlayer.setDataSource(fileName);
            groupListenPlayer.prepare();
            groupListenPlayer.start();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void refreshSongTag() {
        try{
            Bitmap bitmap = SocketExtensionMethods.getAlbumArt(this, parent.getCurrentSongPlaying(), parent.getMacAddress());
            if(bitmap != null)
                albumArt.setImageBitmap(SocketExtensionMethods.getAlbumArt(this, parent.getCurrentSongPlaying(), parent.getMacAddress()));
            else
                albumArt.setImageResource(R.mipmap.unkown_album_art);

            blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) albumArt.getDrawable()).getBitmap()));
            songTitle.setText(parent.getCurrentSongPlaying().getTitle());
            songAlbumArtist.setText(parent.getCurrentSongPlaying().getArtist() + " | " + parent.getCurrentSongPlaying().getAlbum());
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void endGroupListenSessionDialog() {
        new MaterialDialog.Builder(GroupListenActivity.this)
                .content(R.string.group_listen_end_session_prompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        PlayerConstants.parentGroupListener = null;
                        groupListenPlayer.stop();
                        groupListenPlayer = null;
                        SocketExtensionMethods.SendDisconnectMessageFromGroupListen(GroupListenActivity.this, parent.GetClientNSD());
                        Toast.makeText(getBaseContext(), "Group listen seession ended", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).show();
    }

    public static void updateSong(String fileName){
        try {
            if(refreshSongHandler != null)
                refreshSongHandler.sendMessage(refreshSongHandler.obtainMessage(0, fileName));
        }
        catch (Exception ignored){}
    }
    private void initHandler() {
        refreshSongHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                String fileName = (String) message.obj;

                switch (fileName) {

                    case KeyConstants.SOCKET_CURRENT_SONG_NAME_RESULT:
                        refreshSongTag();
                        break;

                    case KeyConstants.SOCKET_GROUP_LISTEN_KICK_OUT_DEVICE:
                        groupListenPlayer.stop();
                        groupListenPlayer = null;
                        finish();
                        break;

                    default:
                        refreshSong(fileName);
                        break;
                }
                return true;
            }
        });
    }
}