package com.prasadam.kmrplayer.UI.Fragments.DialogFragment;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.BlurBuilder;
import com.prasadam.kmrplayer.AudioPackages.MusicServiceClasses.MusicPlayerExtensionMethods;
import com.prasadam.kmrplayer.ModelClasses.Song;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SocketClasses.ClientHelper;
import com.prasadam.kmrplayer.SocketClasses.NetworkServiceDiscovery.NSD;
import com.prasadam.kmrplayer.SocketClasses.SocketExtensionMethods;
import com.prasadam.kmrplayer.UI.Fragments.NetworkFragment.ClientFileTransferFragment;
import com.prasadam.kmrplayer.UI.Fragments.NetworkFragment.ClientOptionsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Prasadam Saiteja on 9/23/2016.
 */

public class NearbyDevicesDetails_DialogFragment extends DialogFragment{

    @BindView (R.id.blurred_album_art) ImageView blurredAlbumArt;
    @BindView (R.id.album_art) ImageView albumArt;
    @BindView (R.id.play_download_fab_button) FloatingActionButton playDownloadFabButton;
    @BindView (R.id.device_name) TextView deviceName;
    @BindView (R.id.device_current_song_details) TextView songDetails;
    @BindView (R.id.fabProgressCircle) FABProgressCircle FabProgressCircle;
    @BindView (R.id.tabs) TabLayout tabLayout;
    @BindView (R.id.view_pager) ViewPager viewPager;

    private final NSD serverObject;
    private static Handler refreshDialogFragmentHandler;

    public NearbyDevicesDetails_DialogFragment(NSD serverObject){
        this.serverObject = serverObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_client_details, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
    public void onResume() {
        super.onResume();
        if(serverObject.getCurrentSongPlaying() == null){
            Toast.makeText(getActivity(), "Device currently not available", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }
        setParamsLayout();
        initHandler();
        initComponents();
        initListeners();
    }
    public void onDestroy(){
        refreshDialogFragmentHandler = null;
        super.onDestroy();
    }

    public static void refreshDialogFragment(String macAddress) {
        try{
            if(refreshDialogFragmentHandler!= null)
                refreshDialogFragmentHandler.sendMessage(refreshDialogFragmentHandler.obtainMessage(0, macAddress));
        }

        catch (Exception ignored){}
    }

    private void initHandler() {
        refreshDialogFragmentHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                String msg = (String)message.obj;
                if(serverObject.getMacAddress().equals(msg)){
                    initComponents();
                    initListeners();
                    return true;
                }
                return false;
            }
        });
    }
    private void setParamsLayout() {
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int height = size.y;
        int width = size.x;
        window.setLayout((int) (width * 0.98), (int) (height * 0.85));
        window.setGravity(Gravity.CENTER);
    }
    private void initComponents() {

        viewPager.setAdapter(new TabAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        songDetails.setSelected(true);
        deviceName.setText(serverObject.GetClientNSD().getServiceName());
        songDetails.setText(serverObject.getCurrentSongTitle(getContext()));

        Bitmap bitmap = SocketExtensionMethods.getAlbumArt(getContext(), serverObject);
        if(bitmap != null){
            albumArt.setImageBitmap(bitmap);
            blurredAlbumArt.setImageBitmap(BlurBuilder.blur(getContext(), ((BitmapDrawable) albumArt.getDrawable()).getBitmap()));
        }

        else{
            albumArt.setImageResource(R.mipmap.unkown_album_art);
            blurredAlbumArt.setImageBitmap(BlurBuilder.blur(getContext(), ((BitmapDrawable) albumArt.getDrawable()).getBitmap()));
        }

        if(AudioExtensionMethods.isSongPresent(serverObject.getCurrentSongPlaying().getHashID())){
            try{
                FabProgressCircle.hide();
            }
            catch (Exception ignored){}
            playDownloadFabButton.setImageResource(R.mipmap.ic_play_arrow_black_24dp);
        }
        else
            playDownloadFabButton.setImageResource(R.drawable.ic_cloud_download_white_24dp);
    }
    private void initListeners() {

        playDownloadFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AudioExtensionMethods.isSongPresent(serverObject.getCurrentSongPlaying().getHashID())){
                    Song song = AudioExtensionMethods.getSongFromHashID(serverObject.getCurrentSongPlaying().getHashID());
                    if(song != null)
                        MusicPlayerExtensionMethods.playNow(getContext(), song);

                    else
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.problem_fetching_song), Toast.LENGTH_SHORT).show();
                }
                else{
                    ClientHelper.requestForCurrentSong(getContext(), serverObject);
                    FabProgressCircle.show();
                }
            }
        });

        albumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivitySwitcher.ExpandedAlbumArtWithTranscition(getActivity(), albumArt, SocketExtensionMethods.getAlbumArtLocation(getContext(), serverObject));
            }
        });
    }

    class TabAdapter extends FragmentPagerAdapter {

        private ClientFileTransferFragment clientFileTransferFragment;
        private ClientOptionsFragment clientOptionsFragment;

        public TabAdapter(FragmentManager fm) {
            super(fm);
            clientFileTransferFragment  = new ClientFileTransferFragment(serverObject);
            clientOptionsFragment = new ClientOptionsFragment(serverObject);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 : return clientOptionsFragment;
                case 1 : return clientFileTransferFragment;
            }
            return null;
        }
        public int getCount() {
            return 2;
        }
        public CharSequence getPageTitle(int position) {
            switch (position){

                case 0 :
                    return getResources().getString(R.string.options_text);

                case 1 :
                    return getResources().getString(R.string.transfer_history);
            }
            return null;
        }
    }
}