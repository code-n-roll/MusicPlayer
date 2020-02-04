package com.romankaranchuk.musicplayer.data.db;

import androidx.annotation.NonNull;

import com.romankaranchuk.musicplayer.data.Album;
import com.romankaranchuk.musicplayer.data.Song;

import java.util.List;

public interface MusicRepository {

    boolean saveAlbum(@NonNull Album album, @NonNull List<Song> songs);

    void saveSongs(@NonNull List<Song> songs);

    void deleteAlbum(@NonNull Album album);

    void deleteSong(@NonNull Song song);

    @NonNull
    List<Album> getAlbums();

    @NonNull
    List<Song> getSongs(@NonNull String albumId, boolean sortByName);
}
