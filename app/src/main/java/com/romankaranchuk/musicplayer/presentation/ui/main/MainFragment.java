package com.romankaranchuk.musicplayer.presentation.ui.main;

import android.annotation.TargetApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.util.LinkedList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.presentation.ui.cube.CubeFragment;
import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TracklistFragment;



public class MainFragment extends Fragment {
    Toolbar toolbar;
    private PlayerFragment fpf;
    private String  FULLSCREEN_TAG = "fullscreenFragment",
                    CUBE_TAG ="cubeFragment",
                    TRACKLIST_TAG = "TRACKLIST_TAG";
    private Song curSelectedSong;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        toolbar = view.findViewById(R.id.activityMainToolbar);
        GridView recentlySongListView = view.findViewById(R.id.recently_played_songs);

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
                        fpf.setFileNewSong(new File(curSelectedSong.getPath()));
                        ft.replace(R.id.fContainerActMain, fpf);
                        ft.addToBackStack(FULLSCREEN_TAG);
                        ft.commit();
                    }
                }
            };
        recentlySongListView.setOnItemClickListener(itemClickListener);

        Button openSongs = view.findViewById(R.id.songs);
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
}
