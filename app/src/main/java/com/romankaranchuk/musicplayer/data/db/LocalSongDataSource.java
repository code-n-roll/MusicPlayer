package com.romankaranchuk.musicplayer.data.db;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.romankaranchuk.musicplayer.data.Song;

import java.util.List;

// TODO() clear comment out code
public class LocalSongDataSource implements ILocalSongDataSource {

    private static LocalSongDataSource INSTANCE;

    private final SupportSQLiteOpenHelper mDbHelper;

    private final ILocalSongDataSource songDao;

    private LocalSongDataSource(@NonNull AppDatabase appDatabase) {
        mDbHelper = appDatabase.getOpenHelper();
        songDao = appDatabase.songDao();
    }

    public static LocalSongDataSource getInstance(@NonNull AppDatabase appDatabase){
        if (INSTANCE == null){
            INSTANCE = new LocalSongDataSource(appDatabase);
        }
        return INSTANCE;
    }

//    private boolean isEntryExist(SupportSQLiteDatabase db, String tableName, String fieldName, String entryId){
//        Cursor c = null;
//        try{
//            String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + fieldName + " = ?";
//            c = db.query(query, new String[]{entryId});
//            return c.moveToFirst() && c.getInt(0) != 0;
//        }
//        finally {
//            if (c != null){
//                c.close();
//            }
//        }
//    }

