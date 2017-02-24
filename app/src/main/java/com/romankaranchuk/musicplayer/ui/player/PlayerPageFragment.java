package com.romankaranchuk.musicplayer.ui.player;

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

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.ui.tracklist.TracklistFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by NotePad.by on 01.11.2016.
 */

public class PlayerPageFragment extends Fragment {
    private ImageView albumCoverImageView;
    private TextView lyricSong;
    private int pageNumber;
    private static String   ARGUMENT_PAGE_NUMBER = "arg_page_number",
                            SAVE_PAGE_NUMBER = "save_page_number",
                            LOG_TAG = "myLogs";


    public PlayerPageFragment(){}

    public static PlayerPageFragment newInstance(int page){
        PlayerPageFragment playerPageFragment = new PlayerPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        playerPageFragment.setArguments(arguments);
        return playerPageFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        }
        Log.d(LOG_TAG, "PlayerPageFragment onCreate: " + pageNumber);

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
        View view = inflater.inflate(R.layout.fragment_player_page, container, false);

        albumCoverImageView = (ImageView) view.findViewById(R.id.fullscreenAlbumCover);
        lyricSong = (TextView) view.findViewById(R.id.lyricSong);

        setAlbumCoverImageViewSize();

        if (pageNumber > -1 && pageNumber < TracklistFragment.getSongs().size()){
            setAlbumCoverImageView(TracklistFragment.getSongs().get(pageNumber));
            setLyricSong(TracklistFragment.getSongs().get(pageNumber));
        }



        Log.d(LOG_TAG, "PlayerPageFragment onCreateView: " + pageNumber);
        return view;
    }

    public void setAlbumCoverImageViewSize(){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels ;
            getAlbumCoverImageView().setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height));
        }
    }

    public TextView getLyricSong(){
        return lyricSong;
    }

    public ImageView getAlbumCoverImageView(){
        return albumCoverImageView;
    }
    public void setAlbumCoverImageView(Song song){
        Picasso.with(getContext()).load(song.getImagePath()).into(albumCoverImageView);
        albumCoverImageView.setTag(song.getImagePath());
    }

    public void setLyricSong(Song song){
        lyricSong.setText(song.getLyricsSong());
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PAGE_NUMBER, pageNumber);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(LOG_TAG, "PlayerPageFragment onAttach: " + pageNumber);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "PlayerPageFragment onActivityCreated: " + pageNumber);
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "PlayerPageFragment onStart: " + pageNumber);
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "PlayerPageFragment onResume: " + pageNumber);
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "PlayerPageFragment onPause: " + pageNumber);
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "PlayerPageFragment onStop: " + pageNumber);
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(LOG_TAG, "PlayerPageFragment onDestroyView: " + pageNumber);
    }
    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(LOG_TAG, "PlayerPageFragment onDetach: " + pageNumber);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "PlayerPageFragment onDestroy: " + pageNumber);
    }
}
