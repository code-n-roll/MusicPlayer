package com.romankaranchuk.musicplayer.presentation.ui.cube;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.romankaranchuk.musicplayer.R;

public class CubeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cube, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager pagerCube = view.findViewById(R.id.pagerCube);
        pagerCube.setAdapter(new CubePagerAdapter(getChildFragmentManager()));
        pagerCube.setPageTransformer(true, new CubeOutTransformer());

        final FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null) {
            restoreDefaultToolbar(fragmentActivity);
        }
    }

    @TargetApi(19)
    public void restoreDefaultToolbar(@NonNull FragmentActivity ma){
        WindowManager.LayoutParams attrs = ma.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ma.getWindow().setAttributes(attrs);
        ma.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
