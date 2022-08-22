package com.romankaranchuk.musicplayer.domain

import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.data.db.MusicRepository
import com.romankaranchuk.musicplayer.data.db.MusicRepositoryImpl
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListViewModel
import java.util.*
import javax.inject.Inject

interface LoadTracksUseCase {
    fun loadSongs(sortBy: String): List<Song>
    suspend fun loadSong(songId: String): Song
}

class LoadTracksUseCaseImpl @Inject constructor(
    private val musicRepository: MusicRepository
) : LoadTracksUseCase {

    override suspend fun loadSong(songId: String): Song {
        return musicRepository.getSong(songId)
    }

    override fun loadSongs(sortBy: String): List<Song> {
        val albums = musicRepository.albums
        val songs = ArrayList<Song>()
        for (album in albums) {
            for (song in musicRepository.getSongs(album.id, false)) {
                if (!songs.contains(song)) {
                    songs.add(song)
                }
            }
        }
        Collections.sort(songs, getComparator(sortBy))

        return songs
    }

//    fun sortSongs(sortBy: String): List<Song> {
//        val sortedSongs = (state.value as TrackListViewModel.ViewState.ShowTracks).tracks
//        Collections.sort(sortedSongs, getComparator(sortBy))
//        return sortedSongs;
//    }

    private fun getComparator(sortBy: String): Comparator<Song> {
        when (sortBy) {
            TrackListViewModel.BY_DURATION -> return Comparator { song1, song2 -> (song1.duration ?: 0) - (song2.duration ?: 0) }
            TrackListViewModel.BY_YEAR -> return Comparator { song1, song2 ->
                val year1 = song1.year
                val year2 = song2.year
                if (year1 == null || year2 == null) {
                    return@Comparator 0
                }
                year1.compareTo(year2)
            }
            TrackListViewModel.BY_DATE_MODIFIED -> return Comparator { song1, song2 ->
                //                        return song1.getDateModified() - song2.getDateModified();
                0
            }
            TrackListViewModel.BY_FORMAT -> return Comparator { song1, song2 ->
                val path1 = song1.path
                val path2 = song2.path
                if (path1 == null || path2 == null) {
                    return@Comparator 0
                }
                val pos1 = path1.lastIndexOf(".")
                val pos2 = path2.lastIndexOf(".")
                val format1 = path1.substring(pos1 + 1)
                val format2 = path2.substring(pos2 + 1)
                format1.compareTo(format2)
            }
            TrackListViewModel.BY_LANGUAGE -> return Comparator { song1, song2 ->
                val lang1 = song1.language
                val lang2 = song2.language
                if (lang1 == null || lang2 == null) {
                    return@Comparator 0
                }
                lang1.compareTo(lang2)
            }
            TrackListViewModel.BY_NAME -> return Comparator { song1, song2 ->
                val name1 = song1.name
                val name2 = song2.name
                if (name1 == null || name2 == null) {
                    return@Comparator 0
                }
                name1.compareTo(name2)
            }
            else -> return Comparator { song1, song2 ->
                val name1 = song1.name
                val name2 = song2.name
                if (name1 == null || name2 == null) {
                    return@Comparator 0
                }
                name1.compareTo(name2)
            }
        }
    }
}