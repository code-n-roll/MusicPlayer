package com.romankaranchuk.musicplayer.presentation.navigation

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.presentation.ui.main.MainFragment
import com.romankaranchuk.musicplayer.presentation.ui.player.actions.SongActionsBottomSheetDialog
import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerFragment
import com.romankaranchuk.musicplayer.presentation.ui.player.sleeptimer.SleepTimerBottomSheetDialog
import com.romankaranchuk.musicplayer.presentation.ui.player.lyrics.SongLyricsBottomSheetDialog
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.edit.EditAudioActionChooserFragment
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment
import javax.inject.Inject

interface Navigator {
    fun openPlayer(song: Song)
    fun openEditActions(song: Song)
    fun openSongActions(songId: String)
    fun openSongLyrics(songId: String)
    fun openSleepTimer(songId: String)
    fun openTrackList()

    var activity: FragmentActivity?
}

class NavigatorImpl @Inject constructor() : Navigator {

    override var activity: FragmentActivity? = null

    override fun openPlayer(song: Song) {
        val activity = activity ?: return
        activity.supportFragmentManager.beginTransaction()
            .replace(
                R.id.ma_container,
                PlayerFragment.newInstance(song),
                PlayerFragment.PLAYER_FRAGMENT_TAG
            )
            .addToBackStack(PlayerFragment.PLAYER_FRAGMENT_TAG)
            .commit()
    }

    override fun openEditActions(song: Song) {
        val audioSettingsFragment = EditAudioActionChooserFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EditAudioActionChooserFragment.SELECTED_SONG, song);
//                putParcelableArrayList(TrackListFragment.LIST_SONGS, ArrayList(TrackListFragment.songs))
            }
        }
        audioSettingsFragment.show(activity!!.supportFragmentManager, "dialog")
    }

    override fun openSongActions(songId: String) {
        SongActionsBottomSheetDialog.newInstance(songId)
            .show(activity!!.supportFragmentManager, SongActionsBottomSheetDialog.TAG)
    }

    override fun openSongLyrics(songId: String) {
        val songActionsFragment = activity!!.supportFragmentManager.findFragmentByTag(
            SongActionsBottomSheetDialog.TAG)
        val songActionsDialog = songActionsFragment as? SongActionsBottomSheetDialog
        songActionsDialog?.dismiss()
        SongLyricsBottomSheetDialog.newInstance(songId)
            .show(activity!!.supportFragmentManager, SongLyricsBottomSheetDialog.TAG)
    }

    override fun openSleepTimer(songId: String) {
        SleepTimerBottomSheetDialog.newInstance(songId)
            .show(activity!!.supportFragmentManager, SleepTimerBottomSheetDialog.TAG)
    }

    override fun openTrackList() {
        val activity = activity ?: return
        val mainFragment = activity.supportFragmentManager.findFragmentByTag(MainFragment.TAG)
        mainFragment!!.childFragmentManager
            .beginTransaction()
            .replace(R.id.fma_container, TrackListFragment(), TrackListFragment.TAG)
            .addToBackStack(TrackListFragment.TAG)
            .commit()
    }
}