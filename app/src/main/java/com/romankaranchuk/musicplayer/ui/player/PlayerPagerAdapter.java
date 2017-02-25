package com.romankaranchuk.musicplayer.ui.player;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.romankaranchuk.musicplayer.data.Song;

import java.util.ArrayList;

/**
 * Created by NotePad.by on 01.11.2016.
 */

public class PlayerPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Song> mSongs;

    PlayerPagerAdapter(FragmentManager fragmentManager, ArrayList<Song> mSongs){
        super(fragmentManager);
        this.mSongs = mSongs;
    }

    @Override
    public int getCount(){
        return mSongs.size();
    }

    @Override
    public Fragment getItem(int position){
        return PlayerPageFragment.newInstance(position);
    }

}
