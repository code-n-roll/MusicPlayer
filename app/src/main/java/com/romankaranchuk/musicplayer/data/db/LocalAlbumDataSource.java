package com.romankaranchuk.musicplayer.data.db;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.romankaranchuk.musicplayer.data.Album;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.data.db.TablesPersistenceContract.SongEntry;

import java.io.IOException;
import java.util.List;

public class LocalAlbumDataSource implements ILocalAlbumDataSource {

    private static final String LOG_TAG = "DB log";
    private static LocalAlbumDataSource INSTANCE;

    private final SupportSQLiteOpenHelper mDbHelper;
    private final ILocalAlbumDataSource albumDao;

    private LocalAlbumDataSource(@NonNull AppDatabase appDatabase) {
        mDbHelper = appDatabase.getOpenHelper();
        albumDao = appDatabase.albumDao();
    }

    public static LocalAlbumDataSource getInstance(@NonNull AppDatabase appDatabase){
        if (INSTANCE == null){
            INSTANCE = new LocalAlbumDataSource(appDatabase);
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
    public void saveAlbum(@NonNull Album album, @NonNull List<Song> songs) {
        albumDao.saveAlbum(album, songs);

//        SupportSQLiteDatabase db = mDbHelper.getWritableDatabase();
//        if (isEntryExist(db, AlbumEntry.TABLE_NAME, AlbumEntry.COLUMN_NAME_ALBUM_PATH, album.getPath())) {
//            return;
//        }
//
//        ContentValues values = new ContentValues();
//        values.put(AlbumEntry.COLUMN_NAME_ENTRY_ID, album.getId());
//        values.put(AlbumEntry.COLUMN_NAME_ALBUM_NAME, album.getName());
//        values.put(AlbumEntry.COLUMN_NAME_ALBUM_ARTIST, album.getArtist());
//        values.put(AlbumEntry.COLUMN_NAME_ALBUM_PATH, album.getPath());
//        values.put(AlbumEntry.COLUMN_NAME_ALBUM_IMAGE, album.getImagePath());
//
//        db.insert(AlbumEntry.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, values);
//        try {
//            db.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void deleteAlbum(@NonNull Album album) {
        albumDao.deleteAlbum(album);


//        SupportSQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        String selection = AlbumEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
//        String[] selectionArgs = {album.getId()};
//        db.delete(AlbumEntry.TABLE_NAME, selection, selectionArgs);
//        try {
//            db.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        removeAlbumSongs(album);
    }

    // TODO() songs related to deleted album should be deleted automatically as well
    private void removeAlbumSongs(@NonNull Album album) {
        SupportSQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selection = SongEntry.COLUMN_NAME_ALBUM_ID + " LIKE ?";
        String[] selectionArgs = {album.getId()};
        db.delete(SongEntry.TABLE_NAME, selection, selectionArgs);
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public List<Album> getAlbums() {
        return albumDao.getAlbums();

//        SupportSQLiteDatabase db = mDbHelper.getReadableDatabase();
//        List<Album> tasks = new ArrayList<>();
//        String[] projection = {
//                AlbumEntry.COLUMN_NAME_ENTRY_ID,
//                AlbumEntry.COLUMN_NAME_ALBUM_NAME,
//                AlbumEntry.COLUMN_NAME_ALBUM_ARTIST,
//                AlbumEntry.COLUMN_NAME_ALBUM_PATH,
//                AlbumEntry.COLUMN_NAME_ALBUM_IMAGE
//        };
//
//        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder(AlbumEntry.TABLE_NAME)
//                .columns(projection)
//                .create();
//        Cursor c = db.query(query);//db.query(AlbumEntry.TABLE_NAME, projection, null, null, null, null, null);
//
//        if (c != null && c.getCount() > 0){
//            while(c.moveToNext()){
//                String albumId =
//                        c.getString(c.getColumnIndexOrThrow(AlbumEntry.COLUMN_NAME_ENTRY_ID));
//                String albumName =
//                        c.getString(c.getColumnIndexOrThrow(AlbumEntry.COLUMN_NAME_ALBUM_NAME));
//                String albumArtist =
//                        c.getString(c.getColumnIndexOrThrow(AlbumEntry.COLUMN_NAME_ALBUM_ARTIST));
//                String albumPath =
//                        c.getString(c.getColumnIndexOrThrow(AlbumEntry.COLUMN_NAME_ALBUM_PATH));
//                String albumImagePath =
//                        c.getString(c.getColumnIndexOrThrow(AlbumEntry.COLUMN_NAME_ALBUM_IMAGE));
//                Album album = new Album(albumId, albumName, albumArtist, albumPath, albumImagePath);
//                tasks.add(album);
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
//        return tasks;
    }
}
