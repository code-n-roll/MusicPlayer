package com.romankaranchuk.musicplayer.presentation.ui.player

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.domain.LoadTracksUseCase
import com.romankaranchuk.musicplayer.presentation.navigation.Navigator
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListViewModel
import com.romankaranchuk.musicplayer.common.MusicPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer,
    private val loadTracksUseCase: LoadTracksUseCase,
    private val context: Context,
    private val navigator: Navigator
) : ViewModel(), DefaultLifecycleObserver {

//    private var intentPlayerService: Intent? = null
    var isServiceBound: Boolean = false

//    private val serviceConnection: ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
//            _state.emit(ViewState.ServiceConnectedState(binder)
//        }
//
//        override fun onServiceDisconnected(name: ComponentName) {
//            _state.emit(ViewState.ServiceDisconnectedState
//        }
//    }

//    private val forwardButtonFromServiceToFragmentBR: BroadcastReceiver =
//        object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                onForwardBtnClick()
////                fastForwardButton.callOnClick()
//            }
//        }
//    private val backwardButtonFromServiceToFragmentBR: BroadcastReceiver =
//        object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                onBackwardBtnClick()
////                fastBackwardButton.callOnClick()
//            }
//        }
//    private val playButtonFromServiceToFragmentBR: BroadcastReceiver =
//        object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
////                playImageButton.setSelected(!playImageButton.isSelected())
////                setStatePlayButton()
//            }
//        }
//
//    private fun registerBroadcastReceivers(context: Context) {
//        context.registerReceiver(
//            forwardButtonFromServiceToFragmentBR,
//            IntentFilter(PlayerFragment.TAG_FORWARD_BUT_PS_TO_F_BR)
//        )
//        context.registerReceiver(
//            backwardButtonFromServiceToFragmentBR,
//            IntentFilter(PlayerFragment.TAG_BACKWARD_BUT_PS_TO_F_BR)
//        )
//        context.registerReceiver(
//            playButtonFromServiceToFragmentBR,
//            IntentFilter(PlayerFragment.TAG_PLAY_BUT_PS_TO_F_BR)
//        )
//    }

    private val _state: MutableSharedFlow<ViewState> = MutableSharedFlow()
    val state: SharedFlow<ViewState> = _state

    private var isShuffleEnabled = false
    private var seekbarCurrentProgress = 0

    private val mainHandler = Handler(Looper.getMainLooper())
    private val mUpdateSongTimerRunnable: Runnable = object : Runnable {
        override fun run() {
            val curDurationFormatted = formatDurationToTime(musicPlayer.getCurrentPosition())

            CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
                _state.emit(ViewState.UpdateSongTimer(curDurationFormatted))
            }
            mainHandler.postDelayed(this, 1000)
        }
    }
    private val mUpdateSeekBarRunnable: Runnable = object : Runnable {
        override fun run() {
            CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
                _state.emit(ViewState.UpdateSongSeekbar(progress = musicPlayer.getCurrentPosition()))
            }
            mainHandler.postDelayed(this, 2000)
        }
    }

    private val songs = mutableListOf<Song>()

    fun fileCurrentSong(): File {
        return File(currentSong!!.path)
    }
    var currentSong: Song? = null
    val listRecentlySongs = LinkedList<Song>()
    private val mediaPlayerCompletionListener = MediaPlayer.OnCompletionListener { onTrackPlayingEnd() }

    override fun onCreate(owner: LifecycleOwner) {
//        if (intentPlayerService == null) {
//            intentPlayerService = Intent(MusicPlayerApp.context, PlayerService::class.java)
//        }
//        MusicPlayerApp.context.startService(intentPlayerService)
//        MusicPlayerApp.context.bindService(intentPlayerService, serviceConnection, 0)
//
//        registerBroadcastReceivers(MusicPlayerApp.context)

        val song = (owner as Fragment).arguments?.getParcelable<Song>(PlayerFragment.ARG_CURRENT_SONG)
        currentSong = song
        Timber.d("onCreate:: currentSong=${song?.name}")

        loadSongsAndPlayCurrent()

        musicPlayer.mediaPlayer.setOnCompletionListener(mediaPlayerCompletionListener)
    }

    override fun onDestroy(owner: LifecycleOwner) {
//        if (isServiceBound) {
//            MusicPlayerApp.context.unbindService(serviceConnection)
//            isServiceBound = false
//        }
//        MusicPlayerApp.context.unregisterReceiver(playButtonFromServiceToFragmentBR)
//        MusicPlayerApp.context.unregisterReceiver(forwardButtonFromServiceToFragmentBR)
//        MusicPlayerApp.context.unregisterReceiver(backwardButtonFromServiceToFragmentBR)

        // TODO() stop player until PlayerService is not implemented
        // completion listener is called on stop() is called
        musicPlayer.mediaPlayer.setOnCompletionListener(null)
        musicPlayer.mediaPlayer.setOnErrorListener { mp, what, extra ->
            Timber.d("error is happened, what=$what, extra=$extra")
            return@setOnErrorListener true
        }
        musicPlayer.stop()
        resetSongProgressUI()
    }

    private fun setupSongProgressUI() {
        mainHandler.postDelayed(mUpdateSongTimerRunnable, 10)
        mainHandler.postDelayed(mUpdateSeekBarRunnable, 10)
    }

    private fun resetSongProgressUI() {
        mainHandler.removeCallbacks(mUpdateSongTimerRunnable)
        mainHandler.removeCallbacks(mUpdateSeekBarRunnable)
    }

    private fun formatDurationToTime(curDurationInMs: Int): String {
        val curDurationInSec = curDurationInMs / 1000.0
        val minutes = (curDurationInSec / 60).toInt()
        val seconds = (curDurationInSec / 60 - minutes) * 60
        val secondsString = if (seconds <= 9) {
            "0" + String.format(Locale.getDefault(), "%01.0f", seconds).substring(0, 1)
        } else {
            String.format(Locale.getDefault(), "%02.0f", seconds).substring(0, 2)
        }
        val minutesString = String.format(Locale.getDefault(), "%d", minutes)
        return "$minutesString:$secondsString"
    }

    private fun loadSongsAndPlayCurrent() {
        if (songs.isEmpty()) {
            musicPlayer.start(fileCurrentSong().toString())
            val songs = loadTracksUseCase.loadSongs(TrackListViewModel.BY_DURATION)

            this.songs.clear()
            this.songs.addAll(songs)
        }

        val durationInMs = musicPlayer.getDuration()

        setupSongProgressUI()

//        Timberog( "currentSong=${currentSong}, loadSongsAndPlayCurrent:: ${songs}")
        val index = songs.indexOf(currentSong)
        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(
                ViewState.TracksFetched(
                    curSongListPos = index,//if (index-1 > 0) index-1 else 0,
                    songs = songs,
                    durationInMs = durationInMs,
                    durationFormatted = formatDurationToTime(durationInMs),
                    curPosition = musicPlayer.getCurrentPosition(),
                    songName = currentSong?.name ?: "unknown",
                    artistName = currentSong?.nameArtist ?: "unknown",
                    albumImagePath = currentSong?.imagePath
                )
            )
        }
    }

    fun onPlayPauseBtnClick() {
        if (musicPlayer.isPlaying()) {
            onPauseBtnClick()
        } else {
            onPlayBtnClick()
        }
    }

    private fun onPauseBtnClick() {
        musicPlayer.pause()

        resetSongProgressUI()

        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(ViewState.PauseState)
        }
    }

    private fun onPlayBtnClick() {
        musicPlayer.resume()

        setupSongProgressUI()

        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(ViewState.PlayState)
        }
    }

    fun onFastForwardRewindClick(isFastForward: Boolean, isClick: Boolean) {
//        val index = songs.indexOf(currentSong)
//        val curSongPos = if (index-1 > 0) index-1 else 0
        val curSongPos = songs.indexOf(currentSong)
        Timber.d("onFastForwardRewindClick:: curSongPos = $curSongPos, isClick = $isClick")
        if (curSongPos <= -1) {
            return
        }

//        isFastForwardOrRewindButtons = true
//        if (isShuffleEnabled) {//&& !swap) {
//            val i = Random().nextInt(songs.size - 1)
//            currentSong = songs[i]
//        }

        val nextSongPos: Int
        if (isClick && !isFastForward && musicPlayer.mediaPlayer.currentPosition >= 3000) {
            musicPlayer.mediaPlayer.seekTo(0)
            nextSongPos = curSongPos
        } else {
            nextSongPos = Math.floorMod(if (isFastForward) curSongPos + 1 else curSongPos - 1, songs.size)
//            nextSongPos = if (index-1 > 0) index-1 else 0
            currentSong = songs[nextSongPos]
            if (musicPlayer.isPlaying()) {
                musicPlayer.start(fileCurrentSong().toString())
            } else {
                musicPlayer.prepare(fileCurrentSong().toString())
            }
        }
        Timber.d("onFastForwardRewindClick:: curSongPos = $curSongPos, nextSongPos = $nextSongPos")

//        val oldLooping = musicPlayer.mediaPlayer.isLooping
//        musicPlayer.mediaPlayer.reset()
//        musicPlayer.mediaPlayer.isLooping = oldLooping
//        try {
//            musicPlayer.mediaPlayer.setDataSource(fileCurrentSong().toString())
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        musicPlayer.mediaPlayer.prepare()
//
////        binding.bottomPart!!.seekbarSongTime.max = musicPlayer.mediaPlayer.duration
//
//        // play
////        binding.bottomPart!!.playPauseSongButton.isSelected = true
////        setupSongProgressUI()
//        musicPlayer.mediaPlayer.start()

//        setupMediaPlayerWithFile(fileCurrentSong())
//        setNameSongArtist(currentSong)
//        setSongFullTimeAndSeekBarProgress(
//            durationInMs = musicPlayer.mediaPlayer.duration,
//            currentPosition = musicPlayer.mediaPlayer.currentPosition
//        )

//        if (isFastForwardOrRewindButtons) {// && !swap) {
////        if (isUserScrollChange) {
//            oldSongPos = nextSongPos
//            val isFromLastToFirst = nextSongPos == 0
//            val isFromFirstToLast = nextSongPos == viewModel.songs.size-1
//            val isSmoothAnim = if (isFastForward) !isFromLastToFirst else true//!isFromFirstToLast TODO() image is not updating when smooth=false
//            binding.pagerFullscreenPlayer.setCurrentItem(nextSongPos, isSmoothAnim)
//        }

//        if (isClick) {
        val duration = musicPlayer.getDuration()

        val isFromLastToFirst = nextSongPos == 0
        val isFromFirstToLast = nextSongPos == songs.size-1
        val isSmoothAnim = if (isFastForward) !isFromLastToFirst else !isFromFirstToLast

        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(
                ViewState.ForwardRewindState(
                    currentPosition = musicPlayer.getCurrentPosition(),
                    duration = duration,
                    durationFormatted = formatDurationToTime(duration),
                    songName = currentSong?.title ?: "unknown",
                    artistName = currentSong?.nameArtist ?: "unknown",
                    nextSongPos = nextSongPos,
                    isClick = isClick,
                    isSmoothAnim = true // isSmoothAnim TODO() scrolling is not working when smooth=false
                )
            )
//            }
        }
    }

    fun onShuffleBtnClick(oldSelectedState: Boolean) {
        // TODO() disable feature until fix
        Toast.makeText(context, "shuffle is not implemented", Toast.LENGTH_SHORT).show()
        return

        isShuffleEnabled = !oldSelectedState

        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(ViewState.ShuffleState)
        }
    }

    fun onSleepTimerClick() {
        navigator.openSleepTimer(currentSong?.id ?: "-1")
    }

    fun onRepeatBtnClick(oldSelectedState: Boolean) {
        val newSelectedState = !oldSelectedState
        musicPlayer.isLooping = newSelectedState

        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(ViewState.RepeatState)
        }
    }

    fun onSeekbarStartTrackingTouch() {
        resetSongProgressUI()

        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(ViewState.StartTrackingTouchState)
        }
    }

    fun onSeekbarStopTrackingTouch() {
        musicPlayer.seekTo(seekbarCurrentProgress)

        setupSongProgressUI()

        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(ViewState.StopTrackingTouchState)
        }
    }

    fun onSeekbarProgressChanged(progress: Int) {
        seekbarCurrentProgress = progress

        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(ViewState.ProgressChangedState(formatDurationToTime(progress)))
        }
    }

    fun onSongNameTitleClick() {
        navigator.openSongActions(currentSong?.id ?: "-1")
    }

    private fun onTrackPlayingEnd() {
        if (musicPlayer.isLooping) {
            musicPlayer.resume()
        } else {
            onFastForwardRewindClick(isFastForward = true, isClick = true)
        }

        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            _state.emit(ViewState.TrackPlayingEnd)
        }
    }

    sealed class ViewState {
        object PlayState : ViewState()
        object PauseState : ViewState()
        class ForwardRewindState(
            val currentPosition: Int,
            val duration: Int,
            val durationFormatted: String,
            val songName: String,
            val artistName: String,
            val nextSongPos: Int,
            val isClick: Boolean,
            val isSmoothAnim: Boolean
        ) : ViewState()
        object ShuffleState : ViewState()
        object RepeatState : ViewState()
        object StartTrackingTouchState : ViewState()
        object StopTrackingTouchState : ViewState()
        class ProgressChangedState(val progressFormatted: String) : ViewState()

        class ServiceConnectedState(val binder: IBinder) : ViewState()
        object ServiceDisconnectedState : ViewState()
        object TrackPlayingEnd : ViewState()

        class TracksFetched(
            val curSongListPos: Int,
            val songs: List<Song>,
            val durationInMs: Int,
            val durationFormatted: String,
            val curPosition: Int,
            val songName: String,
            val artistName: String,
            val albumImagePath: String?
        ) : ViewState()

        class UpdateSongTimer(val timeFormatted: String) : ViewState()
        class UpdateSongSeekbar(val progress: Int) : ViewState()
    }
}