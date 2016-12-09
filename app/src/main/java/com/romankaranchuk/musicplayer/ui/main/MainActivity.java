package com.romankaranchuk.musicplayer.ui.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.LinkedList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.service.SearchService;
import com.romankaranchuk.musicplayer.ui.genres.GenresActivity;
import com.romankaranchuk.musicplayer.ui.tracklist.TracklistActivity;


public class MainActivity extends AppCompatActivity {
    private static LinkedList<Song> listRecentlySongs = new LinkedList<Song>(){{}};
    private String  LOG_TAG = "MyLogs",
                    MAIN_TAG = "MainFragment";

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

    public static LinkedList<Song> getListRecentlySongs(){
        return listRecentlySongs;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MainFragment mainFragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fContainerActMain);

        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fContainerActMain, mainFragment, MAIN_TAG);
            transaction.commit();
        }

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

    public void openAllGenres(View view){
        Intent intent = new Intent(this, GenresActivity.class);
        startActivity(intent);
    }




    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound){
            unbindService(mSearchConnection);
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
            case R.id.albums_search_menu:
                requestPermAndSearchMusic();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_search_menu, menu);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "MainActivity onRestart");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "MainActivity onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "MainActivity onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "MainActivity onPause");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "MainActivity onDestroy");
    }
}