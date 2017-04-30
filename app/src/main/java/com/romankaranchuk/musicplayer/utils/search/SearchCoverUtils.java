package com.romankaranchuk.musicplayer.utils.search;

import android.os.AsyncTask;
import android.util.Log;

import com.romankaranchuk.musicplayer.utils.NetworkUtils;

/**
 * Created by roman on 25.2.17.
 */

public class SearchCoverUtils extends AsyncTask<String, Void, String> {

    @Override
    public String doInBackground(String... params) {
        String urlAlbumCover = NetworkUtils.getUrlAlbumCover(params[0], params[1]);
        Log.d("myLogs", urlAlbumCover);
        return urlAlbumCover;
    }
}
