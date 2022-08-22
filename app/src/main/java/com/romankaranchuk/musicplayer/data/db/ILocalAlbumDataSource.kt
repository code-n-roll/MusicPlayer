package com.romankaranchuk.musicplayer.data.db

import androidx.room.*
import com.romankaranchuk.musicplayer.data.Album
import com.romankaranchuk.musicplayer.data.Song
import java.util.ArrayList

@Dao
interface ILocalAlbumDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAlbum(album: Album, songs: List<Song>)

    @Delete
    fun deleteAlbum(album: Album)

    @Query("SELECT * FROM albums")
    fun getAlbums(): List<Album>
}