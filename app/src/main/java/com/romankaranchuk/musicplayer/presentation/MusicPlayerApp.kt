package com.romankaranchuk.musicplayer.presentation

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.romankaranchuk.musicplayer.BuildConfig
import com.romankaranchuk.musicplayer.di.AppDeps
import com.romankaranchuk.musicplayer.di.DaggerAppComponent
import com.romankaranchuk.musicplayer.di.util.Injectable
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject

class MusicPlayerApp : Application(), HasAndroidInjector {

    companion object {
        lateinit var context: Context
    }

    @Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        setupDagger()
        setupTimber()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setupDagger() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityStopped(activity: Activity) {}

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is FragmentActivity) {
                    activity.supportFragmentManager
                        .registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                            override fun onFragmentPreAttached(
                                fm: FragmentManager,
                                f: Fragment,
                                context: Context
                            ) {
                                super.onFragmentPreAttached(fm, f, context)
                                if (f is Injectable) {
                                    AndroidSupportInjection.inject(f)
                                }
                            }
                        }, true)
                }
            }
        })

        DaggerAppComponent.builder()
            .appDeps(AppDepsImpl())
            .build()
            .inject(this)
    }

    inner class AppDepsImpl : AppDeps {
        override val context: Context = this@MusicPlayerApp
    }
}