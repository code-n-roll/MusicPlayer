package com.romankaranchuk.musicplayer.data

import android.os.Parcelable
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "albums")
@Parcelize
data class Album (
    @PrimaryKey
    @ColumnInfo(name = "album_id")
    var id: String,

    @ColumnInfo(name = "album_name")
    var name: String?,

    @ColumnInfo(name = "album_artist")
    var artist: String?,

    @ColumnInfo(name = "album_path")
    var path: String?,

    @ColumnInfo(name = "album_image")
    var imagePath: String?,
) : Parcelable {

    constructor(
        name: String,
        artist: String,
        path: String,
        imagePath: String
    ) : this(
        id = UUID.randomUUID().toString(),
        name = name,
        artist = artist,
        path = path,
        imagePath = imagePath
    )
}