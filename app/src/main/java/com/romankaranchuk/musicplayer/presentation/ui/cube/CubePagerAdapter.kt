package com.romankaranchuk.musicplayer.presentation.ui.cube

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerFragment
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment

class CubePagerAdapter internal constructor(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            TrackListFragment()
        } else  {
            PlayerFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
