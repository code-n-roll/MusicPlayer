package ru.startandroid.musicplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.RunnableFuture;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;


/**
 * Created by NotePad.by on 18.10.2016.
 */


public class FullscreenPlayerFragment extends Fragment {
    private ImageButton playImageButton, shuffleImageButton, replayImageButton,
            fastForwardButton, fastBackwardButton;
    private SeekBar seekBar;
    private TextView  timeEnd, nameSong, nameArtist;
    private TextView timeStart;
    private static SongCardView currentSong;
    private MediaPlayer curMediaPlayer;
    private static boolean resume = false, paused=false,continued = false;
    private static int curPosition, minutes;
    private static double startTime, finalTime, seconds;
    private Handler myHandler = new Handler();
    private static File fileCurrentSong, filePlayedSong;
    private String TRACKLIST_TAG = "tracklistFragment",
            PAGER_FULLSCREENPLAYER_TAG = "pagerFullscreenPlayer",
            TAG_PLAY = "playNotifPlayerReceiver",
            TAG_PLAY_BUT_PS_TO_F_BR = "playButtonFromPStoFragmentBR",
            TAG_FORWARD_BUT_PS_TO_F_BR = "forwardButtonFromPStoFragmentBR",
            TAG_BACKWARD_BUT_PS_TO_F_BR = "backwardButtonFromPStoFragmentBR",
            currentTimeSong;
    private boolean startThreadTime = true, startThreadSeekbar = true;
    private String LOG_TAG = "myLogs";
    private ViewPager pagerFullscreenPlayer;
    private int oldPosition=-1, newPosition=-1;
    private boolean fastButtons = false, swap = false,
            fastForwardCall = false, fastBackwardCall = false,
            bound = false;
    private FspPageFragment fspPageFragment;
    private FullscreenPlayerAdapter fullscreenPlayerAdapter;
    private Intent intentPlayerService;
    ServiceConnection serviceConnection;
    BroadcastReceiver playButtonFromServiceToFragmentBR,
    forwardButtonFromServiceToFragmentBR,
    backwardButtonFromServiceToFragmentBR;

    private PlayerService playerService;
    private File path;

    public void setPath(File path){
        this.path = path;
    }
    public void setCurrentSong(SongCardView currentSong){
        this.currentSong = currentSong;
    }
    public static SongCardView getCurrentSong(){
        return currentSong;
    }
    public static boolean getResume(){
        return resume;
    }
    public static void setPaused(Boolean value){
        paused = value;
    }
    public static Boolean getPaused(){
        return paused;
    }
    public void setContinued(boolean state){
        continued = state;
    }

