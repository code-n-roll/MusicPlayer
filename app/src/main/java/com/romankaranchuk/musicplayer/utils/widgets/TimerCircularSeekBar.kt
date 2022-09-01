package com.romankaranchuk.musicplayer.utils.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.res.use
import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.databinding.ViewTimerCircularSeekbarBinding
import com.romankaranchuk.musicplayer.utils.spToPx
import timber.log.Timber

class TimerCircularSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TEXT_SIZE_SP = 12f
        private const val DEFAULT_WIDTH_PX = 200
        private const val PROGRESS_MIN = 0f
        private const val PROGRESS_MAX = 60f
        private const val PROGRESS_START_RANGE_LEFT = PROGRESS_MIN
        private const val PROGRESS_START_RANGE_RIGHT = 14f
        private const val PROGRESS_END_RANGE_LEFT = 46f
        private const val PROGRESS_END_RANGE_RIGHT = PROGRESS_MAX
        private const val LAP_COUNT_MAX = 2
        private const val LAP_COUNT_MIN = 0
        private const val SEEKBAR_1_SCALE_OFFSET = 0.2f
        private const val SEEKBAR_2_SCALE_OFFSET = 0.3f
        private const val CLOCK_LABEL_COUNT = 11
        private const val CLOCK_LABEL_MULTIPLIER = 5
    }

    private val clockLabelRange = 0..CLOCK_LABEL_COUNT
    private val labels: List<String> by lazy {
        clockLabelRange.map { "${it * CLOCK_LABEL_MULTIPLIER}" }
    }

    // text
    private val textPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = TEXT_SIZE_SP.spToPx()
            color = Color.BLACK
        }
    }

    private lateinit var binding: ViewTimerCircularSeekbarBinding
    private var showMinutesOnly = true

    private var listener: CircularSeekBar.OnCircularSeekBarChangeListener? = null

    private var lapCount = LAP_COUNT_MIN
    private var isEnd = false
    private var isStart = false
    private val startRange = PROGRESS_START_RANGE_LEFT..PROGRESS_START_RANGE_RIGHT
    private val endRange = PROGRESS_END_RANGE_LEFT..PROGRESS_END_RANGE_RIGHT

    private val seekbarChangeListener = object : CircularSeekBar.OnCircularSeekBarChangeListener {
        override fun onProgressChanged(
            circularSeekBar: CircularSeekBar?,
            _progress: Float,
            fromUser: Boolean
        ) {
            if (fromUser) {
                if (isEnd && _progress in startRange && lapCount + 1 <= LAP_COUNT_MAX) {
                    isEnd = false
                    lapCount++
                    animateLapCountInc()
                } else if (isStart && _progress in endRange && lapCount - 1 >= LAP_COUNT_MIN) {
                    isStart = false
                    lapCount--
                    animateLapCountDec()
                }
            } else {
                val oldLapCount = lapCount
                lapCount = (_progress / PROGRESS_MAX).toInt()

                when {
                    oldLapCount == 0 && lapCount == 1 -> showSeekbar1()
                    oldLapCount == 1 && lapCount == 2 -> showSeekbar2()
                    oldLapCount == 0 && lapCount == 2 -> showSeekbars()
                    oldLapCount == 2 && lapCount == 1 -> hideSeekbar2()
                    oldLapCount == 1 && lapCount == 0 -> hideSeekbar1()
                    oldLapCount == 2 && lapCount == 0 -> hideSeekbars()
                }
            }
            // when fromUser=false _progress can be greater than max=60f
            // add 1 to handle ranges post checks properly
            val progress = _progress % (PROGRESS_MAX + 1)

            isEnd = progress in endRange
            isStart = progress in startRange

            updatePointerLock()

            Timber.d("lapCount = $lapCount, isEnd = $isEnd, isStart = $isStart, progress = $progress")

            binding.time.text = formatProgress(progress + lapCount * PROGRESS_MAX, minOnly = showMinutesOnly)
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
        binding = ViewTimerCircularSeekbarBinding.inflate(LayoutInflater.from(context), this)

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
            DEFAULT_WIDTH_PX
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

    fun setProgress(value: Float, showMinutesOnly: Boolean) {
        this.showMinutesOnly = showMinutesOnly
        binding.seekbar.progress = value
        this.showMinutesOnly = true
    }

    fun setOnSeekBarChangeListener(listener: CircularSeekBar.OnCircularSeekBarChangeListener) {
        this.listener = listener
    }

    private fun animateLapCountInc() {
        when (lapCount) {
            1 -> showSeekbar1()
            2 -> showSeekbar2()
        }
    }

    private fun animateLapCountDec() {
        when (lapCount) {
            0 -> hideSeekbar1()
            1 -> hideSeekbar2()
        }
    }

    private fun updatePointerLock() {
        if (lapCount >= LAP_COUNT_MAX) {
            if (isEnd) {
                binding.seekbar.isLockEnabled = true
            } else if (isStart) {
                binding.seekbar.isLockEnabled = false
            }
        } else if (lapCount == LAP_COUNT_MIN) {
            if (isEnd) {
                binding.seekbar.isLockEnabled = false
            } else if (isStart) {
                binding.seekbar.isLockEnabled = true
            }
        } else {
            binding.seekbar.isLockEnabled = false
        }
    }

    private fun drawTimeText(canvas: Canvas) {
        // radius of circle formed by text
        val circleRadiusFormedByText = (measuredWidth / 2 - 50).toFloat()
        for (i in clockLabelRange) {
            // Draw text at start coordinates
            val startX = (measuredWidth / 2 + circleRadiusFormedByText * Math.sin(Math.PI / 6 * i) - textPaint.measureText(labels[i]) / 2).toFloat()
            val startY = (measuredHeight / 2 - circleRadiusFormedByText * Math.cos(Math.PI / 6 * i) + textPaint.measureText(labels[i]) / 2).toFloat()
            canvas.drawText(labels[i], startX, startY, textPaint)
        }
    }

    private fun formatProgress(progress: Float, minOnly: Boolean): String {
        val min = progress.toInt()
        val sec = ((progress - min) * PROGRESS_MAX).toInt()
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

    private fun hideSeekbar1() {
        if (binding.seekbar1.alpha == 0f) {
            return
        }

        binding.seekbar1.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(0f)
            .start()
    }

    private fun hideSeekbar2() {
        if (binding.seekbar2.alpha == 0f) {
            return
        }

        binding.seekbar2.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(0f)
            .start()
    }

    private fun showSeekbar1() {
        if (binding.seekbar1.alpha == 1f) {
            return
        }

        binding.seekbar1.animate()
            .scaleX(1f - SEEKBAR_1_SCALE_OFFSET)
            .scaleY(1f - SEEKBAR_1_SCALE_OFFSET)
            .alpha(1f)
            .start()
    }

    private fun showSeekbar2() {
        if (binding.seekbar2.alpha == 1f) {
            return
        }

        binding.seekbar2.animate()
            .scaleX(1f - SEEKBAR_2_SCALE_OFFSET)
            .scaleY(1f - SEEKBAR_2_SCALE_OFFSET)
            .alpha(1f)
            .start()
    }

    private fun hideSeekbars() {
        hideSeekbar1()
        hideSeekbar2()
    }

    private fun showSeekbars() {
        showSeekbar1()
        showSeekbar2()
    }
}