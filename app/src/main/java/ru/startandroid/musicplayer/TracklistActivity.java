package ru.startandroid.musicplayer;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by NotePad.by on 14.10.2016.
 */


public class TracklistActivity extends AppCompatActivity
{
    private String TRACKLIST_TAG = "tracklistFragment",
                    FULLSCREEN_TAG = "fullscreenFragment";


    private TracklistFragment tf;
    private static SongCardView curSelectedSong;
    private static ArrayList<String> filesNames = new ArrayList<String>()
    {{
        add("White Stripes - Seven Nation Army (The Glitch Mob Remix-dubstep) (Battlefield 1 ost).mp3");
        add("The Lumineers - Long Way From Home.mp3");
        add("Twenty One Pilots - Stressed Out.mp3");
        add("Лепс Георгий - Рюмка водки на столе (Live).mp3");
        add("Ляпис Трубецкой - Евпатория.mp3");
        add("Muse - Hysteria.mp3");
        add("Brother Dege - The Black Sea.mp3");
        add("Muse - Stockholm Syndrome.mp3");
        add("The Weeknd - False Alarm (Live).mp3");
        add("Баста - Евпатория(acoustic).mp3");
    }};
    private static ArrayList<SongCardView> songsCardView = new ArrayList<SongCardView>(){{
        add(new SongCardView(0, "Holiday", "Green Day", R.drawable.columbia, R.string.stuff, filesNames.get(0)));
        add(new SongCardView(1, "Holiday", "Green Day", R.drawable.columbia, R.string.stuff, filesNames.get(1)));
        add(new SongCardView(2, "Holiday", "Green Day", R.drawable.brasil, R.string.stuff, filesNames.get(2)));
        add(new SongCardView(3, "Holiday", "Green Day", R.drawable.argentina, R.string.stuff, filesNames.get(3)));
        add(new SongCardView(4, "Holiday", "Green Day", R.drawable.chile, R.string.stuff, filesNames.get(4)));
        add(new SongCardView(5, "Jesus of suburbia", "Green Day", R.drawable.uruguay, R.string.stuff, filesNames.get(5)));
        add(new SongCardView(6, "Nothing else matters", "Metallica", R.drawable.chile, R.string.stuff, filesNames.get(6)));
        add(new SongCardView(7, "Holiday", "Green Day", R.drawable.brasil, R.string.stuff, filesNames.get(7)));
        add(new SongCardView(8, "Holiday", "Green Day", R.drawable.argentina, R.string.stuff, filesNames.get(8)));
        add(new SongCardView(9, "Holiday", "Green Day", R.drawable.uruguay, R.string.stuff, filesNames.get(9)));
    }};
    private String LOG_TAG = "MyLogs";

    public static void setCurSelectedSong(SongCardView curSelectedSong){
        TracklistActivity.curSelectedSong = curSelectedSong;
    }
    public static SongCardView getCurSelectedSong(){
        return curSelectedSong;
    }

    public static ArrayList<SongCardView> getSongsCardView(){
        return songsCardView;
    }

    public SongCardViewAdapter getSongCardViewAdapter(){
        TracklistFragment someFragment = (TracklistFragment)
                this.getSupportFragmentManager().findFragmentById(tf.getId());

        return someFragment.getSongCardViewAdapterFragment();
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
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "TracklistActivity onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracklist);

        String showFpf = getIntent().getStringExtra("TAG_SHOW_FPF");

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

            if (getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG) == null) {
                tf = new TracklistFragment();
                ft.add(R.id.fContainerActTracklist, tf, TRACKLIST_TAG);
                if (showFpf != null){
                    if (tf.getFpf() == null) {
                        tf.setFpf(new FullscreenPlayerFragment());
                    }
                    tf.getFpf().setContinued(true);
                    ft.replace(R.id.fContainerActTracklist, tf.getFpf(), FULLSCREEN_TAG);
                    ft.addToBackStack(FULLSCREEN_TAG);
                }

                //            fm.executePendingTransactions();
            }
        ft.commit();


        Log.d(LOG_TAG, "TracklistActivity onCreate");
    }

//    @Override
//    public void onBackPressed(){
//        super.onBackPressed();


//        FullscreenPlayerFragment curFragment = tf.getFpf();
//
//        if (curFragment.getCurMediaPlayer().isPlaying()) {
//            curFragment.getCurMediaPlayer().pause();
//            curFragment.getCurMediaPlayer().stop();
//            ImageButton curPlayImageButton = curFragment.getPlayImageButton();
//            curPlayImageButton.setSelected(!curPlayImageButton.isSelected());
//            curFragment.setStatePlayButton();
//            curFragment.setResume(false);
//            curFragment.getMyHandler().removeCallbacks(curFragment.getUpdateSongTime());
//        }
//    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.song_cardview_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.edit_song_name:
                return true;
            case R.id.delete_song:
                return true;
            case R.id.delete_song_cache:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }*/
}

