package com.romankaranchuk.musicplayer.presentation.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.romankaranchuk.musicplayer.data.Album;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.data.db.MusicRepositoryImpl;
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment;
import com.romankaranchuk.musicplayer.utils.MusicUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SearchService extends Service {
    String LOG_TAG = "myLogs";
    private final SearchBinder mBinder = new SearchBinder();
    private final MusicRepositoryImpl mRepository;
    private boolean isSearchActive = false;

    public SearchService(){
        mRepository = MusicRepositoryImpl.getInstance(this);
    }

    public class SearchBinder extends Binder {
        public SearchService getService(){
            return SearchService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(LOG_TAG,"SearchService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(LOG_TAG, "SearchService onStartCommand");
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        Log.d(LOG_TAG, "SearchService onBind");
        return mBinder;
    }

    public void startMusicSearch(){
        if (!isSearchActive){
            MusicSearcher searcher = new MusicSearcher();
            new Thread(searcher).start();
        }
    }

    private class MusicSearcher implements Runnable {

        private void iterateFiles(File[] files, HashMap<String, List<String>> music){
            for (File file : files){
                if (file.isDirectory()){
                    iterateFiles(file.listFiles(), music);
                } else {
                    String name = file.getName();
                    if (name.endsWith(".mp3")){
                        String dirPath = file.getParent();
                        if (!music.containsKey(dirPath)){
                            music.put(dirPath, new LinkedList<String>());
                        }
                        List<String> songNames = music.get(dirPath);
                        songNames.add(name);
                    }
                }
            }
        }

        @Override
        public void run() {
//            String[] projection = new String[] {
//                    MediaStore.Audio.Media._ID,
//                    MediaStore.Audio.Media.ALBUM,
//                    MediaStore.Audio.Media.ARTIST,
//                    MediaStore.Audio.Media.TITLE,
//                    MediaStore.Audio.Media.RELATIVE_PATH
//            };
//            String selection = "";
//            String[] selectionArgs = null;
//            String sortOrder = "";
//            Cursor cursor = SearchService.this.getContentResolver().query(
//                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    projection,
//                    selection,
//                    selectionArgs,
//                    sortOrder
//                    );
//            while (cursor.moveToNext()) {
//
//            }
//

            File musicFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String musicFolderPath = musicFolder.getAbsolutePath();
            File searchStart = new File(musicFolderPath);

            if (!searchStart.exists() && !searchStart.isDirectory()){
                return;
            }

            final HashMap<String, List<String>> music = new HashMap<>();
            iterateFiles(searchStart.listFiles(), music);

            for (String albumPath: music.keySet()){
                ArrayList<MusicUtils.SongInfo> songsInfo = new ArrayList<>();
                List<String> songsFileName = music.get(albumPath);
                for (String songFileName: songsFileName){
                    String songPath = albumPath + '/' + songFileName;
                    MusicUtils.SongInfo songInfo = MusicUtils.extractSongInfo(songPath);
                    songInfo.lyrics = new SearchLyricUtils().doInBackground(songInfo.artist,songInfo.title);
                    songInfo.language = new SearchLanguageUtils().doInBackground(songInfo.artist,songInfo.title);
                    songInfo.cover = new SearchCoverUtils().doInBackground(songInfo.artist, songInfo.title);
                    if (songInfo.cover.isEmpty()){
//            songInfo.cover = String.valueOf(R.drawable.unknown_album_cover);
                    }
                    songsInfo.add(songInfo);
                }

                MusicUtils.SongInfo firstSongInfo = songsInfo.get(0);

                String albumName = firstSongInfo.album;
                String albumArtist = firstSongInfo.artist;

                String albumCover = firstSongInfo.cover;//MusicUtils.getNextCover();
                Album album = new Album(albumName, albumArtist, albumPath, albumCover);
                ArrayList<Song> songs = new ArrayList<>();
                for (int i = 0; i < songsInfo.size(); ++i){
                    MusicUtils.SongInfo songInfo = songsInfo.get(i);
                    String songName = songInfo.title;
                    String songPath = albumPath + '/' + songsFileName.get(i);
                    String songLyrics = songInfo.lyrics;
                    String songLanguage = songInfo.language;
                    String songYear = songInfo.year;
                    String songDate = songInfo.date;
                    Song song = new Song(songName, songPath, albumCover, songInfo.duration,
                            album.getId(), songLyrics, songYear, songDate, songLanguage);
                    songs.add(song);
                }
                mRepository.saveAlbum(album, songs);
                sendBroadcast(new Intent(TrackListFragment.UPDATE_SONG_BROADCAST));
            }
            isSearchActive = false;
            stopSelf();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "SearchService onDestroy");
    }

    private static class SearchCoverUtils extends AsyncTask<String, Void, String> {

        @Override
        public String doInBackground(String... params) {
            String urlAlbumCover = getUrlAlbumCover(params[0], params[1]);
            Log.d("myLogs", urlAlbumCover);
            return urlAlbumCover;
        }
    }

    private static class SearchLanguageUtils extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... params) {
            String language = getLanguage(params[0], params[1]);
            Log.d("myLogs", language);
            return language;
        }
    }

    private static class SearchLyricUtils extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... params) {
            String lyrics = getLyrics(params[0],params[1]);
            Log.d("myLogs",lyrics);
            return lyrics;
        }
    }

    private static String getUrlAlbumCover(String artist, String song){
        String query = "http://api.musixmatch.com/ws/1.1/matcher.track.get?"
                + "q_artist="
                + artist.replace(" ", "%20").toLowerCase()
                + "&q_track="
                + song.replace(" ", "%20").toLowerCase()
                + "&apikey=53f81a46c21c1a24961ef593d74dd870"
                + "&format=xml";
        String searchResult = "";
        try {
            Document doc = Jsoup.connect(query).get();
            Elements parts = doc.select("track_share_url");
            if (parts.size() > 0)
                doc = Jsoup.connect(parts.text()).get();
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
            parts = doc.select("meta[content$=.jpg]");
            if (parts.size() > 0)
                searchResult = parts.first().attr("content");
            Log.d("NETWORK LOG", searchResult);
        } catch (IOException e){
            e.printStackTrace();
        }
        return searchResult;
    }

    private static String getLyrics(String artist, String song) {
        // construct the REST query URL
        String query = "http://api.musixmatch.com/ws/1.1/matcher.track.get?"
                + "q_artist="
                + artist.replace(" ", "%20").toLowerCase()
                + "&q_track="
                + song.replace(" ", "%20").toLowerCase()
                + "&apikey=53f81a46c21c1a24961ef593d74dd870"
                + "&format=xml";

        // open the URL and get a stream to read from
        String searchResult = "";//getHtmlByQuery(query);
        try {
            Document doc = Jsoup.connect(query).get();
            Elements parts = doc.select("track_share_url");
            if (parts.size() > 0)
                doc = Jsoup.connect(parts.text()).get();
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
            parts = doc.select(".mxm-lyrics__content");
            searchResult = parts.html();
            Log.d("NETWORK LOG", searchResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    private static String getLanguage(String artist, String song) {
        // construct the REST query URL
        String query = "http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?" +
                "q_artist="
                + artist
                + "&q_track="
                + song
                + "&apikey=53f81a46c21c1a24961ef593d74dd870"
                + "&format=xml";
        // open the URL and get a stream to read from
        String searchResult = "";//getHtmlByQuery(query);
        try {
            Document doc = Jsoup.connect(query).get();
            Elements parts = doc.select("lyrics_language_description");
            searchResult = parts.text();
        } catch (IOException e){
            e.printStackTrace();
        }
        return searchResult;
    }
}