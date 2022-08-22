package com.romankaranchuk.musicplayer.di.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject
import javax.inject.Provider

class FragmentInjectionFactory @Inject constructor(
    private val creators: Map<Class<*>, @JvmSuppressWildcards Provider<Fragment>>
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fragmentClass = loadFragmentClass(classLoader, className)
        return creators[fragmentClass]?.let { fragmentProvider ->
            fragmentProvider.get()
        } ?: super.instantiate(classLoader, className)
    }
}
