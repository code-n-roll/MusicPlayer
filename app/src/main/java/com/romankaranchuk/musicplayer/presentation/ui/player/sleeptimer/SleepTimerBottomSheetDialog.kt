package com.romankaranchuk.musicplayer.presentation.ui.player.sleeptimer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.romankaranchuk.musicplayer.databinding.DialogSleepTimerBinding
import com.romankaranchuk.musicplayer.di.util.Injectable
import kotlinx.coroutines.launch
import com.romankaranchuk.musicplayer.utils.widgets.CircularSeekBar
import javax.inject.Inject

class SleepTimerBottomSheetDialog : BottomSheetDialogFragment(), Injectable {

    companion object {
        const val TAG = "SleepTimerBottomSheetDialog"

        fun newInstance(songId: String): SleepTimerBottomSheetDialog {
            return SleepTimerBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putString("songId", songId)
                }
            }
        }
    }

    private var _binding: DialogSleepTimerBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SleepTimerViewModel by viewModels { viewModelFactory }

    private val seekbarChangeListener = object : CircularSeekBar.OnCircularSeekBarChangeListener {
        override fun onProgressChanged(
            circularSeekBar: CircularSeekBar?,
            progress: Float,
            fromUser: Boolean
        ) {

        }

        override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            if (binding.timerOnEndTrackSwitch.isChecked) {
                binding.timerOnEndTrackSwitch.isChecked = !binding.timerOnEndTrackSwitch.isChecked
            }
        }

        override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {

        }
    }

    private val onSwitchCheckedChangeListener = CompoundButton.OnCheckedChangeListener { view, isChecked ->
        viewModel.onSwitchChecked(isChecked)
    }

    private val containerClickListener = View.OnClickListener {
        binding.timerOnEndTrackSwitch.isChecked = !binding.timerOnEndTrackSwitch.isChecked
    }

    private val startStopTimerClickListener = View.OnClickListener {
        viewModel.onStartStopTimerClicked()
    }

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
        _binding = DialogSleepTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewState()
        setupListeners()
        bindViewModel()
    }

    private fun setupViewState() {
    }

    private fun setupListeners() {
        binding.timerOnEndTrackContainer.setOnClickListener(containerClickListener)
        binding.timerOnEndTrackSwitch.setOnCheckedChangeListener(onSwitchCheckedChangeListener)
        binding.seekbar.setOnSeekBarChangeListener(seekbarChangeListener)
        binding.startStopTimer.setOnClickListener(startStopTimerClickListener)
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { viewState ->
                    when (viewState) {
                        is SleepTimerViewModel.State.SetupSleepTimer -> {
                            val durSec = viewState.duration/1000f
                            val progress = durSec / 60f
                            binding.seekbar.setProgress(progress, showMinutesOnly = false)
                        }
                        is SleepTimerViewModel.State.ShowStart -> {
                            binding.startStopTimer.text = "Start"
//                            dismiss()
                        }
                        is SleepTimerViewModel.State.ShowStop -> {
                            binding.startStopTimer.text = "Stop"
//                            dismiss()
                        }
                        is SleepTimerViewModel.State.TickTimer -> {
//                            val durSec = viewState.curDuration/1000f
//                            val progress = durSec / 60f
//                            binding.seekbar = progress
                        }
                    }
                }
            }
        }
    }
}