package com.romankaranchuk.musicplayer.presentation.ui.player

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.palette.graphics.Palette
import androidx.viewpager2.widget.ViewPager2
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.databinding.FragmentPlayerBinding
import com.romankaranchuk.musicplayer.di.util.Injectable
import com.romankaranchuk.musicplayer.presentation.service.PlayerService
import com.romankaranchuk.musicplayer.presentation.service.PlayerService.PlayerBinder
import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerViewModel.ViewState.ServiceConnectedState
import com.romankaranchuk.musicplayer.presentation.ui.player.page.PlayerPagerAdapter
import com.romankaranchuk.musicplayer.utils.widgets.SecondPageSideShownTransformer
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class PlayerFragment : Fragment(), Injectable {

    // assigning non valid null value to oldSongPos to prevent false-positive viewpager swipe detection,
    private var oldSongPos: Int? = null
    private var isFastForwardOrRewindButtons = false
    private var isLeftToRightSwipe = false
    private var isRightToLeftSwipe = false
    private var prevState: Int = -1
    private var isUserScrollChange: Boolean = false

    private val intentPlayerService: Intent by lazy { Intent(requireContext(), PlayerService::class.java) }
    private var playerService: PlayerService? = null
    private var playerPagerAdapter: PlayerPagerAdapter? = null

    private val argbEvaluator = ArgbEvaluator()
    private val centerScaledPageTransformer by lazy { SecondPageSideShownTransformer(requireContext(), isCenterScaled = true) }
    private val pageTransformer by lazy { SecondPageSideShownTransformer(requireContext(), isCenterScaled = false) }


    @Inject lateinit var mViewModelFactory: ViewModelProvider.Factory
    private val viewModel: PlayerViewModel by viewModels { mViewModelFactory }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val mPageChangeListener: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            oldSongPos?.let { oldPos ->
                if (oldPos < position) {
                    isLeftToRightSwipe = true
                } else if (oldPos > position) {
                    isRightToLeftSwipe = true
                }
            }

            oldSongPos = position

            Timber.d("onPageSelected, position = $position, isLTRSwipe=$isLeftToRightSwipe, isRTLSwipe=$isRightToLeftSwipe")
        }

        override fun onPageScrolled(
            position: Int, positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            Timber.d(
                "onPageScrolled, position = " + position +
                        " positionOffset = " + positionOffset +
                        " positionOffsetPixels = " + positionOffsetPixels
            )
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (prevState == ViewPager2.SCROLL_STATE_DRAGGING && state == ViewPager2.SCROLL_STATE_SETTLING) {
                isUserScrollChange = true
            } else if (prevState == ViewPager2.SCROLL_STATE_SETTLING && state == ViewPager2.SCROLL_STATE_IDLE) {
                isUserScrollChange = false
            }
            prevState = state

            Timber.d("onPageScrollStateChanged, state = $state, isUserScrollChange=$isUserScrollChange")

//
//            if ((state == ViewPager.SCROLL_STATE_IDLE || state == ViewPager.SCROLL_STATE_SETTLING) && (isLeftToRightSwipe || isRightToLeftSwipe)) {
//                onFastForwardRewind(isFastForward = isLeftToRightSwipe)
//                isLeftToRightSwipe = false
//                isRightToLeftSwipe = false
//            }

//            if (!isUserScrollChange) {
//                return
//            }

            if (state == ViewPager2.SCROLL_STATE_SETTLING || state == ViewPager2.SCROLL_STATE_IDLE) {
//                swap = true
                if (!isFastForwardOrRewindButtons) {
                    if (isLeftToRightSwipe) {
    //                        binding.bottomPart.toNextSongButton.callOnClick()
                        viewModel.onFastForwardRewindClick(isFastForward = true, isClick = false)
                        isLeftToRightSwipe = false
                    } else if (isRightToLeftSwipe) {
                        viewModel.onFastForwardRewindClick(isFastForward = false, isClick = false)
    //                        binding.bottomPart.toPreviousSongButton.callOnClick()
                        isRightToLeftSwipe = false
    //                    }
                    }
//                swap = false
                }
                isFastForwardOrRewindButtons = false
            }
        }

    }
    private val onPlayBtnClickListener = View.OnClickListener { viewModel.onPlayPauseBtnClick() }
    private val mSeekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            viewModel.onSeekbarProgressChanged(progress)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar) {
            viewModel.onSeekbarStartTrackingTouch()
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            viewModel.onSeekbarStopTrackingTouch()
        }
    }
    private val mFastForwardClickListener = View.OnClickListener { viewModel.onFastForwardRewindClick(
        isFastForward = true,
        isClick = true
    ) }
    private val mFastBackwardClickListener = View.OnClickListener { viewModel.onFastForwardRewindClick(
        isFastForward = false,
        isClick = true
    ) }
    private val mReplayClickListener = View.OnClickListener { viewModel.onRepeatBtnClick(it.isSelected) }
    private val timerClickListener = View.OnClickListener { viewModel.onSleepTimerClick() }
    private val mShuffleClickListener = View.OnClickListener { viewModel.onShuffleBtnClick(it.isSelected) }
    private val songNameClickListener = View.OnClickListener { viewModel.onSongNameTitleClick() }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        lifecycle.addObserver(viewModel)
        Timber.d("onAttach")
    }

    override fun onDetach() {
        super.onDetach()

        lifecycle.removeObserver(viewModel)
        Timber.d("onDetach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

//        mViewModel = ViewModelProvider.NewInstanceFactory.getInstance().create<PlayerViewModel>(PlayerViewModel::class.java)

//        bindService()
        registerBroadcastReceivers()
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService()
        unregisterBroadcastReceivers()
        binding.pagerFullscreenPlayer?.unregisterOnPageChangeCallback(mPageChangeListener)

        oldSongPos = null
        _binding = null
        
        Timber.d("onDestroy")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
//        if (viewModel.isServiceBound) {
//            setSongFullTimeSeekBarProgress()
//        }
        Timber.d("onStart")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreateView")
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupListeners()

        setupAdapter()

//        currentSong = viewModel.curSelectedSong
        //        oldPosition = mSongs.indexOf(currentSong);
//        pagerFullscreenPlayer.setCurrentItem(oldPosition,false);

        restoreViewState(savedInstanceState)
        bindViewModels()
    }

    private val forwardButtonFromServiceToFragmentBR: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
//                onFastForwardRewind(isFastForward = true)
            }
        }
    private val backwardButtonFromServiceToFragmentBR: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
