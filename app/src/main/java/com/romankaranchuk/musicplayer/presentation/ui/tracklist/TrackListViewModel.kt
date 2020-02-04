package com.romankaranchuk.musicplayer.presentation.ui.tracklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.data.db.MusicRepository
import com.romankaranchuk.musicplayer.data.db.MusicRepositoryImpl
import java.util.*
import javax.inject.Inject

class TrackListViewModel @Inject constructor(
        val musicRepository: MusicRepositoryImpl
) : ViewModel() {

    companion object {
        const val BY_NAME = "0"
        const val BY_DURATION = "1"
        const val BY_YEAR = "2"
        const val BY_DATE_MODIFIED = "3"
        const val BY_FORMAT = "4"
        const val BY_LANGUAGE = "5"
    }

    private val songs: MutableLiveData<List<Song>> = MutableLiveData()

    fun getSongs(): LiveData<List<Song>> {
        return songs
    }

    fun loadSongs(sortBy: String) {
        val albums = musicRepository.albums
        val songs = ArrayList<Song>()
        for (album in albums) {
            for (song in musicRepository.getSongs(album.id, false)) {
                if (!songs.contains(song)) {
                    songs.add(song)
                }
            }
        }
        Collections.sort<Song>(songs, getComparator(sortBy))

        this.songs.value = songs
    }

    fun sortSongs(sortBy: String) {
        val sortedSongs = songs.value
        Collections.sort<Song>(sortedSongs, getComparator(sortBy))
        this.songs.value = sortedSongs
    }

    private fun getComparator(sortBy: String): Comparator<Song> {
        when (sortBy) {
            BY_DURATION -> return Comparator { song1, song2 -> song1.duration - song2.duration }
            BY_YEAR -> return Comparator { song1, song2 ->
                if (song1.year != null && song2.year != null) {
                    song1.year.compareTo(song2.year)
                } else {
                    0
                }
            }
            BY_DATE_MODIFIED -> return Comparator { song1, song2 ->
                //                        return song1.getDateModified() - song2.getDateModified();
                0
            }
            BY_FORMAT -> return Comparator { song1, song2 ->
                val pos1 = song1.path.lastIndexOf(".")
                val pos2 = song2.path.lastIndexOf(".")
                val format1 = song1.path.substring(pos1 + 1)
                val format2 = song2.path.substring(pos2 + 1)
                format1.compareTo(format2)
            }
            BY_LANGUAGE -> return Comparator { song1, song2 ->
                if (song1.language != null && song2.language != null) {
                    song1.language.compareTo(song2.language)
                } else {
                    0
                }
            }
            BY_NAME -> return Comparator { song1, song2 -> song1.name.compareTo(song2.name) }
            else -> return Comparator { song1, song2 -> song1.name.compareTo(song2.name) }
        }
    }
}