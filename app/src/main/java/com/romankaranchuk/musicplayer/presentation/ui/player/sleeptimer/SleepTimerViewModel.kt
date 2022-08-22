package com.romankaranchuk.musicplayer.presentation.ui.player.sleeptimer

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.romankaranchuk.musicplayer.domain.LoadTracksUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SleepTimerViewModel @Inject constructor(
    private val loadSongUseCase: LoadTracksUseCase,
    // TODO() remove context
    private val context: Context
) : ViewModel(), DefaultLifecycleObserver {

    private val _state: MutableSharedFlow<State> = MutableSharedFlow()
    val state: SharedFlow<State> = _state
    private var args: Bundle? = null
    private var isStart: Boolean = true

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        args = (owner as? Fragment)?.arguments
    }

    fun onSwitchChecked(isChecked: Boolean) {
        if (!isChecked) {
            return
        }

        val songId = args?.getString("songId")

        if (songId == null) {
            Toast.makeText(context, "songId is not provided can not display song lyrics", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val song = loadSongUseCase.loadSong(songId)
            launch(Dispatchers.Main) {
                _state.emit(State.SetupSleepTimer(song.duration ?: 0))
            }
        }
    }

    fun onStartStopTimerClicked() {
        this.isStart = !isStart
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            launch(Dispatchers.Main) {
                if (isStart) {
                    _state.emit(State.ShowStart())
                } else {
                    _state.emit(State.ShowStop())
                }
            }
        }
    }

    sealed class State {
        class SetupSleepTimer(val duration: Int): State()
        class ShowStop(): State()
        class ShowStart(): State()
        class TickTimer(val curDuration: Long): State()
    }
}