package com.prasadam.kmrplayer;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.actions.SearchIntents;
import com.prasadam.kmrplayer.adapterClasses.recyclerViewAdapters.SongRecyclerViewAdapterForArtistActivity;
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

    @Bind(R.id.songs_recycler_view) RecyclerView songsRecyclerView;

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

        songsLayout.setVisibility(View.INVISIBLE);
        artistLayout.setVisibility(View.INVISIBLE);
        albumLayout.setVisibility(View.INVISIBLE);

        setStatusBarTranslucent(this);

        Intent intent = getIntent();
        if(SearchIntents.ACTION_SEARCH.equals(intent.getAction())){
            searchBox.setText(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
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
}
