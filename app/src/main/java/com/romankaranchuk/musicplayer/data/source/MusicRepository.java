package com.romankaranchuk.musicplayer.data.source;

import android.support.annotation.NonNull;

import com.romankaranchuk.musicplayer.data.Album;
import com.romankaranchuk.musicplayer.data.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by NotePad.by on 25.11.2016.
 */

public class MusicRepository implements MusicDataSource{

    private static MusicRepository INSTANCE = null;

    private final MusicDataSource mTablesLocalDataSource;

    private List<AlbumsRepositoryObserver> mObservers = new ArrayList<>();

    private Map<String, Album> mCachedAlbums;
    private Map<String, Song> mCachedSongs;

    private boolean mCacheAlbumsIsDirty = true;
    private boolean mCacheSongsIsDirty = true;

    public static MusicRepository getInstance(MusicDataSource musicLocalDataSource){
        if (INSTANCE == null){
            INSTANCE = new MusicRepository(musicLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    private MusicRepository(@NonNull MusicDataSource tasksLocalDataSource){
        mTablesLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    public void addContentObserver(AlbumsRepositoryObserver observer){
        if (!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    public void removeContentObserver(AlbumsRepositoryObserver observer){
        if (mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }

    private List<Album> getCachedAlbums(){
        return mCachedAlbums == null ? new ArrayList<Album>() : new ArrayList<>(mCachedAlbums.values());
    }

    private List<Song> getCachedSongs(@NonNull String albumId, boolean sortByName){
        Comparator<Song> comparator;
        if (sortByName){
            comparator = new Comparator<Song>() {
                @Override
                public int compare(Song song1, Song song2) {
                    return song1.getName().compareTo(song2.getName());
                }
            };
        } else {
            comparator = new Comparator<Song>() {
                @Override
                public int compare(Song song1, Song song2) {
                    return song1.getDuration() - song2.getDuration();
                }
            };
        }

        if (mCachedSongs == null){
            return new ArrayList<>();
        }

        ArrayList<Song> songs = new ArrayList<>();
        for (Song song: mCachedSongs.values()){
            if (song.getAlbumId().equals(albumId)){
                songs.add(song);
            }
        }

        Collections.sort(songs, comparator);
        return songs;
    }

    @Override
    public boolean saveAlbum(@NonNull Album album, @NonNull List<Song> songs) {
        saveSongs(songs);

        if (mCachedAlbums == null){
            mCachedAlbums = new LinkedHashMap<>();
        }

        boolean successState = mTablesLocalDataSource.saveAlbum(album, songs);
        if (successState){
            mCachedAlbums.put(album.getId(), album);
            notifyAlbumsChanged();
        }
        return successState;
    }

    @Override
    public void saveSongs(@NonNull List<Song> songs) {
        mTablesLocalDataSource.saveSongs(songs);

        if (mCachedSongs == null){
            mCachedSongs = new LinkedHashMap<>();
        }
        for (Song song: songs){
            mCachedSongs.put(song.getId(), song);
        }
    }

    @Override
    public void deleteAlbum(@NonNull Album album) {
        mTablesLocalDataSource.deleteAlbum(album);

        if (mCachedAlbums != null){
            if (mCachedAlbums.containsKey(album.getId())){
                mCachedAlbums.remove(album.getId());
            }
        }

        if (mCachedSongs != null){
            for (Song song: mCachedSongs.values()){
                if (album.getId().equals(song.getAlbumId())){
                    mCachedSongs.remove(song.getId());
                }
            }
        }

        notifyAlbumsChanged();
    }

    @Override
    public void deleteSong(@NonNull Song song) {
        mTablesLocalDataSource.deleteSong(song);

        if (mCachedSongs != null){
            if (mCachedSongs.containsKey(song.getId())){
                mCachedSongs.remove(song.getId());
            }
        }
    }

    public Album getAlbum(String albumId){
        if (mCachedAlbums == null){
            getAlbums();
        }

        return mCachedAlbums.get(albumId);
    }

    public Song getSong(String songId, String albumId, boolean sortByName){
        if (mCachedSongs == null){
            getSongs(albumId, sortByName);
        }

        return mCachedSongs.get(songId);
    }

    @NonNull
    @Override
    public List<Album> getAlbums() {
        if (!mCacheAlbumsIsDirty){
            return getCachedAlbums();
        } else {
            List<Album> albums = mTablesLocalDataSource.getAlbums();

            mCachedAlbums = new LinkedHashMap<>();
            for (Album album: albums){
                mCachedAlbums.put(album.getId(), album);
            }
            mCacheAlbumsIsDirty = false;

            return albums;
        }
    }

    @NonNull
    @Override
    public List<Song> getSongs(@NonNull String albumId, boolean sortByName) {
        if (!mCacheSongsIsDirty){
            return getCachedSongs(albumId, sortByName);
        } else {
            List<Album> albums = mTablesLocalDataSource.getAlbums();
            mCachedSongs = new LinkedHashMap<>();
            for (Album nextAlbum: albums){
                for (Song nextSong: mTablesLocalDataSource.getSongs(nextAlbum.getId(), sortByName)){
                    mCachedSongs.put(nextSong.getId(), nextSong);
                }
            }

            mCacheSongsIsDirty = false;
            return getCachedSongs(albumId, sortByName);
        }
    }

    @Override
    public ArrayList<Integer> printAllSongs() {
        return mTablesLocalDataSource.printAllSongs();
    }

    private void notifyAlbumsChanged(){
        for (AlbumsRepositoryObserver observer: mObservers){
            observer.onAlbumsChanged();
        }
    }

    public interface AlbumsRepositoryObserver {
        void onAlbumsChanged();
    }
}