//                onFastForwardRewind(isFastForward = false)
            }
        }
    private val playButtonFromServiceToFragmentBR: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                binding.bottomPart.playPauseSongButton.isSelected = !binding.bottomPart.playPauseSongButton.isSelected
            }
        }

    private fun setupAdapter() {
        playerPagerAdapter = PlayerPagerAdapter(this)
        binding.pagerFullscreenPlayer?.let {
            with(it) {
                adapter = playerPagerAdapter
                registerOnPageChangeCallback(mPageChangeListener)
                setPageTransformer(centerScaledPageTransformer)
                clipToPadding = false
                clipChildren = false
                offscreenPageLimit = 2
            }
        }
    }

    private fun registerBroadcastReceivers() {
        with(requireContext()) {
            registerReceiver(forwardButtonFromServiceToFragmentBR, IntentFilter(TAG_FORWARD_BUT_PS_TO_F_BR))
            registerReceiver(backwardButtonFromServiceToFragmentBR, IntentFilter(TAG_BACKWARD_BUT_PS_TO_F_BR))
            registerReceiver(playButtonFromServiceToFragmentBR, IntentFilter(TAG_PLAY_BUT_PS_TO_F_BR))
        }
    }

    private fun unregisterBroadcastReceivers() {
        with(requireContext()) {
            unregisterReceiver(playButtonFromServiceToFragmentBR)
            unregisterReceiver(forwardButtonFromServiceToFragmentBR)
            unregisterReceiver(backwardButtonFromServiceToFragmentBR)
        }
    }

    private fun bindService() {
        with(requireContext()) {
            startService(intentPlayerService)
            this.bindService(intentPlayerService, serviceConnection, 0)
        }
    }

    private fun unbindService() {
        if (viewModel.isServiceBound) {
            requireContext().unbindService(serviceConnection)
            viewModel.isServiceBound = false
        }
    }

    private fun bindViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Timber.d("bindViewModels:: ")
                viewModel.state.collect { viewState ->
                    when (viewState) {
                        PlayerViewModel.ViewState.PlayState -> {
                            binding.bottomPart.playPauseSongButton.isSelected = true
                            val intent = Intent(TAG_PLAY).apply {
                                putExtra("fpfCall", "true")
                                putExtra("isPlaying", !binding.bottomPart.playPauseSongButton.isSelected)
                                putExtra("currentSong", viewModel.currentSong)
                            }
                            requireContext().sendBroadcast(intent)
//                        if (filePlayedSong !== viewModel.fileCurrentSong()) {
//                            val list: LinkedList<Song> = viewModel.listRecentlySongs
//                            if (list.size == 0 || list[0] !== viewModel.currentSong) {
//                                list.addFirst(viewModel.currentSong)
//                                filePlayedSong = viewModel.fileCurrentSong()
//                            }
//                        }
//                            if (playerService == null) {
//                                  bindService()
//                            }
                            binding.pagerFullscreenPlayer?.setPageTransformer(centerScaledPageTransformer)
                        }
                        PlayerViewModel.ViewState.PauseState -> {
                            binding.bottomPart.playPauseSongButton.isSelected = false
                            binding.pagerFullscreenPlayer?.setPageTransformer(pageTransformer)
                        }
                        is PlayerViewModel.ViewState.ForwardRewindState -> {
//                            isUserScrollChange = true
                            if (viewState.isClick) {
                                isFastForwardOrRewindButtons = true
                            }
                            onFastForwardRewind(viewState = viewState)
                        }
                        PlayerViewModel.ViewState.ShuffleState -> {
                            binding.bottomPart.shuffleButton.isSelected = !binding.bottomPart.shuffleButton.isSelected
                        }
                        PlayerViewModel.ViewState.RepeatState -> {
                            binding.bottomPart.replayButton.isSelected = !binding.bottomPart.replayButton.isSelected
                        }
                        PlayerViewModel.ViewState.StopTrackingTouchState -> {

                        }
                        PlayerViewModel.ViewState.StartTrackingTouchState -> {

                        }
                        is PlayerViewModel.ViewState.ProgressChangedState -> {
                            binding.bottomPart.startTime.text = viewState.progressFormatted
                        }
                        is ServiceConnectedState -> {
                            onServiceConnected(viewState.binder)
                        }
                        PlayerViewModel.ViewState.ServiceDisconnectedState -> {
                            Timber.d("onServiceDisconnected")
                            viewModel.isServiceBound = false
                        }
                        PlayerViewModel.ViewState.TrackPlayingEnd -> {
                        }
                        is PlayerViewModel.ViewState.TracksFetched -> {
                            onTracksFetched(viewState)
                        }

                        is PlayerViewModel.ViewState.UpdateSongSeekbar -> {
                            binding.bottomPart.seekbarSongTime.progress = viewState.progress
                        }
                        is PlayerViewModel.ViewState.UpdateSongTimer -> {
                            binding.bottomPart.startTime.text = viewState.timeFormatted
                        }
                        null -> TODO()
                    }
                }
            }
        }
    }

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    private fun onTracksFetched(viewState: PlayerViewModel.ViewState.TracksFetched) {
        if (playerPagerAdapter?.itemCount == 0) {
            playerPagerAdapter?.updateSongs(viewState.songs)
        }

        binding.bottomPart.playPauseSongButton.isSelected = true

        setNameSongArtist(
            songName = viewState.songName,
            artistName = viewState.artistName
        )

        binding.bottomPart.seekbarSongTime.max = viewState.durationInMs
        setSongFullTimeAndSeekBarProgress(
            durationFormatted = viewState.durationFormatted,
            currentPosition = viewState.curPosition
        )

        setBackgroundColor(viewState.albumImagePath, withAnim = false)

        Timber.d("onTracksFetched:: setCurrentItem=${viewState.curSongListPos}")
        binding.pagerFullscreenPlayer?.setCurrentItem(viewState.curSongListPos, false)
    }

    private fun setBackgroundColor(albumImagePath: String?, withAnim: Boolean) {
        if (albumImagePath == null) {
            return
        }
        // TODO() checking views on null because access is asynchronous, find better solution
        Picasso.get().load(albumImagePath).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap == null) {
                    return
                }
                val palette = Palette.Builder(bitmap).generate()
                val colorTo =
