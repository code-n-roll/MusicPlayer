package com.romankaranchuk.musicplayer.utils.widgets

import android.content.Context
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.romankaranchuk.musicplayer.R

class SecondPageSideShownTransformer(
    private val context: Context,
    private val offsetPx: Int = context.resources.getDimensionPixelOffset(R.dimen.offset),
    private val pageMarginPx: Int = context.resources.getDimensionPixelOffset(R.dimen.page_margin),
    private val isCenterScaled: Boolean = false
) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
//        val viewPager = page.parent.parent as ViewPager2
        val offset = position * -(2 * offsetPx + pageMarginPx)
//        if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
//            if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
//                page.translationX = -offset
//            } else {
                page.translationX = offset
//            }
//        } else {
//            page.translationY = offset
//        }

        if (isCenterScaled) {
            page.apply {
//                when {
//                    position < -1 -> { // [-Infinity,-1)
//                        // This page is way off-screen to the left.
//                        page.translationX = offset
//                    }
//                    position <= 1 -> { // [-1,1]
                        val scaleFactor = Math.max(0.85f, 1 - Math.abs(position))
//                        val vertMargin = page.height * (1 - scaleFactor) / 2
//                        val horzMargin = page.width * (1 - scaleFactor) / 2
//                        translationX = if (position < 0) {
//                            horzMargin - vertMargin / 2
//                        } else {
//                            horzMargin + vertMargin / 2
//                        }
//
//                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor
//                    }
//                    else -> {
//                        // This page is way off-screen to the right.
//                        page.translationX = offset
//                    }
//                }
            }
        } else {
            page.apply {
                scaleX = 1f
                scaleY = 1f
            }
        }
    }
}