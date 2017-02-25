package com.romankaranchuk.musicplayer.ui.tracklist;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Album;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.data.source.MusicDataSource;
import com.romankaranchuk.musicplayer.data.source.MusicRepository;
import com.romankaranchuk.musicplayer.data.source.local.MusicLocalDataSource;
import com.romankaranchuk.musicplayer.ui.main.MainActivity;
import com.romankaranchuk.musicplayer.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.utils.JniUtils;
import com.romankaranchuk.musicplayer.utils.MathUtils;


/**
 * Created by NotePad.by on 18.10.2016.
 */

public class TracklistFragment extends Fragment implements
    MusicRepository.AlbumsRepositoryObserver {

    private SongListAdapter songListAdapter;
    private PlayerFragment fpf;
    private String  FULLSCREEN_TAG = "fullscreenFragment",
                    SELECTED_SONG = "selectedSong",
                    LIST_SONGS = "songsSongCardView",
                    LOG_TAG = "myLogs";
    private Song curSelectedSong;
    private MusicRepository mRepository;
    private Handler mMainHandler;
    private List<Album> mAlbums;
    private static ArrayList<Song> mSongs;
    private static int sortBy;
    BroadcastReceiver updateSongs;

    public void setCurSelectedSong(Song item){
        this.curSelectedSong = item;
    }
    public SongListAdapter getSongListAdapter(){
        return this.songListAdapter;
    }
    public static ArrayList<Song> getSongs(){
        return mSongs;
    }
    public static int getSortBy(){
        return sortBy;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        updateSongs = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                loadAndSortSongs(1);
                songListAdapter.notifyDataSetChanged();
            }
        };
        getContext().registerReceiver(updateSongs,
                new IntentFilter("updateSongs"));

        MusicDataSource localDataSource = MusicLocalDataSource.getInstance(getContext());
        mRepository = MusicRepository.getInstance(localDataSource);
        mRepository.addContentObserver(this);

//        ArrayList<Integer> durations = JniUtils.printAllSongs(TracklistFragment.getSongs());
//        ArrayList<Integer> durations = mRepository.printAllSongs(); //NDK
//        JniUtils.checkJNI(durations); //NDK
        Log.d(LOG_TAG, "TracklistFragment onCreate");
    }


    @TargetApi(19)
    public void restoreDefaultToolbar(MainActivity ma){
        WindowManager.LayoutParams attrs = ma.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ma.getWindow().setAttributes(attrs);
        ma.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracklist, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.tracklist_toolbar);


        MainActivity ma = (MainActivity) getActivity();
        ma.setSupportActionBar(toolbar);
//        ma.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ma.getSupportActionBar().setDisplayShowHomeEnabled(true);
        restoreDefaultToolbar(ma);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        mMainHandler = new Handler(getContext().getMainLooper());

        final GridView tracklistList = (GridView) view.findViewById(R.id.tracklist_list);
        loadAndSortSongs(1);

        if (songListAdapter == null) {
            songListAdapter = new SongListAdapter(
                    getActivity(),
                    R.layout.content_songcardview,
                    mSongs);
        }
        tracklistList.setAdapter(songListAdapter);