    @Override
    public void saveSongs(@NonNull List<Song> songs) {
        songDao.saveSongs(songs);

//        SupportSQLiteDatabase db = mDbHelper.getWritableDatabase();
//        for (Song song : songs) {
//            if (isEntryExist(db, SongEntry.TABLE_NAME, SongEntry.COLUMN_NAME_SONG_PATH, song.getPath())) {
//                return;
//            }
//
//
//            ContentValues values = new ContentValues();
//            values.put(SongEntry.COLUMN_NAME_ENTRY_ID, song.getId());
//            values.put(SongEntry.COLUMN_NAME_ALBUM_ID, song.getAlbumId());
//            values.put(SongEntry.COLUMN_NAME_SONG_NAME, song.getName());
//            values.put(SongEntry.COLUMN_NAME_SONG_PATH, song.getPath());
//            values.put(SongEntry.COLUMN_NAME_SONG_DURATION, song.getDuration());
//            values.put(SongEntry.COLUMN_NAME_SONG_IMAGE, song.getImagePath());
//            values.put(SongEntry.COLUMN_NAME_SONG_LYRICS, song.getLyricsSong());
//            values.put(SongEntry.COLUMN_NAME_SONG_YEAR, song.getYear());
//            values.put(SongEntry.COLUMN_NAME_SONG_DATE, song.getDate());
//            values.put(SongEntry.COLUMN_NAME_SONG_LANGUAGE, song.getLanguage());
//            db.insert(SongEntry.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, values);
//        }
//        try {
//            db.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void deleteSong(@NonNull Song song) {
        songDao.deleteSong(song);

//        SupportSQLiteDatabase db = mDbHelper.getWritableDatabase();
//        String selection = SongEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
//        String[] selectionArgs = {song.getId()};
//        db.delete(SongEntry.TABLE_NAME, selection, selectionArgs);
//        try {
//            db.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @NonNull
    @Override
    public List<Song> getSongs(@NonNull String albumId, boolean sortByName) {
        return songDao.getSongs(albumId, sortByName);

//        SupportSQLiteDatabase db = mDbHelper.getReadableDatabase();
//        List<Song> songs = new ArrayList<>();
//        String[] projection = {
//                SongEntry.COLUMN_NAME_ENTRY_ID,
//                SongEntry.COLUMN_NAME_SONG_NAME,
//                SongEntry.COLUMN_NAME_SONG_PATH,
//                SongEntry.COLUMN_NAME_SONG_DURATION,
//                SongEntry.COLUMN_NAME_SONG_IMAGE,
//                SongEntry.COLUMN_NAME_SONG_LYRICS,
//                SongEntry.COLUMN_NAME_SONG_YEAR,
//                SongEntry.COLUMN_NAME_SONG_DATE,
//                SongEntry.COLUMN_NAME_SONG_LANGUAGE
//        };
//
//        String selection = SongEntry.COLUMN_NAME_ALBUM_ID + " LIKE ?";
//        String[] selectionArgs = {albumId};
//
//        String sortBy;
//        if (sortByName){
//            sortBy = SongEntry.COLUMN_NAME_SONG_NAME;
//        } else {
//            sortBy = SongEntry.COLUMN_NAME_SONG_DURATION;
//        }
//
//        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder(SongEntry.TABLE_NAME)
//                .columns(projection)
//                .selection(selection, selectionArgs)
//                .orderBy(sortBy)
//                .create();
//        Cursor c = db.query(query);//db.query(SongEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortBy);
//
//        if (c != null && c.getCount() > 0){
//            while (c.moveToNext()){
//                String songId = c.getString(c.getColumnIndexOrThrow(SongEntry.COLUMN_NAME_ENTRY_ID));
//                String songName = c.getString(c.getColumnIndexOrThrow(SongEntry.COLUMN_NAME_SONG_NAME));
//                String songPath = c.getString(c.getColumnIndexOrThrow(SongEntry.COLUMN_NAME_SONG_PATH));
//                int songDuration = c.getInt(c.getColumnIndexOrThrow(SongEntry.COLUMN_NAME_SONG_DURATION));
//                String songImagePath = c.getString(c.getColumnIndexOrThrow(SongEntry.COLUMN_NAME_SONG_IMAGE));
//                String songLyrics = c.getString(c.getColumnIndexOrThrow(SongEntry.COLUMN_NAME_SONG_LYRICS));
//                String songYear = c.getString(c.getColumnIndexOrThrow(SongEntry.COLUMN_NAME_SONG_YEAR));
//                String songDate = c.getString(c.getColumnIndexOrThrow(SongEntry.COLUMN_NAME_SONG_DATE));
//                String songLanguage = c.getString(c.getColumnIndexOrThrow(SongEntry.COLUMN_NAME_SONG_LANGUAGE));
//
//                if (songId == null) {
//                    continue;
//                }
//
//                Song song = new Song(songId, songName, songPath, songImagePath, songDuration,
//                        albumId, songLyrics, songYear, songDate, songLanguage);
//                songs.add(song);
//            }
//        }
//        if (c != null){
//            c.close();
//        }
//        try {
//            db.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return songs;
    }

    @Override
    public void deleteSongs(@NonNull String albumId) {
        songDao.deleteSongs(albumId);

//        SupportSQLiteDatabase db = mDbHelper.getWritableDatabase();
//        String selection = SongEntry.COLUMN_NAME_ALBUM_ID + " LIKE ?";
//        String[] selectionArgs = {albumId};
//        db.delete(SongEntry.TABLE_NAME, selection, selectionArgs);
//        try {
//            db.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public List<Integer> printAllSongs() {
        return songDao.printAllSongs();

//        SupportSQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//        String table = AlbumEntry.TABLE_NAME + " AS ALBUM INNER JOIN " + SongEntry.TABLE_NAME +" AS SONG "+
//                "ON ALBUM." + AlbumEntry.COLUMN_NAME_ENTRY_ID +" = SONG." + SongEntry.COLUMN_NAME_ALBUM_ID;
//        String columns[] = {
//                "ALBUM." + AlbumEntry.COLUMN_NAME_ALBUM_NAME +" AS ALBUM",
//                "ALBUM." + AlbumEntry.COLUMN_NAME_ALBUM_ARTIST +" AS ARTIST",
//                "SONG." + SongEntry.COLUMN_NAME_SONG_NAME + " AS NAME",
//                "SONG." + SongEntry.COLUMN_NAME_SONG_DURATION + " AS DURATION",
//                "SONG." + SongEntry.COLUMN_NAME_SONG_LYRICS + " AS LYRICS",
//                "SONG." + SongEntry.COLUMN_NAME_SONG_YEAR + " AS YEAR",
//                "SONG." + SongEntry.COLUMN_NAME_SONG_DATE + " AS DATE",
//                "SONG." + SongEntry.COLUMN_NAME_SONG_LANGUAGE + " AS LANGUAGE "
//        };
//        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder(table)
//                .columns(columns)
//                .create();
//        Cursor c = db.query(query);//db.query(table, columns, null, null, null, null, null);
//        ArrayList<Integer> durations = Util.logCursor(c);
//
//        if (c!= null){
//            c.close();
//        }
//        try {
//            db.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return durations;
    }

    @NonNull
    @Override
    public Song getSong(@NonNull String songId) {
        Song song =  songDao.getSong(songId);
        return song;
    }
}
