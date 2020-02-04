package com.romankaranchuk.musicplayer.di

import android.app.Application
import com.romankaranchuk.musicplayer.presentation.MusicPlayerApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules=[
    AppModule::class,
    AndroidSupportInjectionModule::class,
    MainActivityModule::class,
    ViewModelModule::class,
    TrackListFragmentModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: MusicPlayerApp)
}