package com.prasadam.kmrplayer.UI.Activities.HelperActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SharedClasses.ExtensionMethods;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 6/21/2016.
 */

public class ExpandedAlbumartActivity extends AppCompatActivity {

    @BindView(R.id.blurred_album_art) ImageView blurredAlbumArt;
    @BindView(R.id.actual_album_art) ImageView actualAlbumArt;

    @OnClick (R.id.parent_layout)
    public void parentClicked(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            finishAfterTransition();

        else
            finish();
    }

    @OnClick (R.id.share_album_art_button)
    public void shareAlbumart(View view){

        final MaterialDialog progress = new MaterialDialog.Builder(this)
                .content("Please wait...")
                .progress(true, 0)
                .cancelable(false)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    File cachePath = new File(getCacheDir(), "images");
                    cachePath.mkdirs();
                    new File(cachePath + "/image.png").delete();
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                    actualAlbumArt.buildDrawingCache();
                    Bitmap bitmap = actualAlbumArt.getDrawingCache();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.flush();
                    stream.close();

                    File imagePath = new File(getCacheDir(), "images");
                    File newFile = new File(imagePath, "/image.png");
                    Uri contentUri = FileProvider.getUriForFile(ExpandedAlbumartActivity.this, "com.prasadam.kmrplayer.fileprovider", newFile);

                    if (contentUri != null) {

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        progress.dismiss();
                        startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                    }

                }

                catch (Exception ex){
                    Toast.makeText(ExpandedAlbumartActivity.this, "Failed to send album art", Toast.LENGTH_SHORT).show();
                    Log.d("exception", String.valueOf(ex));
                }

                finally {
                    progress.dismiss();
                }
            }
        }).start();


    }

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_albumart_expanded);
        ButterKnife.bind(this);
        initalizer();

        String albumArtPath = getIntent().getExtras().getString("albumArtPath");
        if(albumArtPath != null){
            actualAlbumArt.setImageURI(Uri.parse(albumArtPath));
            blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) actualAlbumArt.getDrawable()).getBitmap()));
        }

        else{
            actualAlbumArt.setImageResource(R.mipmap.unkown_album_art);
            blurredAlbumArt.setImageBitmap(BlurBuilder.blur(this, ((BitmapDrawable) actualAlbumArt.getDrawable()).getBitmap()));
        }
    }
    private void initalizer() {

        if (Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);
        toolbar.setPadding(0, ExtensionMethods.getStatusBarHeight(this), 0, 0);
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
}
