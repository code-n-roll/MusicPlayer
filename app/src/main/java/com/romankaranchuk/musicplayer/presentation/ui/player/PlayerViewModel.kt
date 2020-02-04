package com.romankaranchuk.musicplayer.presentation.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.romankaranchuk.musicplayer.data.Song

class PlayerViewModel : ViewModel() {

    private val songs: MutableLiveData<List<Song>> = MutableLiveData()

    fun getSongs(): LiveData<List<Song>> {
        return songs
    }


}