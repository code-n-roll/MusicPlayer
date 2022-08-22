package com.romankaranchuk.musicplayer.di.module.injector

import androidx.fragment.app.FragmentFactory
import com.romankaranchuk.musicplayer.di.util.FragmentInjectionFactory
import com.romankaranchuk.musicplayer.presentation.ui.main.MainFragment
import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerFragment
import com.romankaranchuk.musicplayer.presentation.ui.player.sleeptimer.SleepTimerBottomSheetDialog
import com.romankaranchuk.musicplayer.presentation.ui.player.actions.SongActionsBottomSheetDialog
import com.romankaranchuk.musicplayer.presentation.ui.player.lyrics.SongLyricsBottomSheetDialog
import com.romankaranchuk.musicplayer.presentation.ui.player.page.PlayerPageFragment
import com.romankaranchuk.musicplayer.presentation.ui.tracklist.TrackListFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @Binds
    abstract fun bindFragmentFactory(factory: FragmentInjectionFactory): FragmentFactory

//    @Binds
//    @IntoMap
//    @ClassKey(TrackListFragment::class)
//    abstract fun bindTrackListFragment(fragment: TrackListFragment): Fragment

//    @Binds
//    @IntoMap
//    @ClassKey(MainFragment::class)
//    abstract fun bindMainFragment(fragment: MainFragment): Fragment

    @ContributesAndroidInjector
    abstract fun contributeTrackListFragment(): TrackListFragment

    @ContributesAndroidInjector
    abstract fun contributePlayerFragment(): PlayerFragment

    @ContributesAndroidInjector
    abstract fun contributePlayerPageFragment(): PlayerPageFragment

    @ContributesAndroidInjector
    abstract fun contributeSongActionsBottomSheetDialog(): SongActionsBottomSheetDialog

    @ContributesAndroidInjector
    abstract fun contributeSongLyricsBottomSheetDialog(): SongLyricsBottomSheetDialog

    @ContributesAndroidInjector
    abstract fun contributeSleepTimerBottomSheetDialog(): SleepTimerBottomSheetDialog

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment
}