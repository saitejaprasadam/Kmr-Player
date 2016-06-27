package com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.activityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.MusicService;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.UtilFunctions;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 5/28/2016.
 */

public class SongRecyclerViewAdapterForArtistActivity extends RecyclerView.Adapter<SongRecyclerViewAdapterForArtistActivity.songsViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Song> songsList;

    public SongRecyclerViewAdapterForArtistActivity(Context context, ArrayList<Song> recentlyAddedSongsList){
        this.context = context;
        this.songsList = recentlyAddedSongsList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public songsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recylcer_view_songs_layout, parent, false);
        return new songsViewHolder(view);
    }
    public void onBindViewHolder(final songsViewHolder holder, final int position) {
        try
        {
            final Song currentSongDetails = songsList.get(position);

            holder.titleTextView.setText(currentSongDetails.getTitle());
            holder.artistTextView.setText(currentSongDetails.getArtist());
            holder.rootLayout.setTag(currentSongDetails.getData());
            holder.favoriteButton.setLiked(currentSongDetails.getIsLiked(context));
            holder.favoriteButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    currentSongDetails.setIsLiked(context, true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    currentSongDetails.setIsLiked(context, false);
                }
            });
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {MusicPlayerExtensionMethods.playSong((Activity) context, songsList, position);}
            });


            {
                holder.contextMenuView.setOnClickListener(new View.OnClickListener() {
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
                                                                songsList =  AudioExtensionMethods.getSongListFromArtist(context, currentSongDetails.getArtist());
                                                                notifyDataSetChanged();
                                                                AudioExtensionMethods.updateLists(context);
                                                            }
                                                        }
                                                    })
                                                    .negativeText(R.string.cancel_text)
                                                    .show();
                                            break;

                                        case R.id.song_context_menu_share:
                                            AudioExtensionMethods.sendSong(context, currentSongDetails.getTitle(), Uri.parse(currentSongDetails.getData()));
                                            break;

                                        case R.id.song_context_menu_details:
                                            AudioExtensionMethods.songDetails(context, currentSongDetails, holder.albumPath);
                                            break;

                                        case R.id.song_context_menu_ringtone:
                                            AudioExtensionMethods.setSongAsRingtone(context, currentSongDetails);
                                            break;

                                        case R.id.song_context_menu_tagEditor:
                                            AudioExtensionMethods.launchTagEditor(context, currentSongDetails.getID());
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


            setAlbumArt(holder, currentSongDetails);
        }

        catch (Exception ignored){}
    }
    public int getItemCount() {
        return songsList.size();
    }

    private void setAlbumArt(songsViewHolder holder, Song currentSongDetails) {

        String albumArtPath = currentSongDetails.getAlbumArtLocation();
        if(albumArtPath != null)
        {
            File imgFile = new File(albumArtPath);
            if(imgFile.exists())
            {
                holder.AlbumArtImageView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                holder.albumPath = imgFile.getAbsolutePath();
            }
            else
                holder.AlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
        }
        else
            holder.AlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
    }

    class songsViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.songTitle_RecyclerView) TextView titleTextView;
        @Bind (R.id.songArtist_recycler_view) TextView artistTextView;
        @Bind (R.id.songAlbumArt_RecyclerView) com.facebook.drawee.view.SimpleDraweeView AlbumArtImageView;
        @Bind (R.id.rootLayout_recycler_view) RelativeLayout rootLayout;
        @Bind (R.id.song_context_menu) ImageView contextMenuView;
        @Bind (R.id.fav_button) LikeButton favoriteButton;
        public String albumPath;

        public songsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
