package com.prasadam.kmrplayer;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.actions.SearchIntents;
import com.prasadam.kmrplayer.activityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.SongRecyclerViewAdapterForArtistActivity;
import com.prasadam.kmrplayer.adapterClasses.uiAdapters.DividerItemDecoration;
import com.prasadam.kmrplayer.audioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.audioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.fragments.NoItemsFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.prasadam.kmrplayer.sharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 7/18/2016.
 */

public class SearchActivity extends AppCompatActivity{

    protected static final int RESULT_SPEECH = 1;
    @Bind(R.id.search_textbox) EditText searchBox;
    @Bind(R.id.songs_layout) LinearLayout songsLayout;
    @Bind(R.id.artist_layout) LinearLayout artistLayout;
    @Bind(R.id.albums_layout) LinearLayout albumLayout;
    @Bind(R.id.fragment_container) FrameLayout fragmentContainer;

    @Bind(R.id.songs_recycler_view) RecyclerView songsRecyclerView;
    @Bind(R.id.artist_recycler_view) RecyclerView artistRecyclerView;
    @Bind(R.id.albums_recycler_view) RecyclerView albumRecyclerView;
    private NoItemsFragment noResultFragment = null;

    private SongRecyclerViewAdapterForArtistActivity songRecyclerViewAdapter;

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
        setStatusBarTranslucent(this);

        setEmptyFragment();
        songsRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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
            songsLayout.setVisibility(View.INVISIBLE);
            artistLayout.setVisibility(View.INVISIBLE);
            albumLayout.setVisibility(View.INVISIBLE);
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
        finish();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchBox.setText(text.get(0));
                }
                break;
            }

        }
    }

    private class setRecyclerViews extends AsyncTask<Void, Void, Void>{

        private ArrayList<Song> songsResult = new ArrayList<>();
        private String searchQuery;

        public setRecyclerViews(String searchQuery){
            this.searchQuery = searchQuery;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            songsResult = AudioExtensionMethods.getSongListForSearch(searchQuery);
            return null;
        }

        @Override
        protected void onPostExecute(Void th){

            if(songsResult.size() > 0){
                songsLayout.setVisibility(View.VISIBLE);
                songRecyclerViewAdapter = new SongRecyclerViewAdapterForArtistActivity(SearchActivity.this, songsResult);
                songsRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                songsRecyclerView.setAdapter(songRecyclerViewAdapter);
            }

            else{
                songRecyclerViewAdapter = null;
                songsRecyclerView.setAdapter(null);
                songsLayout.setVisibility(View.INVISIBLE);
                setEmptyFragment();
            }
        }
    }

}