//                    palette.lightVibrantSwatch?.rgb
                    ColorUtils.blendARGB(
                    palette.lightVibrantSwatch?.rgb ?: Color.WHITE,
//                        palette.darkMutedSwatch?.rgb?.toColor()?.toArgb() ?: Color.WHITE,
                    Color.BLACK,
                    0.4f
                )
//                ?.run {
//                    adjustAlpha(this, 0.65f)
//                }
//                ?: Color.WHITE

                if (withAnim) {
                    val colorFrom = (binding?.root?.background as ColorDrawable).color
                    val colorAnimation = ValueAnimator.ofObject(argbEvaluator, colorFrom, colorTo)
                    with(colorAnimation) {
                        startDelay = 500
                        duration = 500

                        addUpdateListener { animator ->
                            binding?.root?.setBackgroundColor(animator.animatedValue as Int)
                        }
                        start()
                    }
                } else {
                    binding?.root?.setBackgroundColor(colorTo)
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }
        })
    }

    private fun onFastForwardRewind(viewState: PlayerViewModel.ViewState.ForwardRewindState) {
        binding.bottomPart.seekbarSongTime.max = viewState.duration
        // play
//        binding.bottomPart.playPauseSongButton.isSelected = true
//        setupSongProgressUI()
//        setupMediaPlayerWithFile(duration)


        setNameSongArtist(songName = viewState.songName, artistName = viewState.artistName)
        setSongFullTimeAndSeekBarProgress(durationFormatted = viewState.durationFormatted, currentPosition = viewState.currentPosition)

//        if (isFastForwardOrRewindButtons) {// && !swap) {
//        if (isUserScrollChange) {
            oldSongPos = viewState.nextSongPos
        if (viewState.isClick) {
            Timber.d("onFastForwardRewind:: setCurrentItem=${viewState.nextSongPos}")
            binding.pagerFullscreenPlayer?.setCurrentItem(viewState.nextSongPos, viewState.isSmoothAnim)
        }

        setBackgroundColor(viewModel.currentSong?.imagePath, withAnim = true)
    }

    private fun setupUI() {
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
        binding.bottomPart.nameSongFullscreenPlayer.isSingleLine = true
        binding.bottomPart.nameSongFullscreenPlayer.isSelected = true
        binding.bottomPart.nameArtistFullScreenPlayer.isSingleLine = true
        binding.bottomPart.nameArtistFullScreenPlayer.isSelected = true
        binding.bottomPart.startTime.text = "0:00" // TODO() detect hours minutes sec to show 00:00:00
        binding.bottomPart.replayButton.isSelected = false

//        val rotation = (requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
//        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
//            binding.bottomPart.lpfbAlbumImage.isVisible = true
//            Timber.d("orientation=$rotation landscape")
//        } else {
//            binding.bottomPart.lpfbAlbumImage.isGone = true
//            Timber.d("orientation=$rotation portrait")
//        }
    }

    private fun setupListeners() {
        binding.bottomPart.seekbarSongTime.setOnSeekBarChangeListener(mSeekBarChangeListener)
        binding.bottomPart.toNextSongButton.setOnClickListener(mFastForwardClickListener)
        binding.bottomPart.toPreviousSongButton.setOnClickListener(mFastBackwardClickListener)
        binding.bottomPart.replayButton.setOnClickListener(mReplayClickListener)
        binding.bottomPart.timerButton.setOnClickListener(timerClickListener)
        binding.bottomPart.shuffleButton.setOnClickListener(mShuffleClickListener)
        binding.bottomPart.playPauseSongButton.setOnClickListener(onPlayBtnClickListener)
        binding.bottomPart.nameSongFullscreenPlayer.setOnClickListener(songNameClickListener)
    }

    fun setNameSongArtist(songName: String, artistName: String) {
        binding.bottomPart.nameSongFullscreenPlayer.text = songName
        binding.bottomPart.nameArtistFullScreenPlayer.text = artistName
    }

    private fun restoreViewState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
