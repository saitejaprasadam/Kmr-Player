package com.prasadam.smartcast.adapterClasses;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prasadam.smartcast.R;
import com.prasadam.smartcast.audioPackages.AdditionalAudioPackageMethods;
import com.prasadam.smartcast.audioPackages.Song;
import com.prasadam.smartcast.commonClasses.mediaController;
import com.prasadam.smartcast.commonClasses.ExtensionMethods;
import com.turingtechnologies.materialscrollbar.INameableAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;


import java.io.File;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by prasadam saiteja on 2/15/2016.
 */
public class SongRecyclerViewAdapter extends ObservableRecyclerView.Adapter<SongRecyclerViewAdapter.myViewHolder> implements INameableAdapter {

    private LayoutInflater inflater;
    private List<Song> songsList = Collections.emptyList();
    private Context context;

    public SongRecyclerViewAdapter(Context context, List<Song> songsList){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.songsList = songsList;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.songs_recylcer_view_layout, parent, false);
        return new myViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {
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
                    mediaController.music.musicService.setSong(AdditionalAudioPackageMethods.getSongIndex(songsList, view.getTag().toString()));
                    try
                    {
                        mediaController.music.musicService.playSong();
                    }
                    catch (Exception es){}
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
                                    if (id == R.id.song_context_menu_delete)
                                        ExtensionMethods.deleteSong(context, currentSongDetails.getTitle(), currentSongDetails.getData());

                                    else if(id == R.id.song_context_menu_share)
                                        ExtensionMethods.sendSong(context, currentSongDetails.getTitle() ,Uri.parse(currentSongDetails.getData()));

                                    else if(id == R.id.song_context_menu_details)
                                        ExtensionMethods.songDetails(context, currentSongDetails, holder.albumPath);

                                    else if(id == R.id.song_context_menu_ringtone)
                                    {
                                        //ExtensionMethods.setSongAsRingtone(context, currentSongDetails);
                                    }
                                }
                                catch (Exception e){}

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

        catch (Exception a){}
    }

    @Override //gets count of songsList
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
    class myViewHolder extends RecyclerView.ViewHolder{

        @Bind (R.id.songTitle_RecyclerView) TextView titleTextView;
        @Bind (R.id.songArtist_recycler_view) TextView artistTextView;
        @Bind (R.id.songAlbumArt_RecyclerView) com.facebook.drawee.view.SimpleDraweeView AlbumArtImageView;
        @Bind (R.id.rootLayout_recycler_view) RelativeLayout rootLayout;
        @Bind (R.id.song_context_menu) ImageView contextMenuView;
        public String albumPath;

        public myViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
