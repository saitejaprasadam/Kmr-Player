package com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.prasadam.kmrplayer.ArtistActivity;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Artist;
import com.prasadam.kmrplayer.sharedClasses.SharedVariables;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class ArtistRecyclerViewAdapter extends ObservableRecyclerView.Adapter<ArtistRecyclerViewAdapter.ArtistViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private Activity mActivity;
    private Context context;
    private LayoutInflater inflater;

    public ArtistRecyclerViewAdapter(Activity mActivity, Context baseContext) {
        this.mActivity = mActivity;
        this.context = baseContext;
        inflater = LayoutInflater.from(context);
    }

    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_artist_layout, parent, false);
        return new ArtistViewHolder(view);
    }
    public void onBindViewHolder(final ArtistViewHolder holder, int position) {

        final Artist artist = SharedVariables.fullArtistList.get(position);
        holder.artistNameTextView.setText(artist.getArtistTitle());
        setAlbumArt(holder, artist);
        setColor(holder, artist);
        setOnclickListener(holder, artist);
    }
    private void setOnclickListener(final ArtistViewHolder holder, final Artist artist) {

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mActivity, ArtistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, holder.artistAlbumArtImageView, "AlbumArtImageTranscition");
                intent.putExtra(ArtistActivity.ARTIST_EXTRA, artist.getArtistTitle());
                context.startActivity(intent);
            }
        });
    }

    private void setAlbumArt(ArtistViewHolder holder, Artist artist) {

        String albumArtPath = artist.artistAlbumArt;
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
    private void setColor(final ArtistViewHolder holder, final Artist artist){

        if(artist.isColorSet()){
            holder.colorBoxLayout.setBackgroundColor(artist.colorBoxLayoutColor);
            holder.artistNameTextView.setTextColor(artist.artistNameTextViewColor);
        }

        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Bitmap bitmap = AudioExtensionMethods.getBitMap(context, artist.artistAlbumArt);
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
                                    artist.artistNameTextViewColor = vibrantSwatch.getTitleTextColor();
                                }

                                else
                                {
                                    vibrantSwatch = palette.getMutedSwatch();
                                    if (vibrantSwatch != null) {
                                        holder.colorBoxLayout.setBackgroundColor(vibrantSwatch.getRgb());
                                        artist.colorBoxLayoutColor = vibrantSwatch.getRgb();

                                        holder.artistNameTextView.setTextColor(vibrantSwatch.getTitleTextColor());
                                        artist.artistNameTextViewColor = vibrantSwatch.getTitleTextColor();
                                    }
                                }
                            }
                        });
                    }

                    catch (Exception ignored){}

                }
            }).start();
        }
    }
    public int getItemCount() {
        return SharedVariables.fullArtistList.size();
    }

    @NonNull
    public String getSectionName(int position) {
        Character c = SharedVariables.fullArtistList.get(position).getArtistTitle().charAt(0);
        if(Character.isDigit(c) || !Character.isLetter(c)){
            c = '#';
        }

        return String.valueOf(c);
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
