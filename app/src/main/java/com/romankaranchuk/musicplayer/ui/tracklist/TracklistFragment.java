package com.romankaranchuk.musicplayer.ui.tracklist;

import android.content.ServiceConnection;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.ui.genres.GenresActivity;
import com.romankaranchuk.musicplayer.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.ui.main.MainFragment;


/**
 * Created by NotePad.by on 18.10.2016.
 */

public class TracklistFragment extends Fragment {

    private SongListAdapter songListAdapter;
    private ArrayList<Song> songsCardView;
    private Toolbar toolbar;
    private PlayerFragment fpf;
    private MainFragment mf;
    private File path;
    private String FULLSCREEN_TAG = "fullscreenFragment",
                    TRACKLIST_TAG = "tracklistFragment",
                    SHOW_FPF_TAG = "showFpf",
                    SELECTED_SONG = "selectedSong",
                    LIST_SONGS = "songsSongCardView";
    private String LOG_TAG = "myLogs";
    private Song curSelectedSong;


    public void setCurSelectedSong(Song item){
        this.curSelectedSong = item;
    }
    public SongListAdapter getSongCardViewAdapterFragment(){
        return this.songListAdapter;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "TracklistFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);


//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//            String tag = getActivity().getIntent().getStringExtra("TAG_SHOW_FPF");
//            if (tag != null && tag.equals(SHOW_FPF_TAG)){//|| getSupportFragmentManager().findFragmentByTag(FULLSCREEN_TAG) != null){
//                TracklistFragment tf = (TracklistFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG);
//                if (tf.getFpf() == null) {
//                    tf.setFpf(new PlayerFragment());
//                }
//                tf.getFpf().setContinued(true);
//                ft.replace(R.id.fContainerActTracklist, tf.getFpf(), FULLSCREEN_TAG);
//                ft.addToBackStack(FULLSCREEN_TAG);
//            }
//        ft.commit();

        Log.d(LOG_TAG, "TracklistFragment onCreate");
    }


    public void restoreDefaultToolbar(TracklistActivity ta){
        WindowManager.LayoutParams attrs = ta.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ta.getWindow().setAttributes(attrs);
        ta.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracklist, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.tracklist_toolbar);



        TracklistActivity ta = (TracklistActivity) getActivity();
        ta.setSupportActionBar(toolbar);
        ta.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ta.getSupportActionBar().setDisplayShowHomeEnabled(true);

        restoreDefaultToolbar(ta);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GenresActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        final GridView tracklistList = (GridView) view.findViewById(R.id.tracklist_list);
        songsCardView = TracklistActivity.getSongsCardView();



        if (songListAdapter == null) {
            songListAdapter = new SongListAdapter(getActivity(),
                    R.layout.content_songcardview
                    , songsCardView);
        }
        tracklistList.setAdapter(songListAdapter);

//        tracklistList.setDivider(null);

        AdapterView.OnItemClickListener itemClickListener = new
                AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position,
                                            long id){
                        curSelectedSong = TracklistActivity.getCurSelectedSong();
                        if ( curSelectedSong == null)
                            setCurSelectedSong((Song)
                                    parent.getItemAtPosition(position));


                        Song justSelectedSong = (Song)
                                parent.getItemAtPosition(position);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        if (fpf == null){
                            fpf = new PlayerFragment();
                        }


                        if (curSelectedSong == justSelectedSong){
                            if (fpf.getResume()) {
                                fpf.setContinued(true);
                                ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                                ft.addToBackStack(FULLSCREEN_TAG);
                                ft.commit();
                            }
                            else {
                                fpf.setContinued(false);
                                TracklistActivity.setCurSelectedSong(curSelectedSong);
                                ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                                ft.addToBackStack(FULLSCREEN_TAG);
                                ft.commit();
                            }

                        } else {
                            curSelectedSong = justSelectedSong;
                            TracklistActivity.setCurSelectedSong(curSelectedSong);
                            path = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_MUSIC
                            );
                            File file = new File(path,curSelectedSong.getPath());
                            fpf.setFileNewSong(file);
                            ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                            fm.executePendingTransactions();

                            fpf.getPagerFullscreenPlayer().setCurrentItem(Integer.parseInt(curSelectedSong.getId()));
                            fpf.setFastForwardCall(false);
                            fpf.setFastBackwardCall(false);
                        }
//                        fpf.setSongFullTimeSeekBarProgress();
                    }
                };

        tracklistList.setOnItemClickListener(itemClickListener);

        AdapterView.OnItemLongClickListener itemLongClickListener = new
                AdapterView.OnItemLongClickListener(){
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
                                                   long id){
                        Song selectedSong = (Song)
                                parent.getItemAtPosition(position);

                        Bundle args = new Bundle();
                        args.putParcelable(SELECTED_SONG, selectedSong);
                        args.putParcelableArrayList(LIST_SONGS, songsCardView);
                        AudiofileSettingsFragment audiofileSettingsFragment = new AudiofileSettingsFragment();

                        audiofileSettingsFragment.setArguments(args);
                        audiofileSettingsFragment.show(getActivity().getFragmentManager(), "dialog");
                        return true;
                    }
                };
        tracklistList.setOnItemLongClickListener(itemLongClickListener);


//        registerForContextMenu(tracklistList);
        Log.d(LOG_TAG, "TracklistFragment onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "TracklistFragment onActivityCreated");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "TracklistFragment onStart");
    }





    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "TracklistFragment onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "TracklistFragment onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "TracklistFragment onStop");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(LOG_TAG, "TracklistFragment onDestroyView");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "TracklistFragment onDestroy");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(LOG_TAG, "TracklistFragment onDetach");
    }
}
