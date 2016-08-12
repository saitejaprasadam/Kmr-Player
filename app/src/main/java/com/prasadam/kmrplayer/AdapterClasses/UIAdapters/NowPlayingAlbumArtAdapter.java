package com.prasadam.kmrplayer.AdapterClasses.UIAdapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.prasadam.kmrplayer.AdapterClasses.Interfaces.OnDoubleTapListener;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.PlayerConstants;
import com.prasadam.kmrplayer.Fragments.SongsFragment;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.VerticalSlidingDrawerBaseActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by Prasadam Saiteja on 7/27/2016.
 */

public class NowPlayingAlbumArtAdapter extends PagerAdapter {

    private final Activity activity;
    private int count;
    Map<Integer, View> imageViews = new HashMap<>();

    public NowPlayingAlbumArtAdapter(Activity activity) {
        this.activity = activity;
        count = PlayerConstants.SONGS_LIST.size();
    }
    public Map<Integer, View> getImageViews() {
        return imageViews;
    }
    public void updateAlbumArt(ViewPager viewPager){
        viewPager.setCurrentItem(PlayerConstants.SONG_NUMBER);
    }

    @Override
    public int getCount() {
        return count;
    }
    public void dataSetChange(){
        count = PlayerConstants.SONGS_LIST.size();
        notifyDataSetChanged();
    }
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_now_playing_album_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.vertical_slide_drawer_actual_image_view);
        imageView.setOnTouchListener(new OnDoubleTapListener(activity){
            @Override
            public void onDoubleTap(MotionEvent e) {
                if(PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getIsLiked(activity))
                    PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).setIsLiked(activity, false);
                else
                    PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).setIsLiked(activity, true);

                SongsFragment.recyclerViewAdapter.notifyDataSetChanged();
                VerticalSlidingDrawerBaseActivity.updateSongLikeStatus(activity);
                VerticalSlidingDrawerBaseActivity.recyclerViewAdapter.notifyDataSetChanged();
            }
        });

        String albumArtPath = PlayerConstants.SONGS_LIST.get(position).getAlbumArtLocation();

        if(albumArtPath != null) {
            final File imgFile = new File(albumArtPath);
            if (imgFile.exists())
                imageView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));

            else
                imageView.setImageResource(R.mipmap.unkown_album_art);
        }

        else
            imageView.setImageResource(R.mipmap.unkown_album_art);

        container.addView(view);
        imageViews.put(position, imageView);
        return view;
    }
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }
}