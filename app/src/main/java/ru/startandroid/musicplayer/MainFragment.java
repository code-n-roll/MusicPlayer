package ru.startandroid.musicplayer;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static ru.startandroid.musicplayer.MainActivity.convertDpToPixels;

/**
 * Created by NotePad.by on 28.10.2016.
 */

public class MainFragment extends Fragment {
    private Toolbar toolbar;
    private SongCardViewAdapter songCardViewAdapter;
    private FullscreenPlayerFragment fpf;
    private String FULLSCREEN_TAG = "fullscreenFragment";
    private SongCardView curSelectedSong;
    private MediaPlayer mediaPlayer;
    private File path;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_main, container, false);



        toolbar = (Toolbar) view.findViewById(R.id.activityMainToolbar);
        MainActivity ma = (MainActivity) getActivity();
        ma.setSupportActionBar(toolbar);
        ma.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ma.getSupportActionBar().setDisplayShowHomeEnabled(false);

        path = MainActivity.getPath();

        GridView recentlySongListView = (GridView) view.findViewById(R.id.recently_played_songs);

        ArrayList<SongCardView> listRecSongs = MainActivity.getListRecentlySongs();
        Collections.reverse(listRecSongs);
        songCardViewAdapter = new SongCardViewAdapter(getActivity(),
                R.layout.content_recently_songs, listRecSongs);


        recentlySongListView.setAdapter(songCardViewAdapter);


        recentlySongListView.setNumColumns(listRecSongs.size());
        ViewGroup.LayoutParams layoutParams = recentlySongListView.getLayoutParams();
        layoutParams.width = convertDpToPixels(123, getActivity())*listRecSongs.size();
        layoutParams.height = convertDpToPixels(210, getActivity());
        recentlySongListView.setLayoutParams(layoutParams);
  



        AdapterView.OnItemClickListener itemClickListener = new
                AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position,
                                            long id){
                        if (curSelectedSong == null)
                            curSelectedSong = (SongCardView)
                                    parent.getItemAtPosition(position);

                        SongCardView justSelectedSongCardView = (SongCardView)
                                parent.getItemAtPosition(position);

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        if (fpf == null){
                            fpf = new FullscreenPlayerFragment();
                        }



                        if (curSelectedSong == justSelectedSongCardView){
                            if (fpf.getResume()) {
                                fpf.setContinued(true);
                                ft.replace(R.id.fContainerActMain, fpf);
                                ft.addToBackStack(FULLSCREEN_TAG);
                                ft.commit();
                                fm.executePendingTransactions();
                                fpf.setDataFullscreenPlayer(justSelectedSongCardView);
//                                fpf.getPlayImageButton().setSelected(false);
//                                fpf.getPlayImageButton().callOnClick();
                            }
                            else {
                                fpf.setContinued(false);
                                ft.replace(R.id.fContainerActMain, fpf);
                                ft.addToBackStack(FULLSCREEN_TAG);
                                ft.commit();
                                fm.executePendingTransactions();
                                fpf.setDataFullscreenPlayer(justSelectedSongCardView);
                                fpf.getPlayImageButton().setSelected(false);
                                fpf.getPlayImageButton().callOnClick();
                            }

                        } else {
                            curSelectedSong = justSelectedSongCardView;

                            File file = new File(path,
                                    TracklistActivity.getFilesNames().
                                            get(curSelectedSong.getId()));
                            fpf.setFileNewSong(file);
                            ft.replace(R.id.fContainerActMain, fpf);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                            fm.executePendingTransactions();
                            fpf.setDataFullscreenPlayer(justSelectedSongCardView);
                        }
                        fpf.setSongFullTimeSeekBarProgress();


                    }
                };
        recentlySongListView.setOnItemClickListener(itemClickListener);

        return view;
    }
}
