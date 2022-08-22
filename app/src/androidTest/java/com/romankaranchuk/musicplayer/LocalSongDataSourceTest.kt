package com.romankaranchuk.musicplayer

import androidx.room.Room
import com.romankaranchuk.musicplayer.data.db.AppDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.data.db.LocalSongDataSource
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalSongDataSourceTest {

    companion object {
        private val SONGS = listOf(
            Song(
                name = "song1",
                path = "",
                imagePath = "",
                duration = 0,
                albumId = "1",
                lyricsSong = "",
                year = "",
                dateModified = "",
                language = ""
            ),
            Song(
                name = "song2",
                path = "",
                imagePath = "",
                duration = 0,
                albumId = "1",
                lyricsSong = "",
                year = "",
                dateModified = "",
                language = ""
            ),
        )

        private lateinit var database: AppDatabase
        private lateinit var dataSource: LocalSongDataSource

        @AfterClass
        @JvmStatic
        fun closeDb() {
            database.close()
        }

        @BeforeClass
        @JvmStatic
        fun initDb() {
            database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
            dataSource = LocalSongDataSource.getInstance(database)
        }
    }

    @Test
    fun insertAndGetSong_positive() {
        dataSource.saveSongs(SONGS)

        val dbSongs = dataSource.getSongs("1", false)
        assertEquals(SONGS.toTypedArray().contentToString(), dbSongs.toTypedArray().contentToString())
    }

    @Test
    fun insertAndGetSong_negative() {
        dataSource.saveSongs(SONGS)

        val dbSongs = dataSource.getSongs("0", false)
        assertNotEquals(SONGS.toTypedArray().contentToString(), dbSongs.toTypedArray().contentToString())
    }
}