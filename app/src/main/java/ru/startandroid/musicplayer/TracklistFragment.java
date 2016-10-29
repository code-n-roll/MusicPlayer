package ru.startandroid.musicplayer;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
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


/**
 * Created by NotePad.by on 18.10.2016.
 */

public class TracklistFragment extends Fragment {

    private SongCardViewAdapter songCardViewAdapter;
    private ArrayList<SongCardView> songsCardView;
    private Toolbar toolbar;
    private FullscreenPlayerFragment fpf;
    private File path;
    private MediaPlayer mediaPlayer;
    private String FULLSCREEN_TAG = "fullscreenFragment";
    private String LOG_TAG = "myLogs";
    private SongCardView curSelectedSong;


    public MediaPlayer getMediaPlayer(){
        return this.mediaPlayer;
    }
    public void setCurSelectedSong(SongCardView item){
        this.curSelectedSong = item;
    }
    public SongCardViewAdapter getSongCardViewAdapterFragment(){
        return this.songCardViewAdapter;
    }
    public FullscreenPlayerFragment getFpf(){
        return this.fpf;
    }
    public File getPath(){
        return this.path;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "TracklistFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(LOG_TAG, "TracklistFragment onCreate");
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracklist, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.tracklist_toolbar);

        TracklistActivity ta = (TracklistActivity) getActivity();
        ta.setSupportActionBar(toolbar);
        ta.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ta.getSupportActionBar().setDisplayShowHomeEnabled(true);

        WindowManager.LayoutParams attrs = ta.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ta.getWindow().setAttributes(attrs);
        ta.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GenresActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


//        view.setOnKeyListener(new View.OnKeyListener(){
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event){
//                if (keyCode == KeyEvent.KEYCODE_BACK){
//                    if (getFpf().getCurMediaPlayer().isPlaying()) {
//                        getFpf().getCurMediaPlayer().pause();
//                        getFpf().getCurMediaPlayer().stop();
//                        getFpf().getPlayImageButton().setSelected(!getFpf().getPlayImageButton().isSelected());
//                        getFpf().setStatePlayButton();
//                        getFpf().setResume(false);
//                        getFpf().getMyHandler().removeCallbacks(getFpf().getUpdateSongTime());
//                    }
//                }
//                return true;
//            }
//        });



        final GridView tracklistList = (GridView) view.findViewById(R.id.tracklist_list);
        songsCardView = ((TracklistActivity) getActivity()).getSongsCardView();

        if (songCardViewAdapter == null) {
            songCardViewAdapter = new SongCardViewAdapter(getActivity(),
                    R.layout.content_songcardview
                    , songsCardView);
        }
        tracklistList.setAdapter(songCardViewAdapter);

//        tracklistList.setDivider(null);

        AdapterView.OnItemClickListener itemClickListener = new
                AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position,
                                            long id){
                        curSelectedSong = MainActivity.getCurSelectedSong();
                        if ( curSelectedSong == null)
                            setCurSelectedSong((SongCardView)
                                    parent.getItemAtPosition(position));


                        SongCardView justSelectedSongCardView = (SongCardView)
                                parent.getItemAtPosition(position);

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        if (fpf == null){
                            fpf = new FullscreenPlayerFragment();
                        }

                        if (fpf.getCurMediaPlayer() == null) {
                            mediaPlayer = new MediaPlayer();
                            getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                            path = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_MUSIC
                            );
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        }

                        if (curSelectedSong == justSelectedSongCardView){
                            if (fpf.getResume()) {
                                fpf.setContinued(true);
                                ft.replace(R.id.fContainerActTracklist, fpf);
                                ft.addToBackStack(FULLSCREEN_TAG);
                                ft.commit();
                                fm.executePendingTransactions();
                                fpf.setDataFullscreenPlayer(justSelectedSongCardView);
//                                fpf.getPlayImageButton().setSelected(false);
//                                fpf.getPlayImageButton().callOnClick();
                            }
                            else {
                                fpf.setContinued(false);
                                ft.replace(R.id.fContainerActTracklist, fpf);
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
                            ft.replace(R.id.fContainerActTracklist, fpf);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                            fm.executePendingTransactions();
                            fpf.setDataFullscreenPlayer(justSelectedSongCardView);
                        }
                        fpf.setSongFullTimeSeekBarProgress();
                    }
                };

        tracklistList.setOnItemClickListener(itemClickListener);

        AdapterView.OnItemLongClickListener itemLongClickListener = new
                AdapterView.OnItemLongClickListener(){
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
                                                   long id){
                        SongCardView selectedSongCardView = (SongCardView)
                                parent.getItemAtPosition(position);

                        Bundle args = new Bundle();
                        args.putParcelable("selectedSongCardView", selectedSongCardView);
                        args.putParcelableArrayList("songsCardView", songsCardView);
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
