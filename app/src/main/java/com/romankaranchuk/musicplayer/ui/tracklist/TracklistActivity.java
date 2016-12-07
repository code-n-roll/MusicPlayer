package com.romankaranchuk.musicplayer.ui.tracklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.Collections;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.service.PlayerService;
import com.romankaranchuk.musicplayer.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.utils.MathUtils;

/**
 * Created by NotePad.by on 14.10.2016.
 */


public class TracklistActivity extends AppCompatActivity
{
    private String TRACKLIST_TAG = "tracklistFragment",
                    FULLSCREEN_TAG = "fullscreenFragment",
                    SHOW_FPF_TAG = "showFpf",
            TAG_RESTORE_FPF_PS_TO_F_BR="restoreFpfFromPStoF";

    public static File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

    private TracklistFragment tf;
    private static Song curSelectedSong;
    private String LOG_TAG = "MyLogs";
    private PlayerService playerService;
    BroadcastReceiver restoreFpfFromServiceToFragmentBR;


    public PlayerService getPlayerService(){
        return this.playerService;
    }

    public static void setCurSelectedSong(Song curSelectedSong){
        TracklistActivity.curSelectedSong = curSelectedSong;
    }
    public static Song getCurSelectedSong(){
        return curSelectedSong;
    }

    public SongListAdapter getSongCardViewAdapter(){
        TracklistFragment someFragment = (TracklistFragment)
                getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG);

        return someFragment.getSongListAdapter();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(restoreFpfFromServiceToFragmentBR);
        Log.d(LOG_TAG, "TracklistActivity onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracklist);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG) == null) {
            tf = new TracklistFragment();
            ft.add(R.id.fContainerActTracklist, tf, TRACKLIST_TAG);
        }
        ft.commit();


        restoreFpfFromServiceToFragmentBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                playerService = getPlayerService();
                if (playerService != null && playerService.getMediaPlayer().isPlaying()) {
                    PlayerFragment fpf = new PlayerFragment();
                    fpf.setContinued(true);
                    ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                    ft.addToBackStack(FULLSCREEN_TAG);
                }
                ft.commit();
            }
        };
        registerReceiver(restoreFpfFromServiceToFragmentBR,
                new IntentFilter(TAG_RESTORE_FPF_PS_TO_F_BR));
        Log.d(LOG_TAG, "TracklistActivity onCreate");
    }




    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "TracklistActivity onRestart");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "TracklistActivity onStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "TracklistActivity onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "TracklistActivity onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "TracklistActivity onStop");
    }
}

