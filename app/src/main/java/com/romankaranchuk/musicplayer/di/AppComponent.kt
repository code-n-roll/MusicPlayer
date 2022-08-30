package com.romankaranchuk.musicplayer.di

import android.content.Context
import com.romankaranchuk.musicplayer.di.module.AppModule
import com.romankaranchuk.musicplayer.di.module.DataModule
import com.romankaranchuk.musicplayer.di.module.UseCaseModule
import com.romankaranchuk.musicplayer.di.module.ViewModelModule
import com.romankaranchuk.musicplayer.di.module.injector.ActivityModule
import com.romankaranchuk.musicplayer.di.module.injector.FragmentModule
import com.romankaranchuk.musicplayer.di.module.injector.ServiceModule
import com.romankaranchuk.musicplayer.presentation.MusicPlayerApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules=[
    ActivityModule::class,
    FragmentModule::class,
    ServiceModule::class,

    ViewModelModule::class,

    AppModule::class,

    AndroidInjectionModule::class,

    UseCaseModule::class,
    DataModule::class
], dependencies = [AppDeps::class])
interface AppComponent {

    fun inject(app: MusicPlayerApp)

    @Component.Builder
    interface Builder {

        fun appDeps(appDeps: AppDeps): Builder

        fun build(): AppComponent
    }
}

interface AppDeps {
    val context: Context
}