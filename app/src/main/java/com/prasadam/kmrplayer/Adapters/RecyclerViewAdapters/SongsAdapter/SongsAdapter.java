package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.SongsAdapter;

import android.app.Activity;
import android.content.Context;
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
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by prasadam saiteja on 2/15/2016.
 */

public class SongsAdapter extends ObservableRecyclerView.Adapter<SongsAdapter.songsViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private LayoutInflater inflater;
    private Activity activity;
    private Context context;

    public SongsAdapter(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
        inflater = LayoutInflater.from(activity);
    }

    public songsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recylcer_view_songs_layout, parent, false);
        return new songsViewHolder(view);
    }
    public void onBindViewHolder(final songsViewHolder holder, final int position) {
        try
        {
            final Song currentSongDetails = SharedVariables.fullSongsList.get(position);

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
                public void onClick(View view) {
                    MusicPlayerExtensionMethods.playSong(activity, SharedVariables.fullSongsList, SharedVariables.fullSongsList.indexOf(currentSongDetails));
                }
            });
            setContextMenu(holder, currentSongDetails, position);
            setAlbumArt(holder, currentSongDetails);
        }

        catch (Exception ignored){}
    }
    public int getItemCount() {
        return SharedVariables.fullSongsList.size();
    }

    private void setContextMenu(final songsViewHolder holder, final Song currentSong, final int position) {

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
                                            .content("Delete this song \'" +  currentSong.getTitle() + "\' ?")
                                            .positiveText(R.string.delete_text)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    File file = new File(currentSong.getData());
                                                    if(file.delete()) {
                                                        SharedVariables.fullSongsList.remove(position);
                                                        Toast.makeText(context, "Song Deleted : \'" + currentSong.getTitle() + "\'", Toast.LENGTH_SHORT).show();
                                                        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns._ID + "='" + currentSong.getID() + "'", null);
                                                    }

                                                    else
                                                        Toast.makeText(context, context.getResources().getString(R.string.problem_deleting_song), Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .negativeText(R.string.cancel_text)
                                            .show();
                                    break;

                                case R.id.song_context_menu_quick_share:
                                    ActivitySwitcher.jumpToQuickShareActivity(context, currentSong);
                                    break;

                                case R.id.song_context_menu_add_to_dialog:
                                    DialogHelper.AddToDialog(context, currentSong);
                                    break;

                                case R.id.song_context_menu_share:
                                    ShareIntentHelper.sendSong(context, currentSong.getTitle(), Uri.parse(currentSong.getData()));
                                    break;

                                case R.id.song_context_menu_details:
                                    DialogHelper.songDetails(context, currentSong, holder.albumPath);
                                    break;

                                case R.id.song_context_menu_ringtone:
                                    AudioExtensionMethods.setSongAsRingtone(context, currentSong);
                                    break;

                                case R.id.song_context_menu_tagEditor:
                                    ActivitySwitcher.launchTagEditor((Activity) context, currentSong.getID(), currentSong.getHashID());
                                    break;

                                case R.id.song_context_menu_play_next:
                                    MusicPlayerExtensionMethods.playNext(context, currentSong);
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
    private void setAlbumArt(songsViewHolder holder, Song currentSongDetails) {
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

            else
                holder.AlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
        }

        else
            holder.AlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
    }

    @NonNull
    public String getSectionName(int position) {
        Character c = SharedVariables.fullSongsList.get(position).getTitle().charAt(0);
        if(Character.isDigit(c) || !Character.isLetter(c)){
            c = '#';
        }

        return String.valueOf(Character.toUpperCase(c));
    }
    class songsViewHolder extends RecyclerView.ViewHolder{

        @BindView (R.id.songTitle_RecyclerView) TextView titleTextView;
        @BindView (R.id.songArtist_recycler_view) TextView artistTextView;
        @BindView (R.id.songAlbumArt_RecyclerView) ImageView AlbumArtImageView;
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
