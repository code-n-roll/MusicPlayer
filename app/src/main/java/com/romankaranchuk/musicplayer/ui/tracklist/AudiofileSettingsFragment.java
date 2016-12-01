package com.romankaranchuk.musicplayer.ui.tracklist;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
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


public class AudiofileSettingsFragment extends DialogFragment implements
        AdapterView.OnItemClickListener {

    private String LOG_TAG = "myLogs",
            SELECTED_SONG = "selectedSong",
            LIST_SONGS = "songsSongCardView";
    ArrayList<String> itemsAudiofileSettings = new ArrayList<String>(){{
        add("Edit");
        add("Delete");
        add("Delete from cache");
    }};

    ListView listAudiofileSettingsView;
    ArrayAdapter<String> listAudiofileSettingsAdapter;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "AudiofileSettingFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "AudiofileSettingFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_audiofile_settings, null, false);

        listAudiofileSettingsView = (ListView) view.findViewById(R.id.list_audiofile_settings);
        Log.d(LOG_TAG, "AudiofileSettingFragment onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        listAudiofileSettingsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.content_fragment_audiofile_settings,
                R.id.item_audiofile_setting,
                itemsAudiofileSettings);

        listAudiofileSettingsView.setAdapter(listAudiofileSettingsAdapter);
        listAudiofileSettingsView.setOnItemClickListener(this);
        Log.d(LOG_TAG, "AudiofileSettingFragment onActivityCreated");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "AudiofileSettingFragment onStart");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "AudiofileSettingFragment onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "AudiofileSettingFragment onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "AudiofileSettingFragment onStop");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(LOG_TAG, "AudiofileSettingFragment onDestroyView");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "AudiofileSettingFragment onDestroy");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(LOG_TAG, "AudiofileSettingFragment onDetach");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id){
        dismiss();
        switch(itemsAudiofileSettings.get(position)) {
            case "Edit": {
                Toast.makeText(getActivity(), itemsAudiofileSettings.get(position),
                        Toast.LENGTH_SHORT).show();
                break;
            }
            case "Delete": {
                SongListAdapter adapter =
                        ((TracklistActivity) getActivity()).getSongCardViewAdapter();
                Bundle args = getArguments();
                Song selected = args.getParcelable(SELECTED_SONG);
                ArrayList<Song> list = args.getParcelableArrayList(LIST_SONGS);
                list.remove(selected);
                adapter.notifyDataSetChanged();
                break;
            }
            case "Delete from cache": {
                Toast.makeText(getActivity(), itemsAudiofileSettings.get(position),
                        Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
