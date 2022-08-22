package com.romankaranchuk.musicplayer.di.module

import android.app.NotificationManager
import android.app.WallpaperManager
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.romankaranchuk.musicplayer.data.api.IMusixmatchApi
import com.romankaranchuk.musicplayer.data.api.MusixmatchApi
import com.romankaranchuk.musicplayer.data.db.AppDatabase
import com.romankaranchuk.musicplayer.data.db.MusicRepository
import com.romankaranchuk.musicplayer.data.db.MusicRepositoryImpl
import com.romankaranchuk.musicplayer.presentation.navigation.Navigator
import com.romankaranchuk.musicplayer.presentation.navigation.NavigatorImpl
import com.romankaranchuk.musicplayer.common.MusicPlayer
import com.romankaranchuk.musicplayer.common.MusicPlayerImpl
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideNavigator(): Navigator {
        return NavigatorImpl()
    }

    @Provides
    @Singleton
    fun provideMusicRepository(appDatabase: AppDatabase): MusicRepository {
        return MusicRepositoryImpl.getInstance(appDatabase)
    }

    @Provides
    @Singleton
    fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
    }

    @Provides
    @Singleton
    fun provideMusicPlayer(mediaPlayer: MediaPlayer): MusicPlayer {
        return MusicPlayerImpl(mediaPlayer)
    }

    @Provides
    @Singleton
    fun provideNetwork(): IMusixmatchApi {
        return MusixmatchApi()
    }

    @Provides
    @Singleton
    fun provideNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun providePicasso(): Picasso {
        return Picasso.get()
    }

    @Provides
    @Singleton
    fun provideWallpaperManager(context: Context): WallpaperManager {
        return WallpaperManager.getInstance(context)
    }
}