    public MediaPlayer getCurMediaPlayer(){
        return this.curMediaPlayer;
    }
    public void setCurMediaPlayer(MediaPlayer value) {
        this.curMediaPlayer = value;
    }
    public static void setFileCurrentSong(File value){
        fileCurrentSong = value;
    }
    public static File getFileCurrentSong(){
        return fileCurrentSong;
    }
    public static File getFilePlayedSong(){return filePlayedSong;}
    public static void setFilePlayedSong(File value){filePlayedSong = value;}
    public ViewPager getPagerFullscreenPlayer(){
        return this.pagerFullscreenPlayer;
    }
    public void setOldPosition(int oldPosition){
        this.oldPosition = oldPosition;
    }
    public void setFastForwardCall(Boolean fastForwardCall){
        this.fastForwardCall = fastForwardCall;
    }
    public void setFastBackwardCall(Boolean fastBackwardCall){
        this.fastBackwardCall = fastBackwardCall;
    }
    public static double getStartTime(){
        return startTime;
    }
    public static void setStartTime(double value){
        startTime = value;
    }
    public static double getFinalTime(){
        return finalTime;
    }
    public static void setFinalTime(double value){
        finalTime = value;
    }
    public static void setCurPosition(int value){
        curPosition = value;
    }
    public static int getCurPosition(){
        return curPosition;
    }




    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "FullscreenPlayerFragment onAttach");
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
        Log.d(LOG_TAG, "FullscreenPlayerFragment onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "FullscreenPlayerFragment onActivityCreated");
    }

    @Override
    public void onStart(){
        super.onStart();

        Log.d(LOG_TAG, "FullscreenPlayerFragment onStart");
    }

    @Override
    public void onResume(){
        super.onResume();
//        getActivity().startService(intentPlayerService);
//        getActivity().bindService(intentPlayerService, serviceConnection, Context.BIND_AUTO_CREATE);
        if (getActivity().getClass() == TracklistActivity.class){
            path = ((TracklistActivity)getActivity()).getPlayerService().getPath();
            Log.d(LOG_TAG, "FullscreenPlayerFragment onResume");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "FullscreenPlayerFragment onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
//        if (bound){
//            getActivity().unbindService(serviceConnection);
//            bound = false;
//        }
        Log.d(LOG_TAG, "FullscreenPlayerFragment onStop");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(LOG_TAG, "FullscreenPlayerFragment onDestroyView");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().unregisterReceiver(playButtonFromServiceToFragmentBR);
        Log.d(LOG_TAG, "FullscreenPlayerFragment onDestroy");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(LOG_TAG, "FullscreenPlayerFragment onDetach");
    }


    public Handler getMyHandler(){
        return this.myHandler;
    }
    public static void setResume(boolean state){
        resume = state;
    }

    public ImageButton getPlayImageButton (){
        return this.playImageButton;
    }

    public ImageButton getReplayImageButton(){
        return this.replayImageButton;
    }

    public Runnable getUpdateSongTime(){
        return this.UpdateSongTime;
    }
    public Runnable getUpdateSeekBar(){ return this.UpdateSeekBar;}
    static String secondsString, minutesString;

    private Runnable UpdateSongTime = new Runnable(){
        public void run(){
            startTime = curMediaPlayer.getCurrentPosition();

            minutes = (int) (startTime / 60000);
            seconds = ((startTime / 60000 - minutes) * 60);

            if (seconds <= 9) {
                secondsString = "0" + String.format(Locale.getDefault(), "%01.0f", seconds).substring(0, 1);
            } else {
                secondsString = String.format(Locale.getDefault(), "%02.0f", seconds).substring(0, 2);
            }
            minutesString = String.format(Locale.getDefault(), "%d", minutes);
            currentTimeSong = minutesString + ":" + secondsString;
            timeStart.setText(currentTimeSong);
            myHandler.postDelayed(this, 1000);
        }
    };
    private Runnable UpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            startTime = curMediaPlayer.getCurrentPosition();
            seekBar.setProgress((int) startTime);
            myHandler.postDelayed(this, 2000);
        }
    };

    public void setSongFullTimeSeekBarProgress(){
        double minutes = (double)curMediaPlayer.getDuration()/ 60000;
        double seconds = (minutes - (int)minutes)*60;
        String timeEnd = String.format(Locale.getDefault(), "%d", (int)minutes)+":"+
                String.format(Locale.getDefault(), "%02.0f", seconds);
        this.timeEnd.setText(timeEnd);
        this.seekBar.setProgress(curMediaPlayer.getCurrentPosition());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_fullscreen_player, container, false);


        Window w = getActivity().getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


