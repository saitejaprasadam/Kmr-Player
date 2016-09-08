package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.Controls;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.Interfaces.NowPlayingPlaylistInterfaces;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.UI.Activities.VerticalSlidingDrawerBaseActivity;
import com.prasadam.kmrplayer.UI.Fragments.SongsFragment;

import java.io.File;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 6/30/2016.
 */

public class NowPlayingPlaylistAdapter extends RecyclerView.Adapter<NowPlayingPlaylistAdapter.MyViewHolder> implements NowPlayingPlaylistInterfaces.ItemTouchHelperAdapter {

    private Context context;
    private Activity mActivity;
    private LayoutInflater inflater;

    public NowPlayingPlaylistAdapter(Context context, Activity mActivity){
        this.context = context;
        this.mActivity = mActivity;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_now_playing_playlist_layout, parent, false);
        return new MyViewHolder(view);
    }
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Song song = PlayerConstants.getPlayList().get(position);
        holder.songTitleTextView.setText(song.getTitle());
        holder.songArtistTextView.setText(song.getArtist());
        holder.nowPlayingPlaylistLikeButton.setLiked(song.getIsLiked(context));
        holder.songID = song.getID();

        if(position != PlayerConstants.SONG_NUMBER)
            setAlbumArt(holder, song);
        else
            holder.albumArtImageView.setImageResource(R.mipmap.ic_pause_circle_outline_black_24dp);

        if(position < PlayerConstants.SONG_NUMBER)
            holder.cardviewRootLayout.setAlpha(0.5f);
        else
            holder.cardviewRootLayout.setAlpha(1f);

        setContextMenu(holder, position, song);
        holder.nowPlayingPlaylistLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                song.setIsLiked(context, true);
                SongsFragment.recyclerViewAdapter.notifyDataSetChanged();
                VerticalSlidingDrawerBaseActivity.updateSongLikeStatus(context);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                song.setIsLiked(context, false);
                SongsFragment.recyclerViewAdapter.notifyDataSetChanged();
                VerticalSlidingDrawerBaseActivity.updateSongLikeStatus(context);
            }
        });
        holder.cardviewRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerExtensionMethods.changeSong(context, position);
            }
        });
    }

    private void setContextMenu(final MyViewHolder holder, final int position, final Song currentSong) {

        holder.nowPlayingPlaylistContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.song_item_menu_playlist);
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
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                                    File file = new File(currentSong.getData());
                                                                    if (file.delete()) {
                                                                        if (position == PlayerConstants.SONG_NUMBER)
                                                                            Controls.nextControl(context);

                                                                        mActivity.runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                SharedVariables.fullSongsList.remove(currentSong);
                                                                                PlayerConstants.removeSongFromPlaylist(context, position);
                                                                                Toast.makeText(context, "Song Deleted : \'" + currentSong.getTitle() + "\'", Toast.LENGTH_SHORT).show();
                                                                                context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + currentSong.getID() + "'", null);
                                                                            }
                                                                        });
                                                                    } else
                                                                        Toast.makeText(context, context.getResources().getString(R.string.problem_deleting_song), Toast.LENGTH_SHORT).show();
                                                                }
                                                    }).start();
                                                }
                                            })
                                            .negativeText(R.string.cancel_text)
                                            .show();
                                    break;

                                case R.id.song_context_menu_remove_from_now_playing_playlist:
                                    PlayerConstants.removeSongFromPlaylist(context, position);
                                    break;

                                case R.id.song_context_menu_quick_share:
                                    ActivitySwitcher.jumpToQuickShareActivity(context, currentSong);
                                    break;

                                case R.id.song_context_menu_share:
                                    ShareIntentHelper.sendSong(context, currentSong.getTitle(), Uri.parse(currentSong.getData()));
                                    break;

                                case R.id.song_context_menu_play_next:
                                    MusicPlayerExtensionMethods.playNext(context, currentSong);
                                    break;

                                case R.id.song_context_menu_add_to_dialog:
                                    DialogHelper.AddToDialog(context, currentSong);
                                    break;

                                case R.id.song_context_menu_details:
                                    DialogHelper.songDetails(context, currentSong);
                                    break;

                                case R.id.song_context_menu_ringtone:
                                    AudioExtensionMethods.setSongAsRingtone(context, currentSong);
                                    break;

                                case R.id.song_context_menu_tagEditor:
                                    ActivitySwitcher.launchTagEditor((Activity) context, currentSong.getID(), currentSong.getHashID());
                                    break;

                                case R.id.song_context_menu_jump_to_album:
                                    ActivitySwitcher.jumpToAlbum(mActivity, currentSong.getID());
                                    break;

                                case R.id.song_context_menu_jump_to_artist:
                                    ActivitySwitcher.jumpToArtist(context, currentSong.getArtist());
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
    private void setAlbumArt(MyViewHolder holder, Song song) {
        String albumArtPath = song.getAlbumArtLocation();
        if(albumArtPath != null)
        {
            File imgFile = new File(albumArtPath);
            if(imgFile.exists())
                holder.albumArtImageView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));

            else
                holder.albumArtImageView.setImageResource(R.mipmap.unkown_album_art);
        }

        else
            holder.albumArtImageView.setImageResource(R.mipmap.unkown_album_art);
    }

    public int getItemCount() {
        return PlayerConstants.getPlaylistSize();
    }
    public boolean onItemMove(int fromPosition, int toPosition, MyViewHolder sourceViewHolder, MyViewHolder targetViewHolder) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                if(sourceViewHolder.getAdapterPosition() < PlayerConstants.SONG_NUMBER &&  PlayerConstants.SONG_NUMBER <= targetViewHolder.getAdapterPosition())
                    PlayerConstants.SONG_NUMBER = i;

                else
                    if(sourceViewHolder.getAdapterPosition() == PlayerConstants.SONG_NUMBER && targetViewHolder.getAdapterPosition() > sourceViewHolder.getAdapterPosition()){
                        targetViewHolder.cardviewRootLayout.setAlpha(0.5f);
                        PlayerConstants.SONG_NUMBER = i + 1;
                    }

                    else
                        if(sourceViewHolder.songID == PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getID()){
                            PlayerConstants.SONG_NUMBER = i + 1;
                            targetViewHolder.cardviewRootLayout.setAlpha(0.5f);
                        }

                Collections.swap(PlayerConstants.getPlayList(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                if(sourceViewHolder.getAdapterPosition() > PlayerConstants.SONG_NUMBER &&  PlayerConstants.SONG_NUMBER >= targetViewHolder.getAdapterPosition())
                    PlayerConstants.SONG_NUMBER = i;

                else
                    if(sourceViewHolder.getAdapterPosition() == PlayerConstants.SONG_NUMBER && targetViewHolder.getAdapterPosition() < sourceViewHolder.getAdapterPosition()){
                        targetViewHolder.cardviewRootLayout.setAlpha(1f);
                        PlayerConstants.SONG_NUMBER = i - 1;
                    }

                    else
                        if(sourceViewHolder.songID == PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getID()){
                            PlayerConstants.SONG_NUMBER = i - 1;
                            targetViewHolder.cardviewRootLayout.setAlpha(1f);
                        }

                Collections.swap(PlayerConstants.getPlayList(), i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
        return true;
    }
    public void onItemDismiss(int position) {
        PlayerConstants.removeSongFromPlaylist(context, position);
        if(position < PlayerConstants.SONG_NUMBER)
            PlayerConstants.SONG_NUMBER--;
        notifyItemRemoved(position);
        VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements NowPlayingPlaylistInterfaces.ItemTouchHelperViewHolder {

        @BindView(R.id.now_playing_playlist_fav_button) LikeButton nowPlayingPlaylistLikeButton;
        @BindView(R.id.now_playing_playlist_song_context_menu) ImageView nowPlayingPlaylistContextMenu;
        @BindView(R.id.now_playing_playlist_song_title_text_view) TextView songTitleTextView;
        @BindView(R.id.now_playing_playlist_song_artist_text_view) TextView songArtistTextView;
        @BindView(R.id.now_playing_playlist_root_layout) FrameLayout cardviewRootLayout;
        @BindView(R.id.now_playing_playlist_song_album_art) ImageView albumArtImageView;
        public long songID;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
            if(songID == PlayerConstants.getPlayList().get(PlayerConstants.SONG_NUMBER).getID())
                cardviewRootLayout.setAlpha(1f);
            else
                cardviewRootLayout.setAlpha(0.5f);
        }
    }
}
