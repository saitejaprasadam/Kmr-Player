package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.NetworkAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.ModelClasses.TransferableSong;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/22/2016.
 */

public class ReceivedSongsAdapter extends RecyclerView.Adapter<ReceivedSongsAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final Context context;
    private static Handler refreshRecyclerViewHandler;
    private final Activity activity;
    private NSD serverObject;

    public ReceivedSongsAdapter(final Context context, final Activity activity){
        this.activity = activity;
        this.context = context;
        inflater = LayoutInflater.from(context);
        initHandler();
    }
    public ReceivedSongsAdapter(final Context context, final Activity activity,final NSD serverObject) {
        this.activity = activity;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.serverObject = serverObject;
        initHandler();
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.recylcer_view_songs_layout, parent, false));
    }
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try
        {
            final TransferableSong transferableSong;

            if(serverObject == null)
                transferableSong = SharedVariables.getFullTransferList().get(position);
            else
                transferableSong = SharedVariables.TransferList(serverObject).get(position);

            final Song song = AudioExtensionMethods.getSongFromHashID(transferableSong.getSong().getHashID());
            if (song != null) {
                holder.titleTextView.setText(song.getTitle());
                holder.artistTextView.setText(song.getArtist());
                holder.rootLayout.setTag(song.getData());
                holder.favoriteButton.setTag(song.getID());
                holder.favoriteButton.setLiked(song.getIsLiked(context));
                holder.favoriteButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        song.setIsLiked(context, true);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        song.setIsLiked(context, false);
                    }
                });
                holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(serverObject == null){
                            ArrayList<Song> songArrayList = new ArrayList<>();
                            for(TransferableSong temp : SharedVariables.getFullTransferList())
                                songArrayList.add(AudioExtensionMethods.getSongFromHashID(temp.getSong().getHashID()));

                            MusicPlayerExtensionMethods.playSong(context, songArrayList, songArrayList.indexOf(song));
                        }

                        else{
                            ArrayList<Song> songArrayList = new ArrayList<>();
                            for(TransferableSong temp : SharedVariables.TransferList(serverObject))
                                songArrayList.add(AudioExtensionMethods.getSongFromHashID(temp.getSong().getHashID()));

                            MusicPlayerExtensionMethods.playSong(context, songArrayList, songArrayList.indexOf(song));
                        }
                    }

                });
                setContextMenu(holder, position, song);
                setAlbumArt(holder, transferableSong);
            }
        }

        catch (Exception ignored){}
    }
    public int getItemCount() {
        if(serverObject == null)
            return SharedVariables.getFullTransferList().size();
        else
            return SharedVariables.TransferList(serverObject).size();
    }

    private void setContextMenu(final ViewHolder holder, final int position, final Song currentSong) {
        holder.contextMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu popup = new PopupMenu(v.getContext(), v);
                if(context.getClass().getSimpleName().equals("CustomPlaylistInnerActivity"))
                    popup.inflate(R.menu.song_item_menu_custom_playlist);
                else
                    popup.inflate(R.menu.song_item_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        try
                        {
                            int id = item.getItemId();

                            switch(id)
                            {
                                case R.id.song_context_menu_delete:
                                    new MaterialDialog.Builder(context)
                                            .content("Delete this song \'" +  currentSong.getTitle() + "\' ?")
                                            .positiveText(R.string.delete_text)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    File file = new File(currentSong.getData());
                                                    if (file.delete()) {
                                                        notifyItemRemoved(position);
                                                        SharedVariables.fullSongsList.remove(currentSong);
                                                        Toast.makeText(context, "Song Deleted : \'" + currentSong.getTitle() + "\'", Toast.LENGTH_SHORT).show();
                                                        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + currentSong.getID() + "'", null);
                                                    } else
                                                        Toast.makeText(context, context.getResources().getString(R.string.problem_deleting_song), Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .negativeText(R.string.cancel_text)
                                            .show();
                                    break;

                                case R.id.song_context_menu_quick_share:
                                    ActivitySwitcher.jumpToQuickShareActivity(context, currentSong);
                                    break;

                                case R.id.song_context_menu_share:
                                    ShareIntentHelper.sendSong(context, currentSong.getTitle(), Uri.parse(currentSong.getData()));
                                    break;

                                case R.id.song_context_menu_add_to_dialog:
                                    DialogHelper.AddToDialog(context, currentSong);
                                    break;

                                case R.id.song_context_menu_details:
                                    DialogHelper.songDetails(context, currentSong, holder.albumPath);
                                    break;

                                case R.id.song_context_menu_play_next:
                                    MusicPlayerExtensionMethods.playNext(context, currentSong);
                                    break;

                                case R.id.song_context_menu_ringtone:
                                    AudioExtensionMethods.setSongAsRingtone(context, currentSong);
                                    break;

                                case R.id.song_context_menu_tagEditor:
                                    ActivitySwitcher.launchTagEditor(activity, currentSong.getID(), currentSong.getHashID());
                                    break;

                                case R.id.song_context_menu_jump_to_album:
                                    ActivitySwitcher.jumpToAlbum(activity, currentSong.getID());
                                    break;

                                case R.id.song_context_menu_jump_to_artist:
                                    ActivitySwitcher.jumpToArtist(context, currentSong.getArtistID());
                                    break;
                            }
                        }
                        catch (Exception e){
                            Log.e("exception", e.toString());
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });
    }
    private void setAlbumArt(final ViewHolder holder, TransferableSong transferableSong) {
        Bitmap bitmap = SocketExtensionMethods.getAlbumArt(context, transferableSong);
        if(bitmap != null)
            holder.AlbumArtImageView.setImageBitmap(bitmap);
        else
            holder.AlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
    }

    public static void updateAdapter(){
        try {
            if(refreshRecyclerViewHandler != null)
                refreshRecyclerViewHandler.sendEmptyMessage(0);
        }
        catch (Exception ignored){}
    }
    private void initHandler() {
        refreshRecyclerViewHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                notifyDataSetChanged();
                return true;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.songTitle_RecyclerView) TextView titleTextView;
        @BindView (R.id.songArtist_recycler_view) TextView artistTextView;
        @BindView (R.id.songAlbumArt_RecyclerView) com.facebook.drawee.view.SimpleDraweeView AlbumArtImageView;
        @BindView (R.id.rootLayout_recycler_view) RelativeLayout rootLayout;
        @BindView (R.id.song_context_menu) ImageView contextMenuView;
        @BindView (R.id.fav_button) LikeButton favoriteButton;
        public String albumPath;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}