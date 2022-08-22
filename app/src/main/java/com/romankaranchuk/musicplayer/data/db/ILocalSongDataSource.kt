package com.romankaranchuk.musicplayer.data.db

import androidx.room.*
import com.romankaranchuk.musicplayer.data.Song

@Dao
interface ILocalSongDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSongs(songs: List<Song>)

    @Delete
    fun deleteSong(song: Song)

    @Query("DELETE FROM songs WHERE album_id = :albumId")
    fun deleteSongs(albumId: String)

    @Query("SELECT * FROM songs " +
            "WHERE album_id = :albumId " +
            "ORDER BY CASE WHEN :sortByName = 1 THEN song_name ELSE song_duration END")
    fun getSongs(albumId: String, sortByName: Boolean): List<Song>

    @Query("SELECT * FROM songs " +
            "WHERE song_id = :songId ")
    fun getSong(songId: String): Song

    @Query("SELECT song_duration FROM albums JOIN songs ON albums.album_id = songs.album_id")
    fun printAllSongs(): List<Int>
}