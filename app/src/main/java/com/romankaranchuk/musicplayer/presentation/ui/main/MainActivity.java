package com.romankaranchuk.musicplayer.presentation.ui.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.LinkedList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.presentation.service.SearchService;
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TracklistFragment;


public class MainActivity extends AppCompatActivity {
    private static LinkedList<Song> listRecentlySongs = new LinkedList<Song>(){{}};
    private String  LOG_TAG = "MyLogs",
                    TRACKLIST_TAG = "tracklistFragment",
                    FULLSCREEN_TAG = "fullscreenFragment";

    private static String[] PERMISSIONS;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
    }

    private static final int REQUEST_READ_STORAGE = 100;
    private SearchService mSearchService;
    private boolean mServiceBound;

    private static Song curSelectedSong;
    public static File path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);


    public static void setCurSelectedSong(Song song) {
        curSelectedSong = song;
    }
    public static Song getCurSelectedSong() {
        return curSelectedSong;
    }


    public static LinkedList<Song> getListRecentlySongs(){
        return listRecentlySongs;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (getSupportFragmentManager().findFragmentByTag(FULLSCREEN_TAG) != null){
                transaction.replace(R.id.fContainerActMain,
                        getSupportFragmentManager().findFragmentByTag(FULLSCREEN_TAG), FULLSCREEN_TAG);
            } else if (getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG) != null){
                transaction.replace(R.id.fContainerActMain,
                        getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG), TRACKLIST_TAG);
            } else {
                transaction.replace(R.id.fContainerActMain, new TracklistFragment(), TRACKLIST_TAG);
            }
        transaction.commit();


        Log.d(LOG_TAG, "MainActivity onCreate");
    }

    private void searchMusic(){
        if (mServiceBound){
            mSearchService.startMusicSearch();
        } else {
            Intent intent = new Intent(this, SearchService.class);
            bindService(intent, mSearchConnection, BIND_AUTO_CREATE);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound){
            unbindService(mSearchConnection);
            mServiceBound = false;
        }
        Log.d(LOG_TAG, "MainActivity onStop");
    }


    private void requestPermAndSearchMusic(){
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_READ_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        switch(requestCode){
            case REQUEST_READ_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    searchMusic();
                } else {
                    Toast.makeText(this, R.string.read_storage_not_granted, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_add_audio_files_from_sd:
                requestPermAndSearchMusic();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private ServiceConnection mSearchConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(LOG_TAG, "MainActivity onServiceConnected");
            mSearchService = ((SearchService.SearchBinder) binder).getService();
            mServiceBound = true;
            mSearchService.startMusicSearch();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "MainActivity onServiceDisconnected");
            mServiceBound = false;
        }
    };
}