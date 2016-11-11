package ru.startandroid.musicplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by NotePad.by on 01.11.2016.
 */

public class FspPageFragment extends Fragment {
    private ImageView albumCoverImageView;
    private TextView lyricSong, nameSong, nameArtist;
    private int pageNumber;
    private static String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private static String SAVE_PAGE_NUMBER = "save_page_number",
    SAVE_ALBUMCOVER = "save_albumcover", SAVE_LYRICSONG = "save_lyric_song";
    private static String LOG_TAG = "myLogs";
    private String FULLSCREEN_TAG = "fullscreenFragment";


    public FspPageFragment(){}

    public static FspPageFragment newInstance(int page){
        FspPageFragment fspPageFragment = new FspPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        fspPageFragment.setArguments(arguments);
        return fspPageFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        }
        Log.d(LOG_TAG, "FspPageFragment onCreate: " + pageNumber);

        int savedPageNumber = -1;
        if (savedInstanceState != null){
            savedPageNumber = savedInstanceState.getInt(SAVE_PAGE_NUMBER);
        }



        Log.d(LOG_TAG, "savedPageNumber = " + savedPageNumber);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_fsp_page, container, false);
//        View nameSongArtist = inflater.inflate(R.layout.fragment_fullscreen_player, container, false);

        albumCoverImageView = (ImageView) view.findViewById(R.id.fullscreenAlbumCover);
        lyricSong = (TextView) view.findViewById(R.id.lyricSong);

        setAlbumCoverImageViewSize();

        if (pageNumber > -1 && pageNumber < TracklistActivity.getSongsCardView().size()){
            setAlbumCoverImageView(TracklistActivity.getSongsCardView().get(pageNumber));
            setLyricSong(TracklistActivity.getSongsCardView().get(pageNumber));
        }


        Log.d(LOG_TAG, "FspPageFragment onCreateView: " + pageNumber);
        return view;
    }
    public void setAlbumCoverImageViewSize(){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels - MainActivity.convertDpToPixels(20, getActivity());
            this.getAlbumCoverImageView().setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height));
        }
    }
    public ImageView getAlbumCoverImageView(){
        return albumCoverImageView;
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
    public void setDataFullscreenPlayer(FullscreenPlayerFragment fpf, SongCardView item){
        fpf.setCurrentSong(item);
        setAlbumCoverImageView(item);
        setLyricSong(item);
//        setNameArtist(item);
//        setNameSong(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PAGE_NUMBER, pageNumber);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "FspPageFragment onAttach: " + pageNumber);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "FspPageFragment onActivityCreated: " + pageNumber);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "FspPageFragment onStart: " + pageNumber);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "FspPageFragment onResume: " + pageNumber);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "FspPageFragment onPause: " + pageNumber);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "FspPageFragment onStop: " + pageNumber);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(LOG_TAG, "FspPageFragment onDestroyView: " + pageNumber);
    }



    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(LOG_TAG, "FspPageFragment onDetach: " + pageNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "FspPageFragment onDestroy: " + pageNumber);
    }
}
