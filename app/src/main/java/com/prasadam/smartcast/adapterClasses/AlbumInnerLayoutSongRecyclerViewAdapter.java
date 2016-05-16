package com.prasadam.smartcast.adapterClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prasadam.smartcast.R;
import com.prasadam.smartcast.audioPackages.Song;

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

    public AlbumInnerLayoutSongRecyclerViewAdapter(Context context, List<Song> songsList){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.songsList = songsList;
    }

    @Override
    public AlbumInnerLayoutSongRecyclerViewAdapter.songsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recylcer_view_songs_layout, parent, false);
        return new songsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(songsViewHolder holder, int position) {
        try {
            final Song currentSongDetails = songsList.get(position);

            holder.titleTextView.setText(currentSongDetails.getTitle());
            holder.artistTextView.setText(currentSongDetails.getArtist());
            holder.rootLayout.setTag(currentSongDetails.getData());
        }
        catch (Exception ignored){}
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    /// <summary>RecyclerView view holder (Inner class)
    /// <para>creates a view holder for individual song</para>
    /// </summary>
    class songsViewHolder extends RecyclerView.ViewHolder{

        @Bind (R.id.songTitle_RecyclerView) TextView titleTextView;
        @Bind (R.id.songArtist_recycler_view) TextView artistTextView;
        @Bind (R.id.rootLayout_recycler_view) RelativeLayout rootLayout;
        //@Bind (R.id.song_context_menu) ImageView contextMenuView;

        public songsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
