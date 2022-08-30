package com.romankaranchuk.musicplayer.presentation.ui.player.actions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.romankaranchuk.musicplayer.databinding.DialogSongActionsBinding
import com.romankaranchuk.musicplayer.di.util.Injectable
import javax.inject.Inject

class SongActionsBottomSheetDialog : BottomSheetDialogFragment(), Injectable {

    companion object {
        const val TAG = "SongActionsBottomSheetDialog"

        fun newInstance(songId: String): SongActionsBottomSheetDialog {
            return SongActionsBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putString("songId", songId)
                }
            }
        }
    }

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private val viewModel: SongActionsViewModel by viewModels { mViewModelFactory }

    private var _binding: DialogSongActionsBinding? = null
    private val binding get() = _binding!!

    private val lyricsBtnClickListener = View.OnClickListener { viewModel.onLyricsBtnClick() }

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
        _binding = DialogSongActionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupListeners() {
        binding.lyricsBtn.setOnClickListener(lyricsBtnClickListener)
    }
}