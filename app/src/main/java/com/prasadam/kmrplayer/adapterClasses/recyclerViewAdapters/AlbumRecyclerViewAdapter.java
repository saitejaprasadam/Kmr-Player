package com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.prasadam.kmrplayer.AlbumActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.activityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Album;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 3/25/2016.
 */

public class AlbumRecyclerViewAdapter extends ObservableRecyclerView.Adapter<AlbumRecyclerViewAdapter.AlbumViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private Context context;
    private LayoutInflater inflater;
    private Activity mActivity;

    public AlbumRecyclerViewAdapter(Activity mActivity, Context context){

        this.mActivity = mActivity;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public AlbumRecyclerViewAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_albums_layout, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlbumRecyclerViewAdapter.AlbumViewHolder holder, int position) {

        final Album currentAlbum = SharedVariables.fullAlbumList.get(position);
        holder.albumNameTextView.setText(currentAlbum.getTitle());
        holder.artistNameTextView.setText(currentAlbum.getArtist());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivitySwitcher.jumpToAlbumWithTranscition(mActivity, holder.albumArtImageView, currentAlbum.getTitle());
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
                            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                            if (vibrantSwatch != null) {
                                holder.colorBoxLayout.setBackgroundColor(vibrantSwatch.getRgb());
                                currentAlbum.colorBoxLayoutColor = vibrantSwatch.getRgb();

                                holder.albumNameTextView.setTextColor(vibrantSwatch.getBodyTextColor());
                                currentAlbum.albumNameTextViewColor = vibrantSwatch.getBodyTextColor();

                                holder.artistNameTextView.setTextColor(vibrantSwatch.getTitleTextColor());
                                currentAlbum.artistNameTextViewColor = vibrantSwatch.getTitleTextColor();
                            }

                            else
                            {
                                vibrantSwatch = palette.getMutedSwatch();
                                if (vibrantSwatch != null) {
                                    holder.colorBoxLayout.setBackgroundColor(vibrantSwatch.getRgb());
                                    currentAlbum.colorBoxLayoutColor = vibrantSwatch.getRgb();

                                    holder.albumNameTextView.setTextColor(vibrantSwatch.getBodyTextColor());
                                    currentAlbum.albumNameTextViewColor = vibrantSwatch.getBodyTextColor();

                                    holder.artistNameTextView.setTextColor(vibrantSwatch.getTitleTextColor());
                                    currentAlbum.artistNameTextViewColor = vibrantSwatch.getTitleTextColor();
                                }
                            }
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
        return SharedVariables.fullAlbumList.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {

        Character c = SharedVariables.fullAlbumList.get(position).getTitle().charAt(0);
        if(Character.isDigit(c) || !Character.isLetter(c)){
            c = '#';
        }

        return String.valueOf(c);
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder{

        @Bind (R.id.album_art_albumrecyclerview) ImageView albumArtImageView;
        @Bind (R.id.Album_name_albumrecyclerview) TextView albumNameTextView;
        @Bind (R.id.Artist_name_albumrecyclerview) TextView artistNameTextView;
        @Bind (R.id.color_box_layout_albumrecyclerview) RelativeLayout colorBoxLayout;
        @Bind (R.id.root_layout_album_recyler_view) android.support.v7.widget.CardView rootLayout;
        private String albumLocation = null;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
