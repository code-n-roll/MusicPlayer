package com.romankaranchuk.musicplayer.utils.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.use
import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.databinding.ViewTimerCircularSeekbarBinding
import com.romankaranchuk.musicplayer.utils.dpToPx
import com.romankaranchuk.musicplayer.utils.spToPx
import kotlin.math.cos
import kotlin.math.sin

class TimerCircularSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TEXT_SIZE_SP = 12f
        private const val DEFAULT_WIDTH = 200
    }

    private val labels: List<String> by lazy {
        (0..11).map { "${it * 5}" }
    }

    // text
    private val textPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = TEXT_SIZE_SP.spToPx()
            color = Color.BLACK
        }
    }

    private lateinit var binding: ViewTimerCircularSeekbarBinding
    private var minOnly = true

    private var listener: CircularSeekBar.OnCircularSeekBarChangeListener? = null

    //    private var lapCount = 0
//    private var totalProgress = 30f

    private val seekbarChangeListener = object : CircularSeekBar.OnCircularSeekBarChangeListener {
        override fun onProgressChanged(
            circularSeekBar: CircularSeekBar?,
            progress: Float,
            fromUser: Boolean
        ) {
            //            val delta = progress - oldProgress
////            totalProgress += if (Math.abs(delta) < 1) delta else 0f
//            Timber.d("oldProgress=$oldProgress, progress=$progress, lapCount=$lapCount, delta=$delta")
//            val isAddLap = delta < 0 && Math.abs(delta) >= 50 && lapCount + 1 <= 2
//            val isMinusLap = delta > 0 && Math.abs(delta) >= 58
//            if (isAddLap) {
//                lapCount++
//            } else if (isMinusLap) {
//                lapCount--
//            }
//
//            progressInt += lapCount * 60
//
//            if (lapCount == 1 || (lapCount == 2 && progress <= 59f) || (lapCount == 0 && progress >= 59f)) {
//                circularSeekBar!!.isLockEnabled = false
//            } else {
//                circularSeekBar!!.isLockEnabled = true
//            }

            binding.time.text = formatProgress(progress, minOnly = minOnly)
            listener?.onProgressChanged(circularSeekBar, progress, fromUser)
        }

        override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            listener?.onStartTrackingTouch(seekBar)
        }

        override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            listener?.onStopTrackingTouch(seekBar)
        }
    }

    init {
        binding = ViewTimerCircularSeekbarBinding.inflate(LayoutInflater.from(context), this, true)

        context.obtainStyledAttributes(attrs, R.styleable.TimerCircularSeekBar, 0, 0).use {
        }

        if (isInEditMode) {
        }

        binding.seekbar.setOnSeekBarChangeListener(seekbarChangeListener)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val result = if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            DEFAULT_WIDTH
        } else {
            Math.min(widthSpecSize, heightSpecSize)
        }

        setMeasuredDimension(result, result)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        if (canvas == null) {
            return
        }

        drawTimeText(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        this.listener = null
    }

    fun setProgress(value: Float, minOnly: Boolean) {
        this.minOnly = minOnly
        binding.seekbar.progress = value
        this.minOnly = true
    }

    fun setOnSeekBarChangeListener(listener: CircularSeekBar.OnCircularSeekBarChangeListener) {
        this.listener = listener
    }

    private fun drawTimeText(canvas: Canvas?) {
        val textR = (measuredWidth / 2 - 50).toFloat() // radius of circle formed by text
        for (i in 0..11) {
            // Draw text 的起始坐标
            val startX = (measuredWidth / 2 + textR * Math.sin(Math.PI / 6 * i) - textPaint.measureText(labels[i]) / 2).toFloat()
            val startY = (measuredHeight / 2 - textR * Math.cos(Math.PI / 6 * i) + textPaint.measureText(labels[i]) / 2).toFloat()
            canvas?.drawText(labels[i], startX, startY, textPaint)
        }
    }

    private fun formatProgress(progress: Float, minOnly: Boolean): String {
        val min = progress.toInt()
        val sec = ((progress - min) * 60).toInt()
        val minStr = if (min <= 9) {
            "0$min"
        } else {
            "$min"
        }
        val secStr = if (sec <= 9) {
            "0$sec"
        } else {
            "$sec"
        }
        return "${minStr}:${if (minOnly) "00" else secStr}"
    }
}