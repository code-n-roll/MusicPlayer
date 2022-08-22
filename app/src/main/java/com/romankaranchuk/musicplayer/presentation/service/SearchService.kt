package com.romankaranchuk.musicplayer.presentation.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelProvider
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

class SearchService @Inject constructor() : LifecycleService() {
    private val mBinder = SearchBinder()

    @Inject lateinit var viewModel: SearchViewModel

    inner class SearchBinder : Binder() {
        fun onBind() {
            viewModel.onBind()
        }
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
//        viewModel = ViewModelProvider.NewInstanceFactory.instance.create(SearchViewModel::class.java)
        bindViewModels()
        Timber.d("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand")
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Timber.d("onBind")
        viewModel.onBind()
        return mBinder
    }

    private fun bindViewModels() {
        viewModel.state.observe(this) {
            when (it) {
                SearchViewModel.State.OnSearchComplete -> {
                    sendBroadcast(Intent(TrackListFragment.UPDATE_SONG_BROADCAST))
                    stopSelf()
                }
            }
        }
    }
}