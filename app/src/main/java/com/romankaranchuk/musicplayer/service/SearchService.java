package com.romankaranchuk.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.romankaranchuk.musicplayer.data.Album;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.data.source.MusicDataSource;
import com.romankaranchuk.musicplayer.data.source.MusicRepository;
import com.romankaranchuk.musicplayer.data.source.local.MusicLocalDataSource;
import com.romankaranchuk.musicplayer.utils.MusicUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by NotePad.by on 24.11.2016.
 */

public class SearchService extends Service {
    String LOG_TAG = "myLogs";
    private final SearchBinder mBinder = new SearchBinder();
    private final MusicRepository mRepository;
    private boolean isSearchActive = false;

    public SearchService(){
        MusicDataSource localDataSource = MusicLocalDataSource.getInstance(this);
        mRepository = MusicRepository.getInstance(localDataSource);
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
            File musicFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
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
                    MusicUtils.extractSongLyric(songInfo);
                    MusicUtils.extractSongLanguage(songInfo);
                    songsInfo.add(songInfo);
                }

                MusicUtils.SongInfo firstSongInfo = songsInfo.get(0);

                String albumName = firstSongInfo.album;
                String albumArtist = firstSongInfo.artist;
                String albumCover = MusicUtils.getNextCover();
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
                int oldRepSize = mRepository.getAlbums().size();
                mRepository.saveAlbum(album, songs);
                if (oldRepSize != mRepository.getAlbums().size())
                    sendBroadcast(new Intent("updateSongs"));
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
}