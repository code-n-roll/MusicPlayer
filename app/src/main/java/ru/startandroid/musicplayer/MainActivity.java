package ru.startandroid.musicplayer;

import android.Manifest;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static ArrayList<SongCardView> listRecentlySongs = new ArrayList<SongCardView>(){{
//        add(new SongCardView(1, "Holiday", "GreenDay", R.drawable.columbia, R.string.stuff));
//        add(new SongCardView(2, "Holiday", "GreenDay", R.drawable.columbia, R.string.stuff));
//        add(new SongCardView(3, "Holiday", "GreenDay", R.drawable.columbia, R.string.stuff));
//        add(new SongCardView(4, "Holiday", "GreenDay", R.drawable.columbia, R.string.stuff));
//        add(new SongCardView(5, "Holiday", "GreenDay", R.drawable.columbia, R.string.stuff));
//        add(new SongCardView(6, "Holiday", "GreenDay", R.drawable.columbia, R.string.stuff));
//        add(new SongCardView(7, "Holiday", "GreenDay", R.drawable.columbia, R.string.stuff));
//        add(new SongCardView(8, "Holiday", "GreenDay", R.drawable.columbia, R.string.stuff));
//        add(new SongCardView(9, "Holiday", "GreenDay", R.drawable.columbia, R.string.stuff));

    }};
    private String LOG_TAG = "MyLogs";

    private static MediaPlayer mediaPlayer;
    private FullscreenPlayerFragment fpf;
    private static SongCardView curSelectedSong;
    private static File path;
    private String MAIN_TAG = "MainFragment";
    private MainFragment ma;


    public static SongCardView getCurSelectedSong(){
        return curSelectedSong;
    }
    public static void setCurSelectedSong(SongCardView songCardView){
        curSelectedSong = songCardView;
    }
    public static ArrayList<SongCardView> getListRecentlySongs(){
        return listRecentlySongs;
    }
    public static MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }
    public static File getPath(){
        return path;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (getFragmentManager().findFragmentByTag(MAIN_TAG) == null) {
            ma = new MainFragment();

            mediaPlayer = new MediaPlayer();
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC
            );
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fContainerActMain, ma, MAIN_TAG);
            ft.commit();
//            fm.executePendingTransactions();
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
    protected void onStop() {
        super.onStop();
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