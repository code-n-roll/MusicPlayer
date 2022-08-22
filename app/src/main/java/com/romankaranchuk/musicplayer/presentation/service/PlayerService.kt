package com.romankaranchuk.musicplayer.presentation.service

import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

class PlayerService @Inject constructor() : LifecycleService() {

    private val binder = PlayerBinder()
    @Inject lateinit var viewModel: PlayerServiceViewModel

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
//        viewModel = ViewModelProvider.NewInstanceFactory.instance.create(
//            PlayerServiceViewModel::class.java
//        )
        bindViewModels()
        viewModel.onCreate()
        Timber.d("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand")
        viewModel.onStartCommand()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
        Timber.d("onDestroy")
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Timber.d("onBind")
        return binder
    }

    inner class PlayerBinder : Binder() {
        val service: PlayerService
            get() = this@PlayerService
    }

    private fun bindViewModels() {
        viewModel.state.observe(this) { state ->
            when (state) {
                PlayerServiceViewModel.State.OnCancel -> {
                    stopSelf()
                }
                PlayerServiceViewModel.State.OnDestroy -> {

                }
                is PlayerServiceViewModel.State.Start -> {
                    startForeground(1, state.builder.build())
                }
                is PlayerServiceViewModel.State.OnPlay -> {
                    startForeground(1, state.builder.build())
                }
            }
        }
    }

    companion object {
        private const val LOG_TAG = "myLogs"
    }
}