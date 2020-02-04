package com.romankaranchuk.musicplayer.presentation.ui.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.presentation.service.PlayerService;
import com.romankaranchuk.musicplayer.presentation.ui.main.MainActivity;
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TracklistFragment;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;

public class PlayerFragment extends Fragment {

    private static String TAG_PLAY = "playNotifPlayerReceiver";
    private static String TAG_PLAY_BUT_PS_TO_F_BR = "playButtonFromPStoFragmentBR";
    private static String TAG_FORWARD_BUT_PS_TO_F_BR = "forwardButtonFromPStoFragmentBR";
    private static String TAG_BACKWARD_BUT_PS_TO_F_BR = "backwardButtonFromPStoFragmentBR";
    private static String LOG_TAG = "myLogs";

    private static Song currentSong;
    private static MediaPlayer curMediaPlayer;
    private static boolean resume = false;
    private static boolean paused = false;
    private static boolean continued = false;
    private static int curPosition;
    private static int minutes;
    private static double startTime;
    private static double finalTime;
    private static double seconds;

    private ImageButton playImageButton;
    private ImageButton shuffleImageButton;
    private ImageButton replayImageButton;
    private ImageButton fastForwardButton;
    private ImageButton fastBackwardButton;
    private SeekBar seekBar;
    private TextView timeEnd;
    private TextView nameSong;
    private TextView nameArtist;
    private TextView timeStart;

    private Handler myHandler = new Handler();
    private static File fileCurrentSong;
    private static File filePlayedSong;

    private String currentTimeSong;
    private ViewPager pagerFullscreenPlayer;
    private int oldPosition=-1;
    private boolean fastButtons = false;
    private boolean swap = false;
    private boolean fastForwardCall = false;
    private boolean fastBackwardCall = false;
    private boolean bound = false;
    private PlayerPagerAdapter playerPagerAdapter;
    private Intent intentPlayerService;
    ServiceConnection serviceConnection;
    BroadcastReceiver playButtonFromServiceToFragmentBR,
    forwardButtonFromServiceToFragmentBR,
    backwardButtonFromServiceToFragmentBR;

    private PlayerService playerService;
    MediaPlayer.OnCompletionListener onCompletionListenerMediaPlayer;
    View.OnClickListener onClickListenerPlayButton;
    private ArrayList<Song> mSongs;
    static String secondsString, minutesString;

