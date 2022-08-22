package com.romankaranchuk.musicplayer.presentation.ui.player.page

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.data.db.MusicRepository
import com.romankaranchuk.musicplayer.domain.LoadTracksUseCase
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListViewModel.Companion.BY_DURATION
import javax.inject.Inject

class PlayerPageViewModel @Inject constructor(
    private val loadTracksUseCase: LoadTracksUseCase
): ViewModel(), DefaultLifecycleObserver {

    private val _state = MutableLiveData<State>()
    val state get() = _state

    private val songs: MutableList<Song> = mutableListOf()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)


    }

    fun onCreateCalled(pageNum: Int) {
        if (this.songs.isEmpty()) {
            val songs = loadTracksUseCase.loadSongs(BY_DURATION)
            this.songs.clear()
            this.songs.addAll(songs)
        }
        _state.value = State.onCreate(songs[pageNum])
    }

    sealed class State {
        class onCreate(val song: Song) : State()
    }
}