package com.romankaranchuk.musicplayer.utils;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by NotePad.by on 05.12.2016.
 */

public class SearchLyricUtils extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        String lyrics = NetworkUtils.getLyrics(params[0],params[1]);
//        Log.d("myLogs",lyrics);
        return lyrics;
    }
}
