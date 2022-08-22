package com.romankaranchuk.musicplayer.presentation.ui.player.page

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.databinding.FragmentPlayerPageBinding
import com.romankaranchuk.musicplayer.di.util.Injectable
import com.romankaranchuk.musicplayer.utils.MathUtils
import com.squareup.picasso.Picasso
import timber.log.Timber
import javax.inject.Inject

class PlayerPageFragment : Fragment(), Injectable {

    private var _binding: FragmentPlayerPageBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: PlayerPageViewModel by viewModels { viewModelFactory }

    private var pageNumber = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        lifecycle.addObserver(viewModel)
    }

    override fun onDetach() {
        super.onDetach()

        lifecycle.removeObserver(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreateView: $pageNumber")
        _binding = FragmentPlayerPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            pageNumber = arguments!!.getInt(ARGUMENT_PAGE_NUMBER)
        }
        Timber.d("onCreate: $pageNumber")
        var savedPageNumber = -1
        if (savedInstanceState != null) {
            savedPageNumber = savedInstanceState.getInt(SAVE_PAGE_NUMBER)
        }
        Timber.d("savedPageNumber = $savedPageNumber")

        setAlbumCoverImageViewSize()

        bindViewModels()
        viewModel.onCreateCalled(pageNumber)
    }

    private fun bindViewModels() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is PlayerPageViewModel.State.onCreate -> {
                    setAlbumCoverImageView(it.song)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVE_PAGE_NUMBER, pageNumber)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("onActivityCreated: $pageNumber")
    }

    fun setAlbumCoverImageViewSize() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val dm = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(dm)
            val height = dm.heightPixels
            binding.fullscreenAlbumCover.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        }
    }

    fun setAlbumCoverImageView(song: Song) {
        Picasso.get().load(song.imagePath).into(binding.fullscreenAlbumCover)
        binding.fullscreenAlbumCover.tag = song.imagePath
        if (MathUtils.tryParse(song.imagePath) != -1) {
            binding.fullscreenAlbumCover.scaleType = ImageView.ScaleType.CENTER
        }
    }

    companion object {
        private const val ARGUMENT_PAGE_NUMBER = "arg_page_number"
        private const val SAVE_PAGE_NUMBER = "save_page_number"

        fun newInstance(page: Int): PlayerPageFragment {
            return PlayerPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARGUMENT_PAGE_NUMBER, page)
                }
            }
        }
    }
}