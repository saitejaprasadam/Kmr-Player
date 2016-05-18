package com.prasadam.smartcast.adapterClasses;

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
import com.prasadam.smartcast.R;
import com.prasadam.smartcast.TagEditorActivity;
import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.audioPackages.Song;
import com.prasadam.smartcast.commonClasses.mediaController;

import java.io.File;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 5/16/2016.
 */

public class AlbumInnerLayoutSongRecyclerViewAdapter extends RecyclerView.Adapter<AlbumInnerLayoutSongRecyclerViewAdapter.songsViewHolder> {

    private LayoutInflater inflater;
    private List<Song> songsList = Collections.emptyList();
    private Context context;
    private String albumTitle;

    public AlbumInnerLayoutSongRecyclerViewAdapter(Context context, List<Song> songsList, String albumTitle){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.songsList = songsList;
        this.albumTitle = albumTitle;
    }

    @Override
    public AlbumInnerLayoutSongRecyclerViewAdapter.songsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_songs_album_inner_layout, parent, false);
        return new songsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(songsViewHolder holder, int position) {
        try {
            final Song currentSongDetails = songsList.get(position);

            holder.titleTextView.setText(currentSongDetails.getTitle());
            holder.artistTextView.setText(currentSongDetails.getArtist());
            holder.rootLayout.setTag(currentSongDetails.getData());

            String albumArtPath = currentSongDetails.getAlbumArtLocation();
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists())
                    holder.albumPath = imgFile.getAbsolutePath();
            }

            setContextMenu(holder, currentSongDetails);
            setOnClickListenerForRecyclerItem(holder);
        }
        catch (Exception ignored){}
    }

    private void setOnClickListenerForRecyclerItem(songsViewHolder holder) {
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaController.music.musicService.setList(songsList);
                mediaController.music.musicService.setSong(AudioExtensionMethods.getSongIndex(songsList, view.getTag().toString()));
                try
                {
                    mediaController.music.musicService.playSong();
                }
                catch (Exception ignored){}
            }
        });
    }

    private void setContextMenu(final songsViewHolder holder, final Song currentSongDetails) {

        holder.contextMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.album_inner_layout_song_item_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        try {
                            int id = item.getItemId();
                            switch(id)
                            {
                                case R.id.song_context_menu_delete:
                                    new MaterialDialog.Builder(context)
                                            .content("Delete this song " +  currentSongDetails.getTitle())
                                            .positiveText(R.string.delete_text)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    File file = new File(currentSongDetails.getData());
                                                    if(file.delete())
                                                    {
                                                        Toast.makeText(context, "Song Deleted : " + currentSongDetails.getTitle(), Toast.LENGTH_SHORT).show();
                                                        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + currentSongDetails.getID() + "'", null);
                                                        songsList = AudioExtensionMethods.getSongList(context, albumTitle);
                                                        AudioExtensionMethods.updateLists(context);
                                                        notifyDataSetChanged();
                                                        if(songsList.size() == 0)
                                                            ((Activity)context).finish();
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

                                case R.id.song_context_menu_shout:
                                    AudioExtensionMethods.ShoutOut(context, currentSongDetails, holder.albumPath);
                                    break;

                                case R.id.song_context_menu_ringtone:
                                    AudioExtensionMethods.setSongAsRingtone(context, currentSongDetails);
                                    break;

                                case R.id.song_context_menu_tagEditor:
                                    Intent tagEditorIntent = new Intent(context, TagEditorActivity.class);
                                    tagEditorIntent.putExtra("songID", String.valueOf(currentSongDetails.getID()));
                                    context.startActivity(tagEditorIntent);
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

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    /// <summary>RecyclerView view holder (Inner class)t
    /// <para>creates a view holder for individual song</para>
    /// </summary>
    class songsViewHolder extends RecyclerView.ViewHolder{

        @Bind (R.id.songTitle_RecyclerView) TextView titleTextView;
        @Bind (R.id.songArtist_recycler_view) TextView artistTextView;
        @Bind (R.id.rootLayout_recycler_view) RelativeLayout rootLayout;
        @Bind (R.id.song_context_menu) ImageView contextMenuView;
        public String albumPath;

        public songsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
