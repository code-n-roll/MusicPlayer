package com.romankaranchuk.musicplayer.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat

fun hideSystemUI(window: Window) {
    // Enables regular immersive mode.
    // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
    // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
            // Set the content to appear under the system bars so that the
            // content doesn't resize when the system bars hide and show.
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // Hide the nav bar and status bar
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

fun startAlphaAnimation(view: View, duration: Long, visibility: Int, isFillAfter: Boolean = false) {
    val alphaAnimation = if (visibility == View.VISIBLE) AlphaAnimation(0f, 1f) else AlphaAnimation(1f, 0f)
    alphaAnimation.duration = duration
    alphaAnimation.fillAfter = isFillAfter
    view.startAnimation(alphaAnimation)
}

fun getDisplayHeightPx(context: Context): Int? {
    val windowManager = ContextCompat.getSystemService(context, WindowManager::class.java)
    return windowManager?.defaultDisplay?.let { display ->
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        metrics.heightPixels
    }
}