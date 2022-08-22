package com.romankaranchuk.musicplayer.presentation.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.databinding.FragmentMainBinding
import com.romankaranchuk.musicplayer.di.util.Injectable
import com.romankaranchuk.musicplayer.presentation.navigation.Navigator
import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerFragment
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListViewModel
import timber.log.Timber
import javax.inject.Inject

class MainFragment : Fragment(), Injectable {

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }

        const val TAG = "MainFragment"
    }

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var lastProgress = 0f
    private var fragment : Fragment? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        lifecycle.addObserver(viewModel)
        navigator.activity = activity
    }

    override fun onDetach() {
        super.onDetach()

        lifecycle.removeObserver(viewModel)
        navigator.activity = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment = EmptyFragment.newInstance()
//        val song = viewModel.loadTracksUseCase.loadSongs(TrackListViewModel.BY_DURATION).first()
//        fragment = PlayerFragment.newInstance(song).also {
//            childFragmentManager.beginTransaction()
//                .replace(R.id.fma_container_draggable, it)
//                .commitNow()
//        }

        binding.fmaMotionLayout.setTransitionListener(
            object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int
                ) {

                }

                override fun onTransitionChange(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {
//                    if (progress - lastProgress > 0) {
//                        // from start to end
//                        val atEnd = Math.abs(progress - 1f) < 0.1f
//                        if (atEnd && fragment is EmptyFragment) {
//                            val transaction = childFragmentManager.beginTransaction()
//                            transaction
//                                .setCustomAnimations(R.animator.show, 0)
//                            val song = viewModel.loadTracksUseCase.loadSongs(TrackListViewModel.BY_DURATION).first()
//                            fragment = PlayerFragment.newInstance(song).also {
//                                transaction
//                                    .setCustomAnimations(R.animator.show, 0)
//                                    .replace(R.id.fma_container_draggable, it)
//                                    .commitNow()
//                            }
//                        }
//                    } else {
//                        // from end to start
//                        val atStart = progress < 0.9f
//                        if (atStart && fragment is PlayerFragment) {
//                            val transaction = childFragmentManager.beginTransaction()
//                            transaction
//                                .setCustomAnimations(0, R.animator.hide)
//                            fragment = EmptyFragment.newInstance().also {
//                                transaction
//                                    .replace(R.id.fma_container_draggable, it)
//                                    .commitNow()
//                            }
//                        }
//                    }
//                    lastProgress = progress
                }

                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

                }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout?,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {

                }
            }
        )
        Timber.d("onViewCreated")
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}