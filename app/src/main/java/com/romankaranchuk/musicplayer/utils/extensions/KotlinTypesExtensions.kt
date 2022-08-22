package com.romankaranchuk.musicplayer.utils

import android.content.res.Resources
import android.util.TypedValue

fun Int.dpToPx(): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()
fun Float.spToPx(): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)
