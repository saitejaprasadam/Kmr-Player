package com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.prasadam.kmrplayer.MainActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.adapterClasses.uiAdapters.NowPlayingPlaylistInterfaces;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.audioPackages.musicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.fragments.SongsFragment;

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
    private String TAG = "NowPlayingPlaylistAdapter";

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

    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Song song = PlayerConstants.SONGS_LIST.get(position);
        holder.songTitleTextView.setText(song.getTitle());
        holder.songArtistTextView.setText(song.getArtist());
        holder.nowPlayingPlaylistLikeButton.setLiked(song.getIsLiked(context));

        if(position < PlayerConstants.SONG_NUMBER)
            holder.cardviewRootLayout.setAlpha(0.5f);
        else
            holder.cardviewRootLayout.setAlpha(1f);

        holder.nowPlayingPlaylistLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                song.setIsLiked(context, true);
                SongsFragment.recyclerViewAdapter.notifyDataSetChanged();
                MainActivity.updateSongLikeStatus(context);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                song.setIsLiked(context, false);
                SongsFragment.recyclerViewAdapter.notifyDataSetChanged();
                MainActivity.updateSongLikeStatus(context);
            }
        });
    }

    public int getItemCount() {
        return PlayerConstants.SONGS_LIST.size();
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(PlayerConstants.SONGS_LIST, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(PlayerConstants.SONGS_LIST, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
    public void onItemDismiss(int position) {
        PlayerConstants.SONGS_LIST.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements NowPlayingPlaylistInterfaces.ItemTouchHelperViewHolder {

        @Bind(R.id.now_playing_playlist_fav_button) LikeButton nowPlayingPlaylistLikeButton;
        @Bind(R.id.now_playing_playlist_song_context_menu) ImageView nowPlayingPlaylistContextMenu;
        @Bind(R.id.now_playing_playlist_song_title_text_view) TextView songTitleTextView;
        @Bind(R.id.now_playing_playlist_song_artist_text_view) TextView songArtistTextView;
        @Bind(R.id.now_playing_playlist_root_layout) FrameLayout cardviewRootLayout;

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
        }
    }
}