    public static Song getCurrentSong(){
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

    public static MediaPlayer getCurMediaPlayer(){
        return curMediaPlayer;
    }
    public static void setFileCurrentSong(File value){
        fileCurrentSong = value;
    }
    public static File getFileCurrentSong(){
        return fileCurrentSong;
    }
    public static File getFilePlayedSong(){return filePlayedSong;}
    public static void setFilePlayedSong(File value){filePlayedSong = value;}
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

    public static void setResume(boolean state){
        resume = state;
    }
    public Runnable getUpdateSongTime(){
        return this.UpdateSongTime;
    }
    public Runnable getUpdateSeekBar(){ return this.UpdateSeekBar;}

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "PlayerFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (intentPlayerService == null)
            intentPlayerService = new Intent(getActivity(), PlayerService.class);

        if (serviceConnection == null)
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder binder) {
                    Log.d(LOG_TAG, "PlayerFragment onServiceConnected");
                    playerService = ((PlayerService.PlayerBinder) binder).getService();

                    if (playerService.getMediaPlayer() != null && curMediaPlayer == null) {
                        curMediaPlayer = playerService.getMediaPlayer();
                        curMediaPlayer.setOnCompletionListener(onCompletionListenerMediaPlayer);
                        playImageButton.setOnClickListener(onClickListenerPlayButton);
                    }

                    handleStateCurMediaPlayer();

                    if (!playImageButton.isSelected() && !continued) {
                        playImageButton.setSelected(true);
                        playImageButton.callOnClick();
                    }

                    setSongFullTimeSeekBarProgress();
                    myHandler.postDelayed(UpdateSongTime, 10);
                    myHandler.postDelayed(UpdateSeekBar, 10);

                    bound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.d(LOG_TAG, "PlayerFragment onServiceDisconnected");
                    bound = false;
                }
            };
        getContext().startService(intentPlayerService);
        getContext().bindService(intentPlayerService, serviceConnection,0);
        Log.d(LOG_TAG, "PlayerFragment onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "PlayerFragment onActivityCreated");
    }

    @Override
    public void onStart(){
        super.onStart();
        if (bound){
            setSongFullTimeSeekBarProgress();
        }
        Log.d(LOG_TAG, "PlayerFragment onStart");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (bound){
            getContext().unbindService(serviceConnection);
            bound = false;
        }
        getContext().unregisterReceiver(playButtonFromServiceToFragmentBR);
        getContext().unregisterReceiver(forwardButtonFromServiceToFragmentBR);
        getContext().unregisterReceiver(backwardButtonFromServiceToFragmentBR);

        Log.d(LOG_TAG, "PlayerFragment onDestroy");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(LOG_TAG, "PlayerFragment onDetach");
    }


    private Runnable UpdateSongTime = new Runnable(){
        public void run(){
            if (curMediaPlayer != null) {
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
        }
    };
    private Runnable UpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (curMediaPlayer != null) {
                startTime = curMediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) startTime);
                myHandler.postDelayed(this, 2000);
            }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState){
        Log.d(LOG_TAG, "PlayerFragment onCreateView");
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Window w = getActivity().getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        nameSong = view.findViewById(R.id.nameSongFullscreenPlayer);
        nameSong.setSingleLine(true);
        nameSong.setSelected(true);
        nameArtist = view.findViewById(R.id.nameArtistFullScreenPlayer);
        nameArtist.setSingleLine(true);
        nameArtist.setSelected(true);

        timeStart = view.findViewById(R.id.textViewStart);
        timeEnd = view.findViewById(R.id.textViewEnd);
        timeStart.setText(currentTimeSong);

        playImageButton = view.findViewById(R.id.playPauseSongButton);
        shuffleImageButton = view.findViewById(R.id.shuffleButton);
        replayImageButton = view.findViewById(R.id.replayButton);
        fastForwardButton = view.findViewById(R.id.toNextSongButton);
        fastBackwardButton = view.findViewById(R.id.toPreviousSongButton);
        pagerFullscreenPlayer = view.findViewById(R.id.pagerFullscreenPlayer);

        if (mSongs == null){
            mSongs = TracklistFragment.getSongs();
        }
        playerPagerAdapter = new PlayerPagerAdapter(getChildFragmentManager(),mSongs);

        pagerFullscreenPlayer.setAdapter(playerPagerAdapter);
        pagerFullscreenPlayer.setPageTransformer(true, new ViewPager.PageTransformer() {
            private static final float MIN_SCALE = 0.75f;
            @Override
            public void transformPage(View page, float position) {
                int pageWidth = page.getWidth();

                if (position < -1){
                    page.setAlpha(0);
                } else if (position <= 0){
                    page.setAlpha(1);
                    page.setTranslationX(0);
                    page.setScaleX(1);
                    page.setScaleY(1);
                } else if (position <= 1){
                    page.setAlpha(1- position);

                    page.setTranslationX(pageWidth * -position);

                    float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);
                } else {
                    page.setAlpha(0);
                }
            }
        });

        pagerFullscreenPlayer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(LOG_TAG, "onPageSelected, position = " + position);
                if (oldPosition < position) {
                    fastForwardCall = true;
                } else if (oldPosition > position) {
                    fastBackwardCall = true;
                }
                oldPosition = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                Log.d(LOG_TAG, "onPageScrolled, position = " + position +
                        " positionOffset = "+positionOffset +
                        " positionOffsetPixels = " + positionOffsetPixels);
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


        currentSong = MainActivity.getCurSelectedSong();
        oldPosition = mSongs.indexOf(currentSong);
        pagerFullscreenPlayer.setCurrentItem(oldPosition,false);

        if (currentSong != null) {
            nameSong.setText(currentSong.getTitle());
            nameArtist.setText(currentSong.getNameArtist());
        }
        seekBar = view.findViewById(R.id.seekbarSongTime);

        if (savedInstanceState != null){
            Song restoreCurrentSong = savedInstanceState.getParcelable("currentSong");
//            playerPageFragment.setDataFullscreenPlayer(this, restoreCurrentSong);
            playImageButton.setSelected(savedInstanceState.getBoolean("statePlayButton"));
            replayImageButton.setSelected(savedInstanceState.getBoolean("stateReplayButton"));
            shuffleImageButton.setSelected(savedInstanceState.getBoolean("stateShuffleButton"));
//            setStatesButtons();
            continued = true;
            paused = savedInstanceState.getBoolean("statePaused");
            resume = savedInstanceState.getBoolean("stateResume");
//            ((TracklistFragment)getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG)).setCurSelectedSong(restoreCurrentSong);
            currentSong = restoreCurrentSong;
//            setSongFullTimeSeekBarProgress();
            myHandler.postDelayed(UpdateSongTime, 10);
            myHandler.postDelayed(UpdateSeekBar, 10);
            fileCurrentSong = new File (currentSong.getPath());
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
                ArrayList<Song> currentTracklist = TracklistFragment.getSongs();
                if (shuffleImageButton.isSelected() && !swap) {
                    Random randomGenerator = new Random();
                    int i = randomGenerator.nextInt(currentTracklist.size()-1);
                    currentSong = currentTracklist.get(i);
                }
                int curId = mSongs.indexOf(currentSong);
                if (currentTracklist.size() - 1 > curId) {
                    currentSong = currentTracklist.get(curId + 1);
                } else {
                    currentSong = currentTracklist.get(0);
                }


                MainActivity.setCurSelectedSong(currentSong);

                fileCurrentSong = new File(currentSong.getPath());
                forBackwardTrack(fileCurrentSong);

                setNameSongArtist(currentSong);
                setSongFullTimeSeekBarProgress();
                if (fastButtons && !swap) {
                    oldPosition = mSongs.indexOf(currentSong);
                    if (pagerFullscreenPlayer.getCurrentItem() == currentTracklist.size()-1){
                        pagerFullscreenPlayer.setCurrentItem(0, false);
                    } else {
                        pagerFullscreenPlayer.setCurrentItem(mSongs.indexOf(currentSong), true);
                    }
                }
            }
        });

        fastBackwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                fastButtons = true;
                ArrayList<Song> currentTracklist = TracklistFragment.getSongs();
                if (shuffleImageButton.isSelected() && !swap) {
                    Random randomGenerator = new Random();
                    int i = randomGenerator.nextInt(currentTracklist.size()-1);
                    currentSong = currentTracklist.get(i);
                }
                int curId = mSongs.indexOf(currentSong);
                if (curId > 0) {
                    currentSong = currentTracklist.get(curId - 1);
                } else {
                    currentSong = currentTracklist.get(currentTracklist.size()-1);
                }

                MainActivity.setCurSelectedSong(currentSong);

                fileCurrentSong = new File(currentSong.getPath());
                forBackwardTrack(fileCurrentSong);
                setNameSongArtist(currentSong);
                setSongFullTimeSeekBarProgress();
                if (fastButtons && !swap) {
                    oldPosition = mSongs.indexOf(currentSong);
                    if (pagerFullscreenPlayer.getCurrentItem() == 0){
                        pagerFullscreenPlayer.setCurrentItem(currentTracklist.size()-1, false);
                    } else {
                        pagerFullscreenPlayer.setCurrentItem(mSongs.indexOf(currentSong), true);
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



        onCompletionListenerMediaPlayer = new MediaPlayer.OnCompletionListener() {
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
                            ArrayList<Song> currentTracklist = TracklistFragment.getSongs();
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
        };

        forwardButtonFromServiceToFragmentBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fastForwardButton.callOnClick();
            }
        };
        getContext().registerReceiver(forwardButtonFromServiceToFragmentBR,
                new IntentFilter(TAG_FORWARD_BUT_PS_TO_F_BR));

        backwardButtonFromServiceToFragmentBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fastBackwardButton.callOnClick();
            }
        };
        getContext().registerReceiver(backwardButtonFromServiceToFragmentBR,
                new IntentFilter(TAG_BACKWARD_BUT_PS_TO_F_BR));



        playButtonFromServiceToFragmentBR= new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                playImageButton.setSelected(!playImageButton.isSelected());
                setStatePlayButton();
            }
        };
        getContext().registerReceiver(playButtonFromServiceToFragmentBR,
                new IntentFilter(TAG_PLAY_BUT_PS_TO_F_BR));


        if (onClickListenerPlayButton == null) {
            onClickListenerPlayButton = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playImageButton.setSelected(!playImageButton.isSelected());
                    setStatePlayButton();
                    Intent intent = new Intent(TAG_PLAY);
                    intent.putExtra("fpfCall", "true");
                    intent.putExtra("isPlaying", !playImageButton.isSelected());
                    intent.putExtra("currentSong", currentSong);
                    if (getContext() != null)
                        getContext().sendBroadcast(intent);
                    try {
                        if (fileCurrentSong == null) {
                            fileCurrentSong = new File(currentSong.getPath());
                            curMediaPlayer.setDataSource(fileCurrentSong.toString());
                        }
                        if (filePlayedSong != fileCurrentSong) {
                            LinkedList<Song> list = MainActivity.getListRecentlySongs();
                            if (list.size() == 0 || list.get(0) != currentSong) {
                                list.addFirst(currentSong);
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
                                seekBar.setProgress((int) startTime);
                                myHandler.postDelayed(UpdateSongTime, 10);
                                myHandler.postDelayed(UpdateSeekBar, 10);
                                resume = true;
                                paused = false;
                            } else {
                                curMediaPlayer.seekTo(curPosition);
                                curMediaPlayer.start();
                                paused = false;
                            }

                            if (playerService == null) {
                                getContext().startService(intentPlayerService);
                                getContext().bindService(intentPlayerService, serviceConnection, 0);
                            }
                        } else {
                            curMediaPlayer.pause();
                            curPosition = curMediaPlayer.getCurrentPosition();
                            paused = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        if (curMediaPlayer != null){
            playImageButton.setOnClickListener(onClickListenerPlayButton);
            curMediaPlayer.setOnCompletionListener(onCompletionListenerMediaPlayer);
        }
    }

    public void handleStateCurMediaPlayer(){
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
            if (!paused)
                playImageButton.setSelected(true);
            else
                playImageButton.setSelected(false);
            setStatesButtons();
        }
        continued = false;
    }


    public void setNameSongArtist(Song song){
        this.nameSong.setText(song.getTitle());
        this.nameArtist.setText(song.getNameArtist());
    }

    public void setFileNewSong(File file){
        setFileCurrentSong(file);
        try {
            if (curMediaPlayer.isPlaying())
            {
                curMediaPlayer.pause();
                curMediaPlayer.stop();
                setResume(false);
                myHandler.removeCallbacks(getUpdateSongTime());
                myHandler.removeCallbacks(getUpdateSeekBar());
            }
            if (getPaused()){
                curMediaPlayer.stop();
                setResume(false);
                myHandler.removeCallbacks(getUpdateSongTime());
                myHandler.removeCallbacks(getUpdateSeekBar());
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
            if (curMediaPlayer.isPlaying()) {
                curMediaPlayer.pause();
                curMediaPlayer.stop();
                playImageButton.setSelected(!playImageButton.isSelected());
                setStatePlayButton();
                setResume(false);
                myHandler.removeCallbacks(getUpdateSongTime());
                myHandler.removeCallbacks(getUpdateSeekBar());
                curMediaPlayer.reset();
                curMediaPlayer.setDataSource(file.toString());
            } else {
                curMediaPlayer.stop();
                setResume(false);
                myHandler.removeCallbacks(getUpdateSongTime());
                myHandler.removeCallbacks(getUpdateSeekBar());
                curMediaPlayer.reset();
                curMediaPlayer.setDataSource(file.toString());
            }
            playImageButton.callOnClick();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setStatesButtons(){
        setStateReplayButton();
        setStateShuffleButton();
        setStatePlayButton();
        setStateSeekBar();
    }

    public void setStateSeekBar(){
        seekBar.setProgress(curMediaPlayer.getCurrentPosition());
        seekBar.setMax(curMediaPlayer.getDuration());
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
