package ru.startandroid.musicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NotePad.by on 01.11.2016.
 */

public class FullscreenPlayerAdapter extends FragmentPagerAdapter {

    FullscreenPlayerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public int getCount(){
        return 10;
    }


    @Override
    public Fragment getItem(int position){
        return FspPageFragment.newInstance(position);
    }

}
