package ru.startandroid.musicplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by NotePad.by on 01.11.2016.
 */

public class FspPageFragment extends Fragment {
    private static ImageView albumCoverImageView;
    private static TextView lyricSong, nameSong, nameArtist;
    private int pageNumber;
    private static String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private static String SAVE_PAGE_NUMBER = "save_page_number",
    SAVE_ALBUMCOVER = "save_albumcover", SAVE_LYRICSONG = "save_lyric_song";
    private static String LOG_TAG = "myLogs";
    private static FullscreenPlayerFragment fpfInstance;

    public static FspPageFragment newInstance(int page, FullscreenPlayerFragment fpf){
        FspPageFragment fspPageFragment = new FspPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        fspPageFragment.setArguments(arguments);
        fpfInstance = fpf;
        return fspPageFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        }
        Log.d(LOG_TAG, "onCreate: " + pageNumber);

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



        albumCoverImageView = (ImageView) view.findViewById(R.id.fullscreenAlbumCover);
        lyricSong = (TextView) view.findViewById(R.id.lyricSong);
        nameSong = (TextView) view.findViewById(R.id.nameSongFullscreenPlayer);
        nameArtist = (TextView) view.findViewById(R.id.nameArtistFullScreenPlayer);
        if (pageNumber > -1 && pageNumber < TracklistActivity.getSongsCardView().size()){
            setAlbumCoverImageView(TracklistActivity.getSongsCardView().get(pageNumber));
            setLyricSong(TracklistActivity.getSongsCardView().get(pageNumber));
        }
        return view;
    }
    public static ImageView getAlbumCoverImageView(){
        return albumCoverImageView;
    }
    public static void setAlbumCoverImageView(SongCardView item){
        albumCoverImageView.setImageResource(item.getAlbumCoverResource());
        albumCoverImageView.setTag(item.getAlbumCoverResource());
    }

    public static void setLyricSong(SongCardView item){
        lyricSong.setText(item.getLyricSong());
    }

    public static void setNameSong(SongCardView item){
        nameSong.setText(item.getNameSong());
    }

    public static void setNameArtist(SongCardView item){
        nameArtist.setText(item.getNameArtist());
    }
    public static void setDataFullscreenPlayer(FullscreenPlayerFragment fpf, SongCardView item){
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
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy: " + pageNumber);
    }
}
