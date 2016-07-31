package com.prasadam.kmrplayer.AdapterClasses.RecyclerViewAdapters;

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
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.MainActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AdapterClasses.UIAdapters.NowPlayingPlaylistInterfaces;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.AudioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.musicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.Fragments.SongsFragment;
import com.prasadam.kmrplayer.VerticalSlidingDrawerBaseActivity;

import java.io.File;
import java.util.Collections;

import butterknife.Bind;
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

        final Song song = PlayerConstants.SONGS_LIST.get(position);
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
                MusicPlayerExtensionMethods.changeSong(mActivity, position);
            }
        });
    }

    private void setContextMenu(final MyViewHolder holder, final int position, final Song currentSongDetails) {

        holder.nowPlayingPlaylistContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(v.getContext(), v);
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
                                            .content("Delete this song \'" +  currentSongDetails.getTitle() + "\' ?")
                                            .positiveText(R.string.delete_text)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    File file = new File(currentSongDetails.getData());
                                                    if(file.delete())
                                                    {
                                                        Toast.makeText(context, "Song Deleted : \'" + currentSongDetails.getTitle() + "\'", Toast.LENGTH_SHORT).show();
                                                        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + currentSongDetails.getID() + "'", null);
                                                        AudioExtensionMethods.updateLists(context);
                                                        notifyDataSetChanged();
                                                    }
                                                }
                                            })
                                            .negativeText(R.string.cancel_text)
                                            .show();
                                    break;

                                case R.id.song_context_menu_quick_share:
                                    ActivitySwitcher.jumpToQuickShareActivity(context, currentSongDetails);
                                    break;

                                case R.id.song_context_menu_share:
                                    ShareIntentHelper.sendSong(context, currentSongDetails.getTitle(), Uri.parse(currentSongDetails.getData()));
                                    break;

                                case R.id.song_context_menu_add_to_playlist:
                                    AudioExtensionMethods.addToPlaylist(context, currentSongDetails.getHashID());
                                    break;

                                case R.id.song_context_menu_details:
                                    break;

                                case R.id.song_context_menu_ringtone:
                                    AudioExtensionMethods.setSongAsRingtone(context, currentSongDetails);
                                    break;

                                case R.id.song_context_menu_tagEditor:
                                    ActivitySwitcher.launchTagEditor((Activity) context, currentSongDetails.getID(), position);
                                    break;

                                case R.id.song_context_menu_jump_to_album:
                                    ActivitySwitcher.jumpToAlbum(context, currentSongDetails.getAlbum());
                                    break;

                                case R.id.song_context_menu_jump_to_artist:
                                    ActivitySwitcher.jumpToArtist(context, currentSongDetails.getArtist());
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
        return PlayerConstants.SONGS_LIST.size();
    }

    public boolean onItemMove(int fromPosition, int toPosition, MyViewHolder sourceViewHolder, MyViewHolder targetViewHolder) {

        Log.d("Source", String.valueOf(sourceViewHolder.getAdapterPosition()));
        Log.d("nowPlaying", String.valueOf(PlayerConstants.SONG_NUMBER));
        Log.d("target", String.valueOf(targetViewHolder.getAdapterPosition()));

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
                        if(sourceViewHolder.songID == PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getID()){
                            PlayerConstants.SONG_NUMBER = i + 1;
                            targetViewHolder.cardviewRootLayout.setAlpha(0.5f);
                        }

                Collections.swap(PlayerConstants.SONGS_LIST, i, i + 1);
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
                        if(sourceViewHolder.songID == PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getID()){
                            PlayerConstants.SONG_NUMBER = i - 1;
                            targetViewHolder.cardviewRootLayout.setAlpha(1f);
                        }

                Collections.swap(PlayerConstants.SONGS_LIST, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
        return true;
    }

    public void onItemDismiss(int position) {
        PlayerConstants.SONGS_LIST.remove(position);
        if(position < PlayerConstants.SONG_NUMBER)
            PlayerConstants.SONG_NUMBER--;
        notifyItemRemoved(position);
        VerticalSlidingDrawerBaseActivity.updateAlbumAdapter();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements NowPlayingPlaylistInterfaces.ItemTouchHelperViewHolder {

        @Bind(R.id.now_playing_playlist_fav_button) LikeButton nowPlayingPlaylistLikeButton;
        @Bind(R.id.now_playing_playlist_song_context_menu) ImageView nowPlayingPlaylistContextMenu;
        @Bind(R.id.now_playing_playlist_song_title_text_view) TextView songTitleTextView;
        @Bind(R.id.now_playing_playlist_song_artist_text_view) TextView songArtistTextView;
        @Bind(R.id.now_playing_playlist_root_layout) FrameLayout cardviewRootLayout;
        @Bind(R.id.now_playing_playlist_song_album_art) ImageView albumArtImageView;
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
            if(songID == PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getID())
                cardviewRootLayout.setAlpha(1f);
            else
                cardviewRootLayout.setAlpha(0.5f);
        }
    }
}
