package com.romankaranchuk.musicplayer.presentation.ui.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.presentation.service.PlayerService;
import com.romankaranchuk.musicplayer.presentation.ui.main.MainActivity;
import com.romankaranchuk.musicplayer.presentation.ui.player.page.PlayerPagerAdapter;
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;

public class PlayerFragment extends Fragment {

    private static final String TAG_PLAY = "playNotifPlayerReceiver";
    private static final String TAG_PLAY_BUT_PS_TO_F_BR = "playButtonFromPStoFragmentBR";
    private static final String TAG_FORWARD_BUT_PS_TO_F_BR = "forwardButtonFromPStoFragmentBR";
    private static final String TAG_BACKWARD_BUT_PS_TO_F_BR = "backwardButtonFromPStoFragmentBR";
    private static final String TAG = "PlayerFragment";
    public static final String PLAYER_FRAGMENT_TAG = TAG + ".PLAYER_FRAGMENT_TAG";
    
    private static Song currentSong;
    private static MediaPlayer curMediaPlayer;
    private static boolean isResumed = false;
    private static boolean isPaused = false;
    private static boolean isContinued = false;
    private static int currentPosition;
    private static double startTime;
    private static double finalTime;
    private static File fileCurrentSong;
    private static File filePlayedSong;

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
    private ViewPager pagerFullscreenPlayer;

    private String currentTimeSong;
    private int oldPosition=-1;
    private boolean fastButtons = false;
    private boolean swap = false;
    private boolean fastForwardCall = false;
    private boolean fastBackwardCall = false;
    private boolean bound = false;

    private Handler myHandler = new Handler();
    private Intent intentPlayerService;
    private PlayerService playerService;
    private List<Song> mSongs;

