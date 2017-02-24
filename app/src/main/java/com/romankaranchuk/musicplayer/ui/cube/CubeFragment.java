package com.romankaranchuk.musicplayer.ui.cube;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.ui.main.MainActivity;

/**
 * Created by NotePad.by on 11.12.2016.
 */

public class CubeFragment extends Fragment {
    private CubePagerAdapter cubePagerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cube, container, false);
        ViewPager pagerCube = (ViewPager) view.findViewById(R.id.pagerCube);
        cubePagerAdapter = new CubePagerAdapter(getChildFragmentManager());
        pagerCube.setAdapter(cubePagerAdapter);
        pagerCube.setPageTransformer(true, new CubeOutTransformer());

        restoreDefaultToolbar((MainActivity)getActivity());

        return view;
    }
    @TargetApi(19)
    public void restoreDefaultToolbar(MainActivity ma){
        WindowManager.LayoutParams attrs = ma.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ma.getWindow().setAttributes(attrs);
        ma.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
