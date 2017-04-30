package com.romankaranchuk.musicplayer.utils.search;

import android.os.AsyncTask;
import android.util.Log;

import com.romankaranchuk.musicplayer.utils.NetworkUtils;

/**
 * Created by NotePad.by on 05.12.2016.
 */

public class SearchLanguageUtils extends AsyncTask<String, Void, String> {
    @Override
    public String doInBackground(String... params) {
        String language = NetworkUtils.getLanguage(params[0], params[1]);
        Log.d("myLogs", language);
        return language;
    }
}