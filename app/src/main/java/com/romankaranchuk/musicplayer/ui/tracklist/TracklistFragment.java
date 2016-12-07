package com.romankaranchuk.musicplayer.ui.tracklist;

import android.annotation.TargetApi;
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
    private ArrayList<Song> songsCardView;
    private PlayerFragment fpf;
    private String FULLSCREEN_TAG = "fullscreenFragment",
                    TRACKLIST_TAG = "tracklistFragment",
                    SHOW_FPF_TAG = "showFpf",
                    SELECTED_SONG = "selectedSong",
                    LIST_SONGS = "songsSongCardView";
    private String LOG_TAG = "myLogs";
    private Song curSelectedSong;
    private MusicRepository mRepository;
    private Handler mMainHandler;
    private List<Album> mAlbums;
    private static ArrayList<Song> mSongs;
    private static int sortBy;

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

        MusicDataSource localDataSource = MusicLocalDataSource.getInstance(getContext());
        mRepository = MusicRepository.getInstance(localDataSource);
        mRepository.addContentObserver(this);

//        ArrayList<Integer> durations = JniUtils.printAllSongs(TracklistFragment.getSongs());
        ArrayList<Integer> durations = mRepository.printAllSongs();
        JniUtils.checkJNI(durations);
        Log.d(LOG_TAG, "TracklistFragment onCreate");
    }


    @TargetApi(19)
    public void restoreDefaultToolbar(TracklistActivity ta){
        WindowManager.LayoutParams attrs = ta.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ta.getWindow().setAttributes(attrs);
        ta.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracklist, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.tracklist_toolbar);

        TracklistActivity ta = (TracklistActivity) getActivity();
        ta.setSupportActionBar(toolbar);
        if (ta.getSupportActionBar() != null) {
            ta.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ta.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        restoreDefaultToolbar(ta);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        final GridView tracklistList = (GridView) view.findViewById(R.id.tracklist_list);
//        songsCardView = TracklistActivity.getSongsCardView();
        loadAndSortSongs(1);

        if (songListAdapter == null) {
            songListAdapter = new SongListAdapter(getActivity(),
                    R.layout.content_songcardview
                    , mSongs);
        }
        tracklistList.setAdapter(songListAdapter);

//        tracklistList.setDivider(null);

        AdapterView.OnItemClickListener itemClickListener = new
            AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id){
                    curSelectedSong = TracklistActivity.getCurSelectedSong();
                    if ( curSelectedSong == null)
                        setCurSelectedSong((Song)
                                parent.getItemAtPosition(position));


                    Song justSelectedSong = (Song)
                            parent.getItemAtPosition(position);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    if (fpf == null){
                        fpf = new PlayerFragment();
                    }


                    if (curSelectedSong == justSelectedSong){
                        if (fpf.getResume()) {
                            fpf.setContinued(true);
                            ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                        }
                        else {
                            fpf.setContinued(false);
                            TracklistActivity.setCurSelectedSong(curSelectedSong);
                            ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                            ft.addToBackStack(FULLSCREEN_TAG);
                            ft.commit();
                        }

                    } else {
                        curSelectedSong = justSelectedSong;
                        TracklistActivity.setCurSelectedSong(curSelectedSong);
                        File file = new File(curSelectedSong.getPath());
                        fpf.setFileNewSong(file);
                        ft.replace(R.id.fContainerActTracklist, fpf, FULLSCREEN_TAG);
                        ft.addToBackStack(FULLSCREEN_TAG);
                        ft.commit();

                        fpf.getPagerFullscreenPlayer().setCurrentItem(Integer.parseInt(curSelectedSong.getId()));
                        fpf.setFastForwardCall(false);
                        fpf.setFastBackwardCall(false);
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
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "TracklistFragment onAttach");
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "TracklistFragment onActivityCreated");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "TracklistFragment onStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "TracklistFragment onResume");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "TracklistFragment onPause");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "TracklistFragment onStop");
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(LOG_TAG, "TracklistFragment onDestroyView");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "TracklistFragment onDestroy");
    }
    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(LOG_TAG, "TracklistFragment onDetach");
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
        if (mAlbums == null){
            loadAlbums();
        }
        if (mSongs == null) {
            mSongs = new ArrayList<>();
            for (Album album : mAlbums) {
                for (Song song : mRepository.getSongs(album.getId(), false)) {
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


/*@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
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