//            val restoreCurrentSong = savedInstanceState.getParcelable<Song>("currentSong")
            //            playerPageFragment.setDataFullscreenPlayer(this, restoreCurrentSong);
            binding.bottomPart.playPauseSongButton.isSelected = savedInstanceState.getBoolean("statePlayButton")
            binding.bottomPart.replayButton.isSelected = savedInstanceState.getBoolean("stateReplayButton")
            binding.bottomPart.shuffleButton.isSelected = savedInstanceState.getBoolean("stateShuffleButton")
            oldSongPos = savedInstanceState.getInt("oldSongPos")
        //            setStatesButtons();
//            isContinued = true
//            isPaused = savedInstanceState.getBoolean("statePaused")
//            Companion.isResumed = savedInstanceState.getBoolean("stateResume")
            //            ((TrackListFragment)getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG)).setCurSelectedSong(restoreCurrentSong);
//            currentSong = restoreCurrentSong
            //            setSongFullTimeSeekBarProgress();
//            setupSongProgressUI()
//            fileCurrentSong = File(viewModel.curSelectedSong!!.path)
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
//        savedInstanceState.putParcelable("currentSong", currentSong)
        savedInstanceState.putBoolean("stateReplayButton", binding.bottomPart.replayButton.isSelected)
        savedInstanceState.putBoolean("stateShuffleButton", binding.bottomPart.shuffleButton.isSelected)
        savedInstanceState.putBoolean("statePlayButton", binding.bottomPart.playPauseSongButton.isSelected)
        savedInstanceState.putInt("stateFinalTime", binding.bottomPart.seekbarSongTime.max)
