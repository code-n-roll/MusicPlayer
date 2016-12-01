package com.romankaranchuk.musicplayer.utils;

import android.media.MediaMetadataRetriever;

import com.romankaranchuk.musicplayer.data.Song;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by NotePad.by on 24.11.2016.
 */

public final class MusicUtils {

    private static final String mCovers[] = {};
    private static int mCurrentCover = 0;

    private static final int MAX_RECENT_SONGS_SIZE = 10;
    private static final LinkedList<Song> mRecentSongs = new LinkedList<>();


    public static String getNextCover(){
        mCurrentCover = mCurrentCover % mCovers.length;
        return mCovers[mCurrentCover++];
    }

    public static void addToRecent(Song song){
        if (mRecentSongs.contains(song)){
            mRecentSongs.remove(song);
        }

        mRecentSongs.addFirst(song);

        if (mRecentSongs.size() > MAX_RECENT_SONGS_SIZE){
            mRecentSongs.removeLast();
        }
    }

    public static List<Song> getRecent(){
        return mRecentSongs;
    }


    public static class SongInfo {
        public String title;
        public String artist;
        public String album;
        public int duration;
    }



    private static SongInfo getEmptySongInfo(){
        SongInfo emptyInfo = new SongInfo();
        emptyInfo.title = "Unknown title";
        emptyInfo.artist = "Unknown artist";
        emptyInfo.album = "Unknown album";
        emptyInfo.duration = 0;
        return emptyInfo;
    }

    public static SongInfo extractSongInfo(String songPath){
        FileInputStream inputStream;
        try{
            inputStream = new FileInputStream(songPath);
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return getEmptySongInfo();
        }
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        try{
            metaRetriever.setDataSource(inputStream.getFD());
        } catch (Exception e){
            e.printStackTrace();
            return getEmptySongInfo();
        }
        SongInfo songInfo = new SongInfo();

        songInfo.artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        songInfo.title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        songInfo.album = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        songInfo.duration = Integer.parseInt(duration);
        if (songInfo.artist == null){
            songInfo.artist = "Unknown artist";
        }
        if (songInfo.title == null){
            songInfo.title = "Unknown title";
        }
        return songInfo;
    }

}
