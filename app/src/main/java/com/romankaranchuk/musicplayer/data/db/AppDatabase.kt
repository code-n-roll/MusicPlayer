package com.romankaranchuk.musicplayer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.romankaranchuk.musicplayer.data.Album
import com.romankaranchuk.musicplayer.data.Song

@Database(entities = [
    Song::class,
    Album::class
], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun albumDao(): ILocalAlbumDataSource
    abstract fun songDao(): ILocalSongDataSource
}