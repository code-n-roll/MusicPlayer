package ru.startandroid.musicplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
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
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.RunnableFuture;


/**
 * Created by NotePad.by on 18.10.2016.
 */


public class FullscreenPlayerFragment extends Fragment {
    private ImageButton playImageButton, shuffleImageButton, replayImageButton,
            fastForwardButton, fastBackwardButton;
    private SeekBar seekBar;
    private ImageView albumCoverImageView;
    private TextView lyricSong, nameSong, nameArtist, timeStart, timeEnd;
    private SongCardView currentSong;
    private MediaPlayer curMediaPlayer;
    private boolean resume = false, paused=false,continued = false;
    private int curPosition, minutes;
    private double startTime, finalTime, seconds;
    private Handler myHandler = new Handler();
    private File fileCurrentSong, filePlayedSong;
    private String TRACKLIST_TAG = "tracklistFragment", currentTimeSong;
    private boolean startThreadTime = true, startThreadSeekbar = true;
    private String LOG_TAG = "myLogs";


    public SongCardView getCurrentSong(){
        return this.currentSong;
    }
    public boolean getResume(){
        return this.resume;
    }

    public void setContinued(boolean state){
        this.continued = state;
    }

    public MediaPlayer getCurMediaPlayer(){
        return this.curMediaPlayer;
    }
    public void setCurMediaPlayer(MediaPlayer curMediaPlayer) {
        this.curMediaPlayer = curMediaPlayer;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "FullscreenPlayerFragment onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);




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
        Log.d(LOG_TAG, "FullscreenPlayerFragment onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "FullscreenPlayerFragment onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
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
    public void setResume(boolean state){
        this.resume = state;
    }

    public ImageButton getPlayImageButton (){
        return this.playImageButton;
    }



    public Runnable getUpdateSongTime(){
        return UpdateSongTime;
    }

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
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


//        view.setOnKeyListener(new View.OnKeyListener(){
//           @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event){
//               if (keyCode == KeyEvent.KEYCODE_BACK){
//                    onSaveInstanceState(savedInstanceState);
//               }
//               return true;
//           }
//        });
        timeStart = (TextView) view.findViewById(R.id.textViewStart);
        timeEnd = (TextView) view.findViewById(R.id.textViewEnd);
        timeStart.setText(currentTimeSong);



        playImageButton = (ImageButton) view.findViewById(R.id.playPauseSongButton);

        shuffleImageButton = (ImageButton) view.findViewById(R.id.shuffleButton);
        replayImageButton = (ImageButton) view.findViewById(R.id.replayButton);
        fastForwardButton = (ImageButton) view.findViewById(R.id.toNextSongButton);
        fastBackwardButton = (ImageButton) view.findViewById(R.id.toPreviousSongButton);


        albumCoverImageView = (ImageView) view.findViewById(R.id.fullscreenAlbumCover);
        lyricSong = (TextView) view.findViewById(R.id.lyricSong);
        nameSong = (TextView) view.findViewById(R.id.nameSongFullscreenPlayer);
        nameArtist = (TextView) view.findViewById(R.id.nameArtistFullScreenPlayer);


        if (curMediaPlayer == null) {
            curMediaPlayer = MainActivity.getMediaPlayer();
            playImageButton.setSelected(true);
        }
        seekBar = (SeekBar) view.findViewById(R.id.seekbarSongTime);



        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels - MainActivity.convertDpToPixels(20, getActivity());
            albumCoverImageView.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height));
        }

        if (savedInstanceState != null){
            SongCardView restoreCurrentSong = (SongCardView)savedInstanceState.getParcelable("currentSong");
            setDataFullscreenPlayer(restoreCurrentSong);
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
                ArrayList<SongCardView> currentTracklist = TracklistActivity.getSongsCardView();
                int curId = currentSong.getId();
                TracklistFragment curTLFragment = (TracklistFragment) getFragmentManager().findFragmentByTag(TRACKLIST_TAG);
                if (currentTracklist.size() - 1 > curId) {
                    currentSong = currentTracklist.get(curId + 1);
                    fileCurrentSong = new File(MainActivity.getPath(),
                            TracklistActivity.getFilesNames().get(curId+1));
                } else {
                    currentSong = currentTracklist.get(0);
                    fileCurrentSong = new File(MainActivity.getPath(),
                            TracklistActivity.getFilesNames().get(0));
                }
                if ((MainActivity)getActivity() != null){
                    ((MainActivity) getActivity()).setCurSelectedSong(currentSong);
                }
                forBackwardTrack(fileCurrentSong);
                setDataFullscreenPlayer(currentSong);
                setSongFullTimeSeekBarProgress();
            }
        });

        fastBackwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ArrayList<SongCardView> currentTracklist = TracklistActivity.getSongsCardView();
                int curId = currentSong.getId();
                if (curId > 0) {
                    currentSong = currentTracklist.get(curId - 1);
                    fileCurrentSong = new File(MainActivity.getPath(),
                            TracklistActivity.getFilesNames().get(curId - 1));
                }
                else {
                    currentSong = currentTracklist.get(currentTracklist.size()-1);
                    fileCurrentSong = new File(MainActivity.getPath(),
                            TracklistActivity.getFilesNames().get(currentTracklist.size()-1));
                }

                if ((MainActivity)getActivity() != null){
                    ((MainActivity) getActivity()).setCurSelectedSong(currentSong);
                }
                forBackwardTrack(fileCurrentSong);
                setDataFullscreenPlayer(currentSong);
                setSongFullTimeSeekBarProgress();
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






        playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playImageButton.setSelected(!playImageButton.isSelected());
                setStatePlayButton();
                try {
                    if (fileCurrentSong == null) {
                        fileCurrentSong = new File (MainActivity.getPath(),
                                TracklistActivity.getFilesNames().get(currentSong.getId()));

                            curMediaPlayer.setDataSource(fileCurrentSong.toString());
                    }
                    if (filePlayedSong != fileCurrentSong) {
                        ArrayList<SongCardView> list = MainActivity.getListRecentlySongs();
                        if (list.size() == 0 || list.get(list.size()-1) != currentSong) {
                            list.add(list.size(), currentSong);

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


    public void setFileNewSong(File file){
        this.fileCurrentSong = file;
        try {
            if (this.curMediaPlayer.isPlaying())
            {
                this.curMediaPlayer.pause();
                this.curMediaPlayer.stop();
                this.resume = false;
                this.myHandler.removeCallbacks(UpdateSongTime);
                this.myHandler.removeCallbacks(UpdateSeekBar);

            }
            if (paused){
                this.curMediaPlayer.stop();
                this.resume = false;
                this.myHandler.removeCallbacks(UpdateSongTime);
                this.myHandler.removeCallbacks(UpdateSeekBar);
                paused = false;
            }

            this.curMediaPlayer.reset();
            this.curMediaPlayer.setDataSource(this.fileCurrentSong.toString());
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
                resume = false;
                myHandler.removeCallbacks(UpdateSongTime);
                myHandler.removeCallbacks(UpdateSeekBar);
                curMediaPlayer.reset();
                curMediaPlayer.setDataSource(file.toString());
            } else {
                curMediaPlayer.stop();
                resume = false;
                myHandler.removeCallbacks(UpdateSongTime);
                myHandler.removeCallbacks(UpdateSeekBar);
                curMediaPlayer.reset();
                curMediaPlayer.setDataSource(file.toString());
            }
            playImageButton.callOnClick();
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

    public void setAlbumCoverImageView(SongCardView item){
        albumCoverImageView.setImageResource(item.getAlbumCoverResource());
        albumCoverImageView.setTag(item.getAlbumCoverResource());
    }

    public void setLyricSong(SongCardView item){
        lyricSong.setText(item.getLyricSong());
    }

    public void setNameSong(SongCardView item){
        nameSong.setText(item.getNameSong());
    }

    public void setNameArtist(SongCardView item){
        nameArtist.setText(item.getNameArtist());
    }

    public void setDataFullscreenPlayer(SongCardView item){
        currentSong = item;
        setAlbumCoverImageView(item);
        setLyricSong(item);
        setNameArtist(item);
        setNameSong(item);
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
