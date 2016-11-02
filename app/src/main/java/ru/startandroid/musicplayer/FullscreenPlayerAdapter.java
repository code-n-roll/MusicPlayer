package ru.startandroid.musicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NotePad.by on 01.11.2016.
 */

public class FullscreenPlayerAdapter extends FragmentStatePagerAdapter {
    private FullscreenPlayerFragment fpf;

    public FullscreenPlayerAdapter(FragmentManager fragmentManager, FullscreenPlayerFragment fpf){
        super(fragmentManager);
        this.fpf = fpf;
    }

    @Override
    public int getCount(){
        return 12;
    }


    @Override
    public Fragment getItem(int position){
        return FspPageFragment.newInstance(position, fpf);
    }

}
