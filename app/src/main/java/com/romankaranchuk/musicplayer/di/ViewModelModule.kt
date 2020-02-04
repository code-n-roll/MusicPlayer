package com.romankaranchuk.musicplayer.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TrackListViewModel::class)
    abstract fun bindTrackListViewModel(viewModel: TrackListViewModel): ViewModel
}