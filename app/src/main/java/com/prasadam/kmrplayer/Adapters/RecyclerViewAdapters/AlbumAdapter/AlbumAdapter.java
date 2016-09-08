package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.AlbumAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Album;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 3/25/2016.
 */

public class AlbumAdapter extends ObservableRecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private Context context;
    private LayoutInflater inflater;
    private Activity mActivity;
    private ArrayList<Album> albumArrayList = null;

    public AlbumAdapter(Activity mActivity, Context context){

        this.mActivity = mActivity;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    public AlbumAdapter(Activity mActivity, Context context, ArrayList<Album> albumArrayList){

        this.albumArrayList = albumArrayList;
        this.mActivity = mActivity;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(albumArrayList == null)
            return new AlbumViewHolder(inflater.inflate(R.layout.recycler_view_albums_layout, parent, false));
        else
            return new AlbumViewHolder(inflater.inflate(R.layout.recycler_view_album_search_layout, parent, false));
    }
    public void onBindViewHolder(final AlbumAdapter.AlbumViewHolder holder, int position) {

        final Album currentAlbum;
        if(albumArrayList == null)
            currentAlbum = SharedVariables.fullAlbumList.get(position);
        else
            currentAlbum = albumArrayList.get(position);

        holder.albumNameTextView.setText(currentAlbum.getTitle());
        holder.artistNameTextView.setText(currentAlbum.getArtist());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivitySwitcher.jumpToAlbumWithTranscition(mActivity, holder.albumArtImageView, currentAlbum.getID());
            }
        });

        setAlbumArt(holder, currentAlbum);
    }

    private void setAlbumArt(final AlbumViewHolder holder, final Album currentAlbum) {

        String albumArtPath = currentAlbum.getAlbumArtLocation();

        if(albumArtPath != null)
        {
            final File imgFile = new File(albumArtPath);
            if(imgFile.exists())
            {
                setImage(holder, "file://" + imgFile.getAbsolutePath());
                holder.albumLocation = imgFile.getAbsolutePath();
            }

            else
                setImage(holder, null);
        }

        else
            setImage(holder, null);


        if(currentAlbum.isColorSet()){
            holder.colorBoxLayout.setBackgroundColor(currentAlbum.colorBoxLayoutColor);
            holder.albumNameTextView.setTextColor(currentAlbum.albumNameTextViewColor);
            holder.artistNameTextView.setTextColor(currentAlbum.artistNameTextViewColor);
        }

        if(!currentAlbum.isColorSet())
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Bitmap bitmap = AudioExtensionMethods.getBitMap(context, holder.albumLocation);
                    if(bitmap == null)
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art);
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            final int[] colors = ActivityHelper.getAvailableColor(mActivity, palette);
                            holder.colorBoxLayout.setBackgroundColor(colors[0]);
                            currentAlbum.colorBoxLayoutColor = colors[0];

                            holder.albumNameTextView.setTextColor(colors[1]);
                            currentAlbum.albumNameTextViewColor = colors[1];

                            holder.artistNameTextView.setTextColor(colors[2]);
                            currentAlbum.artistNameTextViewColor = colors[2];
                        }
                    });
                }
            }).start();
    }
    private void setImage(final AlbumViewHolder holder, final String albumpath){
        if(albumpath == null)
            holder.albumArtImageView.setImageResource(R.mipmap.unkown_album_art);
        else
            holder.albumArtImageView.setImageURI(Uri.parse(albumpath));
    }

    @Override
    public int getItemCount() {
        if(albumArrayList == null)
            return SharedVariables.fullAlbumList.size();
        return albumArrayList.size();
    }

    @NonNull
    public String getSectionName(int position) {

        Character c;
        if(albumArrayList == null)
            c = SharedVariables.fullAlbumList.get(position).getTitle().charAt(0);
        else
            c = albumArrayList.get(position).getTitle().charAt(0);

        if(Character.isDigit(c) || !Character.isLetter(c)){
            c = '#';
        }

        return String.valueOf(Character.toUpperCase(c));
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder{

        @BindView (R.id.album_art_albumrecyclerview) ImageView albumArtImageView;
        @BindView (R.id.Album_name_albumrecyclerview) TextView albumNameTextView;
        @BindView (R.id.Artist_name_albumrecyclerview) TextView artistNameTextView;
        @BindView (R.id.color_box_layout_albumrecyclerview) RelativeLayout colorBoxLayout;
        @BindView (R.id.root_layout_album_recyler_view) android.support.v7.widget.CardView rootLayout;
        private String albumLocation = null;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
