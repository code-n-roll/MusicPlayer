package com.romankaranchuk.musicplayer.presentation.ui.tracklist.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.databinding.FragmentEditAudioBinding
import timber.log.Timber

class EditAudioFragment : DialogFragment() {

    private var _binding: FragmentEditAudioBinding? = null
    private val binding get() = _binding!!

    companion object {
        private val SELECTED_SONG = "selectedSong"
        private var selectedSong: Song? = null

        fun newInstance(selectedSong: Song): EditAudioFragment {
            return EditAudioFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SELECTED_SONG, selectedSong)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreateView")
        _binding = FragmentEditAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = this.arguments
        if (args != null) {
            selectedSong = args.getParcelable(SELECTED_SONG)
        }

        setupUI()
        setupListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
    }

    private fun setupUI() {
        binding.editSongName.setText(selectedSong!!.title)
        binding.editArtistName.setText(selectedSong!!.nameArtist)
    }

    private fun setupListeners() {
        binding.editCancel.setOnClickListener { dismiss() }
        binding.editOk.setOnClickListener {
            //                selectedSong.setName(song_name.getText().toString());
            //                ArrayList<Song> list = TrackListFragment.getSongs();
            //                for(int i = 0; i < list.size(); i++){
            //                    if (list.get(i).equals(selectedSong)){
            //                        list.set(i,selectedSong);
            //                    }
            //                }
            //                selectedSong.getAlbumId();
            dismiss()
        }
    }
}
