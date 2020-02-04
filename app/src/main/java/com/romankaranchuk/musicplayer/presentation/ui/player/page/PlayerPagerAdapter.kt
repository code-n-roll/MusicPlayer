package com.romankaranchuk.musicplayer.presentation.ui.player.page

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import com.romankaranchuk.musicplayer.data.Song

import java.util.ArrayList

class PlayerPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
    private val mSongs = ArrayList<Song>()

    fun updateSongs(songs: List<Song>) {
        this.mSongs.clear()
        this.mSongs.addAll(songs)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mSongs.size
    }

    override fun getItem(position: Int): Fragment {
        return PlayerPageFragment.newInstance(position)
    }

}
