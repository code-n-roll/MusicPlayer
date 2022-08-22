package com.romankaranchuk.musicplayer.data.db

import android.provider.BaseColumns

open class KBaseColumns {
    val _ID = "_id"
}

internal class TablesPersistenceContract private constructor() {
    internal class AlbumEntry private constructor(): BaseColumns {
        companion object : KBaseColumns() {
            const val TABLE_NAME = "albums"
            const val COLUMN_NAME_ENTRY_ID = "album_id"
            const val COLUMN_NAME_ALBUM_NAME = "album_name"
            const val COLUMN_NAME_ALBUM_ARTIST = "album_artist"
            const val COLUMN_NAME_ALBUM_PATH = "album_path"
            const val COLUMN_NAME_ALBUM_IMAGE = "album_image"
        }
    }

    internal class SongEntry : BaseColumns {
        companion object : KBaseColumns() {
            const val TABLE_NAME = "songs"
            const val COLUMN_NAME_ENTRY_ID = "song_id"
            const val COLUMN_NAME_ALBUM_ID = "album_id"
            const val COLUMN_NAME_SONG_NAME = "song_name"
            const val COLUMN_NAME_SONG_PATH = "song_path"
            const val COLUMN_NAME_SONG_DURATION = "song_duration"
            const val COLUMN_NAME_SONG_IMAGE = "song_image"
            const val COLUMN_NAME_SONG_LYRICS = "song_lyrics"
            const val COLUMN_NAME_SONG_YEAR = "song_year"
            const val COLUMN_NAME_SONG_DATE = "song_date"
            const val COLUMN_NAME_SONG_LANGUAGE = "song_language"
        }
    }
}