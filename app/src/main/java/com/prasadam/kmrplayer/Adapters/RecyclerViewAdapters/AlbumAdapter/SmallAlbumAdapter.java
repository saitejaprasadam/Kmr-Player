package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.AlbumAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.ModelClasses.Album;
import com.prasadam.kmrplayer.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class SmallAlbumAdapter extends ObservableRecyclerView.Adapter<SmallAlbumAdapter.ArtistViewHolder>{

    private Activity mActivity;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Album> albumArrayList;

    public SmallAlbumAdapter(Activity mActivity, Context baseContext, ArrayList<Album> albumArrayList) {
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
    public void onBindViewHolder(final ArtistViewHolder holder, int position) {

        final Album album = albumArrayList.get(position);
        holder.artistNameTextView.setText(album.getTitle());
        setAlbumArt(holder, album);
        setColor(holder, album);
        setOnclickListener(holder, album);
    }
    public int getItemCount() {
        return albumArrayList.size();
    }

    private void setOnclickListener(final ArtistViewHolder holder, final Album album) {

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySwitcher.jumpToAlbumWithTranscition(mActivity, holder.artistAlbumArtImageView, album.getID());
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
                        final int[] colors = ActivityHelper.getAvailableColor(mActivity, palette);
                        holder.colorBoxLayout.setBackgroundColor(colors[0]);
                        artist.colorBoxLayoutColor = colors[0];

                        holder.artistNameTextView.setTextColor(colors[1]);
                        artist.artistNameTextViewColor = colors[1];
                    }
                });
            }
        }

        catch (Exception ignored){}

    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.artist_title) TextView artistNameTextView;
        @BindView(R.id.artist_image) ImageView artistAlbumArtImageView;
        @BindView(R.id.color_box_layout_albumrecyclerview) FrameLayout colorBoxLayout;
        @BindView(R.id.rootLayout_recycler_view) CardView rootLayout;

        public ArtistViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }
}
