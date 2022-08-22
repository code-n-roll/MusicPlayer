package com.romankaranchuk.musicplayer.data.db

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import com.romankaranchuk.musicplayer.data.db.SQLiteOpenHelperImpl
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.romankaranchuk.musicplayer.data.db.TablesPersistenceContract.SongEntry
import com.romankaranchuk.musicplayer.data.db.TablesPersistenceContract.AlbumEntry


class SQLiteOpenHelperImpl internal constructor(
    context: Context?
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_SONGS)
        db.execSQL(SQL_CREATE_TABLE_ALBUMS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + SongEntry.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + AlbumEntry.TABLE_NAME)
        db.execSQL(SQL_CREATE_TABLE_SONGS)
        db.execSQL(SQL_CREATE_TABLE_ALBUMS)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Not required as at version 1
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "music.db"
        private const val TEXT_TYPE = " TEXT"
        private const val INTEGER_TYPE = " INTEGER"
        private const val COMMA_SEP = ","
        private val SQL_CREATE_TABLE_ALBUMS = "CREATE TABLE " + AlbumEntry.TABLE_NAME + " (" +
                AlbumEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                AlbumEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                AlbumEntry.COLUMN_NAME_ALBUM_NAME + TEXT_TYPE + COMMA_SEP +
                AlbumEntry.COLUMN_NAME_ALBUM_ARTIST + TEXT_TYPE + COMMA_SEP +
                AlbumEntry.COLUMN_NAME_ALBUM_IMAGE + TEXT_TYPE + COMMA_SEP +
                AlbumEntry.COLUMN_NAME_ALBUM_PATH + TEXT_TYPE +
                " );"
        private val SQL_CREATE_TABLE_SONGS =  "CREATE TABLE " + SongEntry.TABLE_NAME + " (" +
                SongEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                SongEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                SongEntry.COLUMN_NAME_ALBUM_ID + TEXT_TYPE + COMMA_SEP +
                SongEntry.COLUMN_NAME_SONG_NAME + TEXT_TYPE + COMMA_SEP +
                SongEntry.COLUMN_NAME_SONG_IMAGE + TEXT_TYPE + COMMA_SEP +
                SongEntry.COLUMN_NAME_SONG_DURATION + INTEGER_TYPE + COMMA_SEP +
                SongEntry.COLUMN_NAME_SONG_PATH + TEXT_TYPE + COMMA_SEP +
                SongEntry.COLUMN_NAME_SONG_LYRICS + TEXT_TYPE + COMMA_SEP +
                SongEntry.COLUMN_NAME_SONG_YEAR + TEXT_TYPE + COMMA_SEP +
                SongEntry.COLUMN_NAME_SONG_DATE + TEXT_TYPE + COMMA_SEP +
                SongEntry.COLUMN_NAME_SONG_LANGUAGE + TEXT_TYPE +
                " );"

        fun getSqlCreateTableSongsNEW(tableName: String): String {
            return "CREATE TABLE " + tableName + " (" +
                    SongEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + " PRIMARY KEY NOT NULL " + COMMA_SEP +
                    SongEntry.COLUMN_NAME_ALBUM_ID + TEXT_TYPE + " NOT NULL " + COMMA_SEP +
                    SongEntry.COLUMN_NAME_SONG_NAME + TEXT_TYPE + COMMA_SEP +
                    SongEntry.COLUMN_NAME_SONG_IMAGE + TEXT_TYPE + COMMA_SEP +
                    SongEntry.COLUMN_NAME_SONG_DURATION + INTEGER_TYPE + COMMA_SEP +
                    SongEntry.COLUMN_NAME_SONG_PATH + TEXT_TYPE + COMMA_SEP +
                    SongEntry.COLUMN_NAME_SONG_LYRICS + TEXT_TYPE + COMMA_SEP +
                    SongEntry.COLUMN_NAME_SONG_YEAR + TEXT_TYPE + COMMA_SEP +
                    SongEntry.COLUMN_NAME_SONG_DATE + TEXT_TYPE + COMMA_SEP +
                    SongEntry.COLUMN_NAME_SONG_LANGUAGE + TEXT_TYPE +
                    " );"
        }

        fun getSqlCreateTableAlbumsNEW(tableName: String): String {
            return "CREATE TABLE " + tableName + " (" +
                    AlbumEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + " PRIMARY KEY NOT NULL " + COMMA_SEP +
                    AlbumEntry.COLUMN_NAME_ALBUM_NAME + TEXT_TYPE + COMMA_SEP +
                    AlbumEntry.COLUMN_NAME_ALBUM_ARTIST + TEXT_TYPE + COMMA_SEP +
                    AlbumEntry.COLUMN_NAME_ALBUM_IMAGE + TEXT_TYPE + COMMA_SEP +
                    AlbumEntry.COLUMN_NAME_ALBUM_PATH + TEXT_TYPE +
                    " );"
        }
    }
}