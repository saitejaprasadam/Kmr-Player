package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.SongsAdapter;

import android.app.Activity;
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
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.ActivityHelperClasses.DialogHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ShareIntentHelper;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 5/16/2016.
 */

public class AlbumInnerLayoutSongRecyclerViewAdapter extends RecyclerView.Adapter<AlbumInnerLayoutSongRecyclerViewAdapter.songsViewHolder>{

    private LayoutInflater inflater;
    private ArrayList<Song> songsList = new ArrayList<>();
    private long albumID;
    private Activity context;

    public AlbumInnerLayoutSongRecyclerViewAdapter(final Activity context, final long albumID, ArrayList<Song> songsList){
        this.context = context;
        this.albumID = albumID;
        inflater = LayoutInflater.from(context);
        this.songsList = songsList;
    }
    public AlbumInnerLayoutSongRecyclerViewAdapter.songsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_songs_album_inner_layout, parent, false);
        return new songsViewHolder(view);
    }
    public void onBindViewHolder(final songsViewHolder holder, final int position) {
        try {
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

            String albumArtPath = currentSongDetails.getAlbumArtLocation();
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists())
                    holder.albumPath = imgFile.getAbsolutePath();
            }

            setContextMenu(holder, currentSongDetails, position);
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {MusicPlayerExtensionMethods.playSong(context, songsList, songsList.indexOf(currentSongDetails));}
            });

        }
        catch (Exception ignored){}
    }
    public int getItemCount() {
        return songsList.size();
    }

    private void setContextMenu(final songsViewHolder holder, final Song currentSong, final int position) {

        holder.contextMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu popup = new PopupMenu(context, v);
                popup.inflate(R.menu.song_item_menu_album_inner_layout);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        try {
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
                                                        songsList.remove(position);
                                                        SharedVariables.fullSongsList.remove(currentSong);
                                                        Toast.makeText(context, "Song Deleted : \'" + currentSong.getTitle() + "\'", Toast.LENGTH_SHORT).show();
                                                        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + currentSong.getID() + "'", null);

                                                        if (songsList.size() == 0){
                                                            Intent returnIntent = new Intent();
                                                            returnIntent.putExtra("albumID", albumID);
                                                            returnIntent.setFlags(Activity.RESULT_OK);
                                                            context.finish();
                                                        }

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

                                case R.id.song_context_menu_play_next:
                                    MusicPlayerExtensionMethods.playNext(context, currentSong);
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

                                case R.id.song_context_menu_ringtone:
                                    AudioExtensionMethods.setSongAsRingtone(context, currentSong);
                                    break;

                                case R.id.song_context_menu_jump_to_artist:
                                    ActivitySwitcher.jumpToArtist(context, currentSong.getArtistID());
                                    break;

                                case R.id.song_context_menu_tagEditor:
                                    ActivitySwitcher.launchTagEditor(context, currentSong.getID(), currentSong.getHashID());
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
    public ArrayList<Song> getSongsList(){
        return songsList;
    }

    class songsViewHolder extends RecyclerView.ViewHolder{

        @BindView (R.id.songTitle_RecyclerView) TextView titleTextView;
        @BindView (R.id.songArtist_recycler_view) TextView artistTextView;
        @BindView (R.id.rootLayout_recycler_view) RelativeLayout rootLayout;
        @BindView (R.id.song_context_menu) ImageView contextMenuView;
        @BindView (R.id.fav_button) LikeButton favoriteButton;
        public String albumPath;

        public songsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
