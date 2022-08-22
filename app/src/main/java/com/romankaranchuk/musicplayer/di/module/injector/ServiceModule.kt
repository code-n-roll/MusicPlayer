package com.romankaranchuk.musicplayer.di.module.injector

import com.romankaranchuk.musicplayer.presentation.service.PlayerService
import com.romankaranchuk.musicplayer.presentation.service.SearchService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun contributeSearchService(): SearchService

    @ContributesAndroidInjector
    abstract fun contributePlayerService(): PlayerService
}