//        savedInstanceState.putBoolean("statePaused", isPaused)
//        savedInstanceState.putBoolean("stateResume", Companion.isResumed)
        savedInstanceState.putInt("oldSongPos", oldSongPos ?: 0)
    }

    private fun setSongFullTimeAndSeekBarProgress(durationFormatted: String, currentPosition: Int) {
        binding.bottomPart.endTime.text = durationFormatted
        binding.bottomPart.seekbarSongTime.progress = currentPosition
    }

    fun onServiceConnected(binder: IBinder) {
        Timber.d("onServiceConnected")
        playerService = (binder as PlayerBinder).service
//        viewModel.musicPlayer.mediaPlayer.setOnCompletionListener(onCompletionListenerMediaPlayer)
//        handleStateCurMediaPlayer()
//        if (!binding.bottomPart.playPauseSongButton.isSelected && !isContinued) {
//            binding.bottomPart.playPauseSongButton.callOnClick()
//        }
//        setSongFullTimeSeekBarProgress()
//        setupSongProgressUI()
        viewModel.isServiceBound = true
    }

    fun onServiceDisconnected() {
        Timber.d("onServiceDisconnected")
        viewModel.isServiceBound = false
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            this@PlayerFragment.onServiceConnected(binder)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            this@PlayerFragment.onServiceDisconnected()
        }
    }

    companion object {
        const val ARG_CURRENT_SONG = "CURRENT_SONG"
        const val TAG_PLAY = "playNotifPlayerReceiver"
        const val TAG_PLAY_BUT_PS_TO_F_BR = "playButtonFromPStoFragmentBR"
        const val TAG_FORWARD_BUT_PS_TO_F_BR = "forwardButtonFromPStoFragmentBR"
        const val TAG_BACKWARD_BUT_PS_TO_F_BR = "backwardButtonFromPStoFragmentBR"
        const val TAG = "PlayerFragment"
        const val PLAYER_FRAGMENT_TAG = TAG + ".PLAYER_FRAGMENT_TAG"

        var filePlayedSong: File? = null
        
        fun newInstance(song: Song?): Fragment {
            return PlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CURRENT_SONG, song)
                }
            }
        }
    }
}