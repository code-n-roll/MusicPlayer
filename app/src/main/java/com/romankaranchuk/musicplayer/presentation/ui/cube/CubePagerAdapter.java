package com.romankaranchuk.musicplayer.presentation.ui.cube;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TracklistFragment;



public class CubePagerAdapter extends FragmentPagerAdapter{
    CubePagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TracklistFragment();
        } else if (position == 1){
            return new PlayerFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
