package com.romankaranchuk.musicplayer.utils

import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.romankaranchuk.musicplayer.R

fun com.google.android.material.bottomsheet.BottomSheetDialog.setExpandToFullHeightBehavior(
    isCancellableByDragging: Boolean = true
) {
    val bottomSheet = this.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
    val behavior = BottomSheetBehavior.from<View>(bottomSheet)
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
//    behavior.peekHeight = 0
    behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when(newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    dismiss()
                }
                BottomSheetBehavior.STATE_DRAGGING -> {
                    if (isCancellableByDragging) {
                        return
                    }
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    })
}