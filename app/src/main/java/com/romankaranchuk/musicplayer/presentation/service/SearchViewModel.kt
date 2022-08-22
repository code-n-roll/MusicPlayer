package com.romankaranchuk.musicplayer.presentation.service

import android.os.AsyncTask
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.romankaranchuk.musicplayer.data.Album
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.data.api.MusixmatchApi
import com.romankaranchuk.musicplayer.data.db.MusicRepository
import com.romankaranchuk.musicplayer.utils.MusicUtils
import com.romankaranchuk.musicplayer.utils.MusicUtils.SongInfo
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val musixMatchApi: MusixmatchApi
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    sealed class State {
        object OnSearchComplete: State()
    }

    private var isSearchActive = false

    fun onBind() {
        startMusicSearch()
    }

    private fun startMusicSearch() {
        if (!isSearchActive) {
            val searcher = MusicSearcher()
            Thread(searcher).start()
        }
    }

    private inner class MusicSearcher : Runnable {
        private fun iterateFiles(files: Array<File>, dirPathToFileName: HashMap<String, MutableList<String>>) {
            for (file in files) {
                if (file.isDirectory) {
                    iterateFiles(file.listFiles(), dirPathToFileName)
                } else {
                    val name = file.name
                    if (name.endsWith(".mp3")) {
                        val dirPath = file.parent
                        if (!dirPathToFileName.containsKey(dirPath)) {
                            dirPathToFileName[dirPath] = LinkedList()
                        }
                        val songNames = dirPathToFileName[dirPath]!!
                        songNames.add(name)
                    }
                }
            }
        }

        override fun run() {
//            String[] projection = new String[] {
//                    MediaStore.Audio.Media._ID,
//                    MediaStore.Audio.Media.ALBUM,
//                    MediaStore.Audio.Media.ARTIST,
//                    MediaStore.Audio.Media.TITLE,
//                    MediaStore.Audio.Media.RELATIVE_PATH
//            };
//            String selection = "";
//            String[] selectionArgs = null;
//            String sortOrder = "";
//            Cursor cursor = SearchService.this.getContentResolver().query(
//                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    projection,
//                    selection,
//                    selectionArgs,
//                    sortOrder
//                    );
//            while (cursor.moveToNext()) {
//
//            }
//
            val musicFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val musicFolderPath = musicFolder.absolutePath
            val searchStart = File(musicFolderPath)
            if (!searchStart.exists() && !searchStart.isDirectory) {
                return
            }
            val music = HashMap<String, MutableList<String>>()
            iterateFiles(searchStart.listFiles(), music)
            for (albumPath in music.keys) {
                val songsInfo = ArrayList<SongInfo>()
                val songsFileName: List<String> = music[albumPath]!!
                for (songFileName in songsFileName) {
                    val songPath = "$albumPath/$songFileName"
                    val songInfo = MusicUtils.extractSongInfo(songPath)
                    songInfo.lyrics = SearchLyricUtils().execute(songInfo.artist, songInfo.title).get()
                    songInfo.language = SearchLanguageUtils().execute(songInfo.artist, songInfo.title).get()
                    songInfo.cover = SearchCoverUtils().execute(songInfo.artist, songInfo.title).get()
                    if (songInfo.cover.isEmpty()) {
//            songInfo.cover = String.valueOf(R.drawable.unknown_album_cover);
                    }
                    songsInfo.add(songInfo)
                }
                val firstSongInfo = songsInfo[0]
                val albumName = firstSongInfo.album
                val albumArtist = firstSongInfo.artist
                val albumCover = firstSongInfo.cover //MusicUtils.getNextCover();
                val album = Album(albumName, albumArtist, albumPath, albumCover)
                val songs = ArrayList<Song>()
                for (i in songsInfo.indices) {
                    val songInfo = songsInfo[i]
                    val songName = songInfo.title
                    val songPath = albumPath + '/' + songsFileName[i]
                    val songLyrics = songInfo.lyrics
                    val songLanguage = songInfo.language
                    val songYear = songInfo.year
                    val songDate = songInfo.date
                    val song = Song(
                        songName, songPath, albumCover, songInfo.duration,
                        album.id, songLyrics, songYear, songDate, songLanguage
                    )
                    songs.add(song)
                }
                musicRepository.saveAlbum(album, songs)
            }
            isSearchActive = false
            _state.postValue(State.OnSearchComplete)
        }
    }

    private inner class SearchCoverUtils : AsyncTask<String?, Void?, String>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String {
            val urlAlbumCover: String = musixMatchApi.getUrlAlbumCover(params[0]!!, params[1]!!)!!
            Timber.d(urlAlbumCover)
            return urlAlbumCover
        }
    }

    private inner class SearchLanguageUtils : AsyncTask<String?, Void?, String>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String {
            val language: String = musixMatchApi.getLanguage(params[0]!!, params[1]!!)!!
            Timber.d(language)
            return language
        }
    }

    private inner class SearchLyricUtils : AsyncTask<String?, Void?, String>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String {
            val lyrics: String = musixMatchApi.getLyrics(params[0]!!, params[1]!!)!!
            Timber.d(lyrics)
            return lyrics
        }
    }
}