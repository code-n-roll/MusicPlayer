package com.romankaranchuk.musicplayer.ui.genres;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Genre;
import com.romankaranchuk.musicplayer.ui.tracklist.TracklistActivity;

/**
 * Created by NotePad.by on 14.10.2016.
 */

public class GenresActivity extends AppCompatActivity {
    private Genre[] genres = {
            new Genre("Alternative/Indie", R.drawable.belarusian),
            new Genre("Blues", R.drawable.belarusian),
            new Genre("Children's Music", R.drawable.belarusian),
            new Genre("Christian/Gospel", R.drawable.belarusian),
            new Genre("Classical", R.drawable.belarusian),
            new Genre("Comedy/Spoken Word/Other", R.drawable.belarusian),
            new Genre("Country", R.drawable.belarusian),
            new Genre("Dance/Electronic", R.drawable.belarusian),
            new Genre("Folk", R.drawable.belarusian),
            new Genre("Hip-Hop/Rap", R.drawable.belarusian),
            new Genre("Jazz", R.drawable.belarusian),
            new Genre("Metal", R.drawable.belarusian),
            new Genre("Pop", R.drawable.belarusian),
            new Genre("R&B/Soul", R.drawable.belarusian),
            new Genre("Reggae", R.drawable.belarusian),
            new Genre("Rock", R.drawable.belarusian),
            new Genre("Seasonal", R.drawable.belarusian),
            new Genre("Soundtracks", R.drawable.belarusian),
            new Genre("Vocal/Easy Listening", R.drawable.belarusian),
            new Genre("World", R.drawable.belarusian)
    };

    private GridView genresList;
    private String LOG_TAG = "MyLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        Toolbar toolbar = (Toolbar) findViewById(R.id.genres_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        genresList = (GridView) findViewById(R.id.genres_list);
        GenreListAdapter genreAdapter = new GenreListAdapter(this, R.layout.content_genres, genres);
        genresList.setAdapter(genreAdapter);
//        genresList.setDivider(null);


        AdapterView.OnItemClickListener itemClickListener = new
            AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id){
                    Genre selectedGenre = (Genre)parent.getItemAtPosition(position);
                    switch(selectedGenre.getName()){
                        case "Alternative/Indie": {
//                            openTrackList(genresList);
                            break;
                        }
                        case "Blues":
                            break;
                        case "Children's Music":
                            break;
                        case "Christian/Gospel":
                            break;
                        case "Classical":
                            break;
                        case "Comedy/Spoken Word/Other":
                            break;
                        case "Country":
                            break;
                        case "Dance/Electronic":
                            break;
                        case "Folk":
                            break;
                        case "Hip-Hop/Rap":
                            break;
                        case "Jazz":
                            break;
                        case "Metal":
                            break;
                        case "Pop":
                            break;
                        case "R&B/Soul":
                            break;
                        case "Reggae":
                            break;
                        case "Rock":
                            break;
                        case "Seasonal":
                            break;
                        case "Soundtracks":
                            break;
                        case "Vocal/Easy Listening":
                            break;
                        case "World":
                            break;
                        default:
                            break;
                    }
                }
            };

        genresList.setOnItemClickListener(itemClickListener);
        Log.d(LOG_TAG, "GenresActivity onCreate");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "GenresActivity onRestart");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "GenresActivity onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "GenresActivity onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "GenresActivity onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "GenresActivity onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "GenresActivity onDestroy");
    }

//    public void openTrackList(View view){
//        Intent intent = new Intent(this, TracklistActivity.class);
//        startActivity(intent);
//    }
}
