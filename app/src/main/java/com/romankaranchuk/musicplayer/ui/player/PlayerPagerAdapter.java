package com.romankaranchuk.musicplayer.ui.player;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by NotePad.by on 01.11.2016.
 */

public class PlayerPagerAdapter extends FragmentPagerAdapter {

    PlayerPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public int getCount(){
        return 10;
    }

    @Override
    public Fragment getItem(int position){
        return PlayerPageFragment.newInstance(position);
    }

}
