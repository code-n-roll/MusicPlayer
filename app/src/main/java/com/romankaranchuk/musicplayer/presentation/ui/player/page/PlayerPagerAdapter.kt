package com.romankaranchuk.musicplayer.presentation.ui.player.page

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter

import com.romankaranchuk.musicplayer.data.Song

import java.util.ArrayList

class PlayerPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val songs = ArrayList<Song>()

    fun updateSongs(newSongs: List<Song>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return songs.size
            }

            override fun getNewListSize(): Int {
                return newSongs.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return songs[oldItemPosition].id == newSongs[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = songs[oldItemPosition]
                val newItem = songs[newItemPosition]
                return oldItem == newItem
            }
        })
        this.songs.clear()
        this.songs.addAll(newSongs)

        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun createFragment(position: Int): Fragment {
        return PlayerPageFragment.newInstance(position)
    }
}
