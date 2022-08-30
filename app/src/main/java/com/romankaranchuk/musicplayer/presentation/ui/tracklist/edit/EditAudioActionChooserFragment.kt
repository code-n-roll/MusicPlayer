package com.romankaranchuk.musicplayer.presentation.ui.tracklist.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment

import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.databinding.FragmentEditAudioActionChooserBinding
import timber.log.Timber

import java.util.ArrayList

class EditAudioActionChooserFragment : DialogFragment() {

    companion object {
        val SELECTED_SONG = "selectedSong"

        private val itemsSettings = object : ArrayList<String>() {
            init {
                add("Edit")
                add("Delete")
                add("Delete from cache")
            }
        }
    }

    private var _binding: FragmentEditAudioActionChooserBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedSong: Song

    private val onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
        dismiss()
        when (itemsSettings[position]) {
            "Edit" -> {
                dismiss()
                EditAudioFragment.newInstance(selectedSong)
                    .show(requireActivity().supportFragmentManager, "edit")
            }
            "Delete" -> {
            }//                TrackListFragment fragment =
            //                        (TrackListFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG);
            //                TrackListAdapter adapter = fragment.getSongListAdapter();
            //                Bundle args = getArguments();
            //                Song selected = args.getParcelable(SELECTED_SONG);
            //                ArrayList<Song> list = args.getParcelableArrayList(LIST_SONGS);
            //                if (list != null)
            //                    list.remove(selected);
            //                adapter.notifyDataSetChanged();
            "Delete from cache" -> {
                Toast.makeText(activity, itemsSettings[position],
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView")
        _binding = FragmentEditAudioActionChooserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parseArguments()
        setupAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun parseArguments() {
        val song: Song? = arguments?.getParcelable(SELECTED_SONG)
        if (song == null) {
            Timber.e("song is null")
            Toast.makeText(requireContext(), "song is null", Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            selectedSong = song
        }
    }

    private fun setupAdapter() {
        val listAudioSettingsAdapter = ArrayAdapter(
            requireContext(),
            R.layout.content_fragment_audio_settings,
            R.id.item_audio_setting,
            itemsSettings
        )

        binding.listAudioSettings.adapter = listAudioSettingsAdapter
        binding.listAudioSettings.onItemClickListener = onItemClickListener
    }
}
