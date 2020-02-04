package com.romankaranchuk.musicplayer.presentation.ui.player.page;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment;
import com.romankaranchuk.musicplayer.utils.MathUtils;
import com.squareup.picasso.Picasso;

public class PlayerPageFragment extends Fragment {

    private static String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private static String SAVE_PAGE_NUMBER = "save_page_number";
    private static String LOG_TAG = "myLogs";

    private ImageView albumCoverImageView;
    private TextView lyricSong;
    private int pageNumber;

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
        Log.d(LOG_TAG, "PlayerPageFragment onCreateView: " + pageNumber);
        return inflater.inflate(R.layout.fragment_player_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        albumCoverImageView = view.findViewById(R.id.fullscreenAlbumCover);
        lyricSong = view.findViewById(R.id.lyricSong);

        setAlbumCoverImageViewSize();

        if (pageNumber > -1 && pageNumber < TrackListFragment.getSongs().size()){
            setAlbumCoverImageView(TrackListFragment.getSongs().get(pageNumber));
            setLyricSong(TrackListFragment.getSongs().get(pageNumber));
        }
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

    public ImageView getAlbumCoverImageView(){
        return albumCoverImageView;
    }

    public void setAlbumCoverImageView(Song song){
        Picasso.with(getContext()).load(song.getImagePath()).into(albumCoverImageView);
        albumCoverImageView.setTag(song.getImagePath());
        if (MathUtils.tryParse(song.getImagePath()) != -1) {
            albumCoverImageView.setScaleType(ImageView.ScaleType.CENTER);
        }
    }

    public void setLyricSong(Song song){
        lyricSong.setText(song.getLyricsSong());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PAGE_NUMBER, pageNumber);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "PlayerPageFragment onActivityCreated: " + pageNumber);
    }
}
