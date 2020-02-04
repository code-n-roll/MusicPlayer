package com.romankaranchuk.musicplayer.presentation.ui.tracklist;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;



public class EditAudioFragment extends DialogFragment {
    String LOG_TAG = "myLogs", SELECTED_SONG = "selectedSong";
    Song selectedSong;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Bundle args = this.getArguments();
        if (args != null){
            selectedSong = args.getParcelable(SELECTED_SONG);
        }

        View view = inflater.inflate(R.layout.fragment_edit_audio, container, false);

        final EditText song_name = view.findViewById(R.id.edit_song_name);
        EditText artist_name = view.findViewById(R.id.edit_artist_name);
        song_name.setText(selectedSong.getTitle());
        artist_name.setText(selectedSong.getNameArtist());

        Button edit_cancel = view.findViewById(R.id.edit_cancel);
        Button edit_ok= view.findViewById(R.id.edit_ok);

        edit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        edit_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                selectedSong.setName(song_name.getText().toString());
//                ArrayList<Song> list = TracklistFragment.getSongs();
//                for(int i = 0; i < list.size(); i++){
//                    if (list.get(i).equals(selectedSong)){
//                        list.set(i,selectedSong);
//                    }
//                }
//                selectedSong.getAlbumId();
                dismiss();
            }
        });

        Log.d(LOG_TAG, "EditAudioFragment onCreateView");
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "EditAudioFragment onCreate");
    }
}
