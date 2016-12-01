package com.romankaranchuk.musicplayer.ui.main;

import android.content.ServiceConnection;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.service.PlayerService;
import com.romankaranchuk.musicplayer.ui.genres.GenresActivity;


public class MainActivity extends AppCompatActivity {
    private static LinkedList<Song> listRecentlySongs = new LinkedList<Song>(){{}};
    private String LOG_TAG = "MyLogs";
    private static Song curSelectedSong;
    String MAIN_TAG = "MainFragment";
    MainFragment ma;

    private Intent intentPlayerService;
    ServiceConnection serviceConnection;
    boolean bound;
    private PlayerService playerService;

    public PlayerService getPlayerService(){
        return this.playerService;
    }
    public Intent getIntentPlayerService(){
        return this.intentPlayerService;
    }
    public static Song getCurSelectedSong(){
        return curSelectedSong;
    }
    public static void setCurSelectedSong(Song song){
        curSelectedSong = song;
    }
    public static LinkedList<Song> getListRecentlySongs(){
        return listRecentlySongs;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        if (intentPlayerService == null)
//            intentPlayerService = new Intent(this, PlayerService.class);
//
//        if (serviceConnection == null)
//            serviceConnection = new ServiceConnection() {
//                @Override
//                public void onServiceConnected(ComponentName name, IBinder binder) {
//                    Log.d(LOG_TAG, "PlayerFragment onServiceConnected");
//                    playerService = ((PlayerService.PlayerBinder) binder).getService();
//                    bound = true;
//                }
//
//                @Override
//                public void onServiceDisconnected(ComponentName name) {
//                    Log.d(LOG_TAG, "PlayerFragment onServiceDisconnected");
//                    bound = false;
//                }
//            };
//        startService(intentPlayerService);
//        bindService(intentPlayerService, serviceConnection,0);


        if (getSupportFragmentManager().findFragmentByTag(MAIN_TAG) == null) {
            ma = new MainFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fContainerActMain, ma, MAIN_TAG);
            ft.commit();
        }

        Log.d(LOG_TAG, "MainActivity onCreate");
    }


    public void openAllGenres(View view){
        Intent intent = new Intent(this, GenresActivity.class);
        startActivity(intent);
    }
    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px/ metrics.density;
        return (int)dp;
    }


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
//        startService(intentPlayerService);
//        bindService(intentPlayerService, serviceConnection,0);
        Log.d(LOG_TAG, "MainActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "MainActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (bound){
//            unbindService(serviceConnection);
//            bound = false;
//        }
        Log.d(LOG_TAG, "MainActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "MainActivity onDestroy");
    }

}

/*LinearLayout recentlyPlayedSongs = (LinearLayout) findViewById(R.id.recently_playes_songs);
        for (int i = 0; i < 10; i++){
            CardView cv = new CardView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    300, 400
            );
            cv.setLayoutParams(layoutParams);
            cv.setUseCompatPadding(true);
            TextView tv = new TextView(this);
            tv.setLayoutParams(layoutParams);

            tv.setText(R.string.hw);
            cv.addView(tv);
            recentlyPlayedSongs.addView(cv);
        }*/