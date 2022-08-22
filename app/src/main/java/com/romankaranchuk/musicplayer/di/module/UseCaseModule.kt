package com.romankaranchuk.musicplayer.di.module

import android.content.Context
import com.romankaranchuk.musicplayer.data.db.MusicRepository
import com.romankaranchuk.musicplayer.data.db.MusicRepositoryImpl
import com.romankaranchuk.musicplayer.domain.LoadTracksUseCase
import com.romankaranchuk.musicplayer.domain.LoadTracksUseCaseImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UseCaseModule {

    @Provides
    @Singleton
    fun provideLoadTracksUseCase(musicRepository: MusicRepository): LoadTracksUseCase {
        return LoadTracksUseCaseImpl(musicRepository)
    }
}