//        view.setOnKeyListener(new View.OnKeyListener(){
//           @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event){
//               if (keyCode == KeyEvent.KEYCODE_BACK){
//                    onSaveInstanceState(savedInstanceState);
//               }
//               return true;
//           }
//        });

        nameSong = (TextView) view.findViewById(R.id.nameSongFullscreenPlayer);
        nameArtist = (TextView) view.findViewById(R.id.nameArtistFullScreenPlayer);



        timeStart = (TextView) view.findViewById(R.id.textViewStart);
        timeEnd = (TextView) view.findViewById(R.id.textViewEnd);
        timeStart.setText(currentTimeSong);



        playImageButton = (ImageButton) view.findViewById(R.id.playPauseSongButton);

        shuffleImageButton = (ImageButton) view.findViewById(R.id.shuffleButton);
        replayImageButton = (ImageButton) view.findViewById(R.id.replayButton);
        fastForwardButton = (ImageButton) view.findViewById(R.id.toNextSongButton);
        fastBackwardButton = (ImageButton) view.findViewById(R.id.toPreviousSongButton);


        pagerFullscreenPlayer = (ViewPager) view.findViewById(R.id.pagerFullscreenPlayer);
        fullscreenPlayerAdapter = new FullscreenPlayerAdapter(getChildFragmentManager());
        pagerFullscreenPlayer.setAdapter(fullscreenPlayerAdapter);
        pagerFullscreenPlayer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(LOG_TAG, "onPageSelected, position = " + position);
//                if (oldPosition != -1) {

                    if (oldPosition < position) {
                        fastForwardCall = true;
                    } else if (oldPosition > position) {
                        fastBackwardCall = true;
                    }
//                }
                oldPosition = position;
            }



            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                Log.d(LOG_TAG, "onPageScrolled, position = " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(LOG_TAG, "onPageScrollStateChanged, state = " + state);
                if (state == SCROLL_STATE_IDLE || state == SCROLL_STATE_SETTLING) {
                    swap = true;
                    if (!fastButtons) {
                        if (fastForwardCall) {
                            fastForwardButton.callOnClick();
                            fastForwardCall = false;
                        } else if (fastBackwardCall) {
                            fastBackwardButton.callOnClick();
                            fastBackwardCall = false;
                        }
                    }
                    fastButtons = false;
                    swap = false;
                }
            }
        });


        currentSong = TracklistActivity.getCurSelectedSong();
        oldPosition = currentSong.getId();
        pagerFullscreenPlayer.setCurrentItem(currentSong.getId());


        nameSong.setText(currentSong.getNameSong());
        nameArtist.setText(currentSong.getNameArtist());

//        fspPageFragment = ((FspPageFragment) getChildFragmentManager().findFragmentByTag("fspPageFragment"));
//        if (fspPageFragment == null) {
//            FragmentManager fm = getChildFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            fspPageFragment = new FspPageFragment();
//            ft.add(R.id.pagerFullscreenPlayer, fspPageFragment, "fspPageFragment");
//            ft.commit();
//        }



        if (curMediaPlayer == null) {
//            curMediaPlayer = playerService.getMediaPlayer();
            if (getActivity().getClass() == TracklistActivity.class)
                curMediaPlayer = ((TracklistActivity)getActivity()).getPlayerService().getMediaPlayer();

            playImageButton.setSelected(true);
        }
        seekBar = (SeekBar) view.findViewById(R.id.seekbarSongTime);




        if (savedInstanceState != null){
            SongCardView restoreCurrentSong = (SongCardView)savedInstanceState.getParcelable("currentSong");
//            fspPageFragment.setDataFullscreenPlayer(this, restoreCurrentSong);
            playImageButton.setSelected(savedInstanceState.getBoolean("statePlayButton"));
            replayImageButton.setSelected(savedInstanceState.getBoolean("stateReplayButton"));
            shuffleImageButton.setSelected(savedInstanceState.getBoolean("stateShuffleButton"));
            setStatesButtons(savedInstanceState.getInt("stateFinalTime"));
            continued = true;
            paused = savedInstanceState.getBoolean("statePaused");
            resume = savedInstanceState.getBoolean("stateResume");
            ((TracklistFragment)getFragmentManager().findFragmentByTag("tracklistFragment")).setCurSelectedSong(restoreCurrentSong);
            currentSong = restoreCurrentSong;
            setSongFullTimeSeekBarProgress();
            myHandler.postDelayed(UpdateSongTime, 10);
            myHandler.postDelayed(UpdateSeekBar, 10);
            fileCurrentSong = new File (path,currentSong.getFilePath());
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                myHandler.removeCallbacks(UpdateSongTime);
                myHandler.removeCallbacks(UpdateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (!curMediaPlayer.isPlaying() && !paused) {
                        playImageButton.setSelected(!playImageButton.isSelected());
                        setStatePlayButton();

                        curMediaPlayer.prepare();
                        curMediaPlayer.start();

                        finalTime = curMediaPlayer.getDuration();
                        seekBar.setMax((int) finalTime);
                        resume = true;
                    }
                    startTime = seekBar.getProgress();
                    curMediaPlayer.seekTo((int) startTime);
                    if (paused){
                        curPosition = (int)startTime;
                    }
                    myHandler.postDelayed(UpdateSongTime, 10);
                    myHandler.postDelayed(UpdateSeekBar, 10);

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });



        fastForwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                fastButtons = true;
                ArrayList<SongCardView> currentTracklist = TracklistActivity.getSongsCardView();
                if (shuffleImageButton.isSelected() && !swap) {
                    Random randomGenerator = new Random();
                    int i = randomGenerator.nextInt(currentTracklist.size()-1);
                    currentSong = currentTracklist.get(i);
                }
                int curId = currentSong.getId();
                if (currentTracklist.size() - 1 > curId) {
                    currentSong = currentTracklist.get(curId + 1);
                } else {
                    currentSong = currentTracklist.get(0);
                }


                if (getActivity().getClass() == MainActivity.class){
                    MainActivity.setCurSelectedSong(currentSong);
                } else if (getActivity().getClass() == TracklistActivity.class){
                    TracklistActivity.setCurSelectedSong(currentSong);
                }

                fileCurrentSong = new File(path,currentSong.getFilePath());
                forBackwardTrack(fileCurrentSong);

