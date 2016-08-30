package com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters;

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
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Artist;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;
import com.prasadam.kmrplayer.UI.Activities.ArtistActivity;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 6/22/2016.
 */

public class ArtistAdapter extends ObservableRecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private Activity mActivity;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Artist> artistArrayList = null;

    public ArtistAdapter(Activity mActivity, Context baseContext) {
        this.mActivity = mActivity;
        this.context = baseContext;
        inflater = LayoutInflater.from(context);
    }
    public ArtistAdapter(Activity mActivity, Context baseContext, ArrayList<Artist> artistArrayList) {

        this.artistArrayList = artistArrayList;
        this.mActivity = mActivity;
        this.context = baseContext;
        inflater = LayoutInflater.from(context);
    }

    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(artistArrayList == null)
            view = inflater.inflate(R.layout.recycler_view_artist_layout, parent, false);
        else
            view = inflater.inflate(R.layout.recylcer_view_artist_search_layout, parent, false);
        return new ArtistViewHolder(view);
    }
    public void onBindViewHolder(final ArtistViewHolder holder, int position) {

        final Artist artist;
        if(artistArrayList == null)
            artist = SharedVariables.fullArtistList.get(position);
        else
            artist = artistArrayList.get(position);
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
                                final int[] colors = ActivityHelper.getAvailableColor(mActivity, palette);
                                holder.colorBoxLayout.setBackgroundColor(colors[0]);
                                artist.colorBoxLayoutColor = colors[0];

                                holder.artistNameTextView.setTextColor(colors[1]);
                                artist.artistNameTextViewColor = colors[1];
                            }
                        });
                    }

                    catch (Exception ignored){}

                }
            }).start();
        }
    }
    public int getItemCount() {
        if(artistArrayList == null)
            return SharedVariables.fullArtistList.size();
        else
            return artistArrayList.size();
    }

    @NonNull
    public String getSectionName(int position) {
        Character c;
        if(artistArrayList == null)
            c = SharedVariables.fullArtistList.get(position).getArtistTitle().charAt(0);
        else
            c = artistArrayList.get(position).getArtistTitle().charAt(0);
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
