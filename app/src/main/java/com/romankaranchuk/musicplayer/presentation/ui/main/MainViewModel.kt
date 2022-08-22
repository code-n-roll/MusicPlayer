package com.romankaranchuk.musicplayer.presentation.ui.main

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.romankaranchuk.musicplayer.domain.LoadTracksUseCase
import com.romankaranchuk.musicplayer.presentation.navigation.Navigator
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val navigator: Navigator,
    val loadTracksUseCase: LoadTracksUseCase
) : ViewModel(), DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        
        navigator.openTrackList()
    }

    sealed class ViewState {

    }
}