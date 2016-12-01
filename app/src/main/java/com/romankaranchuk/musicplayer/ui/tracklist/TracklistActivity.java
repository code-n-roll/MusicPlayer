package com.romankaranchuk.musicplayer.ui.tracklist;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.service.PlayerService;
import com.romankaranchuk.musicplayer.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.utils.MusicUtils;

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

    private static ArrayList<Song> songsCardView = new ArrayList<Song>(){{
        add(new Song("0", MusicUtils.extractSongInfo((new File(path,filesNames.get(0))).getPath()).toString(),  filesNames.get(0), "https://upload.wikimedia.org/wikipedia/ru/6/62/7nationarmy.jpg",0,null,Integer.toString(R.string.stuff)));
        add(new Song("1", MusicUtils.extractSongInfo((new File(path,filesNames.get(1))).getPath()).toString(),  filesNames.get(1), "https://upload.wikimedia.org/wikipedia/en/4/4f/Cleopatra_album_cover.jpg", 0,null, Integer.toString(R.string.stuff)));
        add(new Song("2", MusicUtils.extractSongInfo((new File(path,filesNames.get(2))).getPath()).toString(),  filesNames.get(2), "https://upload.wikimedia.org/wikipedia/en/7/7d/Blurryface_by_Twenty_One_Pilots.png", 0,null, Integer.toString(R.string.stuff)));
        add(new Song("3", MusicUtils.extractSongInfo((new File(path,filesNames.get(3))).getPath()).toString(),  filesNames.get(3), "https://upload.wikimedia.org/wikipedia/ru/b/bf/%D0%9D%D0%B0_%D1%81%D1%82%D1%80%D1%83%D0%BD%D0%B0%D1%85_%D0%B4%D0%BE%D0%B6%D0%B4%D1%8F....jpg",0,null, Integer.toString(R.string.stuff)));
        add(new Song("4", MusicUtils.extractSongInfo((new File(path,filesNames.get(4))).getPath()).toString(),  filesNames.get(4), "http://xn--80adh8aedqi8b8f.xn--p1ai/uploads/images/l/j/a/ljapis_trubetskoj_ti_kinula.jpg", 0,null, Integer.toString(R.string.stuff)));
        add(new Song("5", MusicUtils.extractSongInfo((new File(path,filesNames.get(5))).getPath()).toString(),  filesNames.get(5), "https://upload.wikimedia.org/wikipedia/en/a/aa/Muse_hysteria_cd.jpg",0,null, Integer.toString(R.string.stuff)));
        add(new Song("6", MusicUtils.extractSongInfo((new File(path,filesNames.get(6))).getPath()).toString(),  filesNames.get(6), "https://lastfm-img2.akamaized.net/i/u/ar0/00f1113ffd274a7d82acc626ae886b26",0,null, Integer.toString(R.string.stuff)));
        add(new Song("7", MusicUtils.extractSongInfo((new File(path,filesNames.get(7))).getPath()).toString(),  filesNames.get(7), "https://upload.wikimedia.org/wikipedia/en/7/78/Muse_stockholm.jpg",0,null, Integer.toString(R.string.stuff)));
        add(new Song("8", MusicUtils.extractSongInfo((new File(path,filesNames.get(8))).getPath()).toString(),  filesNames.get(8), "https://upload.wikimedia.org/wikipedia/en/3/39/The_Weeknd_-_Starboy.png",0,null, Integer.toString(R.string.stuff)));
        add(new Song("9", MusicUtils.extractSongInfo((new File(path,filesNames.get(9))).getPath()).toString(),  filesNames.get(9), "http://de.redmp3.su/cover/3743068-460x460/lyapis-crew-trub-yut-vol-1.jpg",0,null, Integer.toString(R.string.stuff)));
    }};
    private String LOG_TAG = "MyLogs";
    private Intent intentPlayerService;
    ServiceConnection serviceConnection;
    private PlayerService playerService;
    boolean bound = false;
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

    public static ArrayList<Song> getSongsCardView(){
        return songsCardView;
    }

    public SongListAdapter getSongCardViewAdapter(){
        TracklistFragment someFragment = (TracklistFragment)
                getSupportFragmentManager().findFragmentByTag(TRACKLIST_TAG);

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
//        startService(intentPlayerService);
//        bindService(intentPlayerService, serviceConnection,0);
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
//        if (bound){
//            unbindService(serviceConnection);
//            bound = false;
//        }
        Log.d(LOG_TAG, "TracklistActivity onStop");
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


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

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

        Log.d(LOG_TAG, "TracklistActivity onCreate");
    }



//    @Override
//    public void onBackPressed(){
//        super.onBackPressed();


//        PlayerFragment curFragment = tf.getFpf();
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

