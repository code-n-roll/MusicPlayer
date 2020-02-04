package com.romankaranchuk.musicplayer.presentation.ui.main;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.presentation.ui.cube.CubeFragment;
import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment;

import java.io.File;
import java.util.LinkedList;

public class MainFragment extends Fragment {

    private static final String CUBE_TAG ="cubeFragment";
    private static final String TRACKLIST_TAG = "TRACKLIST_TAG";

    private Toolbar toolbar;
    private PlayerFragment fpf;
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
//        songListAdapter = new TrackListAdapter(getActivity(),R.layout.content_recently_songs, listRecSongs);
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
                        if (fpf.getIsResumed()) {
                            fpf.setContinued(true);
                            ft.replace(R.id.fragment_container_main_activity, fpf);
                            ft.addToBackStack(PlayerFragment.PLAYER_FRAGMENT_TAG);
                            ft.commit();
                        }
                        else {
                            fpf.setContinued(false);
                            ft.replace(R.id.fragment_container_main_activity, fpf);
                            ft.addToBackStack(PlayerFragment.PLAYER_FRAGMENT_TAG);
                            ft.commit();
                        }

                    } else {
                        curSelectedSong = justSelectedSong;
                        fpf.setFileNewSong(new File(curSelectedSong.getPath()));
                        ft.replace(R.id.fragment_container_main_activity, fpf);
                        ft.addToBackStack(PlayerFragment.PLAYER_FRAGMENT_TAG);
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
            TrackListFragment tracklist = new TrackListFragment();
            ft.replace(R.id.fragment_container_main_activity, tracklist, TRACKLIST_TAG);
            ft.addToBackStack(TRACKLIST_TAG);
        }
        ft.commit();
    }

    public void openCube(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        if (getActivity().getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG) == null) {
            CubeFragment cube = new CubeFragment();
            ft.replace(R.id.fragment_container_main_activity, cube, CUBE_TAG);
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
