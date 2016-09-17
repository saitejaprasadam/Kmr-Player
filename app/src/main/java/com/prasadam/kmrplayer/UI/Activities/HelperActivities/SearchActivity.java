package com.prasadam.kmrplayer.UI.Activities.HelperActivities;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.actions.SearchIntents;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.AlbumAdapter.AlbumAdapter;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.ArtistAdapter;
import com.prasadam.kmrplayer.Adapters.RecyclerViewAdapters.SongsAdapter.UnifedSongAdapter;
import com.prasadam.kmrplayer.Adapters.UIAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.ModelClasses.Album;
import com.prasadam.kmrplayer.ModelClasses.Artist;
import com.prasadam.kmrplayer.R;
import com.prasadam.kmrplayer.SubClasses.CustomArrayList.SongsArrayList;
import com.prasadam.kmrplayer.UI.Activities.VerticalSlidingDrawerBaseActivity;
import com.prasadam.kmrplayer.UI.Fragments.NoItemsFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by Prasadam Saiteja on 7/18/2016.
 */

public class SearchActivity extends VerticalSlidingDrawerBaseActivity {

    protected static final int RESULT_SPEECH = 1;
    @BindView(R.id.search_textbox) EditText searchBox;
    @BindView(R.id.songs_layout) LinearLayout songsLayout;
    @BindView(R.id.artist_layout) LinearLayout artistLayout;
    @BindView(R.id.albums_layout) LinearLayout albumLayout;
    @BindView(R.id.fragment_container) FrameLayout fragmentContainer;

    @BindView(R.id.songs_recycler_view) RecyclerView songsRecyclerView;
    @BindView(R.id.artist_recycler_view) RecyclerView artistRecyclerView;
    @BindView(R.id.albums_recycler_view) RecyclerView albumRecyclerView;
    private NoItemsFragment noResultFragment = null;

    private UnifedSongAdapter songRecyclerViewAdapter;
    private ArtistAdapter artistRecyclerViewAdapter;
    private AlbumAdapter albumRecyclerViewAdapter;

    @OnClick(R.id.voice_button)
    public void voiceSearch(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Opps! Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_search_layout);
        ButterKnife.bind(this);
        ActivityHelper.setStatusBarTranslucent(this, findViewById(R.id.colored_status_bar));
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                finish();
            }
        });

        searchBox.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        setEmptyFragment();
        searchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                }
            }
        });
        songsRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchBox.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchQueryChanged();
            }
        });

        Intent intent = getIntent();
        if(SearchIntents.ACTION_SEARCH.equals(intent.getAction())){
            searchBox.setText(intent.getStringExtra(SearchManager.QUERY));
        }
    }
    private void searchQueryChanged() {
        if(searchBox.getText().toString().length() == 0){
            songsLayout.setVisibility(View.GONE);
            artistLayout.setVisibility(View.GONE);
            albumLayout.setVisibility(View.GONE);
            if(noResultFragment == null)
                setEmptyFragment();
        }

        else{

            SearchActivity.setRecyclerViews setRecyclerViews = new setRecyclerViews(searchBox.getText().toString());
            setRecyclerViews.execute();

            if(noResultFragment != null){
                getFragmentManager().beginTransaction().remove(noResultFragment).commit();
                noResultFragment = null;
            }
        }
    }
    private void setEmptyFragment() {
        noResultFragment = ActivityHelper.showEmptyFragment(SearchActivity.this, getResources().getString(R.string.no_results_found_text), fragmentContainer);
    }

    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        super.onBackPressed();
    }
    public void onDestroy(){
        songsRecyclerView.setAdapter(null);
        artistRecyclerView.setAdapter(null);
        albumRecyclerView.setAdapter(null);
        songRecyclerViewAdapter = null;
        artistRecyclerViewAdapter = null;
        albumRecyclerViewAdapter = null;
        super.onDestroy();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchBox.setText(text.get(0));
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                }
                break;
            }

        }
    }

    private class setRecyclerViews extends AsyncTask<Void, Void, Void>{

        private SongsArrayList songsResult;
        private ArrayList<Album> albumResult = new ArrayList<>();
        private ArrayList<Artist> artistResult = new ArrayList<>();
        private String searchQuery;

        public setRecyclerViews(String searchQuery){
            this.searchQuery = searchQuery;
            songsResult = new SongsArrayList() {
                @Override
                public void notifyDataSetChanged() {

                }

                @Override
                public void notifyItemRemoved(int index) {

                }

                @Override
                public void notifyItemInserted(int index) {

                }

                @Override
                public void notifyItemChanged(int index) {

                }
            };
        }

        @Override
        protected Void doInBackground(Void... voids) {

            songsResult.addAll(AudioExtensionMethods.getSongListForSearch(searchQuery));
            albumResult = AudioExtensionMethods.getAlbumListForSearch(searchQuery);
            artistResult = AudioExtensionMethods.getArtistListForSearch(searchQuery);
            return null;
        }

        @Override
        protected void onPostExecute(Void th){

            setSongRecyclerView();
            setAlbumRecyclerView();
            setArtistRecyclerView();

            if(albumResult.size() == 0 && artistResult.size() == 0 && songsResult.size() == 0)
                setEmptyFragment();
        }

        private void setArtistRecyclerView() {
            if(artistResult.size() > 0){
                artistLayout.setVisibility(View.VISIBLE);
                artistRecyclerViewAdapter = new ArtistAdapter(SearchActivity.this, SearchActivity.this, artistResult);
                artistRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.HORIZONTAL, false));
                artistRecyclerView.setAdapter(artistRecyclerViewAdapter);
            }

            else{
                artistRecyclerViewAdapter = null;
                artistRecyclerView.setAdapter(null);
                artistLayout.setVisibility(View.GONE);
            }
        }
        private void setAlbumRecyclerView() {
            if(albumResult.size() > 0){
                albumLayout.setVisibility(View.VISIBLE);
                albumRecyclerViewAdapter = new AlbumAdapter(SearchActivity.this, SearchActivity.this, albumResult);
                albumRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.HORIZONTAL, false));
                albumRecyclerView.setAdapter(albumRecyclerViewAdapter);
            }

            else{
                albumRecyclerViewAdapter = null;
                albumRecyclerView.setAdapter(null);
                albumLayout.setVisibility(View.GONE);
            }
        }
        private void setSongRecyclerView() {
            if(songsResult.size() > 0){
                songsLayout.setVisibility(View.VISIBLE);
                songRecyclerViewAdapter = new UnifedSongAdapter(SearchActivity.this, songsResult);
                songsRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                songsRecyclerView.setAdapter(songRecyclerViewAdapter);
            }

            else{
                songRecyclerViewAdapter = null;
                songsRecyclerView.setAdapter(null);
                songsLayout.setVisibility(View.GONE);
            }
        }
    }
}