package com.romankaranchuk.musicplayer.presentation.ui.player.lyrics

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.romankaranchuk.musicplayer.databinding.DialogSongLyricsBinding
import com.romankaranchuk.musicplayer.di.util.Injectable
import com.romankaranchuk.musicplayer.utils.startAlphaAnimation
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs


class SongLyricsBottomSheetDialog : BottomSheetDialogFragment(), Injectable {

    companion object {
        const val TAG = "SongLyricsBottomSheetDialog"

        private const val PERCENTAGE_TO_SHOW_CONTENT_AT_TOOLBAR = 0.40f
        private const val ALPHA_ANIMATIONS_DURATION = 200

        fun newInstance(songId: String): SongLyricsBottomSheetDialog {
            return SongLyricsBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putString("songId", songId)
                }
            }
        }
    }

    private var _binding: DialogSongLyricsBinding? = null
    private val binding get() = _binding!!

    private var offsetChangedListener: AppBarLayout.OnOffsetChangedListener? = AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
        val maxScroll = appBarLayout.totalScrollRange
        val percentage = abs(offset).toFloat() / maxScroll.toFloat()

        handleToolbarContentVisibility(percentage)
    }

    private var isToolbarContentVisible = true

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private val viewModel: SongLyricsViewModel by viewModels { mViewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        lifecycle.addObserver(viewModel)
    }

    override fun onDetach() {
        super.onDetach()

        lifecycle.removeObserver(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSongLyricsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()

//        requireView().viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
////                view?.updateLayoutParams {
////                    height = (getDisplayHeightPx(requireContext()) ?: 640.dpToPx()) //- 64.dpToPx()
////                }
//
//                val dialog = dialog as BottomSheetDialog? ?: return
//                dialog.setExpandToFullHeightBehavior(isCancellableByDragging = true)
//
//                Timber.d("onGlobalLayout")
//                requireView().viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//        })

        binding.challengeAppbar.addOnOffsetChangedListener(offsetChangedListener)

        binding.challengeCollapsingToolbar.title = "Hello"
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.challengeAppbar.removeOnOffsetChangedListener(offsetChangedListener)
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { viewState ->
                    when (viewState) {
                        is SongLyricsViewModel.State.ShowLyrics -> {
                            binding.songLyrics.text = viewState.songLyrics
                        }
                    }
                }
            }
        }
    }

    private fun handleToolbarContentVisibility(percentage: Float) {
        if (percentage <= PERCENTAGE_TO_SHOW_CONTENT_AT_TOOLBAR) {
            if (!isToolbarContentVisible) {
                startAlphaAnimation(
                    binding.challengeHeader.songName,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.VISIBLE,
                    true
                )
                startAlphaAnimation(
                    binding.challengeHeader.artistName,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.VISIBLE,
                    true
                )
                isToolbarContentVisible = true
            }
        } else {
            if (isToolbarContentVisible) {
                startAlphaAnimation(
                    binding.challengeHeader.songName,
                    0,
                    View.GONE,
                    true
                )
                startAlphaAnimation(
                    binding.challengeHeader.artistName,
                    0,
                    View.GONE,
                    true
                )
                isToolbarContentVisible = false
            }
        }
    }
}