package com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.prasadam.kmrplayer.AlbumActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.activityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Album;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class SmallAlbumRecyclerViewAdapter extends ObservableRecyclerView.Adapter<SmallAlbumRecyclerViewAdapter.ArtistViewHolder>{

    private Activity mActivity;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Album> albumArrayList;

    public SmallAlbumRecyclerViewAdapter(Activity mActivity, Context baseContext, ArrayList<Album> albumArrayList) {
        this.albumArrayList = albumArrayList;
        this.mActivity = mActivity;
        this.context = baseContext;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_small_album_layout, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArtistViewHolder holder, int position) {

        final Album album = albumArrayList.get(position);
        holder.artistNameTextView.setText(album.getTitle());
        setAlbumArt(holder, album);
        setColor(holder, album);
        setOnclickListener(holder, album);
    }

    private void setOnclickListener(final ArtistViewHolder holder, final Album album) {

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySwitcher.jumpToAlbumWithTranscition(mActivity, holder.artistAlbumArtImageView, album.getTitle());
            }
        });
    }
    private void setAlbumArt(ArtistViewHolder holder, Album artist) {

        String albumArtPath = artist.getAlbumArtLocation();
        if(albumArtPath != null)
        {
            File imgFile = new File(albumArtPath);
            if(imgFile.exists()){
                holder.artistAlbumArtImageView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
            }

            else
                holder.artistAlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
        }
        else
            holder.artistAlbumArtImageView.setImageResource(R.mipmap.unkown_album_art);
    }
    private void setColor(final ArtistViewHolder holder, final Album artist){

        try{
            if(artist.isColorSet()){
                holder.colorBoxLayout.setBackgroundColor(artist.colorBoxLayoutColor);
                holder.artistNameTextView.setTextColor(artist.artistNameTextViewColor);
            }

            else{
                Bitmap bitmap = AudioExtensionMethods.getBitMap(context, artist.getAlbumArtLocation());
                if(bitmap == null)
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.unkown_album_art);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                        if (vibrantSwatch != null) {
                            holder.colorBoxLayout.setBackgroundColor(vibrantSwatch.getRgb());
                            artist.colorBoxLayoutColor = vibrantSwatch.getRgb();

                            holder.artistNameTextView.setTextColor(vibrantSwatch.getTitleTextColor());
                            artist.artistNameTextViewColor = vibrantSwatch.getBodyTextColor();
                        }

                        else
                        {
                            vibrantSwatch = palette.getMutedSwatch();
                            if (vibrantSwatch != null) {
                                holder.colorBoxLayout.setBackgroundColor(vibrantSwatch.getRgb());
                                artist.colorBoxLayoutColor = vibrantSwatch.getRgb();

                                holder.artistNameTextView.setTextColor(vibrantSwatch.getTitleTextColor());
                                artist.artistNameTextViewColor = vibrantSwatch.getBodyTextColor();
                            }
                        }
                    }
                });
            }
        }

        catch (Exception e){}

    }

    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }
    public void setAlbumList(ArrayList<Album> albumList){
        this.albumArrayList = albumList;
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.artist_title) TextView artistNameTextView;
        @Bind(R.id.artist_image) ImageView artistAlbumArtImageView;
        @Bind(R.id.color_box_layout_albumrecyclerview) FrameLayout colorBoxLayout;
        @Bind(R.id.rootLayout_recycler_view) CardView rootLayout;

        public ArtistViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }
}
