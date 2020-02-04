package com.romankaranchuk.musicplayer.di

import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TrackListFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeTrackListFragment(): TrackListFragment
}