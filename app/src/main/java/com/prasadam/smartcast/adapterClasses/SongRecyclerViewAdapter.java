package com.prasadam.smartcast.adapterClasses;

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
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.prasadam.smartcast.R;
import com.prasadam.smartcast.TagEditorActivity;
import com.prasadam.smartcast.audioPackages.AudioExtensionMethods;
import com.prasadam.smartcast.audioPackages.Song;
import com.prasadam.smartcast.commonClasses.mediaController;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by prasadam saiteja on 2/15/2016.
 */
public class SongRecyclerViewAdapter extends ObservableRecyclerView.Adapter<SongRecyclerViewAdapter.songsViewHolder> implements INameableAdapter {

    private LayoutInflater inflater;
    private List<Song> songsList = Collections.emptyList();
    private Context context;

    public SongRecyclerViewAdapter(Context context, List<Song> songsList){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.songsList = songsList;
    }

    @Override
    public songsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recylcer_view_songs_layout, parent, false);
        return new songsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final songsViewHolder holder, final int position) {
        try
        {
            final Song currentSongDetails = songsList.get(position);

            holder.titleTextView.setText(currentSongDetails.getTitle());
            holder.artistTextView.setText(currentSongDetails.getArtist());
            holder.rootLayout.setTag(currentSongDetails.getData());

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
                                                                songsList = AudioExtensionMethods.getSongList(context, new ArrayList<Song>());
                                                                notifyDataSetChanged();
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


            {   //Album art
                String albumArtPath = currentSongDetails.getAlbumArtLocation();
                if(albumArtPath != null)
                {
                    File imgFile = new File(albumArtPath);
                    if(imgFile.exists())// /storage/emulated/0/Android/data/com.android.providers.media/albumthumbs/1454267773223
                    {
                        holder.AlbumArtImageView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                        holder.albumPath = imgFile.getAbsolutePath();
                        //Picasso.with(context).load("file://" + imgFile.getAbsolutePath()).into(holder.AlbumArtImageView);
                    }
                }
            }
        }

        catch (Exception ignored){}
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    @Override
    public Character getCharacterForElement(int element) {
        Character c = songsList.get(element).getTitle().charAt(0);
        if(Character.isDigit(c)){
            c = '#';
        }
        return c;
    }

    /// <summary>RecyclerView view holder (Inner class)
    /// <para>creates a view holder for individual song</para>
    /// </summary>
    class songsViewHolder extends RecyclerView.ViewHolder{

        @Bind (R.id.songTitle_RecyclerView) TextView titleTextView;
        @Bind (R.id.songArtist_recycler_view) TextView artistTextView;
        @Bind (R.id.songAlbumArt_RecyclerView) com.facebook.drawee.view.SimpleDraweeView AlbumArtImageView;
        @Bind (R.id.rootLayout_recycler_view) RelativeLayout rootLayout;
        @Bind (R.id.song_context_menu) ImageView contextMenuView;
        public String albumPath;

        public songsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
