package com.romankaranchuk.musicplayer.presentation.ui.tracklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.data.Song

import java.util.ArrayList

class AudioSettingsFragment : DialogFragment(), AdapterView.OnItemClickListener {
    private val itemsSettings = object : ArrayList<String>() {
        init {
            add("Edit")
            add("Delete")
            add("Delete from cache")
        }
    }

    private var selectedSong: Song? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "AudioSettingFragment onCreateView")
        return inflater.inflate(R.layout.fragment_audio_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = this.arguments
        if (args != null) {
            selectedSong = args.getParcelable(SELECTED_SONG)
        }

        val listAudioSettingsAdapter = ArrayAdapter(context!!,
                R.layout.content_fragment_audio_settings,
                R.id.item_audio_setting,
                itemsSettings)

        val listAudioSettingsView = view.findViewById<ListView>(R.id.list_audio_settings)
        listAudioSettingsView.adapter = listAudioSettingsAdapter
        listAudioSettingsView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int,
                             id: Long) {
        dismiss()
        when (itemsSettings[position]) {
            "Edit" -> {
                val args = Bundle()
                args.putParcelable(SELECTED_SONG, selectedSong)
                val editAudioFragment = EditAudioFragment()
                editAudioFragment.arguments = args
                dismiss()
                editAudioFragment.show(requireActivity().supportFragmentManager, "edit")
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

    companion object {

        private val LOG_TAG = "myLogs"
        private val SELECTED_SONG = "selectedSong"
        private val TRACKLIST_TAG = "TRACKLIST_TAG"
    }
}
