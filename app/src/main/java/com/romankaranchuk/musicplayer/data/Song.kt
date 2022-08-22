package com.romankaranchuk.musicplayer.data

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.romankaranchuk.musicplayer.utils.MusicUtils
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "songs")
@Parcelize
data class Song (
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "song_id")
    var id: String,

    @ColumnInfo(name = "song_name")
    var name: String?,

    @ColumnInfo(name = "song_path")
    var path: String?,

    @ColumnInfo(name = "song_image")
    var imagePath: String?,

    @ColumnInfo(name = "song_duration")
    var duration: Int?,

    @ColumnInfo(name = "album_id")
    var albumId: String,

    @ColumnInfo(name = "song_lyrics")
    var lyricsSong: String?,

    @ColumnInfo(name = "song_year")
    var year: String?,

    @ColumnInfo(name = "song_date")
    var date: String?,

    @ColumnInfo(name = "song_language")
    var language: String?,
) : Parcelable {

    constructor(
        name: String?,
        path: String?,
        imagePath: String?,
        duration: Int?,
        albumId: String,
        lyricsSong: String?,
        year: String?,
        dateModified: String?,
        language: String?
    ) : this(
        id = UUID.randomUUID().toString(),
        name = name,
        path = path,
        imagePath = imagePath,
        duration = duration,
        albumId = albumId,
        lyricsSong = lyricsSong,
        year = year,
        date = dateModified,
        language = language
    )

    val title: String
        get() = MusicUtils.extractSongInfo(path).title
    val nameArtist: String
        get() = MusicUtils.extractSongInfo(path).artist
}