//        tracklistList.setDivider(null);



        AdapterView.OnItemClickListener itemClickListener = new
            AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id){
                    curSelectedSong = MainActivity.getCurSelectedSong();
                    if ( curSelectedSong == null)
                        setCurSelectedSong((Song)
                                parent.getItemAtPosition(position));


                    Song justSelectedSong = (Song)
                            parent.getItemAtPosition(position);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    fpf = new PlayerFragment();

                    if (curSelectedSong == justSelectedSong){
                        if (PlayerFragment.getResume()) {
                            fpf.setContinued(true);
                            ft.replace(R.id.fContainerActMain, fpf, FULLSCREEN_TAG);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                        }
                        else {
                            fpf.setContinued(false);
                            MainActivity.setCurSelectedSong(curSelectedSong);
                            ft.replace(R.id.fContainerActMain, fpf, FULLSCREEN_TAG);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                        }

                    } else {
                        curSelectedSong = justSelectedSong;
                        MainActivity.setCurSelectedSong(curSelectedSong);
                        if (PlayerFragment.getCurMediaPlayer() != null) {
                            fpf.setFileNewSong(new File(curSelectedSong.getPath()));
                        }
                        ft.replace(R.id.fContainerActMain, fpf, FULLSCREEN_TAG);
                        ft.addToBackStack(FULLSCREEN_TAG);
                        ft.commit();
                    }
                }
            };
        tracklistList.setOnItemClickListener(itemClickListener);

        AdapterView.OnItemLongClickListener itemLongClickListener = new
            AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
                                               long id){
                    Song selectedSong = (Song)
                            parent.getItemAtPosition(position);

                    Bundle args = new Bundle();
                    args.putParcelable(SELECTED_SONG, selectedSong);
                    args.putParcelableArrayList(LIST_SONGS, mSongs);
                    AudioSettingsFragment audioSettingsFragment = new AudioSettingsFragment();

                    audioSettingsFragment.setArguments(args);
                    audioSettingsFragment.show(getFragmentManager(), "dialog");
                    return true;
                }
            };
        tracklistList.setOnItemLongClickListener(itemLongClickListener);


//        registerForContextMenu(tracklistList);
        Log.d(LOG_TAG, "TracklistFragment onCreateView");
        return view;
    }


    @Override
    public void onDestroy(){
        getContext().unregisterReceiver(updateSongs);
        super.onDestroy();
        Log.d(LOG_TAG, "TracklistFragment onDestroy");
    }


    @Override
    public void onAlbumsChanged() {
        mMainHandler.post(new Runnable(){
            @Override
            public void run(){
                loadAlbums();
            }
        });
    }




    private void loadAlbums(){
        mAlbums = mRepository.getAlbums();
    }
    private void loadAndSortSongs(int sortBy){
        loadAlbums();
        if (mSongs == null){
            mSongs = new ArrayList<>();
        }
        for (Album album : mAlbums) {
            for (Song song : mRepository.getSongs(album.getId(), false)) {
                if (!mSongs.contains(song)) {
                    mSongs.add(song);
                }
            }
        }
        Collections.sort(mSongs, MathUtils.getComparator(sortBy));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.songs_sort_by_name:
                sortBy = 0;
                Toast.makeText(getActivity(),"name",Toast.LENGTH_SHORT).show();
                Collections.sort(mSongs, MathUtils.getComparator(sortBy));
                this.songListAdapter.notifyDataSetChanged();
                return true;
            case R.id.songs_sort_by_duration:
                sortBy = 1;
                Toast.makeText(getActivity(),"duration",Toast.LENGTH_SHORT).show();
                Collections.sort(mSongs, MathUtils.getComparator(sortBy));
                this.songListAdapter.notifyDataSetChanged();
                return true;
            case R.id.songs_sort_by_year:
                sortBy = 2;
                Toast.makeText(getActivity(),"year",Toast.LENGTH_SHORT).show();
                Collections.sort(mSongs, MathUtils.getComparator(sortBy));
                this.songListAdapter.notifyDataSetChanged();
                return true;
            case R.id.songs_sort_by_date_modified:
                sortBy = 3;
                Toast.makeText(getActivity(),"date modified",Toast.LENGTH_SHORT).show();
                Collections.sort(mSongs, MathUtils.getComparator(sortBy));
                this.songListAdapter.notifyDataSetChanged();
                return true;
            case R.id.songs_sort_by_format:
                sortBy = 4;
                Toast.makeText(getActivity(),"format",Toast.LENGTH_SHORT).show();
                Collections.sort(mSongs, MathUtils.getComparator(sortBy));
                this.songListAdapter.notifyDataSetChanged();
                return true;
            case R.id.songs_sort_by_language:
                sortBy = 5;
                Toast.makeText(getActivity(),"language",Toast.LENGTH_SHORT).show();
                Collections.sort(mSongs, MathUtils.getComparator(sortBy));
                this.songListAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.songs_sort_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}