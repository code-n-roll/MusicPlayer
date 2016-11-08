package ru.startandroid.musicplayer;

import android.Manifest;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private String LOG_TAG = "myLogs";

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "MainFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
        Log.d(LOG_TAG, "MainFragment onCreate");
    }

    public MediaPlayer getCurMediaPlayer(){
        return this.mediaPlayer;
    }

    public void restoreDefaultToolbar(MainActivity ma){
        WindowManager.LayoutParams attrs = ma.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ma.getWindow().setAttributes(attrs);
        ma.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.activityMainToolbar);
        MainActivity ma = (MainActivity) getActivity();
        ma.setSupportActionBar(toolbar);
        ma.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ma.getSupportActionBar().setDisplayShowHomeEnabled(false);

        restoreDefaultToolbar(ma);

        path = MainActivity.getPath();

        GridView recentlySongListView = (GridView) view.findViewById(R.id.recently_played_songs);

        ArrayList<SongCardView> listRecSongs = MainActivity.getListRecentlySongs();
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

                        FragmentManager fm = getActivity().getSupportFragmentManager();
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
                                ((FspPageFragment)getActivity().getSupportFragmentManager().findFragmentByTag("fspPageFragment"))
                                .setDataFullscreenPlayer(fpf,justSelectedSongCardView);
//                                fpf.getPlayImageButton().setSelected(false);
//                                fpf.getPlayImageButton().callOnClick();
                            }
                            else {
                                fpf.setContinued(false);
                                ft.replace(R.id.fContainerActMain, fpf);
                                ft.addToBackStack(FULLSCREEN_TAG);
                                ft.commit();
                                fm.executePendingTransactions();
                                ((FspPageFragment)getActivity().getSupportFragmentManager().findFragmentByTag("fspPageFragment"))
                                .setDataFullscreenPlayer(fpf,justSelectedSongCardView);
                                fpf.getPlayImageButton().setSelected(false);
                                fpf.getPlayImageButton().callOnClick();
                            }

                        } else {
                            curSelectedSong = justSelectedSongCardView;

                            File file = new File(path,curSelectedSong.getFilePath());
                            fpf.setFileNewSong(file);
                            ft.replace(R.id.fContainerActMain, fpf);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                            fm.executePendingTransactions();
                            ((FspPageFragment)getActivity().getSupportFragmentManager().findFragmentByTag("fspPageFragment"))
                                    .setDataFullscreenPlayer(fpf, justSelectedSongCardView);
                        }
                        fpf.setSongFullTimeSeekBarProgress();


                    }
                };
        recentlySongListView.setOnItemClickListener(itemClickListener);

        return view;
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
