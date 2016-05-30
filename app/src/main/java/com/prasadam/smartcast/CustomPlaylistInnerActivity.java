package com.prasadam.smartcast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.prasadam.smartcast.audioPackages.BlurBuilder;
import com.prasadam.smartcast.sharedClasses.ExtensionMethods;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.prasadam.smartcast.audioPackages.AudioExtensionMethods.getAlbumArtsForPlaylistCover;

/*
 * Created by Prasadam Saiteja on 5/30/2016.
 */

public class CustomPlaylistInnerActivity extends Activity{

    private String playlistName;
    @Bind(R.id.background_image_view) ImageView blurredBackgroundImageView;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_custom_playlist_inner_layout);
        ButterKnife.bind(this);

        playlistName = getIntent().getExtras().getString("playlistName");
        ArrayList<String> albumArtPathList = getAlbumArtsForPlaylistCover(this, playlistName);
        setAlbumArt(albumArtPathList);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            toolbar.setPadding(0, ExtensionMethods.getStatusBarHeight(this), 0, 0);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();

                else
                    finish();
            }
        });
    }

    private void setAlbumArt(ArrayList<String> albumArtPathList) {
        if(albumArtPathList.size() > 0){
            String albumArtPath = albumArtPathList.get(0);
            if(albumArtPath != null)
            {
                File imgFile = new File(albumArtPath);
                if(imgFile.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    blurredBackgroundImageView.setImageBitmap(BlurBuilder.blur(this, bitmap));
                }
            }
        }
    }
}
