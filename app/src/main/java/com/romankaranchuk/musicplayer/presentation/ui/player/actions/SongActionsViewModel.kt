package com.romankaranchuk.musicplayer.presentation.ui.player.actions

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.romankaranchuk.musicplayer.presentation.navigation.Navigator
import javax.inject.Inject

class SongActionsViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel(), DefaultLifecycleObserver {

    private var songId = "-1"

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        val args = (owner as? Fragment)?.arguments
        songId = args?.getString("songId") ?: "-1"
    }

    fun onLyricsBtnClick() {
        navigator.openSongLyrics(songId)
    }
}
