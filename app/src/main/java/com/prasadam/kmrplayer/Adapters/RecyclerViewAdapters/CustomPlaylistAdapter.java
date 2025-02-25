package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.DatabaseHelperClasses.sqliteHelper;
import com.prasadam.kmrplayer.UI.Activities.Playlist.CustomPlaylistInnerActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods.getAlbumArtsForPlaylistCover;

/*
 * Created by Prasadam Saiteja on 5/30/2016.
 */

public class CustomPlaylistAdapter extends RecyclerView.Adapter<CustomPlaylistAdapter.songsViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> playlistNamesList;

    public CustomPlaylistAdapter(Context context, ArrayList<String> playlistNames){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.playlistNamesList = playlistNames;
    }

    @Override
    public songsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_custom_playlist_layout, parent, false);
        return new songsViewHolder(view);
    }
    public void onBindViewHolder(final songsViewHolder holder, final int position) {
        try{
            final String playlistName = playlistNamesList.get(position);
            int songCount = AudioExtensionMethods.getPlaylistSongCount(context, playlistName);

            holder.playlistNameTextView.setText(playlistName);
            holder.songCountTextView.setText(songCount + " " + context.getResources().getString(R.string.songs_text));
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent customPlaylistInnerActivityIntent = new Intent(context, CustomPlaylistInnerActivity.class);
                    customPlaylistInnerActivityIntent.putExtra("playlistName", playlistName);
                    context.startActivity(customPlaylistInnerActivityIntent);
                }
            });

            addContextMenu(holder, playlistName);

            if(songCount == 0){

            }

            else{
                ArrayList<String> albumArtPathList = getAlbumArtsForPlaylistCover(context, playlistName);
                setAlbumArt(holder, albumArtPathList);
            }
        }

        catch (Exception ignored){}
    }
    public int getItemCount() {
        return playlistNamesList.size();
    }

    private void setAlbumArt(songsViewHolder holder, ArrayList<String> albumArtPathList) {

        if(albumArtPathList.size() == 0){
                holder.albumartImageView1.setImageResource(R.mipmap.unkown_album_art);
                holder.blurredBckgorundImageView.setImageBitmap(BlurBuilder.blur(context, ((BitmapDrawable) holder.albumartImageView1.getDrawable()).getBitmap()));
        }

        if(albumArtPathList.size() > 0){
            String albumArtPath = albumArtPathList.get(0);
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists()){

                    holder.albumartImageView1.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
                    holder.blurredBckgorundImageView.setImageBitmap(BlurBuilder.blur(context, ((BitmapDrawable) holder.albumartImageView1.getDrawable()).getBitmap()));
                }
            }
        }

        if(albumArtPathList.size() > 1){
            String albumArtPath = albumArtPathList.get(1);
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists())
                    holder.albumartImageView2.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
            }
        }

        if(albumArtPathList.size() > 2){
            String albumArtPath = albumArtPathList.get(2);
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists())
                    holder.albumartImageView3.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
            }
        }

        if(albumArtPathList.size() > 3){
            String albumArtPath = albumArtPathList.get(3);
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists())
                    holder.albumartImageView4.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
            }
        }
    }
    private void addContextMenu(songsViewHolder holder, final String playlistName) {
        holder.contextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.custom_playlist_context_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        try {
                            int id = item.getItemId();

                            switch (id) {

                                case R.id.rename_playlist:
                                    new MaterialDialog.Builder(context)
                                            .title(R.string.enter_a_new_name_text)
                                            .inputRangeRes(3, 20, R.color.colorAccentGeneric)
                                            .input(null, null, new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                                    String newName = String.valueOf(input);
                                                    if(newName.equals(playlistName))
                                                        Toast.makeText(context , "Provide a different name!!!", Toast.LENGTH_SHORT).show();

                                                    else{
                                                        sqliteHelper sqliteHelper = new sqliteHelper(context);
                                                        if(sqliteHelper.renamePlaylist(playlistName, newName)){
                                                            Toast.makeText(context, "Name changed successfully", Toast.LENGTH_SHORT).show();
                                                            playlistNamesList = AudioExtensionMethods.getCustomPlaylistNames(context);
                                                            notifyDataSetChanged();
                                                        }

                                                        else
                                                            Toast.makeText(context, "Playlist with same name already exists", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }).show();
                                    break;
                            }
                        }

                        catch (Exception ignored){}

                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    class songsViewHolder extends RecyclerView.ViewHolder{

        @BindView (R.id.background_image_view) ImageView blurredBckgorundImageView;
        @BindView (R.id.album_art_image_view1) ImageView albumartImageView1;
        @BindView (R.id.album_art_image_view2) ImageView albumartImageView2;
        @BindView (R.id.album_art_image_view3) ImageView albumartImageView3;
        @BindView (R.id.album_art_image_view4) ImageView albumartImageView4;
        @BindView (R.id.playlist_name_text_view) TextView playlistNameTextView;
        @BindView (R.id.songs_count_text_view) TextView songCountTextView;
        @BindView (R.id.context_menu) ImageView contextMenu;
        @BindView (R.id.rootLayout_recycler_view) CardView rootView;

        public songsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
