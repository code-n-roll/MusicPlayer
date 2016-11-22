package ru.startandroid.musicplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
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


/**
 * Created by NotePad.by on 18.10.2016.
 */

public class TracklistFragment extends Fragment {

    private SongCardViewAdapter songCardViewAdapter;
    private ArrayList<SongCardView> songsCardView;
    private Toolbar toolbar;
    private FullscreenPlayerFragment fpf;
    private MainFragment mf;
    private File path;
    private String FULLSCREEN_TAG = "fullscreenFragment";
    private String LOG_TAG = "myLogs";
    private SongCardView curSelectedSong;
    private FspPageFragment fspPageFragment;

    private Intent intentPlayerService;
    ServiceConnection serviceConnection;
    private PlayerService playerService;
    boolean bound = false;


    public void setCurSelectedSong(SongCardView item){
        this.curSelectedSong = item;
    }
    public SongCardViewAdapter getSongCardViewAdapterFragment(){
        return this.songCardViewAdapter;
    }
    public FullscreenPlayerFragment getFpf(){
        return this.fpf;
    }
    public void setFpf(FullscreenPlayerFragment fpf){this.fpf = fpf;}
    public File getPath(){
        return this.path;
    }
    public SongCardView getCurSelectedSong(){
        return this.curSelectedSong;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "TracklistFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);

//        if (intentPlayerService == null)
//            intentPlayerService = new Intent(getActivity(), PlayerService.class);
//
//        if (serviceConnection == null)
//            serviceConnection = new ServiceConnection() {
//                @Override
//                public void onServiceConnected(ComponentName name, IBinder binder) {
//                    Log.d(LOG_TAG, "FullscreenPlayerFragment onServiceConnected");
//                    playerService = ((PlayerService.PlayerBinder) binder).getService();
//                    bound = true;
//                }
//
//                @Override
//                public void onServiceDisconnected(ComponentName name) {
//                    Log.d(LOG_TAG, "FullscreenPlayerFragment onServiceDisconnected");
//                    bound = false;
//                }
//            };

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
        songsCardView = TracklistActivity.getSongsCardView();

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
                        curSelectedSong = TracklistActivity.getCurSelectedSong();
                        if ( curSelectedSong == null)
                            setCurSelectedSong((SongCardView)
                                    parent.getItemAtPosition(position));


                        SongCardView justSelectedSongCardView = (SongCardView)
                                parent.getItemAtPosition(position);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        if (fpf == null){
                            fpf = new FullscreenPlayerFragment();
                        }

                        //(mf!= null && mf.getCurMediaPlayer() == null) &&
//                        if (fpf.getCurMediaPlayer() == null) {
//                            mediaPlayer = new MediaPlayer();
//                            getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
//                            path = Environment.getExternalStoragePublicDirectory(
//                                    Environment.DIRECTORY_MUSIC
//                            );
//                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                            fpf.setCurMediaPlayer(mediaPlayer);
//                        }

                        if (curSelectedSong == justSelectedSongCardView){
                            if (fpf.getResume()) {
                                fpf.setContinued(true);
                                ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                                ft.addToBackStack(FULLSCREEN_TAG);
                                ft.commit();
                                fm.executePendingTransactions();
//                                fpf.getPlayImageButton().setSelected(false);
//                                fpf.getPlayImageButton().callOnClick();
                            }
                            else {
                                fpf.setContinued(false);
                                TracklistActivity.setCurSelectedSong(curSelectedSong);
                                ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                                ft.addToBackStack(FULLSCREEN_TAG);
                                ft.commit();
                                fm.executePendingTransactions();
//                                ((FspPageFragment)getFragmentManager().findFragmentByTag("fspPageFragment"))
//                                .setDataFullscreenPlayer(fpf,justSelectedSongCardView);
                                fpf.getPlayImageButton().setSelected(false);
                                fpf.getPlayImageButton().callOnClick();
                            }

                        } else {
                            curSelectedSong = justSelectedSongCardView;
                            TracklistActivity.setCurSelectedSong(curSelectedSong);
                            path = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_MUSIC
                            );
                            File file = new File(path,curSelectedSong.getFilePath());
                            fpf.setFileNewSong(file);
                            ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                            fm.executePendingTransactions();

                            fpf.getPagerFullscreenPlayer().setCurrentItem(curSelectedSong.getId());
                            fpf.setFastForwardCall(false);
                            fpf.setFastBackwardCall(false);
//                            fpf.getFspPageFragment().setDataFullscreenPlayer(fpf, justSelectedSongCardView);
//                            ((FspPageFragment)getActivity().getFragmentManager().findFragmentByTag("fspPageFragment"))
//                            .setDataFullscreenPlayer(fpf,justSelectedSongCardView);
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

//        getActivity().startService(intentPlayerService);
//        getActivity().bindService(intentPlayerService, serviceConnection,0);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (playerService != null && playerService.isPlaying) {
            if (this.getFpf() == null) {
                this.setFpf(new FullscreenPlayerFragment());
            }
            this.getFpf().setContinued(true);
            ft.replace(R.id.fContainerActTracklist, this.fpf, FULLSCREEN_TAG);
            ft.addToBackStack(FULLSCREEN_TAG);

            ft.commit();
        }

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
//        if (bound){
//            getActivity().unbindService(serviceConnection);
//            bound = false;
//        }
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
