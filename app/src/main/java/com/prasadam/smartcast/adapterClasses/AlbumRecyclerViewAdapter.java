package com.prasadam.smartcast.adapterClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.prasadam.smartcast.AlbumActivity;
import com.prasadam.smartcast.R;
import com.prasadam.smartcast.audioPackages.Album;
import com.prasadam.smartcast.commonClasses.CommonVariables;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 3/25/2016.
 */
public class AlbumRecyclerViewAdapter extends ObservableRecyclerView.Adapter<AlbumRecyclerViewAdapter.AlbumViewHolder> implements INameableAdapter {

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

        final Album currentAlbum = CommonVariables.fullAlbumList.get(position);

        holder.albumNameTextView.setText(currentAlbum.getTitle());
        holder.artistNameTextView.setText(currentAlbum.getArtist());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent albumActivityIntent = new Intent(context, AlbumActivity.class);
                albumActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, holder.albumArtImageView, "AlbumArtImageTranscition");
                albumActivityIntent.putExtra("albumTitle", currentAlbum.getTitle());
                mActivity.startActivity(albumActivityIntent, options.toBundle());
            }
        });


        {   //Album art
            String albumArtPath = currentAlbum.getAlbumArtLocation();
            if(albumArtPath != null)
            {
                final File imgFile = new File(albumArtPath);
                if(imgFile.exists())// /storage/emulated/0/Android/data/com.android.providers.media/albumthumbs/1454267773223
                {
                    holder.albumArtImageView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));

                    if(currentAlbum.isColorSet()){
                        holder.colorBoxLayout.setBackgroundColor(currentAlbum.colorBoxLayoutColor);
                        holder.albumNameTextView.setTextColor(currentAlbum.albumNameTextViewColor);
                        holder.artistNameTextView.setTextColor(currentAlbum.artistNameTextViewColor);
                    }

                    if(!currentAlbum.isColorSet())
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

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
                    //Picasso.with(context).load("file://" + imgFile.getAbsolutePath()).into(holder.AlbumArtImageView);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return CommonVariables.fullAlbumList.size();
    }


    @Override
    public Character getCharacterForElement(int element) {

        int position = element * 2;
        Character c = CommonVariables.fullAlbumList.get(position).getTitle().charAt(0);

        if(Character.isDigit(c) || c.equals('<')){
            c = '#';
        }
        return c;
    }


    class AlbumViewHolder extends RecyclerView.ViewHolder{

        @Bind (R.id.album_art_albumrecyclerview) ImageView albumArtImageView;
        @Bind (R.id.Album_name_albumrecyclerview) TextView albumNameTextView;
        @Bind (R.id.Artist_name_albumrecyclerview) TextView artistNameTextView;
        @Bind (R.id.color_box_layout_albumrecyclerview) RelativeLayout colorBoxLayout;
        @Bind (R.id.root_layout_album_recyler_view) android.support.v7.widget.CardView rootLayout;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
