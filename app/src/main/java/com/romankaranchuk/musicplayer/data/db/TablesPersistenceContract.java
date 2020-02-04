package com.romankaranchuk.musicplayer.data.db;

import android.provider.BaseColumns;



final class TablesPersistenceContract {

    private TablesPersistenceContract(){}

    static abstract class AlbumEntry implements BaseColumns{
        static final String TABLE_NAME = "albums";
        static final String COLUMN_NAME_ENTRY_ID = "album_id";
        static final String COLUMN_NAME_ALBUM_NAME = "album_name";
        static final String COLUMN_NAME_ALBUM_ARTIST = "album_artist";
        static final String COLUMN_NAME_ALBUM_PATH = "album_path";
        static final String COLUMN_NAME_ALBUM_IMAGE = "album_image";
    }

    static abstract class SongEntry implements BaseColumns {
        static final String TABLE_NAME = "songs";
        static final String COLUMN_NAME_ENTRY_ID = "song_id";
        static final String COLUMN_NAME_ALBUM_ID = "album_id";
        static final String COLUMN_NAME_SONG_NAME = "song_name";
        static final String COLUMN_NAME_SONG_PATH = "song_path";
        static final String COLUMN_NAME_SONG_DURATION = "song_duration";
        static final String COLUMN_NAME_SONG_IMAGE = "song_image";
        static final String COLUMN_NAME_SONG_LYRICS = "song_lyrics";
        static final String COLUMN_NAME_SONG_YEAR = "song_year";
        static final String COLUMN_NAME_SONG_DATE = "song_date";
        static final String COLUMN_NAME_SONG_LANGUAGE = "song_language";
    }
}