    private PlayerViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "PlayerFragment onServiceConnected");
            playerService = ((PlayerService.PlayerBinder) binder).getService();

            if (playerService.getMediaPlayer() != null && curMediaPlayer == null) {
                curMediaPlayer = playerService.getMediaPlayer();
                curMediaPlayer.setOnCompletionListener(onCompletionListenerMediaPlayer);
                playImageButton.setOnClickListener(onClickListenerPlayButton);
            }

            handleStateCurMediaPlayer();

            if (!playImageButton.isSelected() && !isContinued) {
                playImageButton.setSelected(true);
                playImageButton.callOnClick();
            }

            setSongFullTimeSeekBarProgress();
            myHandler.postDelayed(mUpdateSongTimeRunnable, 10);
            myHandler.postDelayed(mUpdateSeekBarRunnable, 10);

            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "PlayerFragment onServiceDisconnected");
            bound = false;
        }
    };

    private BroadcastReceiver forwardButtonFromServiceToFragmentBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fastForwardButton.callOnClick();
        }
    };
    private BroadcastReceiver backwardButtonFromServiceToFragmentBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fastBackwardButton.callOnClick();
        }
    };
    private BroadcastReceiver playButtonFromServiceToFragmentBR= new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            playImageButton.setSelected(!playImageButton.isSelected());
            setStatePlayButton();
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListenerMediaPlayer = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            try {
                if (!replayImageButton.isSelected()) {
                    curMediaPlayer.pause();
                    curMediaPlayer.stop();
                    playImageButton.setSelected(!playImageButton.isSelected());
                    setStatePlayButton();
                    isResumed = false;
                    myHandler.removeCallbacks(mUpdateSongTimeRunnable);
                    myHandler.removeCallbacks(mUpdateSeekBarRunnable);
                    if (shuffleImageButton.isSelected()) {
                        Random randomGenerator = new Random();
                        List<Song> currentTracklist = TrackListFragment.getSongs();
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

    private View.OnClickListener onClickListenerPlayButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            playImageButton.setSelected(!playImageButton.isSelected());
            setStatePlayButton();
            Intent intent = new Intent(TAG_PLAY);
            intent.putExtra("fpfCall", "true");
            intent.putExtra("isPlaying", !playImageButton.isSelected());
            intent.putExtra("currentSong", currentSong);
            if (getContext() != null) {
                getContext().sendBroadcast(intent);
            }
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
                    if (!isResumed) {
                        curMediaPlayer.prepare();
                        curMediaPlayer.start();

                        startTime = curMediaPlayer.getCurrentPosition();
                        finalTime = curMediaPlayer.getDuration();
                        seekBar.setMax((int) finalTime);
                        seekBar.setProgress((int) startTime);
                        myHandler.postDelayed(mUpdateSongTimeRunnable, 10);
                        myHandler.postDelayed(mUpdateSeekBarRunnable, 10);
                        isResumed = true;
                        isPaused = false;
                    } else {
                        curMediaPlayer.seekTo(currentPosition);
                        curMediaPlayer.start();
                        isPaused = false;
                    }

                    if (playerService == null) {
                        getContext().startService(intentPlayerService);
                        getContext().bindService(intentPlayerService, serviceConnection, 0);
                    }
                } else {
                    curMediaPlayer.pause();
                    currentPosition = curMediaPlayer.getCurrentPosition();
                    isPaused = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            myHandler.removeCallbacks(mUpdateSongTimeRunnable);
            myHandler.removeCallbacks(mUpdateSeekBarRunnable);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            try {
                if (!curMediaPlayer.isPlaying() && !isPaused) {
                    playImageButton.setSelected(!playImageButton.isSelected());
                    setStatePlayButton();

                    curMediaPlayer.prepare();
                    curMediaPlayer.start();

                    finalTime = curMediaPlayer.getDuration();
                    seekBar.setMax((int) finalTime);
                    isResumed = true;
                }
                startTime = seekBar.getProgress();
                curMediaPlayer.seekTo((int) startTime);
                if (isPaused){
                    currentPosition = (int)startTime;
                }
                myHandler.postDelayed(mUpdateSongTimeRunnable, 10);
                myHandler.postDelayed(mUpdateSeekBarRunnable, 10);

            } catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    private ViewPager.PageTransformer mPageTransformer = new ViewPager.PageTransformer() {
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
    };

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected, position = " + position);
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
            Log.d(TAG, "onPageScrolled, position = " + position +
                    " positionOffset = "+positionOffset +
                    " positionOffsetPixels = " + positionOffsetPixels);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d(TAG, "onPageScrollStateChanged, state = " + state);
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
    };

    private View.OnClickListener mFastForwardClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            fastButtons = true;
            List<Song> currentTracklist = TrackListFragment.getSongs();
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
    };

    private View.OnClickListener mFastBackwardClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            fastButtons = true;
            List<Song> currentTracklist = TrackListFragment.getSongs();
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
    };

    private View.OnClickListener mReplayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replayImageButton.setSelected(!replayImageButton.isSelected());
            setStateReplayButton();

        }
    };

    private View.OnClickListener mShuffleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            shuffleImageButton.setSelected(!shuffleImageButton.isSelected());
            setStateShuffleButton();
        }
    };

    public static Fragment newInstance(Song song) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("CURRENT_SONG", song);
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Song getCurrentSong(){
        return currentSong;
    }
    public void setContinued(boolean state){
        isContinued = state;
    }
    public static MediaPlayer getCurMediaPlayer(){
        return curMediaPlayer;
    }

    public static boolean getIsResumed(){
        return isResumed;
    }
    public static void setIsResumed(boolean state){
        isResumed = state;
    }

    public static void setIsPaused(Boolean value){
        isPaused = value;
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

    public static void setCurrentPosition(int value){
        currentPosition = value;
    }
    public static int getCurrentPosition(){
        return currentPosition;
    }


    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        Log.d(TAG, "PlayerFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (intentPlayerService == null) {
            intentPlayerService = new Intent(getActivity(), PlayerService.class);
        }

        Bundle args = getArguments();
        if (args != null) {
            final Song song = args.getParcelable("CURRENT_SONG");
            setViewState(song);
        }

        getContext().startService(intentPlayerService);
        getContext().bindService(intentPlayerService, serviceConnection,0);
        Log.d(TAG, "PlayerFragment onCreate");
    }

    private void setViewState(Song song) {
        Song curSelectedSong = MainActivity.getCurSelectedSong();
        if (curSelectedSong == null) {
            curSelectedSong = song;
        }

        if (curSelectedSong.equals(song)) {
            if (isResumed) {
                isContinued = true;
            } else {
                MainActivity.setCurSelectedSong(curSelectedSong);
                isContinued = false;
            }
        } else {
            curSelectedSong = song;
            MainActivity.setCurSelectedSong(curSelectedSong);
            setFileNewSong(new File(curSelectedSong.getPath()));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PlayerViewModel.class);
        mViewModel.getSongs().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {

            }
        });

        Log.d(TAG, "PlayerFragment onActivityCreated");
    }

    @Override
    public void onStart(){
        super.onStart();
        if (bound){
            setSongFullTimeSeekBarProgress();
        }
        Log.d(TAG, "PlayerFragment onStart");
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

        Log.d(TAG, "PlayerFragment onDestroy");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(TAG, "PlayerFragment onDetach");
    }

    private Runnable mUpdateSongTimeRunnable = new Runnable(){
        public void run(){
            if (curMediaPlayer != null) {
                startTime = curMediaPlayer.getCurrentPosition();

                int minutes = (int) (startTime / 60000);
                double seconds = ((startTime / 60000 - minutes) * 60);

                String secondsString;
                if (seconds <= 9) {
                    secondsString = "0" + String.format(Locale.getDefault(), "%01.0f", seconds).substring(0, 1);
                } else {
                    secondsString = String.format(Locale.getDefault(), "%02.0f", seconds).substring(0, 2);
                }
                String minutesString = String.format(Locale.getDefault(), "%d", minutes);
                currentTimeSong = minutesString + ":" + secondsString;
                timeStart.setText(currentTimeSong);
                myHandler.postDelayed(this, 1000);
            }
        }
    };

    private Runnable mUpdateSeekBarRunnable = new Runnable() {
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
        Log.d(TAG, "PlayerFragment onCreateView");
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setListenersOnViews();

        if (mSongs == null){
            mSongs = TrackListFragment.getSongs();
        }

        PlayerPagerAdapter playerPagerAdapter = new PlayerPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pagerFullscreenPlayer.setAdapter(playerPagerAdapter);
        pagerFullscreenPlayer.setPageTransformer(true, mPageTransformer);
        pagerFullscreenPlayer.addOnPageChangeListener(mPageChangeListener);

        currentSong = MainActivity.getCurSelectedSong();
//        oldPosition = mSongs.indexOf(currentSong);
//        pagerFullscreenPlayer.setCurrentItem(oldPosition,false);

        if (currentSong != null) {
            nameSong.setText(currentSong.getTitle());
            nameArtist.setText(currentSong.getNameArtist());
        }

        restoreViewState(savedInstanceState);

        registerBroadcastReceivers(view.getContext());
    }

    private void restoreViewState(final Bundle savedInstanceState) {
        if (savedInstanceState != null){
            Song restoreCurrentSong = savedInstanceState.getParcelable("currentSong");
//            playerPageFragment.setDataFullscreenPlayer(this, restoreCurrentSong);
            playImageButton.setSelected(savedInstanceState.getBoolean("statePlayButton"));
            replayImageButton.setSelected(savedInstanceState.getBoolean("stateReplayButton"));
            shuffleImageButton.setSelected(savedInstanceState.getBoolean("stateShuffleButton"));
//            setStatesButtons();
            isContinued = true;
            isPaused = savedInstanceState.getBoolean("statePaused");
            isResumed = savedInstanceState.getBoolean("stateResume");
//            ((TrackListFragment)getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG)).setCurSelectedSong(restoreCurrentSong);
            currentSong = restoreCurrentSong;
//            setSongFullTimeSeekBarProgress();
            myHandler.postDelayed(mUpdateSongTimeRunnable, 10);
            myHandler.postDelayed(mUpdateSeekBarRunnable, 10);
            fileCurrentSong = new File (currentSong.getPath());
        }
    }

    private void initViews(@NonNull View view) {
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );

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
        seekBar = view.findViewById(R.id.seekbarSongTime);
    }

    private void setListenersOnViews() {
        seekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        fastForwardButton.setOnClickListener(mFastForwardClickListener);
        fastBackwardButton.setOnClickListener(mFastBackwardClickListener);
        replayImageButton.setOnClickListener(mReplayClickListener);
        shuffleImageButton.setOnClickListener(mShuffleClickListener);
    }

    private void registerBroadcastReceivers(@NonNull Context context) {
        context.registerReceiver(forwardButtonFromServiceToFragmentBR,
                new IntentFilter(TAG_FORWARD_BUT_PS_TO_F_BR));
        context.registerReceiver(backwardButtonFromServiceToFragmentBR,
                new IntentFilter(TAG_BACKWARD_BUT_PS_TO_F_BR));
        context.registerReceiver(playButtonFromServiceToFragmentBR,
                new IntentFilter(TAG_PLAY_BUT_PS_TO_F_BR));
    }

    public void handleStateCurMediaPlayer(){
        if (!playImageButton.isSelected() && !isPaused && !isContinued) {
            if (curMediaPlayer.isPlaying()) {
                curMediaPlayer.pause();
                curMediaPlayer.stop();

                isResumed = false;
                myHandler.removeCallbacks(mUpdateSongTimeRunnable);
                myHandler.removeCallbacks(mUpdateSeekBarRunnable);
            }
            playImageButton.callOnClick();
        }
        if (isPaused && !isContinued){
            curMediaPlayer.stop();
            isResumed = false;
            myHandler.removeCallbacks(mUpdateSongTimeRunnable);
            myHandler.removeCallbacks(mUpdateSeekBarRunnable);
            playImageButton.callOnClick();
        }
        if (isContinued){
            if (!isPaused) {
                playImageButton.setSelected(true);
            } else {
                playImageButton.setSelected(false);
            }
            setStatesButtons();
        }
        isContinued = false;
    }


    public void setNameSongArtist(Song song){
        this.nameSong.setText(song.getTitle());
        this.nameArtist.setText(song.getNameArtist());
    }

    public void setFileNewSong(File file){
        fileCurrentSong = file;
        try {
            if (curMediaPlayer.isPlaying()) {
                curMediaPlayer.pause();
                curMediaPlayer.stop();
                isResumed = false;
                myHandler.removeCallbacks(mUpdateSongTimeRunnable);
                myHandler.removeCallbacks(mUpdateSeekBarRunnable);
            }
            if (isPaused) {
                curMediaPlayer.stop();
                isResumed = false;
                myHandler.removeCallbacks(mUpdateSongTimeRunnable);
                myHandler.removeCallbacks(mUpdateSeekBarRunnable);
                isPaused = false;
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
                isResumed = false;
                myHandler.removeCallbacks(mUpdateSongTimeRunnable);
                myHandler.removeCallbacks(mUpdateSeekBarRunnable);
                curMediaPlayer.reset();
                curMediaPlayer.setDataSource(file.toString());
            } else {
                curMediaPlayer.stop();
                isResumed = false;
                myHandler.removeCallbacks(mUpdateSongTimeRunnable);
                myHandler.removeCallbacks(mUpdateSeekBarRunnable);
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
        savedInstanceState.putBoolean("statePaused", isPaused);
        savedInstanceState.putBoolean("stateResume", isResumed);
    }
}