//                fspPageFragment.setDataFullscreenPlayer(FullscreenPlayerFragment.this, currentSong);
                setNameSongArtist(currentSong);
                setSongFullTimeSeekBarProgress();
                if (fastButtons && !swap) {
                    oldPosition = currentSong.getId();
                    if (pagerFullscreenPlayer.getCurrentItem() == currentTracklist.size()-1){
                        pagerFullscreenPlayer.setCurrentItem(0, false);
                    } else {
                        pagerFullscreenPlayer.setCurrentItem(currentSong.getId(), true);
                    }
                }
            }
        });

        fastBackwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                fastButtons = true;
                ArrayList<SongCardView> currentTracklist = TracklistActivity.getSongsCardView();
                if (shuffleImageButton.isSelected() && !swap) {
                    Random randomGenerator = new Random();
                    int i = randomGenerator.nextInt(currentTracklist.size()-1);
                    currentSong = currentTracklist.get(i);
                }
                int curId = currentSong.getId();
                if (curId > 0) {
                    currentSong = currentTracklist.get(curId - 1);
                } else {
                    currentSong = currentTracklist.get(currentTracklist.size()-1);
                }

                if (getActivity().getClass() == MainActivity.class){
                    MainActivity.setCurSelectedSong(currentSong);
                } else if (getActivity().getClass() == TracklistActivity.class){
                    TracklistActivity.setCurSelectedSong(currentSong);
                }
                fileCurrentSong = new File(path,currentSong.getFilePath());
                forBackwardTrack(fileCurrentSong);
                setNameSongArtist(currentSong);
                setSongFullTimeSeekBarProgress();
                if (fastButtons && !swap) {
                    oldPosition = currentSong.getId();
                    if (pagerFullscreenPlayer.getCurrentItem() == 0){
                        pagerFullscreenPlayer.setCurrentItem(currentTracklist.size()-1, false);
                    } else {
                        pagerFullscreenPlayer.setCurrentItem(currentSong.getId(), true);
                    }
                }
            }
        });

        replayImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replayImageButton.setSelected(!replayImageButton.isSelected());
                setStateReplayButton();

            }
        });

        shuffleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffleImageButton.setSelected(!shuffleImageButton.isSelected());
                setStateShuffleButton();
            }
        });



        curMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    if (!replayImageButton.isSelected()) {
                        curMediaPlayer.pause();
                        curMediaPlayer.stop();
                        playImageButton.setSelected(!playImageButton.isSelected());
                        setStatePlayButton();
                        resume = false;
                        myHandler.removeCallbacks(UpdateSongTime);
                        myHandler.removeCallbacks(UpdateSeekBar);
                        if (shuffleImageButton.isSelected()) {
                            Random randomGenerator = new Random();
                            ArrayList<SongCardView> currentTracklist =
                                    TracklistActivity.getSongsCardView();
                            int i = randomGenerator.nextInt(currentTracklist.size()-1);
                            currentSong = currentTracklist.get(i);
                        }
                        fastForwardButton.callOnClick();
                    } else {
                        curMediaPlayer.pause();
                        curMediaPlayer.stop();
                        curMediaPlayer.prepare();
                        curMediaPlayer.start();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        forwardButtonFromServiceToFragmentBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fastForwardButton.callOnClick();
            }
        };
        getActivity().registerReceiver(forwardButtonFromServiceToFragmentBR,
                new IntentFilter(TAG_FORWARD_BUT_PS_TO_F_BR));

        backwardButtonFromServiceToFragmentBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fastBackwardButton.callOnClick();
            }
        };
        getActivity().registerReceiver(backwardButtonFromServiceToFragmentBR,
                new IntentFilter(TAG_BACKWARD_BUT_PS_TO_F_BR));



        playButtonFromServiceToFragmentBR= new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                playImageButton.setSelected(!playImageButton.isSelected());
                setStatePlayButton();
            }
        };
        getActivity().registerReceiver(playButtonFromServiceToFragmentBR,
                new IntentFilter(TAG_PLAY_BUT_PS_TO_F_BR));


        playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playImageButton.setSelected(!playImageButton.isSelected());
                setStatePlayButton();
                Intent intent = new Intent(TAG_PLAY);
                intent.putExtra("fpfCall", "true");
                intent.putExtra("isPlaying", !playImageButton.isSelected());
                intent.putExtra("currentSong", currentSong);
                ((TracklistActivity)getActivity()).sendBroadcast(intent);
                try {
                    if (fileCurrentSong == null) {
                        fileCurrentSong = new File (path,currentSong.getFilePath());
                        curMediaPlayer.setDataSource(fileCurrentSong.toString());
                    }
                    if (filePlayedSong != fileCurrentSong) {
                        ArrayList<SongCardView> list = MainActivity.getListRecentlySongs();
                        if (list.size() == 0 || list.get(0) != currentSong) {
                            Collections.reverse(list);
                            list.add(list.size(), currentSong);
                            Collections.reverse(list);
                            filePlayedSong = fileCurrentSong;
                        }
                    }
                    if (playImageButton.isSelected()) {
                        if (!resume) {
                            curMediaPlayer.prepare();
                            curMediaPlayer.start();

                            startTime = curMediaPlayer.getCurrentPosition();
                            finalTime = curMediaPlayer.getDuration();
                            seekBar.setMax((int) finalTime);
                            seekBar.setProgress((int)startTime);
                            myHandler.postDelayed(UpdateSongTime,10);
                            myHandler.postDelayed(UpdateSeekBar,10);
                            resume = true;
                            paused = false;
                        } else {
                            curMediaPlayer.seekTo(curPosition);
                            curMediaPlayer.start();
                            paused = false;
                        }
                    } else {
                        curMediaPlayer.pause();
                        curPosition = curMediaPlayer.getCurrentPosition();
                        paused = true;
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        if (!playImageButton.isSelected() && !paused && !continued) {
            if (curMediaPlayer.isPlaying()) {
                curMediaPlayer.pause();
                curMediaPlayer.stop();

                resume = false;
                myHandler.removeCallbacks(UpdateSongTime);
                myHandler.removeCallbacks(UpdateSeekBar);
            }
            playImageButton.callOnClick();
        }
        if (paused && !continued){
            curMediaPlayer.stop();
            resume = false;
            myHandler.removeCallbacks(UpdateSongTime);
            myHandler.removeCallbacks(UpdateSeekBar);
            playImageButton.callOnClick();
        }
        if (continued){
            curPosition = curMediaPlayer.getCurrentPosition();
            seekBar.setProgress(curPosition);
            seekBar.setMax(curMediaPlayer.getDuration());
            if (!paused)
                playImageButton.setSelected(true);
            else
                playImageButton.setSelected(false);
            setStatePlayButton();
        }
        continued = false;
        Log.d(LOG_TAG, "FullscreenPlayerFragment onCreateView");
        return view;
    }

    public void setNameSongArtist(SongCardView songCardView){
        this.nameSong.setText(songCardView.getNameSong());
        this.nameArtist.setText(songCardView.getNameArtist());
    }

    public void setFileNewSong(File file){
        setFileCurrentSong(file);
        try {
            MediaPlayer curMediaPlayer = getCurMediaPlayer();
            if (curMediaPlayer.isPlaying())
            {
                curMediaPlayer.pause();
                curMediaPlayer.stop();
                setResume(false);
                getMyHandler().removeCallbacks(getUpdateSongTime());
                getMyHandler().removeCallbacks(getUpdateSeekBar());
            }
            if (getPaused()){
                curMediaPlayer.stop();
                setResume(false);
                getMyHandler().removeCallbacks(getUpdateSongTime());
                getMyHandler().removeCallbacks(getUpdateSeekBar());
                setPaused(false);
            }

            curMediaPlayer.reset();
            curMediaPlayer.setDataSource(file.toString());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void forBackwardTrack(File file){
        try {
            MediaPlayer curMediaPlayer = getCurMediaPlayer();
            if (curMediaPlayer.isPlaying()) {
                curMediaPlayer.pause();
                curMediaPlayer.stop();
                getPlayImageButton().setSelected(!getPlayImageButton().isSelected());
                setStatePlayButton();
                setResume(false);
                getMyHandler().removeCallbacks(getUpdateSongTime());
                getMyHandler().removeCallbacks(getUpdateSeekBar());
                curMediaPlayer.reset();
                curMediaPlayer.setDataSource(file.toString());
            } else {
                curMediaPlayer.stop();
                setResume(false);
                getMyHandler().removeCallbacks(getUpdateSongTime());
                getMyHandler().removeCallbacks(getUpdateSeekBar());
                curMediaPlayer.reset();
                curMediaPlayer.setDataSource(file.toString());
            }
            getPlayImageButton().callOnClick();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setStatesButtons(int time){
        setStateReplayButton();
        setStateShuffleButton();
        setStatePlayButton();
        setStateSeekBar(time);
    }

    public void setStateSeekBar(int time){
        seekBar.setMax(time);
    }
    public void setStateReplayButton(){
        if (replayImageButton.isSelected()){
            replayImageButton.setBackgroundResource(R.drawable.replay_solid_green);
        } else {
            replayImageButton.setBackgroundResource(R.drawable.replay_solid_black);
        }
    }

    public void setStateShuffleButton(){
        if (shuffleImageButton.isSelected()){
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_solid_green);
        } else {
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_solid_black);
        }
    }

    public void setStatePlayButton(){
        if (playImageButton.isSelected()){
            playImageButton.setImageResource(R.drawable.pause_lines_button);
        } else {
            playImageButton.setImageResource(R.drawable.play_button);
        }
    }






    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelable("currentSong", currentSong);
        savedInstanceState.putBoolean("stateReplayButton", replayImageButton.isSelected());
        savedInstanceState.putBoolean("stateShuffleButton", shuffleImageButton.isSelected());
        savedInstanceState.putBoolean("statePlayButton", playImageButton.isSelected());
        savedInstanceState.putInt("stateFinalTime", seekBar.getMax());
        savedInstanceState.putBoolean("statePaused", paused);
        savedInstanceState.putBoolean("stateResume", resume);
    }
}
