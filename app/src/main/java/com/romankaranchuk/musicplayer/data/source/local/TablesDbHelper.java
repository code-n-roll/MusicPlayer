package com.romankaranchuk.musicplayer.data.source.local;/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.romankaranchuk.musicplayer.data.source.local.TablesPersistenceContract.SongEntry;
import com.romankaranchuk.musicplayer.data.source.local.TablesPersistenceContract.AlbumEntry;
/**
 * Created by NotePad.by on 27.11.2016.
 */

public class TablesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "music.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE_ALBUMS =
            "CREATE TABLE " + AlbumEntry.TABLE_NAME + " (" +
                    AlbumEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                    AlbumEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    AlbumEntry.COLUMN_NAME_ALBUM_NAME + TEXT_TYPE + COMMA_SEP +
                    AlbumEntry.COLUMN_NAME_ALBUM_ARTIST + TEXT_TYPE + COMMA_SEP +
                    AlbumEntry.COLUMN_NAME_ALBUM_IMAGE + TEXT_TYPE + COMMA_SEP +
                    AlbumEntry.COLUMN_NAME_ALBUM_PATH + TEXT_TYPE +
            " );";
    private static final String SQL_CREATE_TABLE_SONGS =
            "CREATE TABLE " + SongEntry.TABLE_NAME + " (" +
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
            " );";

    TablesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_TABLE_SONGS);
        db.execSQL(SQL_CREATE_TABLE_ALBUMS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + SongEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlbumEntry.TABLE_NAME);
        db.execSQL(SQL_CREATE_TABLE_SONGS);
        db.execSQL(SQL_CREATE_TABLE_ALBUMS);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Not required as at version 1
    }
}
