package com.romankaranchuk.musicplayer.presentation.ui.tracklist

import androidx.lifecycle.*
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.data.db.MusicRepositoryImpl
import com.romankaranchuk.musicplayer.domain.LoadTracksUseCase
import com.romankaranchuk.musicplayer.domain.LoadTracksUseCaseImpl
import com.romankaranchuk.musicplayer.presentation.navigation.Navigator
import java.util.*
import javax.inject.Inject

class TrackListViewModel @Inject constructor(
    private val loadTracksUseCase: LoadTracksUseCase,
    private val navigator: Navigator
) : ViewModel(), DefaultLifecycleObserver {

    companion object {
        const val BY_NAME = "0"
        const val BY_DURATION = "1"
        const val BY_YEAR = "2"
        const val BY_DATE_MODIFIED = "3"
        const val BY_FORMAT = "4"
        const val BY_LANGUAGE = "5"
    }

    private val _state: MutableLiveData<ViewState> = MutableLiveData()
    val state: LiveData<ViewState> = _state

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        loadSongs(BY_DURATION)
    }

    fun loadSongs(sortBy: String) {
        val songs = loadTracksUseCase.loadSongs(sortBy)

        _state.value = ViewState.ShowTracks(songs)
    }

    fun sortSongs(sortBy: String) {
//        val sorted = loadTracksUseCase.sortSongs(sortBy)
//        _state.value = ViewState.ShowTracks(sortedSongs)
    }

    fun songClicked(song: Song) {
        navigator.openPlayer(song)
    }

    fun songLongClicked(song: Song) {
        navigator.openEditActions(song)
    }

    sealed class ViewState {
        class ShowTracks(val tracks: List<Song>) : ViewState()
    }
}