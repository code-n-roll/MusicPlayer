package com.romankaranchuk.musicplayer

import androidx.room.Room
import com.romankaranchuk.musicplayer.data.db.AppDatabase
import com.romankaranchuk.musicplayer.data.db.LocalAlbumDataSource
import org.junit.Before
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Test

class LocalAlbumDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var localAlbumDataSource: LocalAlbumDataSource

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),AppDatabase::class.java).build()
        localAlbumDataSource = LocalAlbumDataSource.getInstance(database)
    }

    @After
    fun closeDb() {
        database.close()
    }


}