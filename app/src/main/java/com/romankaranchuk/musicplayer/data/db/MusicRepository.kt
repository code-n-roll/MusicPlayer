package com.romankaranchuk.musicplayer.data.db

import com.romankaranchuk.musicplayer.data.Album
import com.romankaranchuk.musicplayer.data.Song

interface MusicRepository {
    fun saveAlbum(album: Album, songs: List<Song>)
    fun saveSongs(songs: List<Song>)
    fun deleteAlbum(album: Album)
    fun deleteSong(song: Song)
    val albums: List<Album>
    fun getSongs(albumId: String, sortByName: Boolean): List<Song>
    fun getSong(songId: String): Song
}