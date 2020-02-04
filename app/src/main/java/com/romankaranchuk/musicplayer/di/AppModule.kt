package com.romankaranchuk.musicplayer.di

import android.app.Application
import com.romankaranchuk.musicplayer.data.db.MusicRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideMusicRepository(app: Application): MusicRepositoryImpl {
        return MusicRepositoryImpl.getInstance(app)
    }
}