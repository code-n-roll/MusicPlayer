package com.romankaranchuk.musicplayer.ui.cube;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.romankaranchuk.musicplayer.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.ui.tracklist.TracklistFragment;

/**
 * Created by NotePad.by on 11.12.2016.
 */

public class CubePagerAdapter extends FragmentPagerAdapter{
    CubePagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return TracklistFragment.newInstance();
        } else if (position == 1){
            return PlayerFragment.getSingleton();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
