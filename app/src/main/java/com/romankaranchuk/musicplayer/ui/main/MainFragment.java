package com.romankaranchuk.musicplayer.ui.main;

import android.annotation.TargetApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.util.LinkedList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.ui.cube.CubeFragment;
import com.romankaranchuk.musicplayer.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.ui.tracklist.SongListAdapter;
import com.romankaranchuk.musicplayer.ui.tracklist.TracklistFragment;

/**
 * Created by NotePad.by on 28.10.2016.
 */

public class MainFragment extends Fragment {
    Toolbar toolbar;
    SongListAdapter songListAdapter;
    private PlayerFragment fpf;
    private String FULLSCREEN_TAG = "fullscreenFragment", CUBE_TAG ="cubeFragment";
    private Song curSelectedSong;
    private String LOG_TAG = "myLogs", TRACKLIST_TAG = "TRACKLIST_TAG";


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "MainFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "MainFragment onCreate");
    }

    @TargetApi(23)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.activityMainToolbar);
        GridView recentlySongListView = (GridView) view.findViewById(R.id.recently_played_songs);

        MainActivity ma = (MainActivity) getActivity();
        ma.setSupportActionBar(toolbar);
        ma.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ma.getSupportActionBar().setDisplayShowHomeEnabled(false);
        restoreDefaultToolbar(ma);



        LinkedList<Song> listRecSongs = MainActivity.getListRecentlySongs();
//        songListAdapter = new SongListAdapter(getActivity(),R.layout.content_recently_songs, listRecSongs);
//        recentlySongListView.setAdapter(songListAdapter);

//        recentlySongListView.setNumColumns(listRecSongs.size());
//        ViewGroup.LayoutParams layoutParams = recentlySongListView.getLayoutParams();
//        layoutParams.width = MathUtils.convertDpToPixels(123, getActivity())*listRecSongs.size();
//        layoutParams.height = MathUtils.convertDpToPixels(210, getActivity());
//        recentlySongListView.setLayoutParams(layoutParams);



        AdapterView.OnItemClickListener itemClickListener = new
            AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id){

                    if (curSelectedSong == null)
                        curSelectedSong = (Song) parent.getItemAtPosition(position);

                    Song justSelectedSong = (Song) parent.getItemAtPosition(position);

                    FragmentTransaction ft = getActivity().
                            getSupportFragmentManager().
                            beginTransaction();

                    if (fpf == null){
                        fpf = new PlayerFragment();
                    }



                    if (curSelectedSong == justSelectedSong){
                        if (fpf.getResume()) {
                            fpf.setContinued(true);
                            ft.replace(R.id.fContainerActMain, fpf);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                        }
                        else {
                            fpf.setContinued(false);
                            ft.replace(R.id.fContainerActMain, fpf);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                        }

                    } else {
                        curSelectedSong = justSelectedSong;
                        File file = new File(curSelectedSong.getPath());
                        fpf.setFileNewSong(file);

                        ft.replace(R.id.fContainerActMain, fpf);
                        ft.addToBackStack(FULLSCREEN_TAG);
                        ft.commit();

                        fpf.getPagerFullscreenPlayer().setCurrentItem(Integer.parseInt(curSelectedSong.getId()));
                        fpf.setFastForwardCall(false);
                        fpf.setFastBackwardCall(false);
                    }
                }
            };
        recentlySongListView.setOnItemClickListener(itemClickListener);

        Button openSongs = (Button) view.findViewById(R.id.songs);
        openSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openCube();
                openPlayer();
            }
        });

        return view;
    }
    public void openPlayer(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        if (getActivity().getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG) == null) {
            TracklistFragment tracklist = new TracklistFragment();
            ft.replace(R.id.fContainerActMain, tracklist, TRACKLIST_TAG);
            ft.addToBackStack(TRACKLIST_TAG);
        }
        ft.commit();
    }

    public void openCube(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        if (getActivity().getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG) == null) {
            CubeFragment cube = new CubeFragment();
            ft.replace(R.id.fContainerActMain, cube, CUBE_TAG);
            ft.addToBackStack(CUBE_TAG);
        }
        ft.commit();
    }

    @TargetApi(19)
    public void restoreDefaultToolbar(MainActivity ma){
        WindowManager.LayoutParams attrs = ma.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ma.getWindow().setAttributes(attrs);
        ma.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "MainFragment onActivityCreated");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "MainFragment onStart");
    }
    @Override
    public void onResume(){
        super.onResume();

        Log.d(LOG_TAG, "MainFragment onResume");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "MainFragment onPause");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "MainFragment onStop");
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(LOG_TAG, "MainFragment onDestroyView");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "MainFragment onDestroy");
    }
    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(LOG_TAG, "MainFragment onDetach");
    }



}
