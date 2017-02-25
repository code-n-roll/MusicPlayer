package com.romankaranchuk.musicplayer.ui.tracklist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;


public class AudioSettingsFragment extends DialogFragment implements
        AdapterView.OnItemClickListener {

    String LOG_TAG = "myLogs",
            SELECTED_SONG = "selectedSong",
            LIST_SONGS = "songsSongCardView",
    TRACKLIST_TAG = "TRACKLIST_TAG";
    ArrayList<String> itemsSettings = new ArrayList<String>(){{
        add("Edit");
        add("Delete");
        add("Delete from cache");
    }};

    ListView listAudioSettingsView;
    ArrayAdapter<String> listAudioSettingsAdapter;
    Song selectedSong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        Bundle args = this.getArguments();
        if (args != null){
            selectedSong = args.getParcelable(SELECTED_SONG);
        }
        View view = inflater.inflate(R.layout.fragment_audio_settings, container, false);
        listAudioSettingsView = (ListView) view.findViewById(R.id.list_audio_settings);

        Log.d(LOG_TAG, "AudioSettingFragment onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        listAudioSettingsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.content_fragment_audio_settings,
                R.id.item_audio_setting,
                itemsSettings);

        listAudioSettingsView.setAdapter(listAudioSettingsAdapter);
        listAudioSettingsView.setOnItemClickListener(this);
        Log.d(LOG_TAG, "AudioSettingFragment onActivityCreated");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id){
        dismiss();
        switch(itemsSettings.get(position)) {
            case "Edit": {
                Bundle args = new Bundle();
                args.putParcelable(SELECTED_SONG, selectedSong);
                EditAudioFragment editAudioFragment = new EditAudioFragment();
                editAudioFragment.setArguments(args);
                dismiss();
                editAudioFragment.show(getFragmentManager(), "edit");
                break;
            }
            case "Delete": {
                TracklistFragment someFragment = (TracklistFragment)
                        getActivity().getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG);
                SongListAdapter adapter =
                        someFragment.getSongListAdapter();
                Bundle args = getArguments();
                Song selected = args.getParcelable(SELECTED_SONG);
                ArrayList<Song> list = args.getParcelableArrayList(LIST_SONGS);
                if (list != null)
                    list.remove(selected);
                adapter.notifyDataSetChanged();
                break;
            }
            case "Delete from cache": {
                Toast.makeText(getActivity(), itemsSettings.get(position),
                        Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "AudioSettingFragment onAttach");
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "AudioSettingFragment onCreate");
    }
}
