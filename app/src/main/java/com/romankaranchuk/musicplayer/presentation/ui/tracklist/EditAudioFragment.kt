package com.romankaranchuk.musicplayer.presentation.ui.tracklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.data.Song

class EditAudioFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "EditAudioFragment onCreateView")
        return inflater.inflate(R.layout.fragment_edit_audio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = this.arguments
        if (args != null) {
            selectedSong = args.getParcelable(SELECTED_SONG)
        }

        val song_name = view.findViewById<EditText>(R.id.edit_song_name)
        val artist_name = view.findViewById<EditText>(R.id.edit_artist_name)
        song_name.setText(selectedSong!!.title)
        artist_name.setText(selectedSong!!.nameArtist)

        val edit_cancel = view.findViewById<Button>(R.id.edit_cancel)
        val edit_ok = view.findViewById<Button>(R.id.edit_ok)

        edit_cancel.setOnClickListener { dismiss() }

        edit_ok.setOnClickListener {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "EditAudioFragment onCreate")
    }

    companion object {
        private val LOG_TAG = "myLogs"
        private val SELECTED_SONG = "selectedSong"
        private var selectedSong: Song? = null
    }
}
