package com.romankaranchuk.musicplayer.data.source;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.romankaranchuk.musicplayer.data.Album;
import com.romankaranchuk.musicplayer.data.Song;

/**
 * Created by NotePad.by on 25.11.2016.
 */

public interface MusicDataSource {
    boolean saveAlbum(@NonNull Album album, @NonNull List<Song> songs);

    void saveSongs(@NonNull List<Song> songs);

    void deleteAlbum(@NonNull Album album);

    void deleteSong(@NonNull Song song);

    @NonNull
    List<Album> getAlbums();

    @NonNull
    List<Song> getSongs(@NonNull String albumId, boolean sortByName);

    ArrayList<Integer> printAllSongs